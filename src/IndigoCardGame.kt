import java.util.*
import kotlin.system.exitProcess

class IndigoCardGame {

    class IndigoGame {
        init {
            println("Indigo Card Game")
            deck.shuffle()
        }

        val playerCards = getCard(6)
        val playerWinCards = mutableListOf<String>()
        var playerScore = 0
        val compCards = getCard(6)
        val compWinCards = mutableListOf<String>()
        var compScore = 0
        val tableCards = getCard(4)
        val pointCards = mutableListOf("A", "K", "Q", "J", "10")
        var lastWinner: String = ""
        var firstTurn = 0
        var candidates: MutableList<String> = mutableListOf()
        fun winCondition(): Boolean = if (tableCards.size > 1) {
            tableCards.last()[0] in tableCards[tableCards.lastIndex - 1] || tableCards.last()
                .last() in tableCards[tableCards.lastIndex - 1]
        } else false

        fun asking(): String {
            var choose: String
            while (true) {
                println("Play first?")
                choose = readLine()!!.lowercase()
                if (choose == "yes" || choose == "no") break
            }
            return choose
        }

        fun printScore(x: Int) {
            playerScore = 0
            compScore = 0
            for (i in pointCards) {
                for (j in playerWinCards) {
                    if (i in j) playerScore++
                }
                for (j in compWinCards) {
                    if (i in j) compScore++
                }
            }
            if (playerCards.size == 0 && compCards.size == 0 && x == 1) {
                if (playerWinCards.size > compWinCards.size) {
                    playerScore += 3
                } else if (playerWinCards.size == compWinCards.size) {
                    if (firstTurn == 1) playerScore += 3 else compScore += 3
                } else compScore += 3
            }
            println("Score: Player $playerScore - Computer $compScore")
            println("Cards: Player ${playerWinCards.size} - Computer ${compWinCards.size}")
        }


        fun playerTurn() {
            println()
            if (tableCards.size != 0) {
                println("${tableCards.size} cards on the table, and the top card is ${tableCards.last()}")
            } else println("No cards on the table")
            println("Cards in hand: ${playerCards.map { "${playerCards.indexOf(it) + 1})$it" }.joinToString(" ")}")
            var card: String
            while (true) {
                println("Choose a card to play (1-${playerCards.size}):")
                card = readLine()!!.lowercase()
                if (card == "exit") {
                    println("Game Over")
                    exitProcess(0)
                }
                if (card.toIntOrNull() in 1..playerCards.size) break
            }
            tableCards.add(playerCards[card.toInt() - 1])
            playerCards.removeAt(card.toInt() - 1)
            if (playerCards.isEmpty() && deck.isNotEmpty()) playerCards.addAll(getCard(6))
        }

        fun getCandidates() {
            candidates.clear()
            for (i in compCards.indices) {
                if (tableCards.last()[0] in compCards[i] || tableCards.last().last() in compCards[i]) candidates.add(
                    compCards[i]
                )
            }
        }

