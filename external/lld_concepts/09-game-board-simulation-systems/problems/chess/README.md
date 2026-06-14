# Chess LLD

Implementation: `ChessGame.java`

```text
+----------------------+
| ChessGame            |
+----------------------+
| Board board          |
| Rule rule            |
| Player white         |
| Player black         |
| Player current       |
+----------------------+
| startGame()          |
| switchTurn()         |
+----------------------+

+----------------------+
| Board                |
+----------------------+
| Piece[][] grid       |
+----------------------+
| initializeBoard()    |
| getPiece()           |
| movePiece()          |
| printBoard()         |
+----------------------+

+----------------------+
| Rule                 |
+----------------------+
| isValidMove()        |
+----------------------+

+----------------------+
| Player               |
+----------------------+
| String name          |
| Color color          |
+----------------------+

+----------------------+
| Move                 |
+----------------------+
| fromRow              |
| fromCol              |
| toRow                |
| toCol                |
+----------------------+

+----------------------+
| Piece (abstract)     |
+----------------------+
| Color color          |
+----------------------+
| canMove()            |
+----------------------+

        ▲
        |
----------------------------------
|     |      |      |      |      |
v     v      v      v      v      v

King Queen Rook Bishop Knight Pawn
```

## Category
Game and Board Simulation Systems

## Scope
Interview-ready low-level design for **Chess**. The current implementation models an `8 x 8` board, two players, piece-specific movement rules, turn switching, and basic move validation.

## Functional Requirements
- Initialize a standard chess board with all major pieces.
- Support two players with white and black colors.
- Accept a move as source and destination coordinates.
- Validate that the move is inside the board and belongs to the active player.
- Reject moves that land on a same-color piece.
- Apply piece-specific movement logic for king, queen, rook, bishop, knight, and pawn.
- Alternate turns after each valid move.

## Non-Functional Requirements
- **Readability**: Piece rules stay simple and interview-friendly.
- **Extensibility**: New rules like castling, check, and checkmate should be addable without rewriting the board model.
- **Separation of concerns**: Board state, move validation, and piece movement stay separated.
- **Scalability**: For online or persistent play, moves and sessions should be stored outside process memory.

## Main Flow
```text
Create board and players
 -> print board
 -> read move coordinates
 -> validate board bounds and ownership
 -> validate destination occupancy
 -> delegate piece-specific movement check
 -> move piece on board
 -> switch turn
 -> repeat
```

## Schema Design
### In-Memory Object Model
- `Game`: orchestrates board lifecycle, turn order, and input loop.
- `Board`: stores `Piece[][] grid` and handles piece relocation.
- `Player`: stores `name` and `color`.
- `Move`: captures one move request with source and destination coordinates.
- `Rule`: validates board bounds, ownership, and destination rules.
- `Piece`: abstract base class for piece-specific movement behavior.
- Concrete pieces: `King`, `Queen`, `Rook`, `Bishop`, `Knight`, and `Pawn`.

### Production Database Mapping
- `game_session`
  - `game_id` PK
  - `status`
  - `current_turn_color`
  - `created_at`
  - `ended_at`
- `game_player`
  - `game_id` FK
  - `player_id` PK
  - `player_name`
  - `color`
  - `is_active`
- `board_state`
  - `board_state_id` PK
  - `game_id` FK
  - `row_index`
  - `col_index`
  - `piece_type`
  - `piece_color`
- `move_history`
  - `move_id` PK
  - `game_id` FK
  - `player_id` FK
  - `move_number`
  - `from_row`
  - `from_col`
  - `to_row`
  - `to_col`
  - `captured_piece_type` nullable
- `game_result`
  - `game_id` PK/FK
  - `winner_player_id` nullable
  - `result_type`

## Design Notes
- The current implementation validates piece movement shape, but it does not yet check path blocking for rook, bishop, or queen.
- Check, checkmate, stalemate, castling, en passant, and pawn promotion are not modeled yet.
- `Piece` is the main extensibility point; advanced rules can be layered into `Rule` or specialized validators later.
- Persisted `move_history` is the key building block for replay, undo, online sync, and audit.

## Interview Discussion Points
- Use polymorphism in `Piece.canMove()` to avoid large switch statements for piece behavior.
- Keep board mutation in one place so rule validation stays separate from state updates.
- Add `GameStatus` and check or checkmate detection before exposing this model as a multiplayer service.
