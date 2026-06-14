package internal.designPattern.external.lldConcepts.chess;

import java.util.Scanner;

public class ChessGame {

    enum Color {
        WHITE, BLACK
    }

    static class Player {
        private final String name;
        private final Color color;

        public Player(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public Color getColor() {
            return color;
        }
    }

    static class Move {
        int fromRow;
        int fromCol;
        int toRow;
        int toCol;

        public Move(int fromRow, int fromCol,
                    int toRow, int toCol) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
        }
    }

    static abstract class Piece {

        protected Color color;

        public Piece(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public abstract boolean canMove(Board board, Move move);

        public abstract char getSymbol();
    }

    static class Rook extends Piece {

        public Rook(Color color) {
            super(color);
        }

        @Override
        public boolean canMove(Board board, Move move) {
            // Hanthi -  it can move either in same row or same column
            return move.fromRow == move.toRow || move.fromCol == move.toCol;
        }

        @Override
        public char getSymbol() {
            return color == Color.WHITE ? 'R' : 'r';
        }
    }

    static class Bishop extends Piece {

        public Bishop(Color color) {
            super(color);
        }

        @Override
        public boolean canMove(Board board, Move move) {
            // Unt - it can move diagonally - difference between rows should be equal to difference between cols
            return Math.abs(move.toRow - move.fromRow) == Math.abs(move.toCol - move.fromCol);
        }

        @Override
        public char getSymbol() {
            return color == Color.WHITE ? 'B' : 'b';
        }
    }

    static class Knight extends Piece {

        public Knight(Color color) {
            super(color);
        }

        @Override
        public boolean canMove(Board board, Move move) {
            //ghoda - calculate rowDiff and colDiff
            int rowDiff = Math.abs(move.toRow - move.fromRow);
            int colDiff = Math.abs(move.toCol - move.fromCol);

            return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
        }

        @Override
        public char getSymbol() {
            return color == Color.WHITE ? 'N' : 'n';
        }
    }

    static class Queen extends Piece {

        public Queen(Color color) {
            super(color);
        }

        @Override
        public boolean canMove(Board board, Move move) {

            // have combination move of rook and bishop
            boolean rookMove = move.fromRow == move.toRow || move.fromCol == move.toCol;
            boolean bishopMove = Math.abs(move.toRow - move.fromRow) == Math.abs(move.toCol - move.fromCol);

            return rookMove || bishopMove;
        }

        @Override
        public char getSymbol() {
            return color == Color.WHITE ? 'Q' : 'q';
        }
    }

    static class King extends Piece {

        public King(Color color) {
            super(color);
        }

        @Override
        public boolean canMove(Board board, Move move) {

            int rowDiff = Math.abs(move.toRow - move.fromRow);
            int colDiff = Math.abs(move.toCol - move.fromCol);

            return rowDiff <= 1 && colDiff <= 1;
        }

        @Override
        public char getSymbol() {
            return color == Color.WHITE ? 'K' : 'k';
        }
    }

    static class Pawn extends Piece {

        public Pawn(Color color) {
            super(color);
        }

        @Override
        public boolean canMove(Board board, Move move) {

            int direction = color == Color.WHITE ? -1 : 1;

            int rowDiff = move.toRow - move.fromRow;
            int colDiff = Math.abs(move.toCol - move.fromCol);

            Piece destination = board.getPiece(move.toRow, move.toCol);

            // Forward move
            if (colDiff == 0 && destination == null) {
                return rowDiff == direction;
            }

            // Capture
            if (colDiff == 1 && rowDiff == direction && destination != null && destination.getColor() != color) {
                return true;
            }

            return false;
        }

        @Override
        public char getSymbol() {
            return color == Color.WHITE ? 'P' : 'p';
        }
    }

    static class Board {

        private final Piece[][] grid;

        public Board() {
            grid = new Piece[8][8];
            initialize();
        }

        private void initialize() {

            // Black pieces
            grid[0][0] = new Rook(Color.BLACK);
            grid[0][1] = new Knight(Color.BLACK);
            grid[0][2] = new Bishop(Color.BLACK);
            grid[0][3] = new Queen(Color.BLACK);
            grid[0][4] = new King(Color.BLACK);
            grid[0][5] = new Bishop(Color.BLACK);
            grid[0][6] = new Knight(Color.BLACK);
            grid[0][7] = new Rook(Color.BLACK);

            for (int c = 0; c < 8; c++) {
                grid[1][c] = new Pawn(Color.BLACK);
            }

            // White pieces
            grid[7][0] = new Rook(Color.WHITE);
            grid[7][1] = new Knight(Color.WHITE);
            grid[7][2] = new Bishop(Color.WHITE);
            grid[7][3] = new Queen(Color.WHITE);
            grid[7][4] = new King(Color.WHITE);
            grid[7][5] = new Bishop(Color.WHITE);
            grid[7][6] = new Knight(Color.WHITE);
            grid[7][7] = new Rook(Color.WHITE);

            for (int c = 0; c < 8; c++) {
                grid[6][c] = new Pawn(Color.WHITE);
            }
        }

        public Piece getPiece(int row, int col) {
            return grid[row][col];
        }

        public void movePiece(Move move) {

            Piece piece = grid[move.fromRow][move.fromCol];
            grid[move.toRow][move.toCol] = piece;
            grid[move.fromRow][move.fromCol] = null;
        }

        public void printBoard() {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (grid[r][c] == null) {
                        System.out.print(". ");
                    } else {
                        System.out.print(grid[r][c].getSymbol() + " ");
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    static class Rule {

        public boolean isValidMove(Board board, Player player, Move move) {
            // validate in-board condition
            if (move.fromRow < 0 || move.fromRow >= 8 || move.fromCol < 0 || move.fromCol >= 8
                    || move.toRow < 0 || move.toRow >= 8 || move.toCol < 0 || move.toCol >= 8) {
                return false;
            }

            Piece piece = board.getPiece(move.fromRow, move.fromCol);

            if (piece == null) {
                return false;
            }

            if (piece.getColor() != player.getColor()) {
                return false;
            }

            Piece destination = board.getPiece(move.toRow, move.toCol);

            if (destination != null && destination.getColor() == player.getColor()) {
                return false;
            }

            return piece.canMove(board, move);
        }
    }

    static class Game {

        private final Board board;
        private final Rule rule;
        private final Player white;
        private final Player black;
        private Player currentPlayer;

        public Game() {
            board = new Board();
            rule = new Rule();
            white = new Player("White", Color.WHITE);
            black = new Player("Black", Color.BLACK);
            currentPlayer = white;
        }

        private void switchTurn() {
            currentPlayer = currentPlayer == white ? black : white;
        }

        public void startGame() {

            Scanner sc = new Scanner(System.in);
            while (true) {
                board.printBoard();
                System.out.println(currentPlayer.getName() + " Turn");

                System.out.println("Enter: fromRow fromCol toRow toCol");

                int fr = sc.nextInt();
                int fc = sc.nextInt();
                int tr = sc.nextInt();
                int tc = sc.nextInt();

                Move move = new Move(fr, fc, tr, tc);

                if (!rule.isValidMove(board, currentPlayer, move)) {
                    System.out.println("Invalid Move");
                    continue;
                }

                board.movePiece(move);

                switchTurn();
            }
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.startGame();
    }
}