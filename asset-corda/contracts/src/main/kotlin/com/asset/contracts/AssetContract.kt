package com.asset.contracts

import com.asset.states.AssetState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

// ************
// * Contract *
// ************
class AssetContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
        const val ID = "com.asset.contracts.AssetContract"
    }


    override fun verify(tx: LedgerTransaction) {
        if(tx.commands.isEmpty()){
            throw IllegalArgumentException("One command Expected")
        }
        val command = tx.commands[0]
        when(command.value) {
            is Commands.CreateAsset -> requireThat {
                "One Output Expected" using (tx.outputStates.size == 1)

                val outputState = tx.outputsOfType<AssetState>()[0]
                "Shipper Party should be null " using (outputState.shipper == null)
                "Status should be Asset created" using (outputState.status == "ASSET_CREATED")
                "Origin Should be null" using (outputState.origin == null)
                "Destination should be null" using (outputState.destination == null)
                "Transit Should be null" using (outputState.transit == null)

            }
            is Commands.ShipAsset -> requireThat {
                "One Input Expected" using (tx.inputStates.size == 1)
                "One Output Expected" using (tx.outputStates.size == 1)

                val inputState = tx.inputsOfType<AssetState>()[0]
                "Status should be ASSET_CREATED" using (inputState.status == "ASSET_CREATED")

                val outputState = tx.outputsOfType<AssetState>()[0]
                "Status should be Asset created" using (outputState.status == "ASSET_SHIPPED")
                "Origin Should not be null" using (outputState.origin != null)
                "Destination should be null" using (outputState.destination == null)
                "Transit Should be null" using (outputState.transit == null)

            }
            is Commands.DeliverAsset -> requireThat {
                "One Input Expected" using (tx.inputStates.size == 1)
                "One Output Expected" using (tx.outputStates.size == 1)

                val inputState = tx.inputsOfType<AssetState>()[0]
                "Status should be ASSET_SHIPPED" using (inputState.status == "ASSET_SHIPPED")

                val outputState = tx.outputsOfType<AssetState>()[0]
                "Status should be Asset created" using (outputState.status == "ASSET_DELIVERED")
                "Destination should not be null" using (outputState.destination != null)
                "Transit Should bot be null" using (outputState.transit != null)

            }
        }
    }

    // Used to indicate the transaction's intent.

    interface Commands : CommandData {
        class CreateAsset: Commands
        class ShipAsset : Commands
        class DeliverAsset: Commands
    }
}