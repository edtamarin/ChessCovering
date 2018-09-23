package com.chesscover;

import java.util.ArrayList;

public class ChessPiece {

    private String _pieceType;
    private int _pieceX;
    private int _pieceY;
    private ArrayList<ChessPiece> _listOfAllPieces = new ArrayList<>();

    public ChessPiece(String type, int x, int y){
        _pieceType = type;
        _pieceX = x;
        _pieceY = y;
    }

    public void AddPiece(String type, int x, int y){
        _listOfAllPieces.add(new ChessPiece(type,x,y));
    }

    public void DelPiece(ChessPiece piece){
        _listOfAllPieces.remove(piece);
    }
}
