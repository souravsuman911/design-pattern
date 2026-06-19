# Game and Board Simulation Systems

## Problem Shape
These problems model a bounded game world where players or the system take turns, apply rules, mutate board or session state, and eventually reach a terminal outcome such as win, loss, draw, ranking, or game over.

Typical examples in this category:
- Tic Tac Toe
- Snake and Ladder
- Chess
- Ludo
- Minesweeper
- Bowling Game
- Card Game

## What Usually Makes These Problems Different
- A **shared world state** exists: board cells, piece positions, deck state, score sheet, or turn queue.
- A **strict rule engine** decides whether a move is legal.
- The system must maintain **deterministic transitions** from one turn to the next.
- Move history often matters for **replay, audit, undo, analytics, or spectator sync**.
- Most interview implementations are **in-memory first**, with persistence added only when multiplayer or long-running sessions are discussed.

## Core Model
- **Game / Match**: Owns lifecycle, active turn, and terminal state.
- **Board / Arena / Table**: Represents the playable surface or shared game state.
- **Player**: Human, bot, or remote participant.
- **Turn / Move / Action**: One state-changing step in the game.
- **Rule / Validator**: Verifies legality and constraints.
- **WinningStrategy / ResultEvaluator**: Detects winner, rank, draw, or completion.
- **GameStatus**: Created, in progress, paused, finished, abandoned.
- **History**: Ordered actions for replay or persistence.

## Reusable Design Patterns
- **State Pattern**: Useful when the game has setup, active play, paused, and completed modes.
- **Strategy Pattern**: Useful for move validation, bot behavior, scoring, or winning rules.
- **Command Pattern**: Useful when moves must support replay, undo, or audit logs.
- **Observer Pattern**: Useful for UI refresh, spectators, or external notifications after state changes.
- **Factory Pattern**: Useful for creating pieces, cards, boards, or rule sets for different variants.

## Common Sub-Shapes Inside This Category
### 1. Grid-Based Competitive Games
Examples: Tic Tac Toe, Chess, Minesweeper

Key concerns:
- Coordinate system
- Cell occupancy
- Piece or mark placement
- Adjacency or movement rules
- Win, loss, or reveal logic

### 2. Linear Progression Board Games
Examples: Snake and Ladder, Ludo

Key concerns:
- Turn queue
- Dice or randomizer
- Position updates
- Jumps, safe zones, or bonus turns
- Finishing order

### 3. Score-Driven Simulations
Examples: Bowling Game

Key concerns:
- Frame or round progression
- Derived score calculation
- Bonus scoring rules
- End-of-game ranking

### 4. Deck or Hand Driven Games
Examples: Card Game

Key concerns:
- Shuffle and deal
- Hidden player state
- Turn actions
- Round resolution
- Winner calculation

## Problem-to-Abstraction Mapping
| Problem | Shared State | Primary Action | Main Rule Concern | Result Shape |
| --- | --- | --- | --- | --- |
| Tic Tac Toe | `3 x 3` grid | Place symbol | Cell validity + line win | Win or draw |
| Chess | `8 x 8` board + pieces | Move piece | Piece movement + capture rules | Win, loss, draw |
| Snake and Ladder | Linear board + jumps | Roll and move | Position transitions | Winner or ranking |
| Ludo | Track + tokens | Roll and move token | Entry, collision, home rules | Winner or ranking |
| Minesweeper | Hidden mine grid | Reveal or flag | Mine detection + flood reveal | Clear board or lose |
| Bowling Game | Frames + rolls | Record roll | Spare and strike scoring | Final score |
| Card Game | Deck + hands + table | Draw, play, discard | Turn legality + card effects | Winner or points |

## Typical In-Memory Object Model
```text
Game
+ GameStatus
+ TurnManager
+ Board / Arena / ScoreSheet
+ Player[]
+ RuleEngine / Validator[]
+ WinningStrategy / ScoreCalculator
+ MoveHistory
+ Optional: BotStrategy, Randomizer, ReplayService
```

## Typical Production Persistence Schema
- `game_session`
  - One row per match or session.
  - Stores `game_type`, `status`, `created_at`, `ended_at`, and current turn metadata.
- `game_player`
  - Stores session participants, team or color assignment, turn order, and final rank.
- `turn_history` or `move_history`
  - Stores ordered actions with timestamps and actor identifiers.
- `board_state` or `session_snapshot`
  - Stores current materialized state when rebuilding from events is too expensive.
- `game_result`
  - Stores winner, draw reason, score summary, or finishing order.
- `game_config`
  - Stores reusable board size, rule variants, dice count, piece set, or custom settings.

## Design Flow For Interview or LLD Discussion
1. Clarify whether the game is single-player, local multiplayer, or online multiplayer.
2. Identify the smallest shared state needed to make one legal move.
3. Model `Move` or `Action` explicitly instead of mutating board state ad hoc.
4. Separate **rule validation** from **state mutation**.
5. Add a result evaluator that runs after each accepted move.
6. Introduce persistence only if replay, recovery, analytics, or online play is required.
7. Add observers or event publishing only if UI sync, spectators, or notifications are needed.

## Notification Guidance
Game-board simulators can have limited notification design because many interview versions are local and synchronous. Still, notification hooks are worth documenting when the game is online, spectated, or asynchronous.

Use notifications as an **optional cross-cutting concern** for:
- turn started
- move accepted
- move rejected
- player joined or left
- game paused or resumed
- winner declared
- spectator board refresh

Keep notification design lightweight in this category:
- Emit domain events such as `MOVE_PLAYED`, `TURN_CHANGED`, or `GAME_FINISHED`.
- Let a separate notification or pub-sub module consume those events.
- Do not overload the core game engine with channel-specific logic such as email, push, or SMS.

If notifications become central to the problem, the design overlaps with [Notification and Pub-Sub Systems](../06-notification-pubsub-systems/README.md). In that case:
- this category should still own the authoritative game state and rule engine
- the notification category should own subscriber management, fanout, retries, dedupe, and delivery channels

## What To Document Per Problem README
- Scope and assumptions
- Functional and non-functional requirements
- Main entities and responsibilities
- Board or state representation
- Turn flow or action flow
- Rule validation strategy
- Winner, scoring, or completion detection
- In-memory schema
- Production database schema
- Optional event or notification hooks if the game is multiplayer or online

## Implementation Checklist
- Define `Game`, `Player`, and the shared state model first.
- Introduce a `Move` or `Action` object.
- Add turn management.
- Centralize rule validation.
- Add winning or scoring strategy.
- Capture ordered history for replay and debugging.
- Keep persistence and notification concerns outside the core rule engine where possible.

## Problem Readmes In This Folder
- [Bowling Game](./problems/bowling-game/README.md)
- [Card Game](./problems/card-game/README.md)
- [Chess](./problems/chess/README.md)
- [Ludo](./problems/ludo/README.md)
- [Minesweeper](./problems/minesweeper/README.md)
- [Snake and Ladder](./problems/snake-and-ladder/README.md)
- [Tic Tac Toe](./problems/tic-tac-toe/README.md)
