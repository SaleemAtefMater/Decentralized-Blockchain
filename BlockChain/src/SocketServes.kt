package kcoin

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class SocketServes {
        var blockChain = BlockChain()
    fun server() {
        val server = ServerSocket(9999)
        val client = server.accept()
        val output = PrintWriter(client.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(client.inputStream))
        val blockChain = BlockChain()
        val wallet1 = Wallet.createWallet(blockChain)
        val wallet2 = Wallet.createWallet(blockChain)

        println("Wallet 1 balance: ${wallet1.balance}")
        println("Wallet 2 balance: ${wallet2.balance}")

        val tx1 = Transaction.createTransaction(fromAddress = wallet1.publicKey, toAddress = wallet1.publicKey, amount = 100)
        tx1.outputs.add(TransactionOutput(toAddress = wallet1.publicKey, amount = 100, merkle_root = tx1.hash))
        tx1.sign(wallet1.privateKey)


        var genesisBlock = Block(previousHash = "0")
        genesisBlock.addTransaction(tx1)
        genesisBlock = blockChain.addBlock(genesisBlock)
        output.write("$genesisBlock")
        output.println("Wallet 1 balance: ${wallet1.balance}")
        output.println("Wallet 2 balance: ${wallet2.balance}")

        val tx2 = wallet1.sendFundsTo(recipient = wallet2.publicKey, amountToSend = 33)
        val secondBlock = blockChain.addBlock(Block(genesisBlock.hash).addTransaction(tx2))
        output.write("$secondBlock")
        output.println("Wallet 1 balance: ${wallet1.balance}")
        output.println("Wallet 2 balance: ${wallet2.balance}")


    }

    fun client() {
        var client = Socket("192.168.1.231", 9999)

        val output = PrintWriter(client.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(client.inputStream))
        println("Client receive : ")

        val scanner = Scanner(client.inputStream)
        while (scanner.hasNextLine()) {
            println(scanner.nextLine())

        }
        client.close()
    }
}