package kcoin

class BlockChain {

    val chain: MutableList<Block> = mutableListOf()
    private val difficulty = 2
    private val validPrefix = "0".repeat(difficulty)
    var UTXO: MutableMap<String, TransactionOutput> = mutableMapOf()


    fun addBlock(block: Block): Block {
        val minedBlock = if (isMined(block)) block else mineBlock(block)
        chain.add(minedBlock)
        return minedBlock
    }

    private fun isMined(block: Block): Boolean {
        return block.hash.startsWith(validPrefix)
    }

     fun mineBlock(block: Block): Block {

//        println("Mine Block : $block")

        var minedBlock = block.copy()
        while (!isMined(minedBlock)) {
            minedBlock = minedBlock.copy(nonce = minedBlock.nonce + 1)
        }

//        println("Mine Block : $minedBlock")
        updateUTXO(minedBlock)

        return minedBlock
    }

    private fun updateUTXO(block: Block) {

        block.transactions.flatMap { it.inputs }.map { it.hash }.forEach { UTXO.remove(it) }
        UTXO.putAll(block.transactions.flatMap { it.outputs }.associateBy { it.hash })
    }



    fun isValid(): Boolean {
        when {
            chain.isEmpty() -> return true
            chain.size == 1 -> return chain[0].hash == chain[0].calculateHash()
            else -> {
                for (i in 1 until chain.size) {
                    val previousBlock = chain[i - 1]
                    val currentBlock = chain[i]

                    when {
                        currentBlock.hash != currentBlock.calculateHash() -> return false
                        currentBlock.previousHash != previousBlock.calculateHash() -> return false
                        !(isMined(previousBlock) && isMined(currentBlock)) -> return false
                    }
                }
                return true
            }
        }
    }
}