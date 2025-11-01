package com.example.my_project.Service;

public class SimplexSolverManual {

    private double[][] tableau;         // טבלת הסימפלקס
    private int numConstraints;        // מספר אילוצים
    private int numVariables;          // מספר משתני החלטה
    private int rows, cols;            // שורות ועמודות בטבלה
    private static final double EPS = 1e-8; //אם ההפרש בין המס קטן הם נחשבים כשווים

    public SimplexSolverManual(double[][] A, double[] b, double[] c) {
        numConstraints = b.length;
        numVariables = c.length;
        rows = numConstraints + 1;
        cols = numVariables + numConstraints + 1;//בשביל המשתני עזר
        tableau = new double[rows][cols];

        // מילוי האילוצים
        for (int i = 0; i < numConstraints; i++) {
            for (int j = 0; j < numVariables; j++) {
                tableau[i][j] = A[i][j];//A מטריצת מקדמים של האילוצים
            }
            tableau[i][numVariables + i] = 1.0;       // משתנה עזר
            tableau[i][cols - 1] = b[i];              // אגף ימין (גבול)
        }

        // פונקציית המטרה בעיית מינימום
        for (int j = 0; j < numVariables; j++) {
            tableau[rows - 1][j] = c[j];//מקדמים של פונקציית המטרה
        }
    }

    // פתרון הבעיה בעזרת סימפלקס – מחזיר וקטור של כמויות אופטימליות
    public double[] solve() {
        while (true) {
            int pivotCol = findPivotColumn();
            if (pivotCol == -1) break;

            int pivotRow = findPivotRow(pivotCol);
            if (pivotRow == -1) {
                System.err.println("בעיה בלתי חסומה (Unbounded).");
                printTableau();
                return null;
            }

            pivot(pivotRow, pivotCol);
        }

        // חילוץ פתרון לוקחים את כל מי שבבסיס 
        double[] solution = new double[numVariables];
        for (int j = 0; j < numVariables; j++) {
            int basicRow = findBasicRow(j);
            solution[j] = (basicRow != -1) ? tableau[basicRow][cols - 1] : 0.0;
        }
        return solution;
    }

    // מוצא עמודת ציר – הערך החיובי הגדול ביותר בפונקציית המטרה (כי זו בעיית מינימום)
    private int findPivotColumn() {
        int pivotCol = -1;
        double maxCoeff = EPS;

        for (int j = 0; j < cols - 1; j++) {
            if (tableau[rows - 1][j] > maxCoeff) {
                maxCoeff = tableau[rows - 1][j];
                pivotCol = j;
            }
        }
        return pivotCol;
    }

    // מוצא שורת ציר לפי יחס מינימלי
    private int findPivotRow(int col) {
        int pivotRow = -1;
        double minRatio = Double.MAX_VALUE;//הערך המקסימלי שמספר מסוג דאבל יכול לקבל

        for (int i = 0; i < rows - 1; i++) {
            if (tableau[i][col] > EPS) {//אם הערך חיובי (ESP מס קטן מאוד)
                double ratio = tableau[i][cols - 1] / tableau[i][col];//יחס
                if (ratio < minRatio) {
                    minRatio = ratio;
                    pivotRow = i;
                }
            }
        }
        return pivotRow;
    }

    // מבצע פיבוט – הפיכת ערך הציר ל־1 וכל השאר ל־0 באותה עמודה
    private void pivot(int row, int col) {
        double pivotVal = tableau[row][col];
        for (int j = 0; j < cols; j++) {
            tableau[row][j] /= pivotVal;
        }

        for (int i = 0; i < rows; i++) {
            if (i != row) {
                double factor = tableau[i][col];
                for (int j = 0; j < cols; j++) {
                    tableau[i][j] -= factor * tableau[row][j];
                }
            }
        }
    }

    // בודק אם משתנה הוא משתנה בסיסי ומחזיר את השורה שהוא מופיע בה
    private int findBasicRow(int col) {
        int rowFound = -1;
        for (int i = 0; i < rows - 1; i++) {
            if (Math.abs(tableau[i][col] - 1.0) < EPS) {
                boolean isBasic = true;
                for (int k = 0; k < rows - 1; k++) {
                    if (k != i && Math.abs(tableau[k][col]) > EPS) {
                        isBasic = false;
                        break;
                    }
                }
                if (isBasic) {
                    rowFound = i;
                    break;
                }
            }
        }
        return rowFound;
    }

    //  הדפסת הטבלה
    public void printTableau() {
        System.out.println("טבלת הסימפלקס:");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.printf("%10.3f ", tableau[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------------");
    }
}
