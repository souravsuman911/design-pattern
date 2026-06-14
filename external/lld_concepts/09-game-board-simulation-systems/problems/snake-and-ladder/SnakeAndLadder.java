package internal.designPattern.external.lldConcepts.snake_and_ladder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class SnakeAndLadder {
    public class Player {
        private final String name;
        private int position;

        public Player(String name) {
            this.name = name;
            this.position = 0;
        }

        public String getName(){
            return name;
        }

        public int getPosition(){
            return position;
        }

        public void setPosition(int position){
            this.position = position;
        }
    }

    public class Board {
        private final int size;
        private final Map<Integer, Integer> jumps;

        Board(int size, Map<Integer, Integer> jumps) {
            this.size = size;
            this.jumps = jumps;
        }

        public int getSize(){
            return size;
        }

        public Map<Integer, Integer> getJumps(){
            return jumps;
        }

        public void printJumps(){
            for(Map.Entry<Integer, Integer> entry : jumps.entrySet()){
                System.out.print(entry.getKey() + " : " + entry.getValue() + " | ");
            }
            System.out.println();
        }

        public boolean validateBoard(int position){
            return position >= 0 && position <= size;
        }

        public boolean validateJumps(){
            for(int key : jumps.keySet()){
                if(key <= 0 || key > size){
                    return false;
                }
            }

            for(int value : jumps.values()){
                if(value <= 0 || value > size){
                    return false;
                }
            }

            return true;
        }
    }

    public class Dice {
        private int faceValue;

        public int getFaceValue(){
            if(faceValue <= 0 || faceValue > 6){
                System.out.println("Roll dice first");
                return -1;
            }
            return faceValue;
        }

        public int roll(){
            faceValue = (int)(Math.random() * 10) % 6 + 1;
            System.out.println("Dice rolled, value : " + faceValue);
            return faceValue;
        }
    }

    class Rule {
        public boolean hasWon(Board board, Player player) {
            return player.getPosition() == board.getSize();
        }
    }

    class Game {
        private final Board board;
        private final Rule rule;
        private final Dice dice;
        private final Queue<Player> pq;
        private int winningPlace;

        public Game(Board board, Queue<Player> pq) {
            this.board = board;
            this.rule = new Rule();
            this.dice = new Dice();
            this.pq = pq;
            this.winningPlace = 1;
        }

        public void startGame(){
            System.out.println("Game Started");

            if(!board.validateJumps()){
                System.out.println("Invalid jumps added");
                return;
            }

            board.printJumps();

            while(pq.size() > 1){
                System.out.println("Current Player : " + pq.peek().getName() + ", at position : " + pq.peek().getPosition());

                Player currPlayer = pq.poll();
                int currFaceValue = dice.roll();
                int newPosition = currPlayer.getPosition() + currFaceValue;
                // checks for jumps if available
                newPosition = board.getJumps().getOrDefault(newPosition, newPosition);

                if(board.validateBoard(newPosition)){
                    currPlayer.setPosition(newPosition);
                }

                // if any player has won, announce winner, do not add in the queue again
                if(rule.hasWon(board, currPlayer)){
                    System.out.println(currPlayer.getName() + " has finished with winning place : " + winningPlace ++);
                }
                else{
                    pq.offer(currPlayer);
                }
            }

            if(!pq.isEmpty()){
                System.out.println(pq.poll().getName() + " remains in the last :(");
            }
            System.out.println("Game Finished !!");
        }
    }

    public void start(){
        Queue<Player> pq = new LinkedList<>();
        pq.offer(new Player("A"));
        pq.offer(new Player("B"));
        pq.offer(new Player("C"));
        pq.offer(new Player("D"));

        Map<Integer, Integer> jumps = new HashMap<>();
        // ladders
        jumps.put(3, 12);
        jumps.put(14, 37);
        jumps.put(43, 91);
        jumps.put(67, 99);
        // snakes
        jumps.put(13, 5);
        jumps.put(54, 38);
        jumps.put(87, 34);
        jumps.put(98, 2);
        Board board = new Board(100, jumps);
        Game game = new Game(board, pq);
        game.startGame();

    }

    public static void main(String[] args) {
        SnakeAndLadder snakeAndLadder = new SnakeAndLadder();
        snakeAndLadder.start();
    }

//    public interface Jump {
//        int getStart();
//        int getEnd();
//    }
//
//    public class Ladder implements Jump{
//
//        private int start;
//        private int end;
//
//        public Ladder(int start, int end){
//            if(start >= end){
//                throw new IllegalArgumentException("Ladder must go up");
//            }
//
//            this.start = start;
//            this.end = end;
//        }
//
//        @Override
//        public int getStart() {
//            return start;
//        }
//
//        @Override
//        public int getEnd() {
//            return end;
//        }
//    }
//
//    public class Snake implements Jump{
//        private int start;
//        private int end;
//
//        public Snake(int start, int end){
//            if(start >= end){
//                throw new IllegalArgumentException("Snake must go down");
//            }
//
//            this.start = start;
//            this.end = end;
//        }
//
//        @Override
//        public int getStart() {
//            return start;
//        }
//
//        @Override
//        public int getEnd() {
//            return end;
//        }
//    }

}
