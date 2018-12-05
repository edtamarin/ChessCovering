/*
    The manager class handling the analysis and board operations.
    Egor Tamarin, 2018
 */
package com.chesscover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ChessManager {

    private ChessBoard _chessBoard;
    private int _numRows;
    private int _numCols;
    private int _numQueens;
    private int _numBishops;
    private int solutionMode;

    public ChessManager(int m, int n, int nQ, int nB){
        _chessBoard = new ChessBoard(m,n);
        _numRows = m;
        _numCols = n;
        _numQueens = nQ;
        _numBishops = nB;
        // fill the chess board
        // * - empty cell
        // + - under attack
        // Q/B - Queen or Bishop
        for (int i=0; i<m; i++){
            for (int j=0; j<n; j++){
                _chessBoard.getChessBoard()[i][j] = new BoardCell('*');
            }
        }
        // set the mode for solutions
        // 0 is automatic
        // 1 is with user-provided number of bishops
        if (nB == 0){
            solutionMode = 0;
        }else{
            solutionMode = 1;
        }
    }

    //find the possible solutions
    public void FindSolutions(){
        if ((this._numQueens>0) || ((this._numBishops>0) || ((this.solutionMode == 0) && (!_chessBoard.isFilled())))) { // analyze for queens
            AnalyzeBoard(_chessBoard, this._numQueens, this._numBishops);
            if (this._numQueens>0){
                this._numQueens--;
            }else if (this._numBishops>0){
                this._numBishops--;
            }
            /*
            for (int i=0;i<_chessBoard.getBoardRows();i++){
                for(int j=0;j<_chessBoard.getBoardCols();j++){
                    System.out.print(_chessBoard.getChessBoard()[i][j].getNumOfAttacks());
                }
                System.out.println();
            }
            System.out.println();*/
            FindSolutions();
        }else{ // print the solution
            this._chessBoard.printBoard();
        }
    }

    // analyze the board
    private void AnalyzeBoard(ChessBoard cB, int remQ, int remB){
        BoardCell[][] bufferBoard = cB.copyBoard();
        int cCoveredMax = 0;
        int cCovered;
        int[] maxCoverPos = {0,0};
        char pieceType = ' ';
        if (remQ>0){
            pieceType = 'Q';
        }else if ((remB>0) || ((this.solutionMode == 0) && (!this._chessBoard.isFilled()))){
            pieceType = 'B';
        }
        // iterate over the board
        for (int i = 0; i < cB.getBoardRows(); i++) {
            for (int j = 0; j < cB.getBoardCols(); j++) {
                if (!cB.getChessBoard()[i][j].containsPiece()){
                    // find the piece position that covers the most cells
                    cCovered = AnalyzePiecePlacement(bufferBoard,pieceType, i, j);
                    // check if there is a piece nearby before making a decision
                    // placing a queen near another one is rarely efficient as the 3*3 space is completely covered
                    if ((cCoveredMax < cCovered) && (!ChessPiece.isNearby(bufferBoard,pieceType,i,j))){
                        cCoveredMax = cCovered;
                        maxCoverPos[0] = i;
                        maxCoverPos[1] = j;
                    }
                    bufferBoard = cB.copyBoard();
                }
            }
        }
        //AnalyzePiecePlacement(bufferBoard,pieceType,maxCoverPos[0],maxCoverPos[1]);
        bufferBoard[maxCoverPos[0]][maxCoverPos[1]].setCellType(pieceType);
        ChessPiece.AddPiece(bufferBoard[maxCoverPos[0]][maxCoverPos[1]],maxCoverPos[0],maxCoverPos[1]);
/*        for (ChessPiece piece:ChessPiece.getListOfPieces()) {
            System.out.print(piece.getCell().getCellType() + " " + piece.getRow() + " " + piece.getCol() + " | ");
        }
        System.out.println();*/
        cB.renderNewBoard();
        //System.out.println(cCoveredMax);
    }

    private int AnalyzePiecePlacement(BoardCell[][] anBoard, char type, int rPos,int cPos){
        int cellsCovered = 0;
        // check down direction
        //anBoard[rPos][cPos] = 'Q';
        // only check horizontal/vertical for a queen
        if (type == 'Q') {
            //check down
            for (int i = rPos + 1; i < _chessBoard.getBoardRows(); i++) {
                if (anBoard[i][cPos].getCellType() == '*') {
                    cellsCovered++;
                    anBoard[i][cPos].setCellType('+');
                }
                if (anBoard[i][cPos].containsPiece()){
                    break;
                }
            }
            //check up
            for (int i = rPos - 1; i >= 0; i--) {
                if (anBoard[i][cPos].getCellType() == '*') {
                    cellsCovered++;
                    anBoard[i][cPos].setCellType('+');
                }
                if (anBoard[i][cPos].containsPiece()){
                    break;
                }
            }
            //check right
            for (int i = cPos + 1; i < _chessBoard.getBoardCols(); i++) {
                if (anBoard[rPos][i].getCellType() == '*') {
                    cellsCovered++;
                    anBoard[rPos][i].setCellType('+');
                }
                if (anBoard[rPos][i].containsPiece()){
                    break;
                }
            }
            //check left
            for (int i = cPos - 1; i >= 0; i--) {
                if (anBoard[rPos][i].getCellType() == '*') {
                    cellsCovered++;
                    anBoard[rPos][i].setCellType('+');
                }
                if (anBoard[rPos][i].containsPiece()){
                    break;
                }
            }
        }
        // for NE and NW diagonals the sum of i and j should equal the sum of the piece's coordinates
        // for SE and SW diagonals they should fit the identity matrix
        // with the indices shifted by row and column indices
        // check NE diagonal
        outerloop1:
        for (int i=rPos-1;i>=0;i--){
            for (int j=cPos+1;j<_chessBoard.getBoardCols();j++){
                if ((anBoard[i][j].getCellType() == '*') && (i+j==rPos+cPos)){
                    cellsCovered++;
                    anBoard[i][j].setCellType('+');
                }
                if (anBoard[i][j].containsPiece() && (i+j==rPos+cPos)){
                    break outerloop1;
                }
            }
        }
        // check SE diagonal
        outerloop2:
        for (int i=rPos+1;i<_chessBoard.getBoardRows();i++){
            for (int j=cPos+1;j<_chessBoard.getBoardCols();j++){
                if ((anBoard[i][j].getCellType() == '*') && ((i-rPos)==(j-cPos))){
                    cellsCovered++;
                    anBoard[i][j].setCellType('+');
                }
                if (anBoard[i][j].containsPiece() && ((i-rPos)==(j-cPos))){
                    break outerloop2;
                }
            }
        }
        // check NW diagonal
        outerloop3:
        for (int i=rPos-1;i>=0;i--){
            for (int j=cPos-1;j>=0;j--){
                if ((anBoard[i][j].getCellType() == '*') && ((i-rPos)==(j-cPos))){
                    cellsCovered++;
                    anBoard[i][j].setCellType('+');
                }
                if (anBoard[i][j].containsPiece() && ((i-rPos)==(j-cPos))){
                    break outerloop3;
                }
            }
        }
        // check SW diagonal
        outerloop4:
        for (int i=rPos+1;i<_chessBoard.getBoardRows();i++){
            for (int j=cPos-1;j>=0;j--){
                if ((anBoard[i][j].getCellType() == '*') && (i+j==rPos+cPos)){
                    cellsCovered++;
                    anBoard[i][j].setCellType('+');
                }
                if (anBoard[i][j].containsPiece() && (i+j==rPos+cPos)){
                    break outerloop4;
                }
            }
        }
        return cellsCovered;
    }
}
