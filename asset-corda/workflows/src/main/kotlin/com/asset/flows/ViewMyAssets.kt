package com.asset.flows

import com.asset.states.AssetState
import com.r3.corda.lib.accounts.workflows.accountService
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import net.corda.core.node.services.vault.QueryCriteria
import java.util.function.Consumer


@StartableByRPC
@StartableByService
@InitiatingFlow
class ViewMyAssets(private val accountName: String) : FlowLogic<List<AssetState>>(){

    override fun call(): List<AssetState> {

        /***** Getting Account Info *****/
        logger.info("Getting Account Info")
        val shipperAccountStateAndRef = accountService.accountInfo(accountName)
        if (shipperAccountStateAndRef.isEmpty()) {
            logger.error("Account not synced with this host. Account name: $accountName")
            throw NoSuchElementException("Account not synced with this host. Account name: $accountName")
        }
        val shipperAccountInfo = shipperAccountStateAndRef.single().state.data
        logger.info("Account Name : $accountName")

        val accountCriteria = QueryCriteria.VaultQueryCriteria(
            externalIds = listOf(shipperAccountInfo.identifier.id)
        )
        val stateAndRefList = serviceHub.vaultService.queryBy(
            contractStateType = AssetState::class.java,
            criteria = accountCriteria
        ).states

        val assetList: MutableList<AssetState> = ArrayList()
        stateAndRefList.forEach(
            Consumer {
                assetList.add(it.state.data)
            }
        )
        return assetList
    }
}