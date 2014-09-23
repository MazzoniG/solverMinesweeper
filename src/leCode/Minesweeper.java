package lecode;

import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.Timer;

public class Minesweeper {

    private static int width = 693;
    private static int height = 475;
    private static int mines = 99;
    private static int gameTime = 0;
    private static int numberOfSquaresX = 32;
    private static int numberOfSquaresY = 18;
    private static int solX, solY;
    private static int unflaggedMinesRemaining = mines;
    // mine / empty / number...
    private static int[][] squareState = new int[numberOfSquaresX][numberOfSquaresY];
    // still covered by top layer? t/f
    private static boolean[][] coveredState = new boolean[numberOfSquaresX][numberOfSquaresY];
    private static boolean[][] flaggedState = new boolean[numberOfSquaresX][numberOfSquaresY];
    private static Image[] images = new Image[13];
    static MyFrame F = new MyFrame();
    private static ActionListener taskPerformer;
    static Timer timer = new Timer(1000, null);
    private static boolean isTimerRunning = false;
    private static Button restartButton;
    private static Font font = new Font("Arial", Font.PLAIN, 14);

    private static Image imageCovered = null;
    private static Image image1 = null;
    private static Image image2 = null;
    private static Image image3 = null;
    private static Image image4 = null;
    private static Image image5 = null;
    private static Image image6 = null;
    private static Image image7 = null;
    private static Image image8 = null;
    private static Image imageFlag = null;
    private static Image imageMine = null;
    private static Image imageMineClicked = null;
    private static Image imageEmptySquare = null;
    private static Image imageClock = null;
    private static Image imageSolution = null;

    private static int Matrix[][] = new int[numberOfSquaresX][numberOfSquaresY];
    private static int MatrixSolver[][] = new int[numberOfSquaresX][numberOfSquaresY];
    private static boolean MatrixSolverBool[][] = new boolean[numberOfSquaresX][numberOfSquaresY];

    public static void main(String args[]) {
        MyFrame meinFrame;
        meinFrame = new MyFrame();
        meinFrame.loadImages();
        drawCanvas();
        CanvasEvents();
        resetWholeGrid();
        placeMines();
        placeAllSquares();
        addButton();
        MouseEvents();
        startTimer();
    }

