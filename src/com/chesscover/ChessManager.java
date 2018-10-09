package com.chesscover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ChessManager {

    private char[][] _chessBoard;
    private int _numRows;
    private int _numCols;
    private int _numQueens;
    private int _numBishops;
    private int solutionMode;

    public ChessManager(int m, int n, int nQ, int nB){
        _chessBoard = new char[m][n];
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
                _chessBoard[i][j] = '*';
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
    public void FindSolutions(int nQ, int nB){
        if (nQ>0) {
            _chessBoard = AnalyzeBoard(_chessBoard, nQ, nB);
            nQ--;
            for (int i=0;i<getBoardRows();i++){
                for(int j=0;j<getBoardCols();j++){
                    System.out.print(_chessBoard[i][j]);
                }
                System.out.println();
            }
            System.out.println();
            FindSolutions(nQ,nB);
        }else{
            for (int i=0;i<getBoardRows();i++){
                for(int j=0;j<getBoardCols();j++){
                    System.out.print(_chessBoard[i][j]);
                }
                System.out.println();
            }
        }
    }

    // analyze the board
    private char[][] AnalyzeBoard(char[][] board, int remQ, int remB){
        char[][] bufferBoard = copyArray(board);
        int cCoveredMax = 0;
        int cCovered;
        int[] maxCoverPos = {0,0};
        //iterate over the board
        for (int i = 0; i < getBoardRows(); i++) {
            for (int j = 0; j < getBoardCols(); j++) {
                if (board[i][j] != 'Q') {
                    // find the queen position that covers the most cells
                    cCovered = AnalyzeQPlacement(bufferBoard, i, j);
                    if ((cCoveredMax < cCovered) && (!ChessPiece.isNearby(bufferBoard,'Q',i,j))){
                        cCoveredMax = cCovered;
                        maxCoverPos[0] = i;
                        maxCoverPos[1] = j;
                    } //TODO: nearest piece check
                    bufferBoard = copyArray(board);
                }
            }
        }
        AnalyzeQPlacement(bufferBoard,maxCoverPos[0],maxCoverPos[1]);
        bufferBoard[maxCoverPos[0]][maxCoverPos[1]] = 'Q';
        ChessPiece.AddPiece('Q',maxCoverPos[0],maxCoverPos[1]);
        System.out.println(cCoveredMax);
        return bufferBoard;
    }

    private int AnalyzeQPlacement(char[][] anBoard, int rPos,int cPos){
        int cellsCovered = 0;
        // check down direction
        //anBoard[rPos][cPos] = 'Q';
        for (int i=rPos+1;i<getBoardRows();i++){
            if (anBoard[i][cPos] == '*'){
                cellsCovered++;
                anBoard[i][cPos] = '+';
            }
        }
        //check up
        for (int i=rPos-1;i>=0;i--){
            if (anBoard[i][cPos] == '*'){
                cellsCovered++;
                anBoard[i][cPos] = '+';
            }
        }
        //check right
        for (int i=cPos+1;i<getBoardCols();i++){
            if (anBoard[rPos][i] == '*'){
                cellsCovered++;
                anBoard[rPos][i] = '+';
            }
        }
        //check left
        for (int i=cPos-1;i>=0;i--){
            if (anBoard[rPos][i] == '*'){
                cellsCovered++;
                anBoard[rPos][i] = '+';
            }
        }
        // for NE and NW diagonals the sum of i and j should equal the sum of the piece's coordinates
        // for SE and SW diagonals they should fit the identity matrix
        // with the indices shifted by row and column indices
        // check NE diagonal
        for (int i=rPos-1;i>=0;i--){
            for (int j=cPos+1;j<getBoardCols();j++){
                if ((anBoard[i][j] == '*') && (i+j==rPos+cPos)){
                    cellsCovered++;
                    anBoard[i][j] = '+';
                }
            }
        }
        // check SE diagonal
        for (int i=rPos+1;i<getBoardRows();i++){
            for (int j=cPos+1;j<getBoardCols();j++){
                if ((anBoard[i][j] == '*') && ((i-rPos)==(j-cPos))){
                    cellsCovered++;
                    anBoard[i][j] = '+';
                }
            }
        }
        // check NW diagonal
        for (int i=rPos-1;i>=0;i--){
            for (int j=cPos-1;j>=0;j--){
                if ((anBoard[i][j] == '*') && ((i-rPos)==(j-cPos))){
                    cellsCovered++;
                    anBoard[i][j] = '+';
                }
            }
        }
        // check SW diagonal
        for (int i=rPos+1;i<getBoardRows();i++){
            for (int j=cPos-1;j>=0;j--){
                if ((anBoard[i][j] == '*') && (i+j==rPos+cPos)){
                    cellsCovered++;
                    anBoard[i][j] = '+';
                }
            }
        }
        return cellsCovered;
    }

    // we need to copy the arrays for the buffer since Java passes them by reference
    private char[][] copyArray(char[][] source){
        int size = source.length;
        char[][] target = new char[_numRows][_numCols];
        for (int n=0;n<size;n++){
            System.arraycopy(source[n],0,target[n],0,source[n].length);
        }
        return target;
    }


    public int getBoardRows(){
        return _numRows;
    }

    public int getBoardCols(){
        return _numCols;
    }
}
