# Tic Tac Toe LLD

Implementation: `TicTacToe.java`

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

## Category
Game and Board Simulation Systems

## Scope
Interview-ready low-level design for **Tic Tac Toe**. The current implementation focuses on board validation, alternating turns, winner detection, and draw handling.

## Functional Requirements
- Create a fixed 3x3 board.
- Accept two players with distinct symbols.
- Validate each move before placing it.
- Detect row, column, and diagonal wins.
- Detect draw when all cells are occupied.

## Non-Functional Requirements
- **Correctness**: Out-of-bounds and duplicate-cell moves must be rejected.
- **Readability**: Win rules should stay explicit and interview-friendly.
- **Extensibility**: AI player, replay, or board generalization can be added later.
- **Observability**: In production, move history should be persisted separately.

## Main Flow
```text
Create board and players
 -> print empty board
 -> read active player move
 -> validate row/col and occupancy
 -> place move
 -> evaluate winner
 -> evaluate draw
 -> switch turn if game is still active
```

## Schema Design
### In-Memory Object Model
- `Game`: controls lifecycle and current turn.
- `Board`: owns the `3 x 3` grid and validates placements.
- `Player`: stores player name and symbol.
- `Rule`: checks winner and draw conditions.

### Production Database Mapping
- `game_session`
  - `game_id` PK
  - `board_size`
  - `status`
  - `winner_player_id` nullable
  - `created_at`
  - `ended_at`
- `game_player`
  - `game_id` FK
  - `player_id` PK
  - `player_name`
  - `symbol`
  - `turn_order`
- `move_history`
  - `move_id` PK
  - `game_id` FK
  - `player_id` FK
  - `move_number`
  - `row_index`
  - `col_index`
  - `symbol`

## Design Notes
- The current code does not persist move history, but production multiplayer or replay support would require it.
- Move validation and winner detection are kept inside board/rule classes for simplicity.
- A service layer can be introduced later if the game needs API or online play support.
