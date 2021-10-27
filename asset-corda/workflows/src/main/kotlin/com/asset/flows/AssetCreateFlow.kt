/*
 * Author Varatharaj K
 */

package com.asset.flows

import net.corda.core.flows.*
import co.paralleluniverse.fibers.Suspendable
import com.asset.contracts.AssetContract
import com.asset.states.AssetState
import com.r3.corda.lib.accounts.workflows.accountService
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount


import com.r3.corda.lib.accounts.workflows.flows.ShareStateAndSyncAccounts
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.contextLogger


object AssetCreateFlow {
    private val logger = contextLogger()
    @StartableByRPC
    @StartableByService
    @InitiatingFlow
    class Initiator(
    private val asset: AssetState
    ) : FlowLogic<AssetState>(){
        companion object {
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


            /****** Getting creator Account Info ******/
            logger.info("Getting creator account Info")
            val assetOwnerAccountInfo = accountService.accountInfo(asset.creatorAccountName!!)
            if (assetOwnerAccountInfo.isEmpty()) {
                logger.error("Account not synced with this host. Account name: "+asset.creatorAccountName)
                throw NoSuchElementException("Account not synced with this host. Account name: "+asset.owner)
            }
            val creatorAcct = assetOwnerAccountInfo.single().state.data
            val creatorAnonymousParty = subFlow(RequestKeyForAccount(creatorAcct))
            logger.info("Creator Account name :" + asset.creatorAccountName)

            /***** Getting Agent account Info *****/
            logger.info("Getting Agent Account Info")
            val agentAccountInfo = accountService.accountInfo(asset.agentAccountName!!)
            if (agentAccountInfo.isEmpty()) {
                logger.error("Account not synced with this host. Account name: "+asset.agentAccountName)
                throw NoSuchElementException("Account not synced with this host. Account name: "+asset.agentAccountName)
            }
            val agentAccount = agentAccountInfo.single().state.data
            val agentAnonymousParty = subFlow(RequestKeyForAccount(agentAccount))
            logger.info("Agent Account Name :" + asset.agentAccountName)

            /***** Creating transaction builder *****/
            logger.info("Creating transaction builder")
            progressTracker.currentStep = GENERATING_TRANSACTION
            val transactionBuilder = TransactionBuilder(serviceHub.networkMapCache.notaryIdentities.first())

            /**** Adding input state ****/
            val inputState = asset.copy(creator= creatorAnonymousParty,agent = agentAnonymousParty, status = "ASSET_CREATED")
            logger.info("Adding input state")
            transactionBuilder.addOutputState(inputState)
                    .addCommand(AssetContract.Commands.CreateAsset(), listOf(creatorAnonymousParty.owningKey,agentAnonymousParty.owningKey))
            progressTracker.currentStep = SIGNING_TRANSACTION

            /***** Creator Signing (Local Sign)****/
            logger.info("Creator Signing")
            val locallySignedTx = serviceHub.signInitialTransaction(transactionBuilder, listOfNotNull(ourIdentity.owningKey,creatorAnonymousParty.owningKey))

            /***** Initiating and collecting signatures of Agent *****/
            logger.info("Initiating and collecting signatures of agent")
            progressTracker.currentStep = GATHERING_SIGS
            val sessionForAccountToSendTo =  initiateFlow(agentAccount.host)
            val accountToMoveToSignature = subFlow(CollectSignatureFlow(locallySignedTx, sessionForAccountToSendTo, agentAnonymousParty.owningKey))

            val agentSignedTransaction = locallySignedTx.withAdditionalSignatures(accountToMoveToSignature)

            /**** Calling finality flow ****/
            progressTracker.currentStep = FINALISING_TRANSACTION
            val transaction = subFlow(FinalityFlow(agentSignedTransaction, listOf(sessionForAccountToSendTo).filter { it.counterparty != ourIdentity }))
            val stateAndRef = transaction.tx.outRefsOfType<AssetState>().single()
            subFlow(ShareStateAndSyncAccounts(stateAndRef, agentAccount.host))
            logger.info("Asset created")
            return stateAndRef.state.data


        }
    }

    @InitiatedBy(Initiator::class)
    class CreateAssetResponder(val counterPartySession: FlowSession) : FlowLogic<Unit>(){
        @Suspendable
        override fun call() {
            val transactionSigner = object : SignTransactionFlow(counterPartySession) {
                override fun checkTransaction(stx: SignedTransaction) {
                }
            }
            val transaction = subFlow(transactionSigner)
            subFlow(ReceiveFinalityFlow(counterPartySession, expectedTxId = transaction.id))
        }
    }
}
