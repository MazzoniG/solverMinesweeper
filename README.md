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
