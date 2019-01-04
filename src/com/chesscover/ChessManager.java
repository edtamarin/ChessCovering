/*
    Controller class
    Manages board analysis and logging
    Egor Tamarin, 2018
 */
package com.chesscover;

import java.util.ArrayList;
import java.util.Random;

public class ChessManager {

    private ChessBoard _chessBoard;
    private int _numQueens;
    private int _numBishops;
    private int solutionMode;
    private ArrayList<ChessBoard> listOfPossibleSolutions = new ArrayList<>();
    private Random _numberGenerator = new Random();
    ChessBoard boardToPrint;

    // constructor
    public ChessManager(int numOfQueens, int numOfBishops, int boardRows, int boardCols) {
        _numQueens = numOfQueens;
        _numBishops = numOfBishops;
        _chessBoard = new ChessBoard(boardRows, boardCols);
        solutionMode = 1;
        if (numOfBishops == -1) { // set solution mode for finding minimum number of bishops
            solutionMode = 0;
            _numBishops++; // set to zero (initial arg is -1)
        }
    }

    // find solutions and display results
    public int findSolutions() {
        try{
        if (solutionMode == 1) { // if number of Qs and Bs given run the analyzing algorithm once
            analyzeBoard(0, 0, 0);
        } else {
            // if we need to find the minimum number of bishops, run the base algorithm for an increasing number of
            // bishops until some solutions are found
            while (listOfPossibleSolutions.size() == 0) {
                analyzeBoard(0, 0, 0);
                if (listOfPossibleSolutions.size() == 0) {
                    printLogMessage("Iteration complete. No solutions for " + _numBishops + " bishops found.");
                }
                _numBishops++;
            }
        }
        }catch (SolutionFoundException e){
            printLogMessage("A solution for " + _numBishops + " bishops found.");
        }
        System.out.println("---------------");
        // filter the results
        ArrayList<ChessBoard> finalArray = new ArrayList<>();
        if (solutionMode == 0) {
            // for automatic mode, the filtering is done in the analysis stage, not necessary here
            finalArray = listOfPossibleSolutions;
        } else {
            // if number of Qs and Bs provided, there might be solutions with a different number of Bs/Qs in the list
            // discard those
            for (ChessBoard board : listOfPossibleSolutions) {
                if ((board.getNumOfBishops() == _numBishops) && (board.getNumOfQueens() == _numQueens)){
                    finalArray.add(board);
                }
            }
        }
        // notify user if there are no solutions for a picked configuration
        if (finalArray.size() == 0) {
            System.out.println("No valid solution found!");
            System.out.println("Change chesspiece data and try again.");
            return -1;
        }
        // pick a board from the solution set at random to display it
        int boardIndex = _numberGenerator.nextInt(finalArray.size());
        boardToPrint = finalArray.get(boardIndex);
        System.out.println("Solution found!");
        System.out.println();
        // tell the user how many other valid solutions exist
        if (this.solutionMode == 1) { // if manual search, print how many possibilities found
            System.out.println(finalArray.size() - 1 + " other solutions exist.");
        }
        boardToPrint.printBoard();
        // count queens and bishops
        int queensOnBoard = 0;
        int bishopsOnBoard = 0;
        for (ChessPiece piece : boardToPrint.boardToListOfPieces()) {
            if (piece.getPieceType().equals("Q")) {
                queensOnBoard++;
            } else if (piece.getPieceType().equals("B")) {
                bishopsOnBoard++;
            }
        }
        System.out.println(queensOnBoard + " queen(s) and " + bishopsOnBoard + " bishop(s) placed.");
        return bishopsOnBoard;
    }

    /*
     Main working method. The idea is that any cell can contain a Q, a B or be empty. This forms a tree of
     possible solutions. We recursively iterate over the tree until we cover the entire board or run out of pieces.
    */
    private void analyzeBoard(int iteration, int usedQueens, int usedBishops) throws SolutionFoundException{
        // if we have been at every cell or used up all pieces analyze the solution
        if ((iteration == this._chessBoard.getNumCols() * this._chessBoard.getNumRows()) ||
                ((usedQueens == this._numQueens) && (usedBishops == this._numBishops))) {
            ChessBoard possibleSolution = new ChessBoard(this._chessBoard);  // copy the board
            possibleSolution.renderBoard(); // render it
            if ((possibleSolution.isFilled())) { // if solution is valid add it to the list
                listOfPossibleSolutions.add(possibleSolution);
                if (this.solutionMode == 0){
                    throw new SolutionFoundException();
                }
            }
            return;
        }
        // get coordinate of a cell to test, division gives row, modulus gives column.
        int rowToTest = iteration / this._chessBoard.getNumCols();
        int colToTest = iteration % this._chessBoard.getNumCols();
        // place a queen if we didn't run out of them yet
        if (usedQueens < _numQueens) {
            this._chessBoard.setCellType("Q", rowToTest, colToTest);
            analyzeBoard(iteration + 1, usedQueens + 1, usedBishops);
        }
        // place a bishop if we haven't run out of them yet
        if (usedBishops < _numBishops) {
            this._chessBoard.setCellType("B", rowToTest, colToTest);
            analyzeBoard(iteration + 1, usedQueens, usedBishops + 1);
        }
        // place nothing, cell is empty
        this._chessBoard.setCellType("*", rowToTest, colToTest);
        analyzeBoard(iteration + 1, usedQueens, usedBishops);
        return;
    }

    // print an intermediate message in a log format
    public static void printLogMessage(String message){
        System.out.print("[LOG] [T:" + (System.nanoTime()-Main.startTime)/1000000 + " ms] ");
        System.out.println(message);
    }
}

class SolutionFoundException extends Exception{

}