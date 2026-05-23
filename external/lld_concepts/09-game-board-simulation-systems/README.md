# Game and Board Simulation Systems

## Problem Shape
Players take turns, make moves, rules validate moves, and winner/draw is detected.
Examples: snake and ladder, chess, tic-tac-toe, bowling, card game.

## Core Model
- **Game**: Controls lifecycle.
- **Board**: Holds cells/pieces/positions.
- **Player**: Takes actions.
- **Move**: Player action.
- **Rule/Validator**: Checks legality.
- **WinningStrategy**: Detects result.

## Deep Concepts With Compact Examples
- **Turn Management**: Alternate players or apply extra turn.
- **Move Validation**: Chess knight must move in L-shape.
- **Rule Extensibility**: Add custom snake/ladder rules.
- **Board Modeling**: Grid for chess, list for snake-ladder.
- **Move History**: Replay or undo moves.
- **Bot Strategy**: Computer chooses move using strategy.

## Design Options
- **Simple Loop**: Good for small games.
- **Rule Engine**: Good for many rules.
- **Command Moves**: Good for undo/replay.
- **Strategy Bots**: Good for AI/difficulty levels.
- **State Pattern**: Good for game lifecycle.

## Interview Questions: Short Answers
- **Validate moves?** Use move validator/rules.
- **Multiple players?** Use turn manager queue.
- **Detect winner?** Use winning strategy after each move.
- **Add rules?** Add new rule implementation.
- **Replay game?** Store ordered move history.

## Implementation Checklist
- Define `Game`, `Board`, `Player`.
- Model `Move` as object.
- Add turn manager.
- Add validators.
- Add winning strategy.
- Store game status/history.
