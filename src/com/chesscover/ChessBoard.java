package com.chesscover;

import java.util.ArrayList;

public class ChessBoard {

    private String[][] _chessBoard;
    private int _numRows;
    private int _numCols;

    public ChessBoard(int rows, int cols){
        _chessBoard = new String[rows][cols];
        for (int i=0;i<rows;i++){
            for (int j=0;j<cols;j++){
                _chessBoard[i][j] = "*";
            }
        }
        _numRows = rows;
        _numCols = cols;
    }

    public ChessBoard(ChessBoard board){
        this._numRows = board._numRows;
        this._numCols = board._numCols;
        this._chessBoard = new String[board._chessBoard.length][];
        for (int i=0;i<board._chessBoard.length;i++){
            this._chessBoard[i] = board._chessBoard[i].clone();
        }
    }

    public boolean isFilled(){
        for (int i=0;i<this._numRows;i++){
            for (int j=0;j<this._numCols;j++){
                if (this._chessBoard[i][j].equals("*")) return false;
            }
        }
        return true;
    }

    private void emptyBoard(){
        for (int i=0;i<this._numRows;i++){
            for (int j=0;j<this._numCols;j++){
                if (!this.hasPiece(i,j)) this.setCellType("*",i,j);
            }
        }
    }

    public void renderBoard(){
        ArrayList<ChessPiece> listOfPieces;
        listOfPieces = this.boardToListOfPieces(); // get the pieces on a board
        this.emptyBoard();
        for (ChessPiece piece:listOfPieces) { // radiate from each piece
            // horizontals and verticals are only covered by queens
            if (piece.getPieceType().equals("Q")) {
                analyzeHorizontal(piece);
                analyzeVertical(piece);
            }
            analyzeDiagonal(piece);
        }
    }

    private void analyzeHorizontal(ChessPiece pieceToAnalyze){
        //check left
        for (int i = pieceToAnalyze.getPieceCol()-1; i>=0; i--){ // if cell empty increase number of attacks
            if (checkPieceConditionsHorizontal(pieceToAnalyze,i) == 1){
                break;
            }
        }
        //check right
        for (int i = pieceToAnalyze.getPieceCol()+1; i<this.getNumCols(); i++){
            if (checkPieceConditionsHorizontal(pieceToAnalyze,i) == 1){
                break;
            }
        }
    }

    // check up and down directions
    private void analyzeVertical(ChessPiece pieceToAnalyze){
        // check up
        for (int i = pieceToAnalyze.getPieceRow()-1; i>=0; i--){
            if (checkPieceConditionsVertical(pieceToAnalyze,i) == 1){
                break;
            }
        }
        // check down
        for (int i = pieceToAnalyze.getPieceRow()+1; i<this._numRows; i++){
            if (checkPieceConditionsVertical(pieceToAnalyze,i) == 1){
                break;
            }
        }
    }

    private void analyzeDiagonal(ChessPiece pieceToAnalyze){
        //check NE diagonal
        loopNE: // label to break to
        for (int i = pieceToAnalyze.getPieceRow()-1; i>=0; i--){
            for (int j=pieceToAnalyze.getPieceCol()+1;j<this._numCols;j++) {
                if (checkPieceConditionsNE_SW(pieceToAnalyze,i,j) == 1) break loopNE;
            }
        }
        //check SW diagonal
        loopSW:
        for (int i=pieceToAnalyze.getPieceRow()+1;i<this._numRows;i++) {
            for (int j = pieceToAnalyze.getPieceCol() - 1; j >= 0; j--) {
                if (checkPieceConditionsNE_SW(pieceToAnalyze,i,j) == 1) break loopSW;
            }
        }
        //check NW diagonal
        loopNW:
        for (int i=pieceToAnalyze.getPieceRow()-1;i>=0;i--) {
            for (int j = pieceToAnalyze.getPieceCol() - 1; j >= 0; j--) {
                if (checkPieceConditionsNW_SE(pieceToAnalyze,i,j)==1) break loopNW;
            }
        }
        //check SE diagonal
        loopSE:
        for (int i=pieceToAnalyze.getPieceRow()+1;i<this._numRows;i++) {
            for (int j = pieceToAnalyze.getPieceCol() + 1; j < this._numCols; j++) {
                if (checkPieceConditionsNW_SE(pieceToAnalyze,i,j)==1) break loopSE;
            }
        }
    }

