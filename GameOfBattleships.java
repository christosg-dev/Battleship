/*
 *  Christos Gunopulos
 *  Morairis School 
 *  December 2024
 */


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

//package com.mycompany.gameofbattleships;


import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GameOfBattleships {
    public static int numRows = 10;
    public static int numRows2 = 10;
    public static int numCols = 10;
    public static int numCols2 = 10;
    public static int playerShips = 0;
    public static int numComputerShips = 0;
    int sum_all=0;
    int dir=0;
    
    private JFrame frame;
    private JPanel playerPanel, computerPanel;
    private JButton[][] playerButtons = new JButton[10][10];
    private JButton[][] computerButtons = new JButton[10][10];
    private static int[][] probButtons = new int[10][10];
    private static int[][] prefButtons = new int[10][10];
    private int playerShipsLeft = 5;
    private int computerShipsLeft = 5;
    private boolean[][] playerShipsBoard = new boolean[10][10];
    private boolean[][] computerShips = new boolean[10][10];
    private boolean gameInProgress = true;
    int playerPositionsRemaining=17, playerPositionsLeft=17;
    int computerPositionsRemaining=17;
    int center, left, right;
    int strategy;
    int computerSteps=0, playerSteps=0;
    int global_counter=0;
    
    public static String[][] grid = new String[numRows][numCols];   // this is the computer board
    public static String[][] grid2 = new String[numRows2][numCols2];
    
    public static int[][] missedGuesses = new int[numRows][numCols];
    public static int[][] missedPlayerGuesses = new int[numRows2][numCols2];


    public GameOfBattleships() {
        
        initializeGame();

        initializeGUI1();
        initializeGUI2();
        initializeProbArray();
        strategy = 1;
        System.out.println("\nComputer: ");
        placeComputerShipsRandomly(computerShips);  // Computer places ships randomly
        printComputerBoard(computerShips);

    }
    
    
    public static void main(String[] args) {
        
        System.out.println("Welcome to GameOfBattleships!");
        new GameOfBattleships();
    }

    private void initializeGUI1() {
        frame = new JFrame("Battleship Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        // Set layout to ensure player and computer panels are side-by-side
        frame.setLayout(new GridLayout(1, 2)); 

        // Initialize player panel
        playerPanel = new JPanel(new GridLayout(10, 10));
        playerPanel.setBorder(BorderFactory.createTitledBorder("Player Board")); // Adds title to panel
        
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                playerButtons[row][col] = new JButton();
                playerButtons[row][col].setBackground(Color.BLUE);
                playerButtons[row][col].setOpaque(true); 
                playerButtons[row][col].setBorderPainted(true); 
                playerButtons[row][col].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY)); // Add subtle border
                playerPanel.add(playerButtons[row][col]);

                // Add action listener for player ship placement
                playerButtons[row][col].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (playerPositionsRemaining > 0) { 
                            
                            String input = JOptionPane.showInputDialog(
                                "Enter ship position (startRow, startCol, endRow, endCol):"
                            );
                            
                            if (input != null && !input.isEmpty()) {
                                input = input.trim(); // Normalize input
                        
                                if (input.matches("\\d+,\\d+,\\d+,\\d+")) { // Validate format (e.g., "1,1")

                                    String[] parts = input.split(","); // Split input into row and column
                                    int startRow = Integer.parseInt(parts[0].trim()) - 1; 
                                    int startCol = Integer.parseInt(parts[1].trim()) - 1; 
                                    int endRow = Integer.parseInt(parts[2].trim()) - 1;
                                    int endCol = Integer.parseInt(parts[3].trim()) - 1;
                                    
                                    if (startRow >= 0 && startRow < 10 && startCol >= 0 && startCol < 10 && endRow >= 0 && endRow < 10 && endCol >= 0 && endCol < 10 && !playerShipsBoard[startRow][startCol]) {
                                    
                                        placeShip(startRow, startCol, endRow, endCol);
                                        JOptionPane.showMessageDialog(null, "Ship placed successfully at position " + input + "!");
                                    } else {
                                        JOptionPane.showMessageDialog(null, "That position is already occupied or out of range. Try another.", "Warning", JOptionPane.WARNING_MESSAGE);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Invalid position format. Please enter numbers between 1 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "You cannot place a ship here.", "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                });
            }
        }

        frame.add(playerPanel);

    }
    
    private void initializeGUI2() {

        // Initialize computer board
        computerPanel = new JPanel(new GridLayout(10, 10));
        computerPanel.setBorder(BorderFactory.createTitledBorder("Computer Board")); // Adds title to panel

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                computerButtons[row][col] = new JButton();
                computerButtons[row][col].setBackground(Color.GRAY);
                computerButtons[row][col].setOpaque(true); 
                computerButtons[row][col].setBorderPainted(true);
                computerButtons[row][col].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY)); // Add subtle border
                computerPanel.add(computerButtons[row][col]);
                
                int finalRow = row;
                int finalCol = col;

                // Add action listener for shooting at the computer's grid
                computerButtons[row][col].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (gameInProgress && computerPositionsRemaining > 0) {
                            
                            if ( (finalRow < 10) && (finalCol < 10) && (computerShips[finalRow][finalCol]) ) {
                                computerButtons[finalRow][finalCol].setBackground(Color.RED);
                                computerButtons[finalRow][finalCol].setOpaque(true);
                                computerButtons[finalRow][finalCol].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Keep grid-like appearance
                                computerShips[finalRow][finalCol] = false;
                                computerPositionsRemaining--;
                                playerSteps++;

                                if (computerPositionsRemaining == 0) {
                                    System.out.println("Computer steps  " + computerSteps + ",  Player steps:  " + playerSteps);
                                    JOptionPane.showMessageDialog(frame, "Player wins!");
                                    gameInProgress = false;
                                }

                            } else {
                                computerButtons[finalRow][finalCol].setBackground(Color.WHITE);
                                playerSteps++;
                            }
                            computerTurn();

                        }
                    }
                });
            }
        }

        frame.add(computerPanel);
        frame.setVisible(true);
    }

    int min_num(int a, int b, int c, int d)
    {
        int x=d;

        if (a<x) {
            x=a;
        }
        if (b<x) {
            x= b;
        }
        if (c<x) {
            x= c;
        }
        if (d<x) {
            x= d;
        }

        return x;
    }
    
    private void initializeProbArray() 
    {
        for (int i = 1; i <= probButtons.length; i++) {
            for (int j = 1; j <= probButtons.length; j++) {
                    probButtons[i-1][j-1] = min_num(i, j, 11-i, 11-j);
                    prefButtons[i-1][j-1] = 0;
            }
        }
                
        sum_all=0;
        for (int i = 1; i <= probButtons.length; i++) {
            for (int j = 1; j <= probButtons.length; j++) {
                    probButtons[i-1][j-1] = sum_all + probButtons[i-1][j-1];
                    sum_all = probButtons[i-1][j-1];
            }
        }
    }
       

    private void placeShip(int startRow, int startCol, int endRow, int endCol) 
    {
        if (startRow == endRow) {
            for (int i=startCol; i<=endCol; i++) {
                playerButtons[startRow][i].setBackground(Color.GREEN);
                playerButtons[startRow][i].setOpaque(true);
                playerButtons[startRow][i].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Keep grid-like appearance
                            
                playerShipsBoard[startRow][i] = true;
                playerPositionsRemaining--;
            }
        }
        else if (startCol == endCol) {
            for (int i=startRow; i<=endRow; i++) {
                playerButtons[i][startCol].setBackground(Color.GREEN);
                playerButtons[i][startCol].setOpaque(true);
                playerButtons[i][startCol].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Keep grid-like appearance
                            
                playerShipsBoard[i][startCol] = true;
                playerPositionsRemaining--;
            }
        }
    }
    
    public static void initializeGame() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                grid[i][j] = ".";
                missedGuesses[i][j] = 0;
            }
        }

        for (int i = 0; i < numRows2; i++) {
            for (int j = 0; j < numCols2; j++) {
                grid2[i][j] = ".";
                missedPlayerGuesses[i][j] = 0;
            }
        }
    }
    
        
    // Function to placeShip and check if it is within the bounds of the board
    private void placeShip(boolean[][] shipBoard, int shipLength) {
        Random random = new Random();
        boolean placed = false;
        int row, col;
        
        while (!placed) {
            
            // Random choosing row and column
            row = random.nextInt(10);  
            col = random.nextInt(10);
            
            // Randomly decide horizonta/vertical direction
            boolean horizontal = random.nextBoolean();  

            if (canPlaceShip(shipBoard, row, col, shipLength, horizontal)) {
                
                for (int i = 0; i < shipLength; i++) {
                    if (horizontal) {
                        shipBoard[row][col + i] = true;
                        int temp=col+i;

                    } else {
                        shipBoard[row + i][col] = true;
                        int temp=row+i;
                    }
                }
                placed = true;  // Ship is placed successfully
            }
        }
    }


    // Function that checks if a ship can be placed
    private boolean canPlaceShip(boolean[][] shipBoard, int row, int col, int shipLength, boolean horizontal) {
        // Check if ship placement is within board bounds and doesn't overlap with existing ships
        
        int checkRow, checkCol;
        
        for (int i = 0; i < shipLength; i++) {
            checkRow = horizontal ? row : row + i;
            checkCol = horizontal ? col + i : col;

            // Ensure within bounds and not overlapping another ship
            if (checkRow >= 10 || checkCol >= 10 || shipBoard[checkRow][checkCol]) {
                return false;  // Out of bounds or overlapping
            }
        }
        return true;  // Placement is valid
    }
    
    private void placeComputerShipsRandomly(boolean[][] shipBoard) {
        placeShip(shipBoard, 5);    
        placeShip(shipBoard, 4);
        placeShip(shipBoard, 3);
        placeShip(shipBoard, 3);
        placeShip(shipBoard, 2);
    }
    
    
    private void checkAdjacent2(int x, int y) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (newX >= 0 && newY >= 0 && newX < probButtons.length && newY < probButtons.length ) {
                System.out.println("Probable ship at (" + newX + ", " + newY + ")");
            }
        }
    }
    
    private void checkAdjacent(int x, int y) 
    {
        dir=1;
        if (x>1)
            prefButtons[x-1][y] = 1;
        if (x < 9)
            prefButtons[x+1][y]=1;
        if (y>1)
            prefButtons[x][y-1] = 1;
        if (y < 9)
            prefButtons[x][y+1]=1;
    }
    
    
    
    private void computerTurn() {
        Random random = new Random();
        int row=0,col=0, prevrow=0, prevcol=-2,hit=0, hit2=0, first_col=0, first_row=0, first_num=0, max=0;

        center = playerButtons.length/2;
        
        do {
            if (strategy == 0) {
                row = random.nextInt(10);
                col = random.nextInt(10);
            }
            else if (strategy == 1) {
                
                hit2 = random.nextInt(sum_all);
                
                System.out.println("hit2    " + hit2);
                max=hit2;
                outer: // Label for the outer loop
                for (int i=0; i<probButtons.length; i++)
                    for (int j=0;j< probButtons.length; j++)
                        if ( (probButtons[i][j] >= max) && (probButtons[i][j] != 11111) && (probButtons[i][j] != 33333))
                        {
                               if (first_num==0) {
                                    first_row = i;
                                    first_col=j;
                                    first_num++;
                                }
                                row = i;
                                col = j;
                                if (probButtons[i][j] > max)
                                    max = probButtons[i][j];
                                if ( global_counter < 10)
                                    break outer;
                        System.out.println("prob " + probButtons[row][col] + "  row " + row + " col " + col);
                        }
            }
        } while ( (!gameInProgress) && (probButtons[row][col] != 11111) && (probButtons[row][col] != 33333)); 
        

        global_counter++;
        if (max<300) {
            row=first_row;
            col=first_col;
        }
        
        if (playerShipsBoard[row][col]) {
            playerButtons[row][col].setBackground(Color.RED);
            playerShipsBoard[row][col] = false;
            hit=1;
            probButtons[row][col]=11111;
            checkAdjacent(row,col);
            playerPositionsLeft--;
            System.out.println("HIT .... remainingPlayerPositions  " + playerPositionsLeft);

            computerSteps++;
            if (playerPositionsLeft == 0) { 
                System.out.println("Computer steps  " + computerSteps + "Player steps:  " + playerSteps);
                JOptionPane.showMessageDialog(frame, "Computer wins!");
                gameInProgress = false;
            }
            
            System.out.println("row: " + row + "  col: " + col);
            if ( (row>0) && (probButtons[row-1][col]!= 11111) && (probButtons[row-1][col]!= 33333))
                probButtons[row-1][col]+= 300;
            if ( (row < (probButtons.length-1)) && (probButtons[row+1][col]!= 11111) && (probButtons[row+1][col]!= 33333))
                probButtons[row+1][col]+= 300;
            if ( (col>0) && (probButtons[row][col-1]!= 11111) && (probButtons[row][col-1]!= 33333))
                probButtons[row][col-1]+= 300;
            if ( (col < (probButtons.length-1)) && (probButtons[row][col+1]!= 11111) && (probButtons[row][col+1]!= 33333))
                probButtons[row][col+1]+= 300;

        } else {
            playerButtons[row][col].setBackground(Color.WHITE);
            computerSteps++;

            probButtons[row][col]=33333;
            
            hit=0;
        }
    }

    
    // Checks if a move is valid
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < 10 && col >= 0 && col < 10;
    }
    
    public static void gameOver() {
        System.out.println("Game Over!");
        if (playerShips > 0) {
            System.out.println("You won the battle!");
        } else {
            System.out.println("You lost the battle!");
        }
    }

    
    private void printComputerBoard(boolean[][] shipBoard) {
        
        for (int i = 0; i < shipBoard.length; i++) {
            for (int j = 0; j < shipBoard[i].length; j++) {
                //System.out.print(shipBoard[i][j] + " ");
                System.out.print((shipBoard[i][j] ? 1 : 0) + " ");
            }
            System.out.println(); // Newline after each row
        }
        
    }
}






