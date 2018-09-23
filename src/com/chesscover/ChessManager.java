package com.chesscover;

import java.util.ArrayList;
import java.util.Arrays;

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
        char[][] bufferBoard = board;
        int cCoveredMax = 0;
        int cCovered;
        int[] maxCoverPos = {0,0};
        // if there are queen pieces remaining
        if (remQ>0) {
            //iterate over the board
            for (int i = 0; i < getBoardRows(); i++) {
                for (int j = 0; j < getBoardCols(); j++) {
                    // if the cell is empty analyze
                    if (board[i][j] == '*') {
                        // find the queen position that covers the most cells
                        cCovered = AnalyzeQPlacement(bufferBoard, i, j);
                        for(char[] row:bufferBoard)
                            Arrays.fill(row,'*');
                        if (cCoveredMax < cCovered) {
                            cCoveredMax = cCovered;
                            maxCoverPos[0] = i;
                            maxCoverPos[1] = j;
                        }
                    }
                }
            }
        }
        AnalyzeQPlacement(bufferBoard,maxCoverPos[0],maxCoverPos[1]);
        bufferBoard[maxCoverPos[0]][maxCoverPos[1]] = 'Q';
        return bufferBoard;
    }

    private int AnalyzeQPlacement(char[][] anBoard, int rPos,int cPos){
        int cellsCovered = 0;
        // check down direction
        anBoard[rPos][cPos] = 'Q';
        for (int i=rPos+1;i<getBoardRows();i++){
            if (anBoard[i][cPos] == '*'){
                cellsCovered++;
                anBoard[i][cPos] = '+';
            }else{
                break;
            }
        }
        //check up
        for (int i=rPos-1;i>=0;i--){
            if (anBoard[i][cPos] == '*'){
                cellsCovered++;
                anBoard[i][cPos] = '+';
            }else{
                break;
            }
        }
        //check right
        for (int i=cPos+1;i<getBoardCols();i++){
            if (anBoard[rPos][i] == '*'){
                cellsCovered++;
                anBoard[rPos][i] = '+';
            }else{
                break;
            }
        }
        //check left
        for (int i=cPos-1;i>=0;i--){
            if (anBoard[rPos][i] == '*'){
                cellsCovered++;
                anBoard[rPos][i] = '+';
            }else{
                break;
            }
        }
        // check NE diagonal
        for (int i=rPos-1;i>=0;i--){
            for (int j=cPos+1;j<getBoardCols();j++){
                if ((anBoard[i][j] == '*') && (i+j==getBoardRows()-1)){
                    cellsCovered++;
                    anBoard[i][j] = '+';
                }
            }
        }
        // check SE diagonal
        for (int i=rPos+1;i<getBoardRows();i++){
            for (int j=cPos+1;j<getBoardCols();j++){
                if ((anBoard[i][j] == '*') && (i==j)){
                    cellsCovered++;
                    anBoard[i][j] = '+';
                }
            }
        }
        // check NW diagonal
        for (int i=rPos-1;i>=0;i--){
            for (int j=cPos-1;j>=0;j--){
                if ((anBoard[i][j] == '*') && (i==j)){
                    cellsCovered++;
                    anBoard[i][j] = '+';
                }
            }
        }
        // check SW diagonal
        for (int i=rPos+1;i<getBoardRows();i++){
            for (int j=cPos-1;j>=0;j--){
                if ((anBoard[i][j] == '*') && (i+j==getBoardRows()-1)){
                    cellsCovered++;
                    anBoard[i][j] = '+';
                }
            }
        }
        return cellsCovered;
    }


    public int getBoardRows(){
        return _numRows;
    }

    public int getBoardCols(){
        return _numCols;
    }
}