    // function to check cell availability on a horizontal sweep
    private int checkPieceConditionsHorizontal(ChessPiece pieceToCheck, int index){
        if (!this.hasPiece(pieceToCheck.getPieceRow(),index)){
            this.setCellType("+",pieceToCheck.getPieceRow(),index);
        }else{ // if not stop, but still increase number of attacks
            return 1;
        }
        return 0;
    }

    // function to check cell availability on a vertical sweep
    private int checkPieceConditionsVertical(ChessPiece pieceToCheck, int index){
        if (!this.hasPiece(index,pieceToCheck.getPieceCol())){
            this.setCellType("+",index,pieceToCheck.getPieceCol());
        }else{ // if not stop, but still increase number of attacks
            return 1;
        }
        return 0;
    }

    // check cell availability on the NE-SW diagonal
    private int checkPieceConditionsNE_SW(ChessPiece pieceToCheck, int rowIndex, int colIndex){
        if (rowIndex+colIndex==pieceToCheck.getPieceRow()+pieceToCheck.getPieceCol()) {
            if (diagonalConditionsCheck(rowIndex, colIndex)) return 1;
        }
        return 0;
    }

    // check cell availability on the NW-SE diagonal
    private int checkPieceConditionsNW_SE(ChessPiece pieceToCheck, int rowIndex, int colIndex){
        if ((rowIndex-pieceToCheck.getPieceRow())==(colIndex-pieceToCheck.getPieceCol())) {
            if (diagonalConditionsCheck(rowIndex, colIndex)) return 1;
        }
        return 0;
    }

    // cell processing for diagonals is the same, extracted to prevent repetitions
    private boolean diagonalConditionsCheck(int rowIndex, int colIndex) {
        if (!this.hasPiece(rowIndex,colIndex)) {
            this.setCellType("+",rowIndex,colIndex);
        } else { // if not stop, but still increase number of attacks
            return true;
        }
        return false;
    }

    public ArrayList<ChessPiece> boardToListOfPieces(){
        ArrayList<ChessPiece> piecesOnBoard = new ArrayList<>();
        for (int i=0;i<this._numRows;i++){
            for(int j=0;j<this._numCols;j++){
                if (this.hasPiece(i,j)){
                    piecesOnBoard.add(new ChessPiece(i,j,this._chessBoard[i][j]));
                }
            }
        }
        return piecesOnBoard;
    }

    public void printBoard(){
        for (int i=0;i<this._numRows;i++){
            for(int j=0;j<this._numCols;j++){
                System.out.print(this.getCellType(i,j)+"|");
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean hasPiece(int row, int col){
        if ((this._chessBoard[row][col].equals("Q")) || (this._chessBoard[row][col].equals("B"))) return true;
        return false;
    }

    public int getNumOfBishops(){
        int result = 0;
        ArrayList<ChessPiece> piecesonBoard = this.boardToListOfPieces();
        for (ChessPiece piece:piecesonBoard) {
            if (piece.getPieceType().equals("B")){
                result++;
            }
        }
        return result;
    }

    public int getNumRows() {
        return _numRows;
    }

    public int getNumCols() {
        return _numCols;
    }

    public String[][] getBoard() {
        return _chessBoard;
    }

    public String getCellType(int row,int col) {
        return _chessBoard[row][col];
    }

    public void setCellType(String type, int row, int col){
        this._chessBoard[row][col] = type;
    }
}
