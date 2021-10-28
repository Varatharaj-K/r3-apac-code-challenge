package com.asset.states

import com.asset.contracts.AssetContract
import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.AnonymousParty

/**
 * Asset CORDA state
 *
 * */
@BelongsToContract(AssetContract::class)
data class AssetState(
    val creator: AnonymousParty?,
    val agent: AnonymousParty?,
    val shipper: AnonymousParty?,
    val creatorAccountName: String?,
    val agentAccountName: String?,
    val shipperAccountName: String?,
    val owner: String?,
    val origin: String?,
    val transit: String?,
    val destination: String?,
    val status: String?,
    override val linearId: UniqueIdentifier = UniqueIdentifier()
): LinearState {
    override val participants: List<AbstractParty> get() = participants()
    private fun participants(): List<AbstractParty> {
        val participants = mutableListOf<AbstractParty>()
        if (creator != null) {
            participants.add(creator)
        }
        if (agent != null) {
            participants.add(agent)
        }
        if (shipper != null) {
            participants.add(shipper)
        }
        val setOfParties = participants.toSet()
        return setOfParties.toList()
    }
}
