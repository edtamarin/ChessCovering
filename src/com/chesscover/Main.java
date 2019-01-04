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
        boardM = getUserInput("Number of rows: ",1);
        boardN = getUserInput("Number of columns: ",1);
        System.out.println("Enter chesspiece data:");
        numOfQueens = getUserInput("Number of Queens: ",0);
        numOfBishops = getUserInput("Number of Bishops (-1 for automatic): ",-1);
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

    // get user input and sanitize it
    private static int getUserInput(String message,int minBoundary){
        System.out.print(message);
        Scanner inputScan = new Scanner(System.in);
        int result;
        do{
            while(!inputScan.hasNextInt()){
                System.out.println("Not a number! Please try again.");
                inputScan.next();
            }
            result = inputScan.nextInt();
            if (result<minBoundary) System.out.println("Please enter a value larger or equal than " + minBoundary);
        }while(result < minBoundary);
        return result;
    }
}
