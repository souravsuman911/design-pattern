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

## Category
Game and Board Simulation Systems

## Scope
Interview-ready low-level design for **Snake and Ladder**. The current code models turn rotation, dice rolls, board jumps, and winner sequencing in a compact way.

## Functional Requirements
- Create a board with configurable size.
- Support multiple players in round-robin order.
- Roll a dice and move the active player.
- Apply snakes and ladders when a player lands on a jump start.
- Detect winners and assign finishing order.

## Non-Functional Requirements
- **Extensibility**: Jump rules and dice behavior can be extracted later.
- **Readability**: The implementation should remain interview-friendly.
- **Consistency**: Position updates happen through the game loop.
- **Scalability**: In production, game sessions and turn history should be persisted externally.

## Main Flow
```text
Initialize board and players
 -> validate configured jumps
 -> dequeue active player
 -> roll dice
 -> compute next position
 -> apply jump if present
 -> update player position if inside board
 -> mark winner or enqueue player again
 -> continue until ranking is complete
```

## Schema Design
### In-Memory Object Model
- `Game`: orchestrates turns, queue rotation, and winner announcements.
- `Board`: stores `size` and `jumps`.
- `Player`: stores `name` and current `position`.
- `Dice`: generates the next face value.
- `Rule`: checks whether a player reached the last cell.

### Production Database Mapping
- `game_session`
  - `game_id` PK
  - `board_size`
  - `status`
  - `created_at`
- `game_player`
  - `game_id` FK
  - `player_id` PK
  - `player_name`
  - `current_position`
  - `finish_rank`
- `board_jump`
  - `board_jump_id` PK
  - `game_id` FK
  - `start_cell`
  - `end_cell`
  - `jump_type`
- `turn_history`
  - `turn_id` PK
  - `game_id` FK
  - `turn_number`
  - `player_id` FK
  - `dice_value`
  - `start_position`
  - `end_position`

## Design Notes
- The current code uses `Map<Integer, Integer>` for jumps; a dedicated `Jump` entity can be introduced later if needed.
- A queue is a natural fit for cyclic player turns.
- Persistence would typically be added at session creation, after each turn, and when ranking is finalized.
