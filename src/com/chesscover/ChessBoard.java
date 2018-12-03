/*
    Manage the chess board and provide a wrapped for all the other classes to use.
 */
package com.chesscover;

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
