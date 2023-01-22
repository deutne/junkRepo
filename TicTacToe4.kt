/* Notes from Dena:
I tried to make Player an interface, but ran into a brick wall so far.

Ideas I have that are not implemented yet:
    Program should not ask for names after first round.
        -> solution Cecile: program doesn't start "main" again but now create a new empty board (player who lost last game, starts the new game)
    Program should keep track of who won and how many times. Print score after each round.
        -> solution Cecile: implemented
    Notes Cecile: I add an interface GameAction that contains the main actions for games (sort of) and override the method in tictactoe
                  I made the fun in player as lambdas
                  Need to be done: We still get an error when we enter just a letter ('g' instead of 'B2' for exemple)
                  -> solution Caroline: getMoveFromUser now checks that length is 2. Removed redundant code that was repeated in isValidMove.
    Notes Caroline: Point system gave points to current player if result was a draw. Fixed with if-sentence
 */

import kotlin.system.exitProcess

abstract class Game{
    abstract val gameName: String
}

data class GameBoard (val numRows : Int, val numCols : Int) {
    val board = Array(numRows) { CharArray(numCols) { ' ' }}
}

interface GameAction {
    fun play(row: Int, column: Int, marking: Char)
    fun checkWin(): String
    fun checkDraw(): Boolean
}

class TicTacToeGame: Game(),GameAction {
    override val gameName = "Tic Tac Toe"
    var gameboard = GameBoard(3, 3)
    val rowChars = charArrayOf('A', 'B', 'C')
    var numMoves = 0

    init {
        println("Welcome to $gameName!")
        printGameBoard()
    }

    fun printGameBoard() {
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
                //gameboard.board[row][column] = player.playerMarker
                numMoves++
                //printGameBoard()
                return true
            } else {
                println("Cell is already filled. Try another.")
                return false
            }
        } else {
            println("\nInvalid input. ${player.playerName}, Please try again.")
            return false
        }
    }
    fun checkRows(): Any {
        for (i in 0 until gameboard.board.size) {
            if (gameboard.board[i][0] == gameboard.board[i][1] && gameboard.board[i][1] == gameboard.board[i][2] && gameboard.board[i][0] != ' ') {
                return rowChars[i]
            }
        }
        return -1
    }

    fun checkColumns(): Int{
        for (i in 0 until gameboard.board.size) {
            if (gameboard.board[0][i] == gameboard.board[1][i] && gameboard.board[1][i] == gameboard.board[2][i] && gameboard.board[0][i] != ' ') {
                return i + 1
            }
        }
        return -1
    }

    fun checkDiagonals(): Int {
        if (gameboard.board[0][0] == gameboard.board[1][1] && gameboard.board[1][1] == gameboard.board[2][2] && gameboard.board[0][0] != ' ') {
            return 1
        }
        if (gameboard.board[0][2] == gameboard.board[1][1] && gameboard.board[1][1] == gameboard.board[2][0] && gameboard.board[0][2] != ' ') {
            return 2
        }
        return -1
    }

    fun getMoveFromUser(playerName : String) : IntArray {
        val moveArray = IntArray(2)
        var askAgain = true
        var move : String

        do {
            print("${playerName}, please enter the row and column for your move (e.g. B2) (or press 'Q' to quit): ")
            move = readln().replace(" ", "").uppercase()
            if (move == "Q")
                exitProcess(0)
            if (move.length == 2)
                askAgain = false
            if (askAgain)
                println("\nInvalid input. ${playerName}, please try again.")
        }  while (askAgain == true)

        moveArray[0] = move[0].uppercaseChar() - 'A'
        moveArray[1] = move[1] - '1'
        return moveArray
    }

    override fun checkDraw(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (gameboard.board[i][j] == ' ') {
                    return false
                }
            }
        }
        return true
    }
    override fun checkWin(): String {
        val rowWinner = checkRows()
        if (rowWinner != -1) {
            return "Winner on row: $rowWinner"
        }
        val colWinner = checkColumns()
        if (colWinner != -1) {
            return "Winner on column: $colWinner"

        }
        val diagonalWinner = checkDiagonals()
        if (diagonalWinner != -1) {
            return ("Winner on diagonal: $diagonalWinner")
        }
        val draw = checkDraw()
        if(draw){
            return ("It's a draw!")
        }
        return " "
    }

    override fun play(row: Int, column: Int, marking: Char) {
        gameboard.board[row][column] = marking
        printGameBoard()
    }


// end fun
} // end class

class Player (private var playerScore : Int =0, val playerNum : Int, val playerMarker : Char) {
    var playerName : String = ""

    init {
        println("Enter name of player $playerNum: ")
        playerName = readln().replaceFirstChar ({ it.uppercase() })
        println("$playerName:, you are '$playerMarker'.")
    }

    val getPlayerName : () -> String = {playerName}
    val getPlayerMark : () -> Char = {playerMarker}
    val getPlayerScore : () -> Int = {playerScore}
    val increaseScore : () -> Unit = {playerScore += 1}
    val printPlayerScore: () -> Unit = {println("Player $playerName has ${getPlayerScore()} point(s)")}
}



fun main() {
    val TicTacToe = TicTacToeGame()
    val player1 = Player(playerNum = 1, playerMarker = 'O')
    val player2 = Player(playerNum = 2, playerMarker = 'X')
    var currPlayer = player1

    while (true) {
        val moveArray = TicTacToe.getMoveFromUser(currPlayer.getPlayerName())

        if (TicTacToe.isValidMove(moveArray[0], moveArray[1], currPlayer)) {
            TicTacToe.play(moveArray[0], moveArray[1], currPlayer.getPlayerMark())
            //currPlayer = if (currPlayer == player1) player2 else player1

            if (TicTacToe.numMoves > 4) {
                if (TicTacToe.checkWin() != " ") {
                    println("$TicTacToe.checkWin()")
                    if (TicTacToe.checkWin() != "It's a draw!")
                        currPlayer.increaseScore()
                    player1.printPlayerScore()
                    player2.printPlayerScore()
                    println("Do you want to play again? Y for yes, N for no: ")
                    val answer = readln()
                    if (answer.uppercase() == "Y") {
                        TicTacToe.gameboard = GameBoard(3, 3)
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
