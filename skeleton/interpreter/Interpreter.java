package interpreter;

import java.io.*;
import java.util.Random;
import java.util.HashMap;

import parser.ParserWrapper;
import ast.*;

public class Interpreter {

    // Process return codes
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_PARSING_ERROR = 1;
    public static final int EXIT_STATIC_CHECKING_ERROR = 2;
    public static final int EXIT_DYNAMIC_TYPE_ERROR = 3;
    public static final int EXIT_NIL_REF_ERROR = 4;
    public static final int EXIT_QUANDARY_HEAP_OUT_OF_MEMORY_ERROR = 5;
    public static final int EXIT_DATA_RACE_ERROR = 6;
    public static final int EXIT_NONDETERMINISM_ERROR = 7;

    static private Interpreter interpreter;

    public static Interpreter getInterpreter() {
        return interpreter;
    }

    public static void main(String[] args) {
        String gcType = "NoGC"; // default for skeleton, which only supports NoGC
        long heapBytes = 1 << 14;
        int i = 0;
        String filename;
        long quandaryArg;
        try {
            for (; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-")) {
                    if (arg.equals("-gc")) {
                        gcType = args[i + 1];
                        i++;
                    } else if (arg.equals("-heapsize")) {
                        heapBytes = Long.valueOf(args[i + 1]);
                        i++;
                    } else {
                        throw new RuntimeException("Unexpected option " + arg);
                    }
                } else {
                    if (i != args.length - 2) {
                        throw new RuntimeException("Unexpected number of arguments");
                    }
                    break;
                }
            }
            filename = args[i];
            quandaryArg = Long.valueOf(args[i + 1]);
        } catch (Exception ex) {
            System.out.println("Expected format: quandary [OPTIONS] QUANDARY_PROGRAM_FILE INTEGER_ARGUMENT");
            System.out.println("Options:");
            System.out.println("  -gc (MarkSweep|Explicit|NoGC)");
            System.out.println("  -heapsize BYTES");
            System.out.println("BYTES must be a multiple of the word size (8)");
            return;
        }

        Program astRoot = null;
        Reader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            astRoot = ParserWrapper.parse(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            Interpreter.fatalError("Uncaught parsing error: " + ex, Interpreter.EXIT_PARSING_ERROR);
        }
        // astRoot.println(System.out);
        interpreter = new Interpreter(astRoot);
        interpreter.initMemoryManager(gcType, heapBytes);
        String returnValueAsString = interpreter.executeRoot(astRoot, quandaryArg).toString();
        System.out.println("Interpreter returned " + returnValueAsString);
    }

    final Program astRoot;
    final Random random;
    final HashMap<String, Object> env;

    private Interpreter(Program astRoot) {
        this.astRoot = astRoot;
        this.random = new Random();
        this.env = new HashMap<String, Object>();
    }

    void initMemoryManager(String gcType, long heapBytes) {
        if (gcType.equals("Explicit")) {
            throw new RuntimeException("Explicit not implemented");
        } else if (gcType.equals("MarkSweep")) {
            throw new RuntimeException("MarkSweep not implemented");
        } else if (gcType.equals("RefCount")) {
            throw new RuntimeException("RefCount not implemented");
        } else if (gcType.equals("NoGC")) {
            // Nothing to do
        }
    }

    Object executeRoot(Program astRoot, long arg) {
        env.put(astRoot.getArgName(), arg);
        return execute(astRoot.getStmtList());
    }

    Object execute(Stmt stmt) {
        if (stmt instanceof StmtList) {
            StmtList sl = (StmtList) stmt;
            Object retVal = execute(sl.getFirst());
            if (retVal != null) {
                return retVal;
            }
            if (sl.getRest() != null) {
                return execute(sl.getRest());
            }
            return null;
        } else if (stmt instanceof DeclStmt) {
            DeclStmt declStmt = (DeclStmt) stmt;
            env.put(declStmt.getVarName(), evaluate(declStmt.getExpr()));
            return null;
        } else if (stmt instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt) stmt;
            if (evaluate(ifStmt.getCond())) {
                return execute(ifStmt.getThenStmt());
            } else if (ifStmt.getElseStmt() != null) {
                return execute(ifStmt.getElseStmt());
            }
            return null;
        } else if (stmt instanceof ReturnStmt) {
            ReturnStmt returnStmt = (ReturnStmt) stmt;
            return evaluate(returnStmt.getExpr());
        } else if (stmt instanceof PrintStmt) {
            System.out.println(evaluate(((PrintStmt) stmt).getExpr()));
            return null;
        } else {
            throw new RuntimeException("Unhandled statement type");
        }
    }

    Object evaluate(Expr expr) {
        if (expr instanceof ConstExpr) {
            return ((ConstExpr) expr).getValue();
        } else if (expr instanceof IdentExpr) {
            return env.get(((IdentExpr) expr).getVarName());
        } else if (expr instanceof UnaryMinusExpr) {
            return -1 * (Long) evaluate(((UnaryMinusExpr) expr).getExpr());
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) expr;
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS:
                    return (Long) evaluate(binaryExpr.getLeftExpr()) + (Long) evaluate(binaryExpr.getRightExpr());
                case BinaryExpr.MINUS:
                    return (Long) evaluate(binaryExpr.getLeftExpr()) - (Long) evaluate(binaryExpr.getRightExpr());
                case BinaryExpr.TIMES:
                    return (Long) evaluate(binaryExpr.getLeftExpr()) * (Long) evaluate(binaryExpr.getRightExpr());
                default:
                    throw new RuntimeException("Unhandled operator");
            }
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

    // evaluate conditional and logical expression
    boolean evaluate(Cond cond) {
        if (cond instanceof CompCond) {
            CompCond compCond = (CompCond) cond;
            switch (compCond.getOperator()) {
                case CompCond.EQ:
                    return (long) evaluate(compCond.getLeftExpr()) == (long) evaluate(compCond.getRightExpr());
                case CompCond.NE:
                    return (long) evaluate(compCond.getLeftExpr()) != (long) evaluate(compCond.getRightExpr());
                case CompCond.LT:
                    return (long) evaluate(compCond.getLeftExpr()) < (long) evaluate(compCond.getRightExpr());
                case CompCond.LE:
                    return (long) evaluate(compCond.getLeftExpr()) <= (long) evaluate(compCond.getRightExpr());
                case CompCond.GT:
                    return (long) evaluate(compCond.getLeftExpr()) > (long) evaluate(compCond.getRightExpr());
                case CompCond.GE:
                    return (long) evaluate(compCond.getLeftExpr()) >= (long) evaluate(compCond.getRightExpr());
                default:
                    throw new RuntimeException("Unhandled operator");
            }
        } else if (cond instanceof LogicalCond) {
            LogicalCond logicalCond = (LogicalCond) cond;
            switch (logicalCond.getOperator()) {
                case LogicalCond.AND:
                    return evaluate(logicalCond.getLeftCond()) && evaluate(logicalCond.getRightCond());
                case LogicalCond.OR:
                    return evaluate(logicalCond.getLeftCond()) || evaluate(logicalCond.getRightCond());
                case LogicalCond.NOT:
                    return !evaluate(logicalCond.getLeftCond());
                default:
                    throw new RuntimeException("Unhandled operator");
            }
        }
        return false;

    }

    public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
    }
}