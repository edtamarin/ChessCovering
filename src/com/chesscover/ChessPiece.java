/*
    Data structure class
    Provides a way to store information about chess pieces separate from the board
    Egor Tamarin, 2018
 */
package com.chesscover;

public class ChessPiece {

    private int _pieceRow;
    private int _pieceCol;
    private String _pieceType;

    public ChessPiece(int _pieceRow, int _pieceCol, String _pieceType) {
        this._pieceRow = _pieceRow;
        this._pieceCol = _pieceCol;
        this._pieceType = _pieceType;
    }

    public int getPieceRow() {
        return _pieceRow;
    }

    public int getPieceCol() {
        return _pieceCol;
    }

    public String getPieceType() {
        return _pieceType;
    }

}
