/*
    Manage the chess board and provide a wrapped for all the other classes to use.
 */
package com.chesscover;

import java.util.ArrayList;

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
                if (this._chessBoard[i][j].getCellType() == '*') return false;
            }
        }
        return true;
    }

    // copy the board
    public BoardCell[][] copyBoard(){
        int size = this._chessBoard.length;
        BoardCell[][] target = new BoardCell[_numRows][_numCols];
        for (int n=0;n<this._numRows;n++){
            for (int m=0;m<this._numCols;m++){
                BoardCell BC = this._chessBoard[n][m];
                target[n][m] = new BoardCell(BC);
            }
        }
        return target;
    }

    public void emptyBoard(){
        for (BoardCell[] cellArray:this._chessBoard) {
            for (BoardCell cell:cellArray){
                cell.setCellType(' ');
                cell.setNumOfAttacks(0);
            }
        }
    }

    // analyze the piece placement
    public void renderNewBoard(){
        ArrayList<ChessPiece> listOfPieces = ChessPiece.getListOfPieces();
        this.emptyBoard(); // empty the board
        for (ChessPiece piece:listOfPieces) { // place pieces on board
            this._chessBoard[piece.getX()][piece.getY()] = piece.getCell();
        }
        for (ChessPiece piece:listOfPieces) { // radiate from each piece
            analyzeHorizontal(piece);
            analyzeVertical(piece);
            analyzeDiagonal(piece);
        }
    }

    // check left and right directions from a piece
    private void analyzeHorizontal(ChessPiece pieceToAnalyze){
        //check left
        for (int i= pieceToAnalyze.getY()-1;i>=0;i--){ // if cell empty increase number of attacks
            if (!this._chessBoard[pieceToAnalyze.getX()][i].containsPiece()){
                this._chessBoard[pieceToAnalyze.getX()][i].setAttackedByMore();
                this._chessBoard[pieceToAnalyze.getX()][i].setCellType('*');
            }else{ // if not stop, but still increase number of attacks
                this._chessBoard[pieceToAnalyze.getX()][i].setAttackedByMore();
                break;
            }
        }
        //check right
        for (int i=pieceToAnalyze.getX()+1;i<this.getBoardCols();i++){
            if (!this._chessBoard[pieceToAnalyze.getX()][i].containsPiece()){
                this._chessBoard[pieceToAnalyze.getX()][i].setAttackedByMore();
                this._chessBoard[pieceToAnalyze.getX()][i].setCellType('*');
            }else{ // if not stop, but still increase number of attacks
                this._chessBoard[pieceToAnalyze.getX()][i].setAttackedByMore();
                break;
            }
        }
    }

    private void analyzeVertical(ChessPiece pieceToAnalyze){

    }

    private void analyzeDiagonal(ChessPiece pieceToAnalyze){

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
