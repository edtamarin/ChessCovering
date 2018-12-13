/*
    The manager class handling the analysis and board operations.
    Egor Tamarin, 2018
 */
package com.chesscover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
    public void findSolutions(){
        // run this either until we run out of pieces  of a minimum solution has been found
        if ((this._numQueens>0) || (this._numBishops>0) || ((this.solutionMode == 0) && (!this._doneAnalyzing))) {
            if(this.solutionMode == 0) {
                analyzeBoard(this._numQueens, this._numBishops);
            }else{
                analyzeBoard(this._numQueens, this._numBishops);
            }
            // update number of pieces
            if (this._numQueens>0){
                this._numQueens--;
            }else if (this._numBishops>0){
                this._numBishops--;
            }
            // run the algorithm again
            findSolutions();
        }else{ // print the solution
            // find the boards that are filled (correct solutions)
            int numOfDistinctBoards = 0;
            System.out.println();
            System.out.println("---------------");
            ArrayList<ChessBoard> finalBoardArray = new ArrayList<>();
            for (ChessBoard board : this._possibleBoards) {
                if (board.isFilled()) {
                    finalBoardArray.add(board);
                }
            }
            // count unique solutions
            // the array of solutions contains duplicates
            numOfDistinctBoards = countUniqueSolutions(finalBoardArray);
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
                System.out.println(numOfDistinctBoards-1 + " other solutions exist.");
            }
            boardToPrint.printBoard();
            // count queens and bishops
            int queensOnBoard = 0;
            int bishopsOnBoard = 0;
            for (ChessPiece piece:boardToPrint.boardToListOfPieces()){
                if (piece.getCell().getCellType() == 'Q'){
                    queensOnBoard++;
                }else if (piece.getCell().getCellType() == 'B'){
                    bishopsOnBoard++;
                }
            }
            System.out.println(queensOnBoard + " queen(s) and " + bishopsOnBoard + " bishop(s) placed.");
        }
    }

    // analyze the board
    private ArrayList<ChessPiece> analyzeBoard(ChessBoard cB, int remQ, int remB){
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
                    cCovered = analyzePiecePlacement(bufferBoard,pieceType, i, j);
                    if (bufferBoard[i][j].getNumOfAttacks() == 0){
                        cCovered++;
                    }
                    // check if there is a piece nearby before making a decision
                    // placing a queen near another one is rarely efficient as the 3*3 space is completely covered
                    if ((cCoveredMax <= cCovered) && (!ChessPiece.isNearby(bufferBoard,pieceType,i,j))){
                        cCoveredMax = cCovered;
                        coverPositions.add(new CoverInfo(cCovered,new ChessPiece(new BoardCell(pieceType),i,j)));
                    }
                    //bufferBoard = cB.copyBoard();
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

    private void analyzeBoard(int remQ, int remB){
        ArrayList<ChessPiece> listOfMaxPlacements;
        ArrayList<ChessBoard> listOfNewBoards = new ArrayList<>();
        for (ChessBoard board:this._possibleBoards){ // check each possible board
            // if we're in automatic mode and we find a filled board, then a minimum number has been reached, we quit
            if ((board.isFilled()) && (this.solutionMode==0)){
                this._doneAnalyzing = true;
                return;
            }
            // get the number of cells covered by the next piece
            listOfMaxPlacements = analyzeBoard(board, remQ, remB);
            for (ChessPiece piece:listOfMaxPlacements) {
                // for each piece in the possible placements list render a new board
                ChessBoard newBoard = new ChessBoard(board.getBoardRows(),board.getBoardCols());
                newBoard.setChessBoard(board.copyBoard());
                newBoard.renderNewBoard(piece);
                listOfNewBoards.add(newBoard);
            }
        }
        printLogMessage("Iteration complete, " + listOfNewBoards.size() + " new boards created.");
        // update the global list of possible solutions
        this._possibleBoards = listOfNewBoards;
    }

    private int analyzePiecePlacement(BoardCell[][] board, char type, int rPos, int cPos){
        int cellsCovered = 0;
        // check down direction
        // only check horizontal/vertical for a queen
        if (type == 'Q') {
            //check down
            for (int i = rPos + 1; i < _chessBoard.getBoardRows(); i++) {
                if (board[i][cPos].getCellType() == '*') {
                    cellsCovered++; // if cell fits description, it's covered
                    //board[i][cPos].setCellType('+');
                }
                if (board[i][cPos].containsPiece()){
                    break; // if we hit a piece stop
                }
            }
            //check up
            for (int i = rPos - 1; i >= 0; i--) {
                if (board[i][cPos].getCellType() == '*') {
                    //board[i][cPos].setCellType('+');
                    cellsCovered++;
                }
                if (board[i][cPos].containsPiece()){
                    break;
                }
            }
            //check right
            for (int i = cPos + 1; i < _chessBoard.getBoardCols(); i++) {
                if (board[rPos][i].getCellType() == '*') {
                    cellsCovered++;
                    //[rPos][i].setCellType('+');
                }
                if (board[rPos][i].containsPiece()){
                    break;
                }
            }
            //check left
            for (int i = cPos - 1; i >= 0; i--) {
                if (board[rPos][i].getCellType() == '*') {
                    cellsCovered++;
                    //board[rPos][i].setCellType('+');
                }
                if (board[rPos][i].containsPiece()){
                    break;
                }
            }
        }
        // for NE and NW diagonals the sum of i and j should equal the sum of the piece's coordinates
        // for SE and SW diagonals they should fit the identity matrix
        // with the indices shifted by row and column indices
        // check NE diagonal
        NEloop:
        for (int i=rPos-1;i>=0;i--){
            for (int j=cPos+1;j<_chessBoard.getBoardCols();j++){ // if cell fits description, it's covered
                if ((board[i][j].getCellType() == '*') && (i+j==rPos+cPos)){
                    cellsCovered++;
                    //board[i][j].setCellType('+');
                }
                if (board[i][j].containsPiece() && (i+j==rPos+cPos)){
                    break NEloop; // if we hit a piece stop
                }
            }
        }
        // check SE diagonal
        SEloop:
        for (int i=rPos+1;i<_chessBoard.getBoardRows();i++){
            for (int j=cPos+1;j<_chessBoard.getBoardCols();j++){
                if ((board[i][j].getCellType() == '*') && ((i-rPos)==(j-cPos))){
                    cellsCovered++;
                    //board[i][j].setCellType('+');
                }
                if (board[i][j].containsPiece() && ((i-rPos)==(j-cPos))){
                    break SEloop; // if we hit a piece stop
                }
            }
        }
        // check NW diagonal
        NWloop:
        for (int i=rPos-1;i>=0;i--){
            for (int j=cPos-1;j>=0;j--){
                if ((board[i][j].getCellType() == '*') && ((i-rPos)==(j-cPos))){
                    cellsCovered++;
                    //board[i][j].setCellType('+');
                }
                if (board[i][j].containsPiece() && ((i-rPos)==(j-cPos))){
                    break NWloop; // if we hit a piece stop
                }
            }
        }
        // check SW diagonal
        SWloop:
        for (int i=rPos+1;i<_chessBoard.getBoardRows();i++){
            for (int j=cPos-1;j>=0;j--){
                if ((board[i][j].getCellType() == '*') && (i+j==rPos+cPos)){
                    cellsCovered++;
                    //board[i][j].setCellType('+');
                }
                if (board[i][j].containsPiece() && (i+j==rPos+cPos)){
                    break SWloop; // if we hit a piece stop
                }
            }
        }
        return cellsCovered;
    }

    // get rid of duplicates by placing the stringified boards in a Set
    // sets disallow duplicates
    private int countUniqueSolutions(ArrayList<ChessBoard> boardArray){
        String boardString = "";
        Set<String> boardHashSet = new HashSet<>();
        for (ChessBoard board: boardArray) {
            for (int i = 0; i <board.getBoardRows(); i++) {
                for (int j = 0; j < board.getBoardCols(); j++) {
                    boardString = boardString + board.getChessBoardCell(i,j);
                }
            }
            boardHashSet.add(boardString);
            boardString = "";
        }
        return boardHashSet.size();
    }


    // logging method
    // for boards with high execution time provides a larger insight into how the program runs.
    public static void printLogMessage(String message){
        System.out.print("[LOG] [T:" + ((System.nanoTime()-Main.startTime)/1000000) + " ms] \t");
        System.out.println(message);
    }
}


// data structure class
class CoverInfo{
    int positionsCovered;
    ChessPiece coveringPiece;

    CoverInfo(int pC, ChessPiece cP){
        positionsCovered = pC;
        coveringPiece = cP;
    }
}