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

    public char[][] getChessBoard(){
        return this._chessBoard;
    }

    public int getBoardRows(){
        return _numRows;
    }

    public int getBoardCols(){
        return _numCols;
    }
}
