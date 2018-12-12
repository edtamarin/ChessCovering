/*
    Manage and analyze existing chess pieces
 */
package com.chesscover;

public class ChessPiece{

    private static final int[][] Q_NEIGHBOURS = {
            {-1, -1}, {-1, 0}, {-1, +1},
            { 0, -1},          { 0, +1},
            {+1, -1}, {+1, 0}, {+1, +1}};

    /*private static final int[][] B_NEIGHBOURS = {
            {-1, -1},          {-1, +1},
            {+1, -1},          {+1, +1}};*/

    private BoardCell _pieceCell;
    private int _pieceRow;
    private int _pieceColumn;

    public ChessPiece(BoardCell cell, int x, int y){
        _pieceCell = cell;
        _pieceRow = x;
        _pieceColumn = y;
    }

    // find out if there is a piece in any one of the next cells
    // placing two similar pieces next to each other on covering positions often isn't efficient
    public static boolean isNearby(BoardCell[][] testBoard, char type, int rowC, int colC){
        int boardRows = testBoard.length;
        int boardCols = testBoard[0].length;
        if (type == 'Q') { // queen neighbours
            for (int[] offset : Q_NEIGHBOURS) {
                if (((rowC + offset[0] >= 0) && (colC + offset[1] >= 0)) && ((rowC + offset[0] < boardRows) && (colC + offset[1] < boardCols))) {
                    if (testBoard[rowC + offset[0]][colC + offset[1]].getCellType() == type) {
                        return true;
                    }
                }
            }
        }
        /*else{ // bishop neighbours
            for (int[] offset : B_NEIGHBOURS) {
                if (((rowC + offset[0] >= 0) && (colC + offset[1] >= 0)) && ((rowC + offset[0] < boardRows) && (colC + offset[1] < boardCols))) {
                    if (testBoard[rowC + offset[0]][colC + offset[1]].getCellType() == type) {
                        return true;
                    }
                }
            }
        }*/
        return false;
    }

    public int getRow(){
        return this._pieceRow;
    }

    public int getCol(){
        return this._pieceColumn;
    }

    public BoardCell getCell(){return this._pieceCell;}
}
