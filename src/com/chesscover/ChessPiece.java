/*
    Manage chess pieces: keep track of ones that are on the board and analyze specific pieces
 */
package com.chesscover;

import java.util.ArrayList;
import java.util.Objects;

public class ChessPiece{

    private static final int[][] Q_NEIGHBOURS = {
            {-1, -1}, {-1, 0}, {-1, +1},
            { 0, -1},          { 0, +1},
            {+1, -1}, {+1, 0}, {+1, +1}};

    private static final int[][] B_NEIGHBOURS = {
            {-1, -1},          {-1, +1},
            {+1, -1},          {+1, +1}};

    private BoardCell _pieceCell;
    private int _pieceX;
    private int _pieceY;
    private static ArrayList<ChessPiece> _listOfAllPieces = new ArrayList<>();

    public ChessPiece(BoardCell cell, int x, int y){
        _pieceCell = cell;
        _pieceX = x;
        _pieceY = y;
    }

    public static void AddPiece(BoardCell cell, int x, int y){
        _listOfAllPieces.add(new ChessPiece(cell,x,y));
    }

    public static void DelPiece(ChessPiece piece){
        _listOfAllPieces.remove(piece);
    }

    // find out if there is a piece in any one of the next cells
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
        }else{ // bishop neighbours
            for (int[] offset : B_NEIGHBOURS) {
                if (((rowC + offset[0] >= 0) && (colC + offset[1] >= 0)) && ((rowC + offset[0] < boardRows) && (colC + offset[1] < boardCols))) {
                    if (testBoard[rowC + offset[0]][colC + offset[1]].getCellType() == type) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static ArrayList<ChessPiece> getListOfPieces(){
        return _listOfAllPieces;
    }

    public int getX(){
        return this._pieceX;
    }

    public int getY(){
        return this._pieceY;
    }

    public BoardCell getCell(){return this._pieceCell;}
}
