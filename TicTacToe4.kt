
import kotlin.system.exitProcess

abstract class Game {
    abstract val gameName: String
}

data class GameBoard (val numRows : Int, val numCols : Int) {
    val board = Array(numRows) { CharArray(numCols) { ' ' }}
}

interface GameAction {
    fun play(row: Int, column: Int, marking: Char)
    fun checkWin(player: Player): String
    fun printGameBoard()
}

class Player (private var playerScore : Int =0, val playerNum : Int, val playerMarker : Char) {
    var playerName : String = ""

    init {
        println("Enter name of player $playerNum: ")
        playerName = readln().replaceFirstChar { it.uppercase() }
        println("$playerName:, you are '$playerMarker'.")
    }

    fun increaseScore() = playerScore++
    fun printPlayerScore() = println("Player $playerName (marker $playerMarker) has $playerScore point(s)")
}

class TicTacToeGame: Game(),GameAction {
    override val gameName = "Tic Tac Toe"
    var gameboard = GameBoard(3, 3)
    private val rowChars = charArrayOf('A', 'B', 'C')
    var numMoves = 0

    init {
        println("Welcome to $gameName!")
        printGameBoard()
    }

    override fun printGameBoard() {
        println("    1    2    3")
        for (i in 0..2) {
            print("${'A'+i} ")
            for (j in 0..2) {
                print("| ${gameboard.board[i][j]} |")
            }
            println()
        }
    }
    fun isValidMove(row: Int, column: Int, player: Player): Boolean {
        // Code for updating the board
        if (row in 0..2 && column in 0..2) {
            if (gameboard.board[row][column] == ' ') {
                numMoves++
            } else {
                println("Cell is already filled. Try another.")
                return false
            }
        } else {
            println("\nInvalid input. ${player.playerName} (marker ${player.playerMarker}), Please try again.")
            return false
        }
        return true
    }


    private fun checkRows(): Any {
        for (i in 0 until gameboard.board.size) {
            if (gameboard.board[i][0] == gameboard.board[i][1] && gameboard.board[i][1] == gameboard.board[i][2] && gameboard.board[i][0] != ' ') {
                return rowChars[i]
            }
        }
        return -1
    }

    private fun checkColumns(): Int{
        for (i in 0 until gameboard.board.size) {
            if (gameboard.board[0][i] == gameboard.board[1][i] && gameboard.board[1][i] == gameboard.board[2][i] && gameboard.board[0][i] != ' ') {
                return i + 1
            }
        }
        return -1
    }

    private fun checkDiagonals(): Int {
        if (gameboard.board[0][0] == gameboard.board[1][1] && gameboard.board[1][1] == gameboard.board[2][2] && gameboard.board[0][0] != ' ') {
            return 1
        }
        if (gameboard.board[0][2] == gameboard.board[1][1] && gameboard.board[1][1] == gameboard.board[2][0] && gameboard.board[0][2] != ' ') {
            return 2
        }
        return -1
    }

    //metoden som er høyere ordens funksjon og kaller in lambda funksjon "findIndex"
    fun getMoveFromUser(player: Player, indexMove: (Char, Char) -> Int ) : IntArray {
        val moveArray = IntArray(2)
        var askAgain = true
        var move : String

        do {
            print("${player.playerName} (marker ${player.playerMarker}), please enter the row and column for your move (e.g. B2) (or press 'Q' to quit): ")
            move = readln().replace(" ", "").uppercase()  // Trim white space, capitalize.
            if (move == "Q")
                exitProcess(0)
            if (move.length == 2)
                askAgain = false
            if (askAgain)
                println("\nInvalid input. ${player.playerName} (marker ${player.playerMarker}), please try again.")
        }  while (askAgain)

        moveArray[0] = indexMove(move[0].uppercaseChar(),'A')
        moveArray[1] = indexMove(move[1], '1')

        return moveArray
    }

    override fun checkWin(player : Player): String {
        val rowWinner = checkRows()
        if (rowWinner != -1) {
            return "Player ${player.playerName} wins on row $rowWinner."
        }
        val colWinner = checkColumns()
        if (colWinner != -1) {
            return "Player ${player.playerName} wins on column $colWinner"
        }
        val diagonalWinner = checkDiagonals()
        if (diagonalWinner != -1) {
            return ("Player ${player.playerName} wins on diagonal $diagonalWinner")
        }
        if (numMoves == 9) return ("It's a draw!")

        return " "
    }

    override fun play(row: Int, column: Int, marking: Char) {
        gameboard.board[row][column] = marking
        printGameBoard()
    }
} // end class

fun main() {
    val ticTacToe = TicTacToeGame()
    val player1 = Player(playerNum = 1, playerMarker = 'O')
    val player2 = Player(playerNum = 2, playerMarker = 'X')
    var currPlayer = player1

    //lambda funksjon som beregner forskjell mellom to char
    val findIndex = {a: Char , b: Char -> (a - b) }

    while (true) {
        val moveArray = ticTacToe.getMoveFromUser(currPlayer, findIndex)

        if (ticTacToe.isValidMove(moveArray[0], moveArray[1], currPlayer)) {
            ticTacToe.play(moveArray[0], moveArray[1], currPlayer.playerMarker)

            if (ticTacToe.numMoves > 4) {
                if (ticTacToe.checkWin(currPlayer) != " ") {
                    println(ticTacToe.checkWin(currPlayer))
                    if (ticTacToe.checkWin(currPlayer) != "It's a draw!")
                        currPlayer.increaseScore()
                    player1.printPlayerScore()
                    player2.printPlayerScore()
                    println("Do you want to play again? Y for yes, N for no: ")
                    val answer = readln()
                    if (answer.uppercase() == "Y") {
                        ticTacToe.gameboard = GameBoard(3, 3)
                        ticTacToe.printGameBoard()
                        ticTacToe.numMoves = 0
                    } else {
                        println("Goodbye")
                        break
                    }
                } // end if
            } //end if
            currPlayer = if (currPlayer == player1) player2 else player1
        } //end if
    } //end while
} //end main()
