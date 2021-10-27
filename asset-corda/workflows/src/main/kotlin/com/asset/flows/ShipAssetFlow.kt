/*
 * Author Varatharaj K
 */

package com.asset.flows

import co.paralleluniverse.fibers.Suspendable
import com.asset.contracts.AssetContract
import com.asset.states.AssetState
import com.r3.corda.lib.accounts.workflows.accountService
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import com.r3.corda.lib.accounts.workflows.flows.ShareStateAndSyncAccounts
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.contextLogger
import java.security.PublicKey
import java.util.function.Consumer

object ShipAssetFlow {
    private val logger = contextLogger()

    @InitiatingFlow
    @StartableByRPC
    class Initiator(private val creatorAccountName: String, private val agentAccountName:String, private val  shipperAccountName: String,
                    private val origin: String, private val linearId: String) :
        FlowLogic<AssetState>() {
        companion object {
            object QUERYING : ProgressTracker.Step("Querying existing state.")
            object GENERATING_KEYS : ProgressTracker.Step("Generating Keys for transactions.")
            object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction for between accounts")
            object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints.")
            object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with our private key.")
            object GATHERING_SIGS : ProgressTracker.Step("Gathering the counterparty's signature.") {
                override fun childProgressTracker() = CollectSignaturesFlow.tracker()
            }

            object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
                override fun childProgressTracker() = FinalityFlow.tracker()
            }

            fun tracker() = ProgressTracker(
                    QUERYING,
                    GENERATING_KEYS,
                    GENERATING_TRANSACTION,
                    VERIFYING_TRANSACTION,
                    SIGNING_TRANSACTION,
                    GATHERING_SIGS,
                    FINALISING_TRANSACTION
            )
        }

        override val progressTracker = tracker()

        @Suspendable
        override fun call(): AssetState {

            /***** Getting Agent Account Info *****/
            logger.info("Getting Agent Account Info")
            val agentAccountStateAndRef = accountService.accountInfo(agentAccountName)
            if (agentAccountStateAndRef.isEmpty()) {
                logger.error("Account not synced with this host. Account name: $agentAccountName")
                throw NoSuchElementException("Account not synced with this host. Account name: $agentAccountName")
            }
            val agentAccount = agentAccountStateAndRef.single().state.data
            val agentAnonymousParty = subFlow(RequestKeyForAccount(agentAccount))
            logger.info("Agent Account Name : $agentAccountName")

            /******** Getting the Asset from vault *****/
            logger.info("Getting the Asset from vault")
            progressTracker.currentStep = QUERYING
            val accountCriteria = QueryCriteria.VaultQueryCriteria(
                    externalIds = listOf(agentAccount.identifier.id)
            )

            val idCriteria    = QueryCriteria.LinearStateQueryCriteria(
                    linearId = listOf(UniqueIdentifier.fromString(linearId))
            )
            val stateAndRefList = serviceHub.vaultService.queryBy(
                    contractStateType = AssetState::class.java,
                    criteria = accountCriteria.and(idCriteria)
            ).states
            if (stateAndRefList.isEmpty()) {
                logger.error("Asset not found")
                throw NoSuchElementException("Asset not found")
            }
            val assetStateAndRef = stateAndRefList.single()
            val assetStateFromVault = assetStateAndRef.state.data



            /****** Getting creator Account Info ******/
            logger.info("Getting Creator Account Info")
            val creatorAccountInfoStateAndRef = accountService.accountInfo(creatorAccountName)
            if (creatorAccountInfoStateAndRef.isEmpty()) {
                logger.error("Account not synced with this host. Account name: $creatorAccountName")
                throw NoSuchElementException("Account not synced with this host. Account name: $creatorAccountName")
            }
            val creatorAccountInfo = creatorAccountInfoStateAndRef.single().state.data
            val creatorAnonymousParty = subFlow(RequestKeyForAccount(creatorAccountInfo))
            logger.info("Creator Account name :$creatorAccountName")


            /****** Getting Shipper Account Info ******/
            logger.info("Getting Shipper Account Info")
            val shipperAccountInfoStateAndRef = accountService.accountInfo(shipperAccountName)
            if (shipperAccountInfoStateAndRef.isEmpty()) {
                logger.error("Account not synced with this host. Account name: $shipperAccountName")
                throw NoSuchElementException("Account not synced with this host. Account name: $shipperAccountName")
            }
            val shipperAccountInfo = shipperAccountInfoStateAndRef.single().state.data
            val shipperAnonymousParty = subFlow(RequestKeyForAccount(shipperAccountInfo))
            logger.info("Shipper Account name :$shipperAccountName")

            /***** Setting status and origin ****/
            logger.info("Setting status and origin")
            val outputState = assetStateFromVault.copy(origin = origin,status = "ASSET_SHIPPED",shipperAccountName = shipperAccountName, shipper = shipperAnonymousParty)

            /******** Creating transaction builder *****/
            logger.info("Creating transaction builder")
            progressTracker.currentStep = GENERATING_TRANSACTION
            val transactionBuilder = TransactionBuilder(serviceHub.networkMapCache.notaryIdentities.first())

            /******** Collecting signatures *******/
            logger.info("Collecting signatures ")
            val signers : MutableList<PublicKey> = ArrayList()
            signers.add(creatorAnonymousParty.owningKey)
            signers.add(agentAnonymousParty.owningKey)
            signers.add(shipperAnonymousParty.owningKey)


            /******* Setting input and output state ******/
            logger.info("Setting input and output state")
            transactionBuilder.addOutputState(outputState)
                    .addCommand(AssetContract.Commands.ShipAsset(), signers).addInputState(assetStateAndRef)

            /***** Sign with our identity*/
            logger.info("Local sign")
            progressTracker.currentStep = SIGNING_TRANSACTION
            val locallySignedTx = serviceHub.signInitialTransaction(transactionBuilder, listOfNotNull(ourIdentity.owningKey,agentAnonymousParty.owningKey))

            progressTracker.currentStep = GATHERING_SIGS
            val flowSession: MutableList<FlowSession> = ArrayList()
            var allHost: MutableList<Party> = ArrayList()
            allHost.add(creatorAccountInfo.host)
            allHost.add(shipperAccountInfo.host)
            val set = allHost.toSet()
            allHost = set.toMutableList()
            allHost.forEach(
                Consumer {
                    val session = initiateFlow(it)
                    flowSession.add(session)
                }
            )
            val signedTransaction = subFlow(CollectSignaturesFlow(partiallySignedTx = locallySignedTx,
                sessionsToCollectFrom = flowSession.toSet(),myOptionalKeys = listOf(agentAnonymousParty.owningKey)
                ,progressTracker = GATHERING_SIGS.childProgressTracker()
            ))

            /******* Finality flow *****/
            logger.info("Finality flow")
            progressTracker.currentStep = FINALISING_TRANSACTION
            val transaction = subFlow(FinalityFlow(signedTransaction, flowSession.filter { it.counterparty != ourIdentity}))
            val stateAndRef = transaction.tx.outRefsOfType<AssetState>().single()
            subFlow(ShareStateAndSyncAccounts(stateAndRef, agentAccount.host))
            logger.info("Ship asset flow succeeded")
            return stateAndRef.state.data
        }
    }
    @InitiatedBy(Initiator::class)
    class ShipAssetFlowResponder(val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {
        @Suspendable
        override fun call(): SignedTransaction {
            val signTransactionFlow = object : SignTransactionFlow(counterpartySession) {
                override fun checkTransaction(stx: SignedTransaction) = requireThat {
                }
            }
            val txId = subFlow(signTransactionFlow).id
            return subFlow(ReceiveFinalityFlow(counterpartySession, expectedTxId = txId))
        }
    }
}
