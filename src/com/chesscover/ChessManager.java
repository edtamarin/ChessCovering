/*
    The manager class handling the analysis and board operations.
    Egor Tamarin, 2018
 */
package com.chesscover;

import java.util.ArrayList;
import java.util.Random;

public class ChessManager {

    private ChessBoard _chessBoard;
    private int _numQueens;
    private int _numBishops;
    private int solutionMode;
    private boolean _doneAnalyzing = false;
    private ArrayList<ChessBoard> _possibleBoards = new ArrayList<>();
    private Random _numberGenerator;

    public ChessManager(int m, int n, int nQ, int nB){
        _chessBoard = new ChessBoard(m,n);
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
        _possibleBoards.add(_chessBoard);
        _numberGenerator = new Random();
    }

    //find the possible solutions
    public void FindSolutions(){
        // run this either until we run out of pieces  of a minimum solution has been found
        if ((this._numQueens>0) || (this._numBishops>0) || ((this.solutionMode == 0) && (!this._doneAnalyzing))) {
            if(this.solutionMode == 0) {
                AnalyzeBoard(this._numQueens, this._numBishops);
            }else{
                AnalyzeBoard(this._numQueens, this._numBishops);
            }
            // update number of pieces
            if (this._numQueens>0){
                this._numQueens--;
            }else if (this._numBishops>0){
                this._numBishops--;
            }
            // run the algorithm again
            FindSolutions();
        }else{ // print the solution
            // find the boards that are filled (correct solutions)
            System.out.println("---------------");
            ArrayList<ChessBoard> finalBoardArray = new ArrayList<>();
            for (ChessBoard board:this._possibleBoards){
                if (board.isFilled()) finalBoardArray.add(board);
            }
            // if no solution found notify user
            if (finalBoardArray.size() == 0){
                System.out.println("No valid solution found!");
                System.out.println("Change chesspiece data and try again.");
                return;
            }
            // pick a random board to print
            int boardIndex = _numberGenerator.nextInt(finalBoardArray.size());
            ChessBoard boardToPrint = finalBoardArray.get(boardIndex);
            System.out.println("Solution found!");
            System.out.println();
            if (this.solutionMode == 1) { // if manual search, print how many possibilities found
                System.out.println(finalBoardArray.size()-1 + " other solutions exist.");
            }
            boardToPrint.printBoard();
        }
    }

    // analyze the board
    private ArrayList<ChessPiece> AnalyzeBoard(ChessBoard cB, int remQ, int remB){
        BoardCell[][] bufferBoard = cB.copyBoard();
        ArrayList<CoverInfo> coverPositions = new ArrayList<>();
        ArrayList<ChessPiece> maximumCoverPositions = new ArrayList<>();
        int cCoveredMax = 0;
        int cCovered;
        char pieceType = ' ';
        if (remQ>0){
            pieceType = 'Q';
        }else if ((remB>0) || ((this.solutionMode == 0) && (!cB.isFilled()))){
            pieceType = 'B';
        }
        // iterate over the board
        for (int i = 0; i < cB.getBoardRows(); i++) {
            for (int j = 0; j < cB.getBoardCols(); j++) {
                if (!cB.getChessBoard()[i][j].containsPiece()){
                    // find the piece position that covers the most cells
                    cCovered = AnalyzePiecePlacement(bufferBoard,pieceType, i, j);
                    if (bufferBoard[i][j].getNumOfAttacks() == 0){
                        cCovered++;
                    }
                    // check if there is a piece nearby before making a decision
                    // placing a queen near another one is rarely efficient as the 3*3 space is completely covered
                    if ((cCoveredMax <= cCovered) && (!ChessPiece.isNearby(bufferBoard,pieceType,i,j))){
                        cCoveredMax = cCovered;
                        coverPositions.add(new CoverInfo(cCovered,new ChessPiece(new BoardCell(pieceType),i,j)));
                    }
                    bufferBoard = cB.copyBoard();
                }
            }
        }
        // take only the positions with maximum coverage
        for (CoverInfo info:coverPositions){
            if (info.positionsCovered >= cCoveredMax-1){
                maximumCoverPositions.add(info.coveringPiece);
            }
        }
        return maximumCoverPositions;
    }

    private void AnalyzeBoard(int remQ, int remB){
        ArrayList<ChessPiece> listOfMaxPlacements;
        ArrayList<ChessBoard> listOfNewBoards = new ArrayList<>();
        for (ChessBoard board:this._possibleBoards){ // check each possible board
            // if we're in automatic mode and we find a filled board, then a minimum number has been reached, we quit
            if ((board.isFilled()) && (this.solutionMode==0)){
                this._doneAnalyzing = true;
                return;
            }
            // get the number of cells covered by the next piece
            listOfMaxPlacements = AnalyzeBoard(board, remQ, remB);
            for (ChessPiece piece:listOfMaxPlacements) {
                // for each piece in the possible placements list render a new board
                ChessBoard newBoard = new ChessBoard(board.getBoardRows(),board.getBoardCols());
                newBoard.setChessBoard(board.copyBoard());
                newBoard.renderNewBoard(piece);
                listOfNewBoards.add(newBoard);
            }
        }
        // update the global list of possible solutions
        this._possibleBoards = listOfNewBoards;
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

class CoverInfo{
    int positionsCovered;
    ChessPiece coveringPiece;

    CoverInfo(int pC, ChessPiece cP){
        positionsCovered = pC;
        coveringPiece = cP;
    }
}