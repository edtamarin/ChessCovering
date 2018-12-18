package com.chesscover;

import java.util.ArrayList;
import java.util.Random;

public class ChessManager {

    private ChessBoard _chessBoard;
    private int _numQueens;
    private int _numBishops;
    private int solutionMode;
    private int _minNumBishops;
    private ArrayList<ChessBoard> listOfPossibleSolutions = new ArrayList<>();
    private Random _numberGenerator = new Random();
    ChessBoard boardToPrint;

    public ChessManager(int numOfQueens, int numOfBishops, int boardRows, int boardCols){
        _numQueens = numOfQueens;
        _numBishops = numOfBishops;
        _chessBoard = new ChessBoard(boardRows,boardCols);
        _minNumBishops = boardRows*boardCols;
        solutionMode = 1;
        if (numOfBishops == -1) solutionMode = 0;
    }

    public void findSolutions(){
        try {
            if(solutionMode ==1){
                analyzeBoard(0, 0, 0);
            }else{
                analyzeBoard(0, 0, Math.max(_chessBoard.getNumCols(),_chessBoard.getNumRows()));
            }
        }catch (SolutionFoundException e) {
        }
        System.out.println("---------------");
        if (listOfPossibleSolutions.size() == 0){
            System.out.println("No valid solution found!");
            System.out.println("Change chesspiece data and try again.");
            return;
        }
        if (solutionMode == 0){
            for (ChessBoard board:listOfPossibleSolutions){
                if (board.getNumOfBishops() == _minNumBishops) boardToPrint = board;
            }
        }else {
            int boardIndex = _numberGenerator.nextInt(listOfPossibleSolutions.size());
            boardToPrint = listOfPossibleSolutions.get(boardIndex);
        }
        System.out.println("Solution found!");
        System.out.println();
        if (this.solutionMode == 1) { // if manual search, print how many possibilities found
            System.out.println(listOfPossibleSolutions.size()-1 + " other solutions exist.");
        }
        boardToPrint.printBoard();
        // count queens and bishops
        int queensOnBoard = 0;
        int bishopsOnBoard = 0;
        for (ChessPiece piece:boardToPrint.boardToListOfPieces()){
            if (piece.getPieceType().equals("Q")){
                queensOnBoard++;
            }else if (piece.getPieceType().equals("B")){
                bishopsOnBoard++;
            }
        }
        System.out.println(queensOnBoard + " queen(s) and " + bishopsOnBoard + " bishop(s) placed.");
    }

    private void analyzeBoard(int iteration, int usedQueens, int usedBishops) throws SolutionFoundException{
        if ((iteration == this._chessBoard.getNumCols()*this._chessBoard.getNumRows()) ||
                ((usedQueens==this._numQueens) && (usedBishops==this._numBishops) && (solutionMode == 1)) ||
                ((usedBishops == 0) && (solutionMode == 0))){
            ChessBoard possibleSolution = new ChessBoard(this._chessBoard);
            possibleSolution.renderBoard();
            if ((possibleSolution.isFilled())) {
                if (solutionMode == 1) {
                    listOfPossibleSolutions.add(possibleSolution);
                }else if (solutionMode == 0){
                    int bishopsUsed = possibleSolution.getNumOfBishops();
                    if (bishopsUsed<_minNumBishops){
                        listOfPossibleSolutions.add(possibleSolution);
                        _minNumBishops = bishopsUsed;
                    }
                }
            }
            return;
        }
        int rowToTest = iteration / this._chessBoard.getBoard().length;
        int colToTest = iteration % this._chessBoard.getBoard().length;
        if (usedQueens<_numQueens){
            this._chessBoard.setCellType("Q",rowToTest,colToTest);
            analyzeBoard(iteration+1,usedQueens+1,usedBishops);
        }
        if (solutionMode == 1){
            if (usedBishops<_numBishops){
                this._chessBoard.setCellType("B",rowToTest,colToTest);
                analyzeBoard(iteration+1,usedQueens,usedBishops+1);
            }
        }else{
            this._chessBoard.setCellType("B",rowToTest,colToTest);
            analyzeBoard(iteration+1,usedQueens,usedBishops-1);
        }
        this._chessBoard.setCellType("*",rowToTest,colToTest);
        analyzeBoard(iteration+1,usedQueens,usedBishops);
        return;
    }
}

class SolutionFoundException extends Exception{
    public SolutionFoundException(String message){
        super(message);
    }
}
