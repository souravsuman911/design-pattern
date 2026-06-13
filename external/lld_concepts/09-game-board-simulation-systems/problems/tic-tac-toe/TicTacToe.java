package internal.designPattern.external.lldConcepts.tic_tac_toe;

import java.util.Arrays;
import java.util.Scanner;

public class TicTacToe {
    class Player {
        private final String name;
        private final char symbol;

        public Player(String name, char symbol){
            this.name = name;
            this.symbol = symbol;
        }

        public String getName(){
            return name;
        }

        public char getSymbol(){
            return symbol;
        }
    }

    class Board {
        private final char[][] grid;

        public Board(){
            grid = new char[3][3];

            for(int r = 0; r < 3; r ++){
                Arrays.fill(grid[r], '-');
            }
        }

        public char[][] getGrid(){
            return grid;
        }

        public boolean validateBoard(int row, int col){
            if(row >= 0 && row <= 3 && col >= 0 && col <= 3 && grid[row][col] == '-'){
                return true;
            }

            return false;
        }

        public boolean placeMove(int row, int col, char symbol){

            if(validateBoard(row, col)){
                grid[row][col] = symbol;
                printBoard();
                return true;
            }

            return false;
        }

        public void printBoard(){
            for(int r = 0; r < 3; r ++){
                for(int c = 0; c < 3; c ++){
                    System.out.print(grid[r][c] + " ");
                }
                System.out.println();
            }
        }
    }

    class Rule {
        public boolean isWinner(Board board, Player player){
            char[][] grid = board.getGrid();
            char symbol = player.getSymbol();

            // Rows
            for(int r = 0; r < 3; r ++){
                boolean win = true;

                for(int c = 0; c < 3; c ++){
                    if(grid[r][c] != symbol){
                        win = false;
                    }
                }

                if(win){
                    return true;
                }
            }

            // Cols
            for(int c = 0; c < 3; c ++){
                boolean win = true;

                for(int r = 0; r < 3; r ++){
                    if(grid[r][c] != symbol){
                        win = false;
                    }
                }

                if(win){
                    return true;
                }
            }

            // left diagonal
            boolean win = true;

            for(int i = 0; i < 3; i ++){
                if(grid[i][i] != symbol) {
                    win = false;
                }
            }

            if(win){
                return true;
            }

            // right diagonal
            win = true;

            for (int i = 0; i < 3; i++) {
                if (grid[i][2 - i] != symbol) {
                    win = false;
                    break;
                }
            }

            if (win) {
                return true;
            }

            return false;
        }

        public boolean isDraw(Board board){
            char[][] grid = board.getGrid();

            for(int r = 0; r  < 3; r ++){
                for(int c = 0; c < 3; c ++){
                    if(grid[r][c] == '-'){
                        return false;
                    }
                }
            }

            return true;
        }
    }

    class Game {
        private final Board board;
        private final Rule rule;
        private final Player p1;
        private final Player p2;
        private Player currPlayer;

        public Game(Player p1, Player p2) {
            this.board = new Board();
            this.rule = new Rule();
            this.p1 = p1;
            this.p2 = p2;
            this.currPlayer = p1;
        }

        public void startGame(){
            Scanner sc = new Scanner(System.in);
            System.out.println("Game Started" + '\n');
            board.printBoard();

            while(true){
                System.out.println(currPlayer.getName() + " turn, Symbol : " + currPlayer.getSymbol());
                System.out.println("Enter row and col : ");
                int row = sc.nextInt();
                int col = sc.nextInt();

                if(!board.placeMove(row, col, currPlayer.getSymbol())){
                    System.out.println("Invalid Move");
                    continue;
                }

                if(rule.isWinner(board, currPlayer)){
                    System.out.println("Congratulations, " + currPlayer.getName() + " is Winner !!");
                    break;
                }

                if(rule.isDraw(board)){
                    System.out.println("Game Draw");
                    break;
                }

                switchTurn();
            }
        }

        public void switchTurn() {
            currPlayer = currPlayer == p1 ? p2 : p1;
        }
    }

    public void start(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter player 1 name");
        String p1Name = sc.nextLine();

        System.out.println("Enter player 2 name");
        String p2Name = sc.nextLine();

        Game game = new Game(new Player(p1Name, 'X'), new Player(p2Name, 'O'));
        game.startGame();
    }

    public static void main(String[] args) {
        TicTacToe ticTacToe= new TicTacToe();
        ticTacToe.start();
    }

}
