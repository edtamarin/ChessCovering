package com.chesscover;

import javax.xml.crypto.Data;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class ChessManager{

    private ChessBoard _chessBoard;
    private int _numQueens;
    private int _numBishops;
    private int solutionMode;
    private int _minNumBishops;
    private ArrayList<ChessBoard> _possibleBoards = new ArrayList<>();
    private ArrayList<ChessBoard> listOfPossibleSolutions = new ArrayList<>();
    private Random _numberGenerator = new Random();
    private DataPackage _parameter;
    ChessBoard boardToPrint;

    public ChessManager(int numOfQueens, int numOfBishops, int boardRows, int boardCols){
        _numQueens = numOfQueens;
        _numBishops = numOfBishops;
        _chessBoard = new ChessBoard(boardRows,boardCols);
        _possibleBoards.add(_chessBoard);
        _minNumBishops = boardRows*boardCols;
        solutionMode = 1;
        if (numOfBishops == -1) {
            solutionMode = 0;
            _numBishops++;
        }
    }

    public void findSolutions(){
        try {
            if(solutionMode == 1){
                analyzeBoard(0, 0, 0);
            }else{
                solutionMode = 1;
                while (listOfPossibleSolutions.size() == 0) {
                    analyzeBoard(0, 0, 0);
                    System.out.println("Iteration complete, no solutions for " + _numBishops + " bishops.");
                    _numBishops++;
                }
                solutionMode = 0;
            }
        }catch (SolutionFoundException e) {
        }
        System.out.println("---------------");
        if (listOfPossibleSolutions.size() == 0){
            System.out.println("No valid solution found!");
            System.out.println("Change chesspiece data and try again.");
            return;
        }
        /*
        try {
            PrintWriter writer = new PrintWriter("7x74q1b.txt", "UTF-8");
            for (ChessBoard board:listOfPossibleSolutions){
                String str = "";
                for (int i=0;i<board.getNumRows();i++){
                    for (int j=0;j<board.getNumCols();j++){
                        str = str+ board.getCellType(i,j) + "|";
                    }
                    str = str+"\r\n";
                }
                writer.println(str);
            }
            writer.close();
        }catch (Exception e){}
        */
        ArrayList<ChessBoard> finalArray = new ArrayList<>();
        if (solutionMode == 0){
            finalArray = listOfPossibleSolutions;
        }else {
            for (ChessBoard board: listOfPossibleSolutions){
                if (board.getNumOfBishops() == _numBishops){
                    finalArray.add(board);
                }
            }
        }
        int boardIndex = _numberGenerator.nextInt(finalArray.size());
        boardToPrint = finalArray.get(boardIndex);
        System.out.println("Solution found!");
        System.out.println();
        if (this.solutionMode == 1) { // if manual search, print how many possibilities found
            System.out.println(finalArray.size()-1 + " other solutions exist.");
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
 //           possibleSolution.printBoard();
            if ((possibleSolution.isFilled())) {
                if (solutionMode == 1) {
                    listOfPossibleSolutions.add(possibleSolution);
                }else if (solutionMode == 0){
                    int bishopsUsed = possibleSolution.getNumOfBishops();
                    if (bishopsUsed<_minNumBishops){
                        listOfPossibleSolutions.add(possibleSolution);
                        _minNumBishops = bishopsUsed;
                        System.out.println("Potential minimum: " + _minNumBishops);
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
        }else if ((usedQueens == this._numQueens) && (solutionMode ==0)){
            this._chessBoard.setCellType("B",rowToTest,colToTest);
            analyzeBoard(iteration+1,usedQueens,usedBishops-1);
        }
        this._chessBoard.setCellType("*",rowToTest,colToTest);
        analyzeBoard(iteration+1,usedQueens,usedBishops);
        return;
    }

    private void analyzeBoardMin(int remQ) throws SolutionFoundException{
        ArrayList<ChessBoard> listOfNewBoards = new ArrayList<>();
        if (remQ == 0){
            for (ChessBoard board:this._possibleBoards){
                board.renderBoard();
                if (board.isFilled()){
                    this.listOfPossibleSolutions.add(board);
                    throw new SolutionFoundException("");
                }
                for (int i=0;i<board.getNumRows();i++){
                    for (int j=0;j<board.getNumCols();j++) {
                        if (!board.hasPiece(i,j)){
                            ChessBoard newBoard= new ChessBoard(board);
                            newBoard.setCellType("B",i,j);
                            listOfNewBoards.add(newBoard);
                        }
                    }
                }
            }
        }else{
            for (ChessBoard board:this._possibleBoards){
                for (int i=0;i<board.getNumRows();i++){
                    for (int j=0;j<board.getNumCols();j++){
                        if (!board.hasPiece(i,j)){
                            ChessBoard newBoard= new ChessBoard(board);
                            newBoard.setCellType("Q",i,j);
                            listOfNewBoards.add(newBoard);
                        }
                    }
                }
            }
            remQ--;
        }
        this._possibleBoards.clear();
        this._possibleBoards.addAll(listOfNewBoards);
        analyzeBoardMin(remQ);
    }
}

class SolutionFoundException extends Exception{
    public SolutionFoundException(String message){
        super(message);
    }
}

class DataPackage{
    int iteration;
    int usedQueens;
    int usedBishops;

    public DataPackage(int iteration, int usedQueens, int usedBishops) {
        this.iteration = iteration;
        this.usedQueens = usedQueens;
        this.usedBishops = usedBishops;
    }
}
