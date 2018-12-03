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
            _chessBoard.setChessBoard(AnalyzeBoard(_chessBoard, this._numQueens, this._numBishops));
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
            for (int i=0;i<_chessBoard.getBoardRows();i++){
                for(int j=0;j<_chessBoard.getBoardCols();j++){
                    System.out.print(_chessBoard.getChessBoard()[i][j].getCellType());
                }
                System.out.println();
            }
        }
    }

    // analyze the board
    private BoardCell[][] AnalyzeBoard(ChessBoard cB, int remQ, int remB){
        BoardCell[][] bufferBoard = cB.copyBoard();
        int cCoveredMax = 0;
        int cCovered;
        int[] maxCoverPos = {0,0};
        char pieceType = ' ';
        if (remQ>0){
            pieceType = 'Q';
        }else if ((remB>0) || ((this.solutionMode == 0) && (!_chessBoard.isFilled()))){
            pieceType = 'B';
        }
        // iterate over the board
        for (int i = 0; i < cB.getBoardRows(); i++) {
            for (int j = 0; j < cB.getBoardCols(); j++) {
                if ((cB.getChessBoardCell(i,j) != 'Q') || (cB.getChessBoardCell(i,j) != 'B')){
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
        AnalyzePiecePlacement(bufferBoard,pieceType,maxCoverPos[0],maxCoverPos[1]);
        bufferBoard[maxCoverPos[0]][maxCoverPos[1]].setCellType(pieceType);
        ChessPiece.AddPiece(bufferBoard[maxCoverPos[0]][maxCoverPos[1]],maxCoverPos[0],maxCoverPos[1]);
        //System.out.println(cCoveredMax);
        return bufferBoard;
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
            }
            //check up
            for (int i = rPos - 1; i >= 0; i--) {
                if (anBoard[i][cPos].getCellType() == '*') {
                    cellsCovered++;
                    anBoard[i][cPos].setCellType('+');
                }
            }
            //check right
            for (int i = cPos + 1; i < _chessBoard.getBoardCols(); i++) {
                if (anBoard[rPos][i].getCellType() == '*') {
                    cellsCovered++;
                    anBoard[rPos][i].setAttackedByMore();
                    anBoard[rPos][i].setCellType('+');
                }
            }
            //check left
            for (int i = cPos - 1; i >= 0; i--) {
                if (anBoard[rPos][i].getCellType() == '*') {
                    cellsCovered++;
                    anBoard[rPos][i].setCellType('+');
                }
            }
        }
        // for NE and NW diagonals the sum of i and j should equal the sum of the piece's coordinates
        // for SE and SW diagonals they should fit the identity matrix
        // with the indices shifted by row and column indices
        // check NE diagonal
        for (int i=rPos-1;i>=0;i--){
            for (int j=cPos+1;j<_chessBoard.getBoardCols();j++){
                if ((anBoard[i][j].getCellType() == '*') && (i+j==rPos+cPos)){
                    cellsCovered++;
                    anBoard[i][j].setCellType('+');
                }
            }
        }
        // check SE diagonal
        for (int i=rPos+1;i<_chessBoard.getBoardRows();i++){
            for (int j=cPos+1;j<_chessBoard.getBoardCols();j++){
                if ((anBoard[i][j].getCellType() == '*') && ((i-rPos)==(j-cPos))){
                    cellsCovered++;
                    anBoard[i][j].setCellType('+');
                }
            }
        }
        // check NW diagonal
        for (int i=rPos-1;i>=0;i--){
            for (int j=cPos-1;j>=0;j--){
                if ((anBoard[i][j].getCellType() == '*') && ((i-rPos)==(j-cPos))){
                    cellsCovered++;
                    anBoard[i][j].setCellType('+');
                }
            }
        }
        // check SW diagonal
        for (int i=rPos+1;i<_chessBoard.getBoardRows();i++){
            for (int j=cPos-1;j>=0;j--){
                if ((anBoard[i][j].getCellType() == '*') && (i+j==rPos+cPos)){
                    cellsCovered++;
                    anBoard[i][j].setCellType('+');
                }
            }
        }
        return cellsCovered;
    }

    // fix bishop blocking verticals and horizontals
    private void fixBishopPlacement(BoardCell[][] board){
        ArrayList<ChessPiece> specialCells = new ArrayList<>();
        BoardCell[] boardRow;
        BoardCell[] boardCol;
        char lastSeen = ' ';
        int[] lastCoords = {0,0};
        boolean edgeOccupied = false;
        boolean hasQ = false;
        for(int i=0; i<_chessBoard.getBoardRows();i++){ // first check row by row
            boardRow = board[i];
            for (int j=0;j<_chessBoard.getBoardCols();j++){
                if (boardRow[j].containsPiece()) { // if there is a piece add it and check if it's a queen
                    specialCells.add(new ChessPiece(boardRow[j],i,j));
                    if (boardRow[j].getCellType() == 'Q'){
                        hasQ = true;
                    }
                }
            }
            if ((specialCells.isEmpty() || (!hasQ))){
                break; // if the row is empty or has no queens nothing should be changed
            }else{
                if ((specialCells.get(0).getX() == 0) && (!specialCells.get(0).getCell().containsPiece())){
                    lastSeen = 'E'; // if the edge on the cell is empty declare it
                }
                if ((specialCells.get(specialCells.size()-1).getX() == 0) && (specialCells.get(specialCells.size()-1).getCell().containsPiece())){
                    specialCells.add(new ChessPiece(new BoardCell('E'),i,specialCells.get(0).getY())); // same for other edge
                }
            }
            for (ChessPiece piece:specialCells) {
                switch (piece.getCell().getCellType()){
                    case 'B': // if we see a bishop it blocks cells behind him but only if there was no queen
                        if (lastSeen == 'E'){
                            for (int x=0;x<=piece.getX();x++){
                                board[piece.getX()][piece.getY()].setAttackedByLess();
                            }
                        }
                        lastSeen = 'B';
                        lastCoords[0] = piece.getX();
                        lastCoords[1] = piece.getY();
                        break;
                    case 'Q': // if we see a queen nothing changes
                        lastSeen = 'Q';
                        lastCoords[0] = piece.getX();
                        lastCoords[1] = piece.getY();
                    case 'E': // if we reach the edge
                        if (lastSeen == 'B'){
                            for (int x=lastCoords[0];x<=piece.getX();x++){
                                board[piece.getX()][piece.getY()].setAttackedByLess();
                            }
                        }
                }
            }
        }
    }
}
