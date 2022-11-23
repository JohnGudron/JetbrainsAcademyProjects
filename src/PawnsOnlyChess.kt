import kotlin.math.abs
import kotlin.system.exitProcess

class PawnsOnlyChess {
    fun main() {
        val words = ('a'..'h').toMutableList()
        println(" Pawns-Only Chess")
        println("First Player's name:")
        val name1 = readLine()!!
        println("Second Player's name:")
        val name2 = readLine()!!
        val turnRegex = Regex("[a-h][1-8][a-h][1-8]")
        val chessBoard = mutableListOf(
            MutableList(8){" "},
            MutableList(8){"W"},
            MutableList(8){" "},
            MutableList(8){" "},
            MutableList(8){" "},
            MutableList(8){" "},
            MutableList(8){"B"},
            MutableList(8){" "}
        )
        var blackFturn = true // variable that is used in the passant check
        var whiteFturn = true
        var lastBlackTurnLen = 0 // variable that is used to save last turn length
        var lastWhiteTurnLen = 0
        var blackPawnsOnBoard = 8 // variable that is used to count pawns on board after last turn
        var whitePawnsOnBoard = 8

        fun winCondition(): Boolean {
            blackPawnsOnBoard = 0
            whitePawnsOnBoard = 0
            for (i in chessBoard) {
                blackPawnsOnBoard += i.count { it == "B" } // count sum of black pawns
                whitePawnsOnBoard += i.count { it == "W" } // count sum of white pawns
            }
            val trueWin = blackPawnsOnBoard == 0 && whitePawnsOnBoard >= 1 || whitePawnsOnBoard == 0 && blackPawnsOnBoard >= 1 ||
                    chessBoard[0].contains("B") || chessBoard[7].contains("W")
            return trueWin
        }

        fun stalemate(): Boolean {
            var blackStalemate = 0
            var whiteStalemate = 0
            for (i in chessBoard.indices) {
                for (j in chessBoard.indices) {
                    if (chessBoard[i][j] == "W") {
                        if ((chessBoard[i + 1][j] != " " || (i == 1 && chessBoard[i + 2][j] != " ")) &&
                            ((j in 1..6 && chessBoard[i + 1][j + 1] != "B" && chessBoard[i + 1][j - 1] != "B") ||
                                    (j == 0 && chessBoard[i + 1][j + 1] != "B") || (j == 7 && chessBoard[i + 1][j - 1] != "B"))
                        ) whiteStalemate++
                    }
                    if (chessBoard[i][j] == "B") {
                        if ((chessBoard[i - 1][j] != " " || (i == 6 && chessBoard[i - 2][j] != " ")) &&
                            ((j in 1..6 && chessBoard[i - 1][j + 1] != "W" && chessBoard[i - 1][j - 1] != "W") ||
                                    (j == 0 && chessBoard[i - 1][j + 1] != "W") || (j == 7 && chessBoard[i - 1][j - 1] != "W"))
                        ) blackStalemate++
                    }
                }
            }
            return (blackStalemate == blackPawnsOnBoard || whiteStalemate == whitePawnsOnBoard)

        }

        fun printChessBoard() {
            val fieldSeparator = "  +---+---+---+---+---+---+---+---+"
            for (i in 8 downTo 1) {
                println("$fieldSeparator\n" + "$i | ${chessBoard[i - 1].joinToString(" | ")} |")
            }
            println(fieldSeparator)
            println("    ${words.joinToString("   ")}")
        }

        fun turn(player: String) {
            if (player == "white") println("$name1's turn:") else println("$name2's turn:")
            val currentTurn = readLine()!!
            val ind ="${words.indexOf(currentTurn[0])}" + "${currentTurn[1] - 1}" + // translate input turn into String of digits ("a2a3" = "0102")
                    "${words.indexOf(currentTurn[2])}" + "${currentTurn[3] - 1}"
            if (currentTurn == "exit") {
                println("Bye!")
                exitProcess(0)
            }
            if (currentTurn.matches(turnRegex)) {
                if (chessBoard[ind[1].digitToInt()][ind[0].digitToInt()] != "W" && player == "white" || // checking existence of pawn
                    chessBoard[ind[1].digitToInt()][ind[0].digitToInt()] != "B" && player == "black") {
                    println("No $player pawn at ${currentTurn.take(2)}")
                    turn(player)
                } else if (blackFturn && player == "white" && lastBlackTurnLen == 2 && ind[1].digitToInt() == 4 && // checking passant
                    chessBoard[ind[1].digitToInt()][ind[2].digitToInt()] == "B" && abs(ind[0] - ind[2]) == 1 ||
                    whiteFturn && player == "black" && lastWhiteTurnLen == 2 && ind[1].digitToInt() == 3 &&
                    chessBoard[ind[1].digitToInt()][ind[2].digitToInt()] == "W" && abs(ind[0] - ind[2]) == 1
                ) {
                    chessBoard[ind[3].digitToInt()][ind[2].digitToInt()] =
                        chessBoard[ind[1].digitToInt()][ind[0].digitToInt()]
                    chessBoard[ind[1].digitToInt()][ind[0].digitToInt()] = " "
                    chessBoard[ind[1].digitToInt()][ind[2].digitToInt()] = " "

                    printChessBoard()

                } else if ( //checking almost all invalid inputs
                    abs(ind[1] - ind[3]) !in 1..2 || ind[0] != ind[2] && chessBoard[ind[3].digitToInt()][ind[2].digitToInt()] == " " ||
                    abs(ind[0] - ind[2]) > 1 ||
                    ind[3] < ind[1] && player == "white" ||
                    ind[3] > ind[1] && player == "black" ||
                    (abs(ind[1] - ind[3]) == 2) && (ind[1].digitToInt() != 1) && player == "white" ||
                    (abs(ind[1] - ind[3]) == 2) && (ind[1].digitToInt() != 6) && player == "black" ||
                    chessBoard[ind[3].digitToInt()][ind[2].digitToInt()] != " " && ind[0] == ind[2]
                ) {
                    println("Invalid Input")
                    turn(player)
                } else if (abs(ind[1] - ind[3]) == 1 && abs(ind[0] - ind[2]) == 1 && // checking capture
                    (chessBoard[ind[3].digitToInt()][ind[2].digitToInt()] == "B" && player == "white" ||
                            chessBoard[ind[3].digitToInt()][ind[2].digitToInt()] == "W" && player == "black")
                ) {
                    chessBoard[ind[3].digitToInt()][ind[2].digitToInt()] =
                        chessBoard[ind[1].digitToInt()][ind[0].digitToInt()]
                    chessBoard[ind[1].digitToInt()][ind[0].digitToInt()] = " "
                    blackFturn = false
                    whiteFturn = false
                    printChessBoard()
                } else { // making a simple turn (without passant or capture)
                    chessBoard[ind[3].digitToInt()][ind[2].digitToInt()] =
                        chessBoard[ind[1].digitToInt()][ind[0].digitToInt()]
                    chessBoard[ind[1].digitToInt()][ind[0].digitToInt()] = " "
                    if (player == "white") {
                        whiteFturn = true
                        lastWhiteTurnLen = abs(ind[1] - ind[3])
                    } else {
                        blackFturn = true
                        lastBlackTurnLen = abs(ind[1] - ind[3])
                    }
                    printChessBoard()
                }
            } else {
                println("Invalid Input")
                turn(player)
            }
        }

        printChessBoard()

        while (true) {
            turn("white")
            if (winCondition()) {
                println("White Wins!")
                break
            } else if (stalemate()) {
                println("Stalemate!")
                break
            }
            turn("black")
            if (winCondition()) {
                println("Black Wins!")
                break
            } else if (stalemate()) {
                println("Stalemate!")
                break
            }
        }
        println("Bye!")
    }

}