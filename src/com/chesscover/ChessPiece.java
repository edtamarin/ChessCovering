package com.chesscover;

import java.util.ArrayList;

public class ChessPiece {

    private static final int[][] NEIGHBOURS = {
            {-1, -1}, {-1, 0}, {-1, +1},
            { 0, -1},          { 0, +1},
            {+1, -1}, {+1, 0}, {+1, +1}};

    private char _pieceType;
    private int _pieceX;
    private int _pieceY;
    private static ArrayList<ChessPiece> _listOfAllPieces = new ArrayList<>();

    public ChessPiece(char type, int x, int y){
        _pieceType = type;
        _pieceX = x;
        _pieceY = y;
    }

    public static void AddPiece(char type, int x, int y){
        _listOfAllPieces.add(new ChessPiece(type,x,y));
    }

    public static void DelPiece(ChessPiece piece){
        _listOfAllPieces.remove(piece);
    }

    // find out if there is a piece in any one of the next cells
    public boolean isNearby(char[][] testBoard, char type, int rowC, int colC){
        for(int[] offset:NEIGHBOURS){
            if ((rowC+offset[0]>=0) && (colC+offset[1]>=0)){
                if (testBoard[rowC+offset[0]][colC+offset[1]] == 'Q'){
                    return true;
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
}
