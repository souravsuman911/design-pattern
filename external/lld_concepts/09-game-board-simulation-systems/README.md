# Game and Board Simulation Systems

## Problem Shape
Players take turns, make moves, rules validate moves, and winner or draw is detected.
Examples: snake and ladder, chess, tic-tac-toe, bowling, card game.

## Core Model
- **Game**: Controls lifecycle and turn progression.
- **Board**: Holds cells, coordinates, pieces, or positions.
- **Player**: Performs actions on each turn.
- **Move**: Represents one user or system action.
- **Rule/Validator**: Checks move legality and terminal conditions.
- **WinningStrategy**: Detects winner, rank, or draw.

## Recommended LLD Sections Per Problem
- Scope and constraints.
- Functional and non-functional requirements.
- Core entities and their responsibilities.
- Status model if the game has lifecycle states.
- Main game loop or turn flow.
- In-memory object schema.
- Production database schema for persisted game sessions.
- Extensibility notes for bots, replay, or multiplayer.

## Typical Schema Design
### In-Memory Schema
```text
Game
+ Board
+ Player
+ Move / Turn
+ Rule / WinningStrategy
+ Optional: GameStatus, MatchHistory, BotStrategy
```

### Production Persistence Schema
- `game_session`: One row per match with board metadata and lifecycle state.
- `game_player`: Players participating in a session and their turn order.
- `move_history` or `turn_history`: Ordered sequence of moves/dice rolls.
- `board_config`: Optional reusable board definition for games like snake and ladder or chess variants.
- `result_summary`: Optional denormalized winner, score, or ranking table.

## Deep Concepts With Compact Examples
- **Turn Management**: Alternate players or apply extra turn.
- **Move Validation**: Chess knight must move in L-shape.
- **Rule Extensibility**: Add custom snake or ladder rules.
- **Board Modeling**: Grid for chess, linear track for snake and ladder.
- **Move History**: Replay, undo, analytics, or dispute resolution.
- **Bot Strategy**: Computer chooses move using strategy.

## Design Options
- **Simple Loop**: Good for small offline games.
- **Rule Engine**: Good for many interacting rules.
- **Command Moves**: Good for undo and replay.
- **Strategy Bots**: Good for AI and difficulty levels.
- **State Pattern**: Good for game lifecycle and pause or resume flows.

## Interview Questions: Short Answers
- **Validate moves?** Use move validator or rule objects.
- **Multiple players?** Use turn manager queue or ordered list.
- **Detect winner?** Run winning strategy after each move.
- **Add rules?** Add new rule implementations or pluggable strategies.
- **Replay game?** Store ordered move history.
- **Persist matches?** Save session, players, and move history separately.

## Implementation Checklist
- Define `Game`, `Board`, and `Player` first.
- Model `Move` or `Turn` as a domain object.
- Add turn manager.
- Add validators.
- Add winning strategy.
- Store game status and history.
- Map in-memory entities to a production schema in the README.
