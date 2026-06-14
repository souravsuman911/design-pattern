# Tic Tac Toe LLD

Implementation: `TicTacToeClient.java`

## Class Diagram

```text
+----------------+
| TicTacToeGame  |
+----------------+
| Board board    |
| Player current |
| Player p1      |
| Player p2      |
+----------------+
| start()        |
| makeMove()     |
| switchTurn()   |
+----------------+

+----------------+
| Board          |
+----------------+
| char[][] grid  |
+----------------+
| placeMove()    |
| isValidMove()  |
| printBoard()   |
+----------------+

+----------------+
| Player         |
+----------------+
| String name    |
| char symbol    |
+----------------+

+----------------+
| Rule           |
+----------------+
| checkWinner()  |
| isDraw()       |
+----------------+
```
