package kcoin

import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.util.*

data class TransactionOutput(val toAddress: PublicKey,
                             val amount: Int,
                             val merkle_root: String,
                             var hash: String = "") {
    init {
        hash = "${toAddress.encodeToString()}$amount$merkle_root".hash()
    }

    fun isMine(me: PublicKey) : Boolean {
        return toAddress == me
    }
}

data class Transaction(val fromAddress: PublicKey,
                       val toAddress: PublicKey,
                       val amount: Int,
                       var hash: String = "",
                       val inputs: MutableList<TransactionOutput> = mutableListOf(),
                       val outputs: MutableList<TransactionOutput> = mutableListOf()) {

    private var signature: ByteArray = ByteArray(0)

    init {
        hash = "${fromAddress.encodeToString()}${toAddress.encodeToString()}$amount$salt".hash()
    }

    companion object {
        fun createTransaction(fromAddress: PublicKey, toAddress: PublicKey, amount: Int) : Transaction {
            return Transaction(fromAddress, toAddress, amount)
        }

        var salt: Long = 0
            get() {
                field += 1
                return field
            }
    }

    fun sign(privateKey: PrivateKey) : Transaction {
        signature = "${fromAddress.encodeToString()}${toAddress.encodeToString()}$amount".sign(privateKey)
        return this
    }

    fun isSignatureValid() : Boolean {
        return "${fromAddress.encodeToString()}${toAddress.encodeToString()}$amount".verifySignature(fromAddress, signature)
    }
}

