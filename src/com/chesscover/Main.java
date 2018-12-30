package com.chesscover;

import java.util.Scanner;

public class Main {

    static long startTime;
    public static void main(String[] args) {
        int boardM;
        int boardN;
        int numOfQueens;
        int numOfBishops;
        // get user input
        System.out.println();
        System.out.println("Enter the board dimensions (M*N):");
        System.out.print("Number of rows: ");
        Scanner inputScanner = new Scanner(System.in);
        boardM = inputScanner.nextInt();
        System.out.print("Number of columns: ");
        boardN = inputScanner.nextInt();
        System.out.println("Enter chesspiece data:");
        System.out.print("Number of Queens: ");
        numOfQueens = inputScanner.nextInt();
        System.out.print("Number of Bishops (-1 for automatic): ");
        numOfBishops = inputScanner.nextInt();
        System.out.println("---------------");
        System.out.println("Search started.");
        // log starting time
        startTime = System.nanoTime();
        // run the algorithm
        ChessManager gameManager = new ChessManager(numOfQueens,numOfBishops,boardM,boardN);
        gameManager.findSolutions();
        // log end time
        long endTime = System.nanoTime();
        // print time statistics
        System.out.println("---------------");
        System.out.println("Done! Finished in " + (endTime-startTime)/1000000 + " ms.");
    }
}
