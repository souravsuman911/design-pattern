# Snake And Ladder LLD

Implementation: `SnakeAndLadder.java`

```text
+------------------------+
| SnakeAndLadderGame     |
+------------------------+
| Board board            |
| Rule rule              |
| Dice dice              |
| Queue<Player> players  |
| int winningPlace       |
+------------------------+
| startGame()            |
+------------------------+

+------------------------+
| Board                  |
+------------------------+
| int size               |
| Map<Integer,Integer>   |
| jumps                  |
+------------------------+
| getSize()              |
| getJumps()             |
| printJumps()           |
| validateBoard()        |
| validateJumps()        |
+------------------------+

+------------------------+
| Player                 |
+------------------------+
| String name            |
| int position           |
+------------------------+
| getName()              |
| getPosition()          |
| setPosition()          |
+------------------------+

+------------------------+
| Dice                   |
+------------------------+
| int faceValue          |
+------------------------+
| roll()                 |
| getFaceValue()         |
+------------------------+

+------------------------+
| Rule                   |
+------------------------+
| hasWon()               |
+------------------------+
```