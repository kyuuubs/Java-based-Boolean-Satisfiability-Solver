// IN1002 Introduction to Algorithms
// Coursework 2022/2023
//
// Submission by
// YOUR_NAME_GOES_HERE
// YOUR_EMAIL_GOES_HERE


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


public class Solver {

    private int[][] clauseDatabase = null;
    private int numberOfVariables = 0;

    /* Your  answers go below here */

    // Part A.1
    // Worst case complexity : O(v)
    // Best case complexity : O(1)
    public boolean checkClause(int[] assignment, int[] clause) {
        for (int literal : clause) {
            int var = Math.abs(literal);
            int sign = Integer.signum(literal);
            if (sign * assignment[var] > 0) {
                return true;
            }
        }
        return false;
    }

    // Part A.2
    // Worst case complexity : O(cl)
    // Best case complexity : O(l)
    public boolean checkClauseDatabase(int[] assignment, int[][] clauseDatabase) {
        boolean canConfirm = true;
        for (int i = 0; i < clauseDatabase.length; i++) {
            int[] clause = clauseDatabase[i];
            if (!checkClause(assignment, clause)) {
                canConfirm = false;
                break;
            }
        }
        return canConfirm;
    }


    // Part A.3
    // Worst case complexity : O(v)
    // Best case complexity : O(1)
    public int checkClausePartial(int[] partialAssignment, int[] clause) {
        int numUnknowns = 0;
        for (int literal : clause) {
            int variable = Math.abs(literal);
            int assignment = partialAssignment[variable];
            if ((literal > 0 && assignment == 1) || (literal < 0 && assignment == -1)) {
                return 1;
            }
            if (assignment == 0) {
                numUnknowns++;
            }
        }
        return (numUnknowns > 0) ? 0 : -1;
    }

    // Part A.4
    // Worst case complexity : O(v)
    // Best case complexity : O(1)

    public int findUnit(int[] partialAssignment, int[] clause) {
        int numUnknowns = 0;
        int unknownLiteral = 0;
        for (int literal : clause) {
            int variable = Math.abs(literal);
            if (partialAssignment[variable] == 0) {
                numUnknowns++;
                unknownLiteral = literal;
            } else if ((literal > 0 && partialAssignment[variable] == 1) ||
                    (literal < 0 && partialAssignment[variable] == -1)) {
                return 0;
            }
        }
        return (numUnknowns == 1) ? unknownLiteral : 0;
    }

    // Part B
    // I think this can solve from 1 to 11.
    public int[] checkSat(int[][] clauseDatabase) {
        int[] assignment;
        int length = 0;
        for (int[] clause : clauseDatabase) {
            for (int literal : clause) {
                length = Math.max(length, Math.abs(literal));
            }
        }
        assignment = new int[length + 1];
        HashSet<Integer> literalSet = new HashSet<>();
        for (int[] clause : clauseDatabase) {
            for (int literal : clause) {
                literalSet.add(literal);
            }
        }
        for (int literal : literalSet) {
            if (pureLiteral(literalSet, literal)) {
                assignment[Math.abs(literal)] = Integer.signum(literal);
            }
        }
        return recurrence(clauseDatabase, assignment);
    }

    public int[] recurrence(int[][] clauseDatabase, int[] assignment) {
        if (checkClauseDatabase(assignment, clauseDatabase) && emptyAssignment(assignment) != 0) {
            int literal = emptyAssignment(assignment);
            assignment[literal] = 1;
            return recurrence(clauseDatabase, Arrays.copyOf(assignment, assignment.length));
        }
        if (checkClauseDatabase(assignment, clauseDatabase)) {
            return assignment;
        }
        for (int[] clause : clauseDatabase) {
            int onlyUnassigned = findUnit(assignment, clause);
            if (onlyUnassigned != 0) {
                int[] newAssignment = Arrays.copyOf(assignment, assignment.length);
                newAssignment[Math.abs(onlyUnassigned)] = Integer.signum(onlyUnassigned);
                return recurrence(clauseDatabase, newAssignment);
            }
        }
        int emptyVariable = emptyAssignment(assignment);
        if (emptyVariable == 0) {
            return null;
        }
        int[] trueAssignment = Arrays.copyOf(assignment, assignment.length);
        trueAssignment[emptyVariable] = 1;
        int[] result = recurrence(clauseDatabase, trueAssignment);
        if (result != null) {
            return result;
        }
        int[] falseAssignment = Arrays.copyOf(assignment, assignment.length);
        falseAssignment[emptyVariable] = -1;
        result = recurrence(clauseDatabase, falseAssignment);
        if (result != null) {
            return result;
        }
        return null;
    }

    public int emptyAssignment(int[] assignment) {
        int index = 1;
        while (index < assignment.length && assignment[index] != 0) {
            index++;
        }
        return index == assignment.length ? 0 : index;
    }

    public boolean pureLiteral(HashSet<Integer> literalSet, int literal) {
        return literalSet.contains(literal) && !literalSet.contains(-literal);
    }



    /*****************************************************************\
     *** DO NOT CHANGE! DO NOT CHANGE! DO NOT CHANGE! DO NOT CHANGE! ***
     *******************************************************************
     *********** Do not change anything below this comment! ************
     \*****************************************************************/

    public static void main(String[] args) {
        try {
            Solver mySolver = new Solver();

            System.out.println("Enter the file to check");

            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            String fileName = br.readLine();

            int returnValue = 0;

            Path file = Paths.get(fileName);
            BufferedReader reader = Files.newBufferedReader(file);
            returnValue = mySolver.runSatSolver(reader);

            return;

        } catch (Exception e) {
            System.err.println("Solver failed :-(");
            e.printStackTrace(System.err);
            return;

        }
    }

    public int runSatSolver(BufferedReader reader) throws Exception, IOException {

        // First load the problem in, this will initialise the clause
        // database and the number of variables.
        loadDimacs(reader);

        // Then we run the part B algorithm
        int [] assignment = checkSat(clauseDatabase);

        // Depending on the output do different checks
        if (assignment == null) {
            // No assignment to check, will have to trust the result
            // is correct...
            System.out.println("s UNSATISFIABLE");
            return 20;

        } else {
            // Cross check using the part A algorithm
            boolean checkResult = checkClauseDatabase(assignment, clauseDatabase);

            if (checkResult == false) {
                throw new Exception("The assignment returned by checkSat is not satisfiable according to checkClauseDatabase?");
            }

            System.out.println("s SATISFIABLE");

            // Check that it is a well structured assignment
            if (assignment.length != numberOfVariables + 1) {
                throw new Exception("Assignment should have one element per variable.");
            }
            if (assignment[0] != 0) {
                throw new Exception("The first element of an assignment must be zero.");
            }
            for (int i = 1; i <= numberOfVariables; ++i) {
                if (assignment[i] == 1 || assignment[i] == -1) {
                    System.out.println("v " + (i * assignment[i]));
                } else {
                    throw new Exception("assignment[" + i + "] should be 1 or -1, is " + assignment[i]);
                }
            }

            return 10;
        }
    }

    // This is a simple parser for DIMACS file format
    void loadDimacs(BufferedReader reader) throws Exception, IOException {
        int numberOfClauses = 0;

        // Find the header line
        do {
            String line = reader.readLine();

            if (line == null) {
                throw new Exception("Found end of file before a header?");
            } else if (line.startsWith("c")) {
                // Comment line, ignore
                continue;
            } else if (line.startsWith("p cnf ")) {
                // Found the header
                String counters = line.substring(6);
                int split = counters.indexOf(" ");
                numberOfVariables = Integer.parseInt(counters.substring(0,split));
                numberOfClauses = Integer.parseInt(counters.substring(split + 1));

                if (numberOfVariables <= 0) {
                    throw new Exception("Variables should be positive?");
                }
                if (numberOfClauses < 0) {
                    throw new Exception("A negative number of clauses?");
                }
                break;
            } else {
                throw new Exception("Unexpected line?");
            }
        } while (true);

        // Set up the clauseDatabase
        clauseDatabase = new int[numberOfClauses][];

        // Parse the clauses
        for (int i = 0; i < numberOfClauses; ++i) {
            String line = reader.readLine();

            if (line == null) {
                throw new Exception("Unexpected end of file before clauses have been parsed");
            } else if (line.startsWith("c")) {
                // Comment; skip
                --i;
                continue;
            } else {
                // Try to parse as a clause
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                String working = line;

                do {
                    int split = working.indexOf(" ");

                    if (split == -1) {
                        // No space found so working should just be
                        // the final "0"
                        if (!working.equals("0")) {
                            throw new Exception("Unexpected end of clause string : \"" + working + "\"");
                        } else {
                            // Clause is correct and complete
                            break;
                        }
                    } else {
                        int var = Integer.parseInt(working.substring(0,split));

                        if (var == 0) {
                            throw new Exception("Unexpected 0 in the middle of a clause");
                        } else {
                            tmp.add(var);
                        }

                        working = working.substring(split + 1);
                    }
                } while (true);

                // Add to the clause database
                clauseDatabase[i] = new int[tmp.size()];
                for (int j = 0; j < tmp.size(); ++j) {
                    clauseDatabase[i][j] = tmp.get(j);
                }
            }
        }

        // All clauses loaded successfully!
        return;
    }

}