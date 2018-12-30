# Chess Covering problem
## Problem definition
A chess board with M rows and N columns is given. Only Queens and Bishops are used in this game. It is requested to find a minimum
number of these pieces and place them on a given board in such a way that all the free squares on the board are attacked by at least
one piece.
## Functionality
The user provides the following as the inputs:
  * Number of rows and columns
  * Number of Queens
  * Number of Bishops
    * If set to a non-negative value, the program will try to find all soulutions for a given amount of pieces.
    * If set to -1, the program will find the minimum number of Bishops needed to cover the board.
## Output
The program will output:
  * A valid board configuration, if found
  * The number of pieces placed
  * Time to solve
  * **Optional:** if the program is finding the minimum number of Bishops, logs will be displayed to notify user of the progress.
