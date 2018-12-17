/*
    Manage the chess board.
 */
package com.chesscover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ChessBoard {

    private BoardCell[][] _chessBoard;
    private int _numRows;
    private int _numCols;

    public ChessBoard(int rows, int cols){
        _chessBoard = new BoardCell[rows][cols];
        _numRows = rows;
        _numCols = cols;
    }

    public boolean isFilled(){
        for (int i=0;i<this._numRows;i++){
            for (int j=0;j<this._numCols;j++){
                if (this.getChessBoardCell(i,j) == '*') return false;
            }
        }
        return true;
    }

    // copy the board
    // this is needed so the board is not passed by reference, so we do a deep copy
    public BoardCell[][] copyBoard(){
        BoardCell[][] target = new BoardCell[_numRows][_numCols];
        for (int n=0;n<this._numRows;n++){
            for (int m=0;m<this._numCols;m++){
                BoardCell BC = this._chessBoard[n][m];
                target[n][m] = new BoardCell(BC);
            }
        }
        return target;
    }

    @Override
    public boolean equals(Object o) {
        //if (this == o) return true;
        //if (o == null || getClass() != o.getClass()) return false;
        ChessBoard board = (ChessBoard) o;
        return Arrays.deepEquals(_chessBoard, board._chessBoard);
    }

    @Override
    public int hashCode() {
       return Arrays.hashCode(_chessBoard);
    }

    public static ArrayList<ChessBoard> removeDuplicates(ArrayList<ChessBoard> dupesList){
        Set<ChessBoard> finalSet = new HashSet<>();
        ArrayList<ChessBoard> finalList = new ArrayList<>(dupesList);
        finalSet.addAll(finalList);
        finalList.clear();
        finalList.addAll(finalSet);
        return finalList;
    }

    // scrape the pieces on a provided board into a list
    // easier to work with than a global storage since pieces are stored on boards anyway
    public ArrayList<ChessPiece> boardToListOfPieces(){
        ArrayList<ChessPiece> piecesOnBoard = new ArrayList<>();
        for (int i=0;i<this._numRows;i++){
            for(int j=0;j<this._numCols;j++){
                if (this._chessBoard[i][j].containsPiece()){
                    piecesOnBoard.add(new ChessPiece(new BoardCell(this._chessBoard[i][j]),i,j));
                }
            }
        }
        return piecesOnBoard;
    }

    // empty the board
    public void emptyBoard(){
        for (BoardCell[] cellArray:this._chessBoard) {
            for (BoardCell cell:cellArray){
                cell.setCellType('*');
                cell.setNumOfAttacks(0);
            }
        }
    }

    // analyze the piece placement
    public void renderNewBoard(){
        ArrayList<ChessPiece> listOfPieces;
        listOfPieces = this.boardToListOfPieces(); // get the pieces on a board
        //this.emptyBoard(); // empty the board
        //for (ChessPiece piece:listOfPieces) { // place pieces on board
        //    this._chessBoard[piece.getRow()][piece.getCol()].setCellType(piece.getCell().getCellType());
        //}
        for (ChessPiece piece:listOfPieces) { // radiate from each piece
            // horizontals and verticals are only covered by queens
            if (piece.getCell().getCellType() == 'Q') {
                analyzeHorizontal(piece);
                analyzeVertical(piece);
            }
            analyzeDiagonal(piece);
        }
    }

    // check left and right directions from a piece
    private void analyzeHorizontal(ChessPiece pieceToAnalyze){
        //check left
        for (int i = pieceToAnalyze.getCol()-1; i>=0; i--){ // if cell empty increase number of attacks
            if (checkPieceConditionsHorizontal(pieceToAnalyze,i) == 1){
                break;
            }
        }
        //check right
        for (int i = pieceToAnalyze.getCol()+1; i<this.getBoardCols(); i++){
            if (checkPieceConditionsHorizontal(pieceToAnalyze,i) == 1){
                break;
            }
        }
    }

    // check up and down directions
    private void analyzeVertical(ChessPiece pieceToAnalyze){
        // check up
        for (int i = pieceToAnalyze.getRow()-1; i>=0; i--){
            if (checkPieceConditionsVertical(pieceToAnalyze,i) == 1){
                break;
            }
        }
        // check down
        for (int i = pieceToAnalyze.getRow()+1; i<this._numRows; i++){
            if (checkPieceConditionsVertical(pieceToAnalyze,i) == 1){
                break;
            }
        }
    }

    private void analyzeDiagonal(ChessPiece pieceToAnalyze){
        //check NE diagonal
        loopNE: // label to break to
        for (int i = pieceToAnalyze.getRow()-1; i>=0; i--){
            for (int j=pieceToAnalyze.getCol()+1;j<this._numCols;j++) {
                if (checkPieceConditionsNE_SW(pieceToAnalyze,i,j) == 1) break loopNE;
            }
        }
        //check SW diagonal
        loopSW:
        for (int i=pieceToAnalyze.getRow()+1;i<this._numRows;i++) {
            for (int j = pieceToAnalyze.getCol() - 1; j >= 0; j--) {
                if (checkPieceConditionsNE_SW(pieceToAnalyze,i,j) == 1) break loopSW;
            }
        }
        //check NW diagonal
        loopNW:
        for (int i=pieceToAnalyze.getRow()-1;i>=0;i--) {
            for (int j = pieceToAnalyze.getCol() - 1; j >= 0; j--) {
                if (checkPieceConditionsNW_SE(pieceToAnalyze,i,j)==1) break loopNW;
            }
        }
        //check SE diagonal
        loopSE:
        for (int i=pieceToAnalyze.getRow()+1;i<this._numRows;i++) {
            for (int j = pieceToAnalyze.getCol() + 1; j < this._numCols; j++) {
                if (checkPieceConditionsNW_SE(pieceToAnalyze,i,j)==1) break loopSE;
            }
        }
    }

    // function to check cell availability on a horizontal sweep
    private int checkPieceConditionsHorizontal(ChessPiece pieceToCheck, int index){
        if (!this._chessBoard[pieceToCheck.getRow()][index].containsPiece()){
            this._chessBoard[pieceToCheck.getRow()][index].setAttackedByMore();
            this._chessBoard[pieceToCheck.getRow()][index].setCellType('+');
        }else{ // if not stop, but still increase number of attacks
            this._chessBoard[pieceToCheck.getRow()][index].setAttackedByMore();
            return 1;
        }
        return 0;
    }

    // function to check cell availability on a vertical sweep
    private int checkPieceConditionsVertical(ChessPiece pieceToCheck, int index){
        if (!this._chessBoard[index][pieceToCheck.getCol()].containsPiece()){
            this._chessBoard[index][pieceToCheck.getCol()].setAttackedByMore();
            this._chessBoard[index][pieceToCheck.getCol()].setCellType('+');
        }else{ // if not stop, but still increase number of attacks
            this._chessBoard[index][pieceToCheck.getCol()].setAttackedByMore();
            return 1;
        }
        return 0;
    }

    // check cell availability on the NE-SW diagonal
    private int checkPieceConditionsNE_SW(ChessPiece pieceToCheck, int rowIndex, int colIndex){
        if (rowIndex+colIndex==pieceToCheck.getRow()+pieceToCheck.getCol()) {
            if (diagonalConditionsCheck(rowIndex, colIndex)) return 1;
        }
        return 0;
    }

    // check cell availability on the NW-SE diagonal
    private int checkPieceConditionsNW_SE(ChessPiece pieceToCheck, int rowIndex, int colIndex){
        if ((rowIndex-pieceToCheck.getRow())==(colIndex-pieceToCheck.getCol())) {
            if (diagonalConditionsCheck(rowIndex, colIndex)) return 1;
        }
        return 0;
    }

    // cell processing for diagonals is the same, extracted to prevent repetitions
    private boolean diagonalConditionsCheck(int rowIndex, int colIndex) {
        if ((!this._chessBoard[rowIndex][colIndex].containsPiece())) {
            this._chessBoard[rowIndex][colIndex].setAttackedByMore();
            this._chessBoard[rowIndex][colIndex].setCellType('+');
        } else { // if not stop, but still increase number of attacks
            this._chessBoard[rowIndex][colIndex].setAttackedByMore();
            return true;
        }
        return false;
    }

    public void printBoard(){
        for (int i=0;i<this._numRows;i++){
            for(int j=0;j<this._numCols;j++){
                System.out.print(this._chessBoard[i][j].getCellType()+"|");
            }
            System.out.println();
        }
        System.out.println();
    }

    public BoardCell[][] getChessBoard(){
        return this._chessBoard;
    }

    public char getChessBoardCell(int row, int col){ return  this._chessBoard[row][col].getCellType();}

    public void setChessBoard(BoardCell[][] board){ this._chessBoard = board;}

    public int getBoardRows(){
        return _numRows;
    }

    public int getBoardCols(){
        return _numCols;
    }
}