        fun compTurn() {
            println()
            var turnCard = ""
            if (tableCards.size != 0) {
                println("${tableCards.size} cards on the table, and the top card is ${tableCards.last()}")
            } else println("No cards on the table")
            println(compCards.joinToString(" "))
            if (tableCards.size > 0) getCandidates()
            val sameSuits = compCards.groupBy { it.last() }.maxByOrNull { it.value.size }?.value?.toMutableList()
                ?: emptyList<String>()
            val sameSuitsCandidates =
                candidates.groupBy { it.last() }.maxByOrNull { it.value.size }?.value?.toMutableList()
                    ?: emptyList<String>()
            val sameRanks = compCards.groupBy { it.first() }.maxByOrNull { it.value.size }?.value?.toMutableList()
                ?: emptyList<String>()
            val sameRanksCandidates =
                candidates.groupBy { it.first() }.maxByOrNull { it.value.size }?.value?.toMutableList()
                    ?: emptyList<String>()
            if (compCards.size == 1) turnCard = compCards.first()
            else if (candidates.size == 1) turnCard = candidates.first()
            else if (tableCards.size == 0) {
                turnCard = if (sameSuits.size > 1) {
                    sameSuits[Random().nextInt(sameSuits.size)]
                } else if (sameRanks.size > 1) {
                    sameRanks[Random().nextInt(sameRanks.size)]
                } else compCards[Random().nextInt(compCards.size)]
            } else if (tableCards.size >= 1 && candidates.size == 0) {
                turnCard = if (sameSuits.size > 1) {
                    sameSuits[Random().nextInt(sameSuits.size)]
                } else if (sameRanks.size > 1) {
                    sameRanks[Random().nextInt(sameRanks.size)]
                } else compCards[Random().nextInt(compCards.size)]
            } else if (candidates.size >= 2) {
                turnCard = if (sameSuitsCandidates.size > 1) {
                    sameSuitsCandidates[Random().nextInt(sameRanksCandidates.size)]
                } else if (sameRanksCandidates.size > 1) {
                    sameRanksCandidates[Random().nextInt(sameRanksCandidates.size)]
                } else candidates[Random().nextInt(candidates.size)]
            }
            tableCards.add(turnCard)
            println("Computer plays $turnCard")
            compCards.remove(turnCard)
            if (compCards.isEmpty() && deck.isNotEmpty()) compCards.addAll(getCard(6))
        }

        fun game(choose: String) {
            println("Initial cards on the table: ${tableCards.joinToString(" ")}")
            if (choose == "yes") {
                firstTurn = 1
                while (tableCards.size != 52) {
                    playerTurn()
                    if (winCondition()) {
                        lastWinner = "player"
                        println("Player wins cards")
                        playerWinCards.addAll(tableCards)
                        tableCards.clear()
                        printScore(0)
                    }
                    compTurn()
                    if (winCondition()) {
                        lastWinner = "computer"
                        println("Computer wins cards")
                        compWinCards.addAll(tableCards)
                        tableCards.clear()
                        printScore(0)
                    }
                    if (compCards.size == 0) println()
                    if (playerCards.size == 0) break
                }
            } else {
                firstTurn = 2
                while (tableCards.size != 52) {
                    compTurn()
                    if (winCondition()) {
                        lastWinner = "computer"
                        println("Computer wins cards")
                        compWinCards.addAll(tableCards)
                        tableCards.clear()
                        printScore(0)
                    }
                    playerTurn()
                    if (winCondition()) {
                        lastWinner = "player"
                        println("Player wins cards")
                        playerWinCards.addAll(tableCards)
                        tableCards.clear()
                        printScore(0)
                    }
                    if (playerCards.size == 0) println()
                    if (playerCards.size == 0) break
                }
            }
            if (tableCards.size != 0) {
                println("${tableCards.size} cards on the table, and the top card is ${tableCards.last()}")
            } else println("No cards on the table")
            if (deck.isEmpty() && playerCards.isEmpty() && compCards.isEmpty()) {
                if (lastWinner == "player" && tableCards.size != 0) {
                    playerWinCards.addAll(tableCards)
                    tableCards.clear()
                } else if (lastWinner == "computer" && tableCards.size != 0) {
                    compWinCards.addAll(tableCards)
                    tableCards.clear()
                } else if (lastWinner == "") {
                    if (choose == "yes") playerWinCards.addAll(tableCards) else compWinCards.addAll(tableCards)
                    tableCards.clear()
                }
                printScore(1)
                println("Game Over")
            }
        }

        companion object Deck {
            val rank = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
            val suit = listOf("♦", "♥", "♠", "♣")
            val deck = mutableListOf<String>()

            init {
                for (i in suit) {
                    for (j in rank) {
                        deck.add(j + i)
                    }
                }
            }

            fun shuffle() = deck.shuffle()

            fun reset() {
                deck.clear()
                for (i in suit) {
                    for (j in rank) {
                        deck.add(j + i)
                    }
                }
                println("Card deck is reset.")
            }

            fun getCard(num: Int): MutableList<String> {
                val gotCards: MutableList<String> = mutableListOf()
                repeat(num) {
                    gotCards.add(deck.last())
                    deck.remove(deck.last())
                }
                return gotCards
            }

        }
    }

    fun main() {
        val x = IndigoGame()
        x.game(x.asking())
    }
}