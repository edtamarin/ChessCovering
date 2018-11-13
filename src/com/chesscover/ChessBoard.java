/*
    Manage the chess board and provide a wrapped for all the other classes to use.
 */
package com.chesscover;

public class ChessBoard {

    private char[][] _chessBoard;
    private int _numRows;
    private int _numCols;

    public ChessBoard(int rows, int cols){
        _chessBoard = new char[rows][cols];
        _numRows = rows;
        _numCols = cols;
    }

    public boolean isFilled(){
        for (int i=0;i<this._numRows;i++){
            for (int j=0;j<this._numCols;j++){
                if (this._chessBoard[i][j] == '*') return false;
            }
        }
        return true;
    }

    public char[][] getChessBoard(){
        return this._chessBoard;
    }

    public char getChessBoardCell(int row, int col){ return  this._chessBoard[row][col];}

    public void setChessBoard(char[][] board){ this._chessBoard = board;}

    public int getBoardRows(){
        return _numRows;
    }

    public int getBoardCols(){
        return _numCols;
    }
}
