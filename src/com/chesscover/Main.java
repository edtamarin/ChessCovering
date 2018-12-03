package com.chesscover;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int boardM;
        int boardN;
        int numOfQueens;
        int numOfBishops;
        // get user input
        System.out.println("Enter the board dimensions (M*N):");
        System.out.print("M: ");
        Scanner inputScanner = new Scanner(System.in);
        boardM = inputScanner.nextInt();
        System.out.print("N: ");
        boardN = inputScanner.nextInt();
        System.out.println("Enter chesspiece data:");
        System.out.print("Number of Queens: ");
        numOfQueens = inputScanner.nextInt();
        System.out.print("Number of Bishops (0 for automatic): ");
        numOfBishops = inputScanner.nextInt();
        // create a CHessManager;
        ChessManager gameManager = new ChessManager(boardM,boardN,numOfQueens,numOfBishops);
        gameManager.FindSolutions();
    }
}