    private static void MouseEvents() {
        F.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                // LMB was clicked
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (getIsTimerRunning() == false) {
                        startTimer();
                        setIsTimerRunning(true);
                    }
                    squareClicked(e.getX(), e.getY(), "LMB");
                } // RMB was clicked
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    squareClicked(e.getX(), e.getY(), "RMB");
                } // MMB was clicked
                else if (e.getButton() == MouseEvent.BUTTON2) {
                    squareClicked(e.getX(), e.getY(), "MMB");
                }
            }

        });
    }

    private static void repaintTimerEverySecond() {
        F.repaint(width - 50, 20, 40, 30);

    }

    private static void startTimer() {
        timer.start();
        timer.addActionListener(taskPerformer);
        taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // System.out.println(gameTime);
                gameTime++;
                repaintTimerEverySecond();
            }
        };
    }

    private static void setIsTimerRunning(boolean isRunning) {
        isTimerRunning = isRunning;
    }

    private static boolean getIsTimerRunning() {
        return isTimerRunning;
    }

    private static void squareClicked(int mouseX, int mouseY, String mouseButton) {
        // if clicked on grid
        if (getGridOffsetX() < mouseX
                && mouseX < (getGridOffsetX() + getSquareSize()
                * numberOfSquaresX)
                && getGridOffsetY() < mouseY
                && mouseY < (getGridOffsetY() + getSquareSize()
                * numberOfSquaresY)) {
            int x = (mouseX - getGridOffsetX()) / getSquareSize();
            int y = (mouseY - getGridOffsetY()) / getSquareSize();
            uncover(x, y, mouseButton);
        }
    }

    private static void uncover(int squareX, int squareY, String mouseButton) {
        // LMB on a square
        if (mouseButton == "LMB" && coveredState[squareX][squareY]
                && !flaggedState[squareX][squareY]) {

            // clicked on a mine
            if (squareState[squareX][squareY] == 10) {
                gameOver();
                squareState[squareX][squareY] = 11;
                F.repaint();
            } // empty square
            else if (squareState[squareX][squareY] == 12) {
                uncoverEmptySquaresAround(squareX, squareY);
                F.repaint();
            }
            coveredState[squareX][squareY] = false;
            F.repaint(getGridOffsetX() + squareX * getSquareSize(),
                    getGridOffsetY() + squareY * getSquareSize(),
                    getSquareSize(), getSquareSize());
            checkIfGameWon();
        } // RMB on a square
        else if (mouseButton == "RMB" && coveredState[squareX][squareY]) {
            flaggedState[squareX][squareY] = !flaggedState[squareX][squareY];
            mineUnflagged(squareX, squareY, flaggedState[squareX][squareY]);
            F.repaint(getGridOffsetX() + squareX * getSquareSize(),
                    getGridOffsetY() + squareY * getSquareSize(),
                    getSquareSize(), getSquareSize());
        } // MMB on a square
        else if (mouseButton == "MMB" & !coveredState[squareX][squareY]
                && squareState[squareX][squareY] >= 1
                && squareState[squareX][squareY] <= 8) {
            checkIfAbleToUncoverMinesAround(squareX, squareY);
        }
        solver(squareX, squareY);
    }

    // this method is doing the calculation for the string displaying: remaining
    // number of mines - number of flags
    private static void mineUnflagged(int x, int y, boolean isFlagged) {
        if (isFlagged) {
            unflaggedMinesRemaining--;
        } else if (!isFlagged) {
            unflaggedMinesRemaining++;
        }
        // this drawing-method is a workaround for a repaint()-bug. Can't
        // specify the correct area.. Also placed in paint().
        Graphics g = F.getGraphics().create();
        g.setFont(font);
        g.clearRect(width - 115, 25, 25, 20);
        g.drawString("" + unflaggedMinesRemaining, width - 115, 50);
    }

    private static void checkIfGameWon() {
        for (int i = 0; i < numberOfSquaresX; i++) {
            for (int j = 0; j < numberOfSquaresY; j++) {
                if (coveredState[i][j] && squareState[i][j] != 10) {
                    return;
                }
            }
        }
        gameOver();
        F.repaint();
    }

    private static void gameOver() {
        timer.stop();
        // System.out.println(gameTime);
        setWholeGridCovered(false);
    }

    // Event after a middle-mouse-button click
    private static void checkIfAbleToUncoverMinesAround(int x, int y) {
        int numberOfAdjacentFlags = 0;
        int j;
        for (int i = -1; i < 2; i++) {
            for (j = -1; j < 1; j++) {
                if (x + i < numberOfSquaresX && x + i >= 0
                        && y + j < numberOfSquaresY && y + j >= 0
                        && flaggedState[x + i][y + j] == true) {
                    numberOfAdjacentFlags++;
                }
            }
            if (x + i < numberOfSquaresX && x + i >= 0
                    && y + j < numberOfSquaresY && y + j >= 0
                    && flaggedState[x + i][y + j] == true) {
                numberOfAdjacentFlags++;
            }
        }

        uncoverAdjacentFieldsAfterMiddleclick(x, y, numberOfAdjacentFlags);
    }

    private static void uncoverAdjacentFieldsAfterMiddleclick(int x, int y,
            int numberOfAdjacentFlags) {
        // checking if there is the same number of adjacent flags as there are
        // mines --> if not: quit this method
        if (numberOfAdjacentFlags != squareState[x][y]) {
            return;
        }

        int j;
        for (int i = -1; i < 2; i++) {
            for (j = -1; j < 1; j++) {
                // Simulates a left mouseclick. The "if" is just for the first
                // and last squares (X and Y) so there are no errors.
                if (x + i < numberOfSquaresX && x + i >= 0
                        && y + j < numberOfSquaresY && y + j >= 0) {
                    uncover(x + i, y + j, "LMB");
                }
            }
            // Simulates a left mouseclick. The "if" is just for the first and
            // last squares (X and Y) so there are no errors.
            if (x + i < numberOfSquaresX && x + i >= 0
                    && y + j < numberOfSquaresY && y + j >= 0) {
                uncover(x + i, y + j, "LMB");
            }
        }

    }

    // This is the method that uncovers all the plain squares around a plain
    // square that has just been pressed
    private static void uncoverEmptySquaresAround(int x, int y) {
        // Has to check cross first: then has to uncover those plain squares
        // Then check all 8 adjacent squares for every single plain square and
        // uncover them fucking numbers
        int adjacentX;
        int adjacentY;
        boolean uncoverRight = false;
        boolean uncoverLeft = false;
        boolean uncoverAbove = false;
        boolean uncoverBelow = false;

        adjacentX = x + 1;
        adjacentY = y;
        if ((adjacentX) >= 0 && (adjacentX) < numberOfSquaresX
                && (adjacentY) >= 0 && (adjacentY) < numberOfSquaresY
                && coveredState[adjacentX][adjacentY]
                && !flaggedState[adjacentX][adjacentY]) {
            if (squareState[adjacentX][adjacentY] == 12) {
                coveredState[adjacentX][adjacentY] = false;
                uncoverRight = true;
            }
        }

        adjacentX = x - 1;
        adjacentY = y;
        if ((adjacentX) >= 0 && (adjacentX) < numberOfSquaresX
                && (adjacentY) >= 0 && (adjacentY) < numberOfSquaresY
                && coveredState[adjacentX][adjacentY]
                && !flaggedState[adjacentX][adjacentY]) {
            if (squareState[adjacentX][adjacentY] == 12) {
                coveredState[adjacentX][adjacentY] = false;
                uncoverLeft = true;
            }
        }

        adjacentX = x;
        adjacentY = y + 1;
        if ((adjacentY) >= 0 && (adjacentY) < numberOfSquaresY
                && coveredState[adjacentX][adjacentY]
                && !flaggedState[adjacentX][adjacentY]) {
            if (squareState[adjacentX][adjacentY] == 12) {
                coveredState[adjacentX][adjacentY] = false;
                uncoverAbove = true;
            }
        }

        adjacentX = x;
        adjacentY = y - 1;
        if ((adjacentY) >= 0 && (adjacentY) < numberOfSquaresY
                && coveredState[adjacentX][adjacentY]
                && !flaggedState[adjacentX][adjacentY]) {
            if (squareState[adjacentX][adjacentY] == 12) {
                coveredState[adjacentX][adjacentY] = false;
                uncoverBelow = true;
            }
        }

        if (uncoverBelow) {
            uncoverEmptySquaresAround(x, y - 1);
        }
        if (uncoverAbove) {
            uncoverEmptySquaresAround(x, y + 1);
        }
        if (uncoverLeft) {
            uncoverEmptySquaresAround(x - 1, y);
        }
        if (uncoverRight) {
            uncoverEmptySquaresAround(x + 1, y);
        }
        uncoverAbove = false;
        uncoverBelow = false;
        uncoverLeft = false;
        uncoverRight = false;

        int j;
        for (int i = -1; i < 2; i++) {
            for (j = -1; j < 1; j++) {
                if ((x + i) >= 0 && (x + i) < numberOfSquaresX && (y + j) >= 0
                        && (y + j) < numberOfSquaresY
                        && coveredState[x + i][y + j]
                        && !flaggedState[x + i][y + j]
                        && squareState[x + i][y + j] != 12) {
                    coveredState[x + i][y + j] = false;
                }
            }
            if ((x + i) >= 0 && (x + i) < numberOfSquaresX && (y + j) >= 0
                    && (y + j) < numberOfSquaresY && coveredState[x + i][y + j]
                    && !flaggedState[x + i][y + j]
                    && squareState[x + i][y + j] != 12) {
                coveredState[x + i][y + j] = false;
            }
        }

    }

    private static void drawCanvas() {
        F.setTitle("Minesweeper");
        F.setSize(width, height);
        F.setLayout(null);
        F.setVisible(true);
    }

    // actions for closing and resizing
    private static void CanvasEvents() {

        // closing window
        F.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        // resizing window
        F.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                // changed height
                if (F.getSize().height != height) {
                    // System.out.println(F.getSize().height + " " + height);
                    F.setSize(F.getSize().width, height);
                }

                // changed width
                if (F.getSize().width != width) {
                    width = F.getSize().width;
                    height = F.getSize().height;
                    while (getGridOffsetY() < 55) {
                        height += 2;
                    }
                    while (getGridOffsetY() > 58) {
                        height -= 2;
                    }
                    F.setSize(F.getSize().width, height);
                }

                F.repaint();
                relocateButtonWithFrameSize(width, height);
            }
        });
    }

    private static void addButton() {
        restartButton = new Button("Restart");
        restartButton.setLocation(width / 2, 25);
        restartButton.setSize(60, 25);
        restartButton.setVisible(true);
        ActionListener buttonEvent = new ActionListener() {

            @Override
            // Restart-Button was pressed
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                timer.removeActionListener(taskPerformer);
                gameTime = 0;
                unflaggedMinesRemaining = mines;
                timer.restart();
                resetWholeGrid();
                placeMines();
                placeAllSquares();
                F.repaint();

            }
        };
        restartButton.addActionListener(buttonEvent);
        F.add(restartButton);
    }

    private static void relocateButtonWithFrameSize(int width, int height) {
        restartButton.setLocation(width / 2, 55);
    }

    private static int getSquareSize() {
        int squareWidth = (width - 15) / numberOfSquaresX;
        return squareWidth;
    }

    private static int getGridOffsetX() {
        // the distance of the grid to the left side of the window so it appears
        // centered
        int gridOffset = (width - (getSquareSize() * numberOfSquaresX)) / 2;
        return gridOffset;
    }

    private static int getGridOffsetY() {
        // the distance of the grid to the top of the window so it is at the
        // bottom
        int gridOffset = height - getSquareSize() * numberOfSquaresY - 10;
        return gridOffset;
    }

    private static void resetWholeGrid() {
        int xIncr;
        for (int yIncr = 0; yIncr < numberOfSquaresY; yIncr++) {
            for (xIncr = 0; xIncr < numberOfSquaresX - 1; xIncr++) {
                coveredState[xIncr][yIncr] = true;
                squareState[xIncr][yIncr] = 0;
                flaggedState[xIncr][yIncr] = false;
            }
            coveredState[xIncr][yIncr] = true;
            squareState[xIncr][yIncr] = 0;
            flaggedState[xIncr][yIncr] = false;
        }
    }

    private static void setWholeGridCovered(boolean covered) {
        int xIncr;
        for (int yIncr = 0; yIncr < numberOfSquaresY; yIncr++) {
            for (xIncr = 0; xIncr < numberOfSquaresX - 1; xIncr++) {
                coveredState[xIncr][yIncr] = covered;
            }
            coveredState[xIncr][yIncr] = covered;
        }
    }

    private static void placeMines() {
        for (int i = 0; i < mines; i++) {
            double randomX = Math.random() * numberOfSquaresX;
            double randomY = Math.random() * numberOfSquaresY;
            if (squareState[(int) randomX][(int) randomY] == 10) {
                // if there is a mine on random square
                i--;
            } else {
                // if there is no mine on the random square
                squareState[(int) randomX][(int) randomY] = 10;
            }
        }
    }

    public static void solver(int x, int y) {

        for (int i = 0; i < squareState[i].length; i++) {
            for (int j = 0; j < squareState.length; j++) {
                if (!coveredState[j][i]) {
                    Matrix[j][i] = squareState[j][i];
                }
            }
        }

        try {
            if (Matrix[x - 1][y - 1] >= 1 && Matrix[x - 1][y - 1] <= 8) {
                solverInception(x - 1, y - 1);
            } else if (Matrix[x - 1][y - 1] == 0) {
                MatrixSolver[x - 1][y - 1]++;
            }

            if (Matrix[x][y - 1] >= 1 && Matrix[x][y - 1] <= 8) {
                solverInception(x, y - 1);
            } else if (Matrix[x][y - 1] == 0) {
                MatrixSolver[x][y - 1]++;
            }

            if (Matrix[x + 1][y - 1] >= 1 && Matrix[x + 1][y - 1] <= 8) {
                solverInception(x + 1, y - 1);
            } else if (Matrix[x + 1][y - 1] == 0) {
                MatrixSolver[x + 1][y - 1]++;
            }

            if (Matrix[x - 1][y] >= 1 && Matrix[x - 1][y] <= 8) {
                solverInception(x - 1, y);
            } else if (Matrix[x - 1][y] == 0) {
                MatrixSolver[x - 1][y]++;
            }

            if (Matrix[x][y] >= 1 && Matrix[x][y] <= 8) {
                solverInception(x, y);
            } else if (Matrix[x][y] == 0) {
                MatrixSolver[x][y]++;
            }

            if (Matrix[x + 1][y] >= 1 && Matrix[x + 1][y] <= 8) {
                solverInception(x + 1, y);
            } else if (Matrix[x + 1][y] == 0) {
                MatrixSolver[x + 1][y]++;

            }

            if (Matrix[x - 1][y + 1] >= 1 && Matrix[x - 1][y + 1] <= 8) {
                solverInception(x - 1, y + 1);
            } else if (Matrix[x - 1][y + 1] == 0) {
                MatrixSolver[x - 1][y + 1]++;
            }

            if (Matrix[x][y + 1] >= 1 && Matrix[x][y + 1] <= 8) {
                solverInception(x, y + 1);
            } else if (Matrix[x][y + 1] == 0) {
                MatrixSolver[x][y + 1]++;
            }

            if (Matrix[x + 1][y + 1] >= 1 && Matrix[x + 1][y + 1] <= 8) {
                solverInception(x + 1, y + 1);
            } else if (Matrix[x + 1][y + 1] == 0) {
                MatrixSolver[x + 1][y + 1]++;
            }

        } catch (Exception ex) {

        }

        for (int i = 0; i < squareState[i].length; i++) {
            for (int j = 0; j < squareState.length; j++) {
                if (!coveredState[j][i]) {
                    if (squareState[j][i] == 12) {
                        MatrixSolver[j][i] = 0;
                    }
                }
            }
        }

        int minValueXY = Integer.MAX_VALUE;
        int X = 0, Y = 0;
        for (int i = 0; i < MatrixSolver[i].length; i++) {
            for (int j = 0; j < MatrixSolver.length; j++) {
                if ((MatrixSolver[j][i] <= minValueXY && MatrixSolver[j][i] != 0) && coveredState[j][i] == true && MatrixSolverBool[j][i] == false) {
                    minValueXY = MatrixSolver[j][i];
                    X = i;
                    Y = j;
                }
            }
        }

        MatrixSolverBool[Y][X] = true;

        for (int i = 0; i < MatrixSolver[i].length; i++) {
            for (int j = 0; j < MatrixSolver.length; j++) {
                System.out.printf(" " + MatrixSolver[j][i] + " ");
            }
            System.out.println("");
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(X + "," + Y);
        solX = Y;
        solY = X;
        F.repaint();
    }

    public static void solverInception(int x, int y) {

        try {
            if (Matrix[x - 1][y - 1] >= 1 && Matrix[x - 1][y - 1] <= 8) {
                //solverInception(x - 1, y - 1);
            } else if (Matrix[x - 1][y - 1] == 0) {
                MatrixSolver[x - 1][y - 1]++;
            }

            if (Matrix[x][y - 1] >= 1 && Matrix[x][y - 1] <= 8) {
                //solverInception(x, y - 1);
            } else if (Matrix[x][y - 1] == 0) {
                MatrixSolver[x][y - 1]++;
            }

            if (Matrix[x + 1][y - 1] >= 1 && Matrix[x + 1][y - 1] <= 8) {
                //solverInception(x + 1, y - 1);
            } else if (Matrix[x + 1][y - 1] == 0) {
                MatrixSolver[x + 1][y - 1]++;
            }

            if (Matrix[x - 1][y] >= 1 && Matrix[x - 1][y] <= 8) {
                //solverInception(x - 1, y);
            } else if (Matrix[x - 1][y] == 0) {
                MatrixSolver[x - 1][y]++;
            }

            if (Matrix[x][y] >= 1 && Matrix[x][y] <= 8) {
                //solverInception(x, y);
            } else if (Matrix[x][y] == 0) {
                MatrixSolver[x][y]++;
            }

            if (Matrix[x + 1][y] >= 1 && Matrix[x + 1][y] <= 8) {
                //solverInception(x + 1, y);
            } else if (Matrix[x + 1][y] == 0) {
                MatrixSolver[x + 1][y]++;

            }

            if (Matrix[x - 1][y + 1] >= 1 && Matrix[x - 1][y + 1] <= 8) {
                //solverInception(x - 1, y + 1);
            } else if (Matrix[x - 1][y + 1] == 0) {
                MatrixSolver[x - 1][y + 1]++;
            }

            if (Matrix[x][y + 1] >= 1 && Matrix[x][y + 1] <= 8) {
                //solverInception(x, y + 1);
            } else if (Matrix[x][y + 1] == 0) {
                MatrixSolver[x][y + 1]++;
            }

            if (Matrix[x + 1][y + 1] >= 1 && Matrix[x + 1][y + 1] <= 8) {
                //solverInception(x + 1, y + 1);
            } else if (Matrix[x + 1][y + 1] == 0) {
                MatrixSolver[x + 1][y + 1]++;
            }

        } catch (Exception ex) {
        }

    }

    private static void placeAllSquares() {
        int numberOfAdjacentMines = 0;
        int xIncr;

        for (int yIncr = 0; yIncr < numberOfSquaresY; yIncr++) {
            for (xIncr = 0; xIncr < numberOfSquaresX; xIncr++) {
                for (int i = 0; i < 3; i++) {

                    for (int j = 0; j < 3; j++) {
                        // so the clicked outer squares don't search for
                        // non-existing arrays
                        if ((xIncr - 1 + i) >= 0
                                && (xIncr + i) <= numberOfSquaresX
                                && (yIncr - 1 + j) >= 0
                                && (yIncr + j) <= numberOfSquaresY) {
                            if (squareState[xIncr - 1 + i][yIncr - 1 + j] == 10) {
                                numberOfAdjacentMines++;
                            }
                        }
                    }

                }
                // for squares with adjascent mines, the number are placed
                if (squareState[xIncr][yIncr] < 9) {
                    squareState[xIncr][yIncr] = numberOfAdjacentMines;
                    if (numberOfAdjacentMines == 0) {
                        squareState[xIncr][yIncr] = 12;
                    }
                } else {
                }
                numberOfAdjacentMines = 0;
            }
        }
    }

    private static class MyFrame extends Frame {

        private static final long serialVersionUID = 1L;

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            g.setFont(font);
            g.drawString("" + gameTime, width - 50, 50);
            g.clearRect(width - 100, 25, 25, 20);
            g.drawString("" + unflaggedMinesRemaining, width - 115, 49);
            g.drawImage(imageFlag, width - 130, 38, width - 115, 52, 0, 0, 64,
                    64, null);
            g.drawImage(imageClock, width - 70, 38, width - 55, 52, 0, 0, 64,
                    64, null);

            // all Squares covered, initial state --> state 0
            // numbers (adjacent to mines) --> state 1 - 8
            // flag --> state 9
            // mine --> state 10
            // mine clicked --> state 11
            // clicked and empty square --> state 12
            int xIncr;
            for (int yIncr = 0; yIncr < numberOfSquaresY; yIncr++) {
                for (xIncr = 0; xIncr < numberOfSquaresX - 1; xIncr++) {

                    if (coveredState[xIncr][yIncr]) {
                        if (flaggedState[xIncr][yIncr]) {
                            g.drawImage(images[9], xIncr * getSquareSize()
                                    + getGridOffsetX(), yIncr * getSquareSize()
                                    + getGridOffsetY(), xIncr * getSquareSize()
                                    + getSquareSize() + getGridOffsetX(), yIncr
                                    * getSquareSize() + getSquareSize()
                                    + getGridOffsetY(), 0, 0, 64, 64, null);
                        } else {
                            g.drawImage(images[0], xIncr * getSquareSize()
                                    + getGridOffsetX(), yIncr * getSquareSize()
                                    + getGridOffsetY(), xIncr * getSquareSize()
                                    + getSquareSize() + getGridOffsetX(), yIncr
                                    * getSquareSize() + getSquareSize()
                                    + getGridOffsetY(), 0, 0, 64, 64, null);
                        }
                    } else {
                        g.drawImage(images[squareState[xIncr][yIncr]], xIncr
                                * getSquareSize() + getGridOffsetX(), yIncr
                                * getSquareSize() + getGridOffsetY(), xIncr
                                * getSquareSize() + getSquareSize()
                                + getGridOffsetX(), yIncr * getSquareSize()
                                + getSquareSize() + getGridOffsetY(), 0, 0, 64,
                                64, null);
                    }

                }

                if (coveredState[xIncr][yIncr]) {
                    if (flaggedState[xIncr][yIncr]) {
                        g.drawImage(images[9], xIncr * getSquareSize()
                                + getGridOffsetX(), yIncr * getSquareSize()
                                + getGridOffsetY(), xIncr * getSquareSize()
                                + getSquareSize() + getGridOffsetX(), yIncr
                                * getSquareSize() + getSquareSize()
                                + getGridOffsetY(), 0, 0, 64, 64, null);
                    } else {
                        g.drawImage(images[0], xIncr * getSquareSize()
                                + getGridOffsetX(), yIncr * getSquareSize()
                                + getGridOffsetY(), xIncr * getSquareSize()
                                + getSquareSize() + getGridOffsetX(), yIncr
                                * getSquareSize() + getSquareSize()
                                + getGridOffsetY(), 0, 0, 64, 64, null);
                    }
                } else {
                    g.drawImage(images[squareState[xIncr][yIncr]], xIncr
                            * getSquareSize() + getGridOffsetX(), yIncr
                            * getSquareSize() + getGridOffsetY(), xIncr
                            * getSquareSize() + getSquareSize()
                            + getGridOffsetX(), yIncr * getSquareSize()
                            + getSquareSize() + getGridOffsetY(), 0, 0, 64, 64,
                            null);
                }
            }

            g.drawImage(imageSolution, solX
                    * getSquareSize() + getGridOffsetX(), solY
                    * getSquareSize() + getGridOffsetY(), solX
                    * getSquareSize() + getSquareSize()
                    + getGridOffsetX(), solY * getSquareSize()
                    + getSquareSize() + getGridOffsetY(), 0, 0, 64, 64,
                    null);

            g.drawImage(imageSolution, (numberOfSquaresX / 2 - 1)
                    * getSquareSize() + getGridOffsetX(), (numberOfSquaresY / 2 - 1)
                    * getSquareSize() + getGridOffsetY(), (numberOfSquaresX / 2 - 1)
                    * getSquareSize() + getSquareSize()
                    + getGridOffsetX(), (numberOfSquaresY / 2 - 1) * getSquareSize()
                    + getSquareSize() + getGridOffsetY(), 0, 0, 64, 64,
                    null);

        }

        private void loadImages() {
            try {
                imageCovered = ImageIO.read(getClass().getClassLoader()
                        .getResource("leCode/covered.png"));
                image1 = ImageIO.read(getClass().getClassLoader().getResource(
                        "leCode/1.jpg"));
                image2 = ImageIO.read(getClass().getClassLoader().getResource(
                        "leCode/2.jpg"));
                image3 = ImageIO.read(getClass().getClassLoader().getResource(
                        "leCode/3.jpg"));
                image4 = ImageIO.read(getClass().getClassLoader().getResource(
                        "leCode/4.jpg"));
                image5 = ImageIO.read(getClass().getClassLoader().getResource(
                        "leCode/5.jpg"));
                image6 = ImageIO.read(getClass().getClassLoader().getResource(
                        "leCode/6.jpg"));
                image7 = ImageIO.read(getClass().getClassLoader().getResource(
                        "leCode/7.jpg"));
                image8 = ImageIO.read(getClass().getClassLoader().getResource(
                        "leCode/8.jpg"));
                imageFlag = ImageIO.read(getClass().getClassLoader()
                        .getResource("leCode/flag.jpg"));
                imageMine = ImageIO.read(getClass().getClassLoader()
                        .getResource("leCode/mine.png"));
                imageMineClicked = ImageIO.read(getClass().getClassLoader()
                        .getResource("leCode/mine_activated.png"));
                imageEmptySquare = ImageIO.read(getClass().getClassLoader()
                        .getResource("leCode/empty_pressed.jpg"));
                imageClock = ImageIO.read(getClass().getClassLoader()
                        .getResource("leCode/clock.jpg"));
                imageSolution = ImageIO.read(getClass().getClassLoader()
                        .getResource("leCode/click.png"));

            } catch (IOException ex) {
                System.out.println("Image is null");
            }
            images[0] = imageCovered;
            images[1] = image1;
            images[2] = image2;
            images[3] = image3;
            images[4] = image4;
            images[5] = image5;
            images[6] = image6;
            images[7] = image7;
            images[8] = image8;
            images[9] = imageFlag;
            images[10] = imageMine;
            images[11] = imageMineClicked;
            images[12] = imageEmptySquare;

        }
    }
}
