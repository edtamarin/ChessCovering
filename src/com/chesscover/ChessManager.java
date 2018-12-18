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

    public ChessManager(int numOfQueens, int numOfBishops, int boardRows, int boardCols) {
        _numQueens = numOfQueens;
        _numBishops = numOfBishops;
        _chessBoard = new ChessBoard(boardRows, boardCols);
        solutionMode = 1;
        if (numOfBishops == -1) {
            solutionMode = 0;
            _numBishops++;
        }
    }

    public void findSolutions() {
        if (solutionMode == 1) {
            analyzeBoard(0, 0, 0);
        } else {
            solutionMode = 1;
            while (listOfPossibleSolutions.size() == 0) {
                analyzeBoard(0, 0, 0);
                printLogMessage("Iteration complete. No solutions for " + _numBishops + " bishops found.");
                _numBishops++;
            }
        }
        solutionMode = 0;
        System.out.println("---------------");
        if (listOfPossibleSolutions.size() == 0) {
            System.out.println("No valid solution found!");
            System.out.println("Change chesspiece data and try again.");
            return;
        }
        ArrayList<ChessBoard> finalArray = new ArrayList<>();
        if (solutionMode == 0) {
            finalArray = listOfPossibleSolutions;
        } else {
            for (ChessBoard board : listOfPossibleSolutions) {
                if (board.getNumOfBishops() == _numBishops) {
                    finalArray.add(board);
                }
            }
        }
        int boardIndex = _numberGenerator.nextInt(finalArray.size());
        boardToPrint = finalArray.get(boardIndex);
        System.out.println("Solution found!");
        System.out.println();
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
    }

    private void analyzeBoard(int iteration, int usedQueens, int usedBishops) {
        if ((iteration == this._chessBoard.getNumCols() * this._chessBoard.getNumRows()) ||
                ((usedQueens == this._numQueens) && (usedBishops == this._numBishops))) {
            ChessBoard possibleSolution = new ChessBoard(this._chessBoard);
            possibleSolution.renderBoard();
            if ((possibleSolution.isFilled())) {
                listOfPossibleSolutions.add(possibleSolution);
            }
            return;
        }
        int rowToTest = iteration / this._chessBoard.getBoard().length;
        int colToTest = iteration % this._chessBoard.getBoard().length;
        if (usedQueens < _numQueens) {
            this._chessBoard.setCellType("Q", rowToTest, colToTest);
            analyzeBoard(iteration + 1, usedQueens + 1, usedBishops);
        }
        if (solutionMode == 1) {
            if (usedBishops < _numBishops) {
                this._chessBoard.setCellType("B", rowToTest, colToTest);
                analyzeBoard(iteration + 1, usedQueens, usedBishops + 1);
            }
        } else if ((usedQueens == this._numQueens) && (solutionMode == 0)) {
            this._chessBoard.setCellType("B", rowToTest, colToTest);
            analyzeBoard(iteration + 1, usedQueens, usedBishops - 1);
        }
        this._chessBoard.setCellType("*", rowToTest, colToTest);
        analyzeBoard(iteration + 1, usedQueens, usedBishops);
        return;
    }

    public static void printLogMessage(String message){
        System.out.print("[LOG] [T:" + (System.nanoTime()-Main.startTime)/1000000 + " ms] ");
        System.out.println(message);
    }
}