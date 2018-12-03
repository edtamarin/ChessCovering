package com.chesscover;

public class BoardCell {

    private char _cellType;
    private int _numOfAttacks;

    public BoardCell(char type){
        _cellType = type;
        _numOfAttacks = 0;
    }

    public BoardCell(BoardCell cell){
        _cellType = cell.getCellType();
        _numOfAttacks = cell.getNumOfAttacks();
    }

    public boolean containsPiece(){
        if ((this._cellType == 'B') || (this._cellType == 'Q')){
            return true;
        }
        return false;
    }

    public void setAttackedByMore(){
        this._numOfAttacks++;
    }

    public void setAttackedByLess(){
        this._numOfAttacks--;
    }

    public void setCellType(char type){
        this._cellType = type;
    }

    public void setNumOfAttacks(int attacks){
        this._numOfAttacks = attacks;
    }

    public char getCellType(){return this._cellType;}

    public int getNumOfAttacks(){return this._numOfAttacks;}
}
