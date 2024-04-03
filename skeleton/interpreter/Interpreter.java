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

    private Interpreter(Program astRoot) {
        this.astRoot = astRoot;
        this.random = new Random();
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
        FuncDef mainFuncDef = astRoot.getFuncs().lookupFuncDef("main");
        HashMap<String, QVal> mainEnv = new HashMap<String, QVal>();
        mainEnv.put(mainFuncDef.getParams().getFirst(), new QInt(arg));
        return execute(mainFuncDef.getBody(), mainEnv);

    }

    QVal execute(Stmt stmt, HashMap<String, QVal> env) {
        if (stmt instanceof StmtList) {
            StmtList sl = (StmtList) stmt;
            QVal retVal = execute(sl.getFirst(), env);
            if (retVal != null) {
                return retVal;
            }
            if (sl.getRest() != null) {
                return execute(sl.getRest(), env);
            }
            return null;
        } else if (stmt instanceof DeclStmt) {
            DeclStmt declStmt = (DeclStmt) stmt;
            env.put(declStmt.getVarName(), evaluate(declStmt.getExpr(), env));
            return null;
        } else if (stmt instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt) stmt;
            if (evaluate(ifStmt.getCond(), env)) {
                return execute(ifStmt.getThenStmt(), env);
            } else if (ifStmt.getElseStmt() != null) {
                return execute(ifStmt.getElseStmt(), env);
            }
            return null;
        } else if (stmt instanceof ReturnStmt) {
            ReturnStmt returnStmt = (ReturnStmt) stmt;
            return evaluate(returnStmt.getExpr(), env);
        } else if (stmt instanceof PrintStmt) {
            System.out.println(evaluate(((PrintStmt) stmt).getExpr(), env));
            return null;
        } else if (stmt instanceof WhileStmt) {
            /**
             * The while loop statement now working properly.
             */
            WhileStmt whileStmt = (WhileStmt) stmt;
            while (evaluate(whileStmt.getCond(), env)) {
                QVal retValue = execute(whileStmt.getStmt(), env);
                if (retValue != null) {
                    return retValue;
                }
            }
            return null;
        } else if (stmt instanceof AssignStmt) {
            AssignStmt assignStmt = (AssignStmt) stmt;
            QVal newValue = evaluate(assignStmt.getExpr(), env);
            env.put(assignStmt.getVarName(), newValue);
            return null;
        } else if (stmt instanceof CallStmt) {
            CallStmt callStmt = (CallStmt) stmt;
            if (callStmt.getFuncName().equals("randomInt")) {
                long num = ((QInt) evaluate(callStmt.getArgs().getFirst(), env)).getVal();
                long result = Math.abs(random.nextLong()) % num;
                return new QInt(result);
            } else if (callStmt.getFuncName().equals("isNil")) {
                /**
                 * * isNil() implementation. It returns 1 if the argument is nil.
                 */
                QVal val = evaluate(callStmt.getArgs().getFirst(), env);
                return new QInt(val instanceof QRef && ((QRef) val).isNil() ? 1 : 0);
            } else if (callStmt.getFuncName().equals("isAtom")) {
                /**
                 * * isAtom() implementation. It returns 1 if the argument is an int or nil.
                 */
                QVal val = evaluate(callStmt.getArgs().getFirst(), env);
                if (val instanceof QInt) {
                    return new QInt(1);
                } else if (val instanceof QRef) {
                    return new QInt(((QRef) val).isNil() ? 1 : 0);
                } else {
                    return new QInt(0);
                }
            } else if (callStmt.getFuncName().equals("left")) {
                QVal val = evaluate(callStmt.getArgs().getFirst(), env);
                QRef ref = (QRef) val;
                QObj obj = (QObj) ref.getReferent();
                return obj.getLeft();
            } else if (callStmt.getFuncName().equals("right")) {
                QVal val = evaluate(callStmt.getArgs().getFirst(), env);
                QRef ref = (QRef) val;
                QObj obj = (QObj) ref.getReferent();
                return obj.getRight();
            }
            /**
             * The setLeft and setRight function are now working.
             */
            else if (callStmt.getFuncName().equals("setLeft")) {
                QVal val = evaluate(callStmt.getArgs().getFirst(), env);
                QRef ref = (QRef) val;
                QObj obj = (QObj) ref.getReferent();

                QVal val2 = evaluate(callStmt.getArgs().getRest().getFirst(), env);
                obj.setLeft(val2);
                return null;
            } else if (callStmt.getFuncName().equals("setRight")) {
                QVal val = evaluate(callStmt.getArgs().getFirst(), env);
                QRef ref = (QRef) val;
                QObj obj = (QObj) ref.getReferent();

                QVal val2 = evaluate(callStmt.getArgs().getRest().getFirst(), env);
                obj.setRight(val2);
                return null;
            }
            FuncDef callee = astRoot.getFuncs().lookupFuncDef(callStmt.getFuncName());
            HashMap<String, QVal> calleeEnv = new HashMap<>();
            FormalDeclList currFormalList = callee.getParams();
            ExprList currExprList = callStmt.getArgs();
            while (currFormalList != null) {
                calleeEnv.put(currFormalList.getFirst(), evaluate(currExprList.getFirst(), env));
                currFormalList = currFormalList.getRest();
                currExprList = currExprList.getRest();
            }
            /**
             * interpreter should handle call statements by ignoring the return value of the
             * callee, not by returning the value from the caller.
             */
            execute(callee.getBody(), calleeEnv);
            return null;
        } else {
            throw new RuntimeException("Unhandled statement type");
        }
    }

    QVal evaluate(Expr expr, HashMap<String, QVal> env) {
        if (expr instanceof NilExpr) {
            return new QRef(null);
        } else if (expr instanceof ConstExpr) {
            return ((ConstExpr) expr).getValue();
        } else if (expr instanceof IdentExpr) {
            return env.get(((IdentExpr) expr).getVarName());
        } else if (expr instanceof UnaryMinusExpr) {
            return new QInt(-((QInt) evaluate(((UnaryMinusExpr) expr).getExpr(), env)).getVal());
        } else if (expr instanceof CallExpr) {
            CallExpr callExpr = (CallExpr) expr;
            if (callExpr.getFuncName().equals("randomInt")) {
                long num = ((QInt) evaluate(callExpr.getArgs().getFirst(), env)).getVal();
                long result = Math.abs(random.nextLong()) % num;
                return new QInt(result);
            } else if (callExpr.getFuncName().equals("isNil")) {
                /**
                 * isNil() implementation. It returns 1 if the argument is nil.
                 */
                QVal val = evaluate(callExpr.getArgs().getFirst(), env);
                return new QInt(val instanceof QRef && ((QRef) val).isNil() ? 1 : 0);
            } else if (callExpr.getFuncName().equals("isAtom")) {
                /*
                 * isAtom() implementation. It returns 1 if the argument is an int or nil.
                 */
                QVal val = evaluate(callExpr.getArgs().getFirst(), env);
                if (val instanceof QInt) {
                    return new QInt(1);
                } else if (val instanceof QRef) {
                    return new QInt(((QRef) val).isNil() ? 1 : 0);
                } else {
                    return new QInt(0);
                }
            } else if (callExpr.getFuncName().equals("left")) {
                QVal val = evaluate(callExpr.getArgs().getFirst(), env);
                QRef ref = (QRef) val;
                QObj obj = (QObj) ref.getReferent();
                return obj.getLeft();
            } else if (callExpr.getFuncName().equals("right")) {
                QVal val = evaluate(callExpr.getArgs().getFirst(), env);
                QRef ref = (QRef) val;
                QObj obj = (QObj) ref.getReferent();
                return obj.getRight();
            } else if (callExpr.getFuncName().equals("setLeft")) {
                /**
                 * * Sets the left field of the object referenced by r to value, and returns 1
                 */
                QVal val = evaluate(callExpr.getArgs().getFirst(), env);
                QRef ref = (QRef) val;
                QObj obj = (QObj) ref.getReferent();

                QVal val2 = evaluate(callExpr.getArgs().getRest().getFirst(), env);
                obj.setLeft(val2);
                return new QInt(1);
            } else if (callExpr.getFuncName().equals("setRight")) {
                /**
                 * * Sets the right field of the object referenced by r to value, and returns 1
                 */
                QVal val = evaluate(callExpr.getArgs().getFirst(), env);
                QRef ref = (QRef) val;
                QObj obj = (QObj) ref.getReferent();

                QVal val2 = evaluate(callExpr.getArgs().getRest().getFirst(), env);
                obj.setRight(val2);
                return new QInt(1);
            }
            FuncDef callee = astRoot.getFuncs().lookupFuncDef(callExpr.getFuncName());
            HashMap<String, QVal> calleeEnv = new HashMap<>();
            FormalDeclList currFormalList = callee.getParams();
            ExprList currExprList = callExpr.getArgs();
            while (currFormalList != null) {
                calleeEnv.put(currFormalList.getFirst(), evaluate(currExprList.getFirst(), env));
                currFormalList = currFormalList.getRest();
                currExprList = currExprList.getRest();
            }
            return execute(callee.getBody(), calleeEnv);
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) expr;
            QVal leftVal = evaluate(binaryExpr.getLeftExpr(), env);
            QVal rightVal = evaluate(binaryExpr.getRightExpr(), env);
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS:
                    return new QInt(((QInt) leftVal).getVal() + ((QInt) rightVal).getVal());
                case BinaryExpr.MINUS:
                    return new QInt(((QInt) leftVal).getVal() - ((QInt) rightVal).getVal());
                case BinaryExpr.TIMES:
                    return new QInt(((QInt) leftVal).getVal() * ((QInt) rightVal).getVal());
                case BinaryExpr.DOT:
                    /**
                     * DOT expression implementation
                     * TODO:
                     * It only supports dot expression such as (1 . 1), (2. 1).
                     * It does not support nested dot expression such as (1 . nil).
                     */
                    QObj obj = new QObj(leftVal, rightVal);
                    return new QRef(obj);
                default:
                    throw new RuntimeException("Unhandled operator");
            }
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

    // evaluate conditional and logical expression
    boolean evaluate(Cond cond, HashMap<String, QVal> env) {
        if (cond instanceof CompCond) {
            CompCond compCond = (CompCond) cond;
            long leftVal = ((QInt) evaluate(compCond.getLeftExpr(), env)).getVal();
            long rightVal = ((QInt) evaluate(compCond.getRightExpr(), env)).getVal();
            switch (compCond.getOperator()) {
                case CompCond.EQ:
                    return leftVal == rightVal;
                case CompCond.NE:
                    return leftVal != rightVal;
                case CompCond.LT:
                    return leftVal < rightVal;
                case CompCond.LE:
                    return leftVal <= rightVal;
                case CompCond.GT:
                    return leftVal > rightVal;
                case CompCond.GE:
                    return leftVal >= rightVal;
                default:
                    throw new RuntimeException("Unhandled operator");
            }
        } else if (cond instanceof LogicalCond) {
            LogicalCond logicalCond = (LogicalCond) cond;
            switch (logicalCond.getOperator()) {
                case LogicalCond.AND:
                    return evaluate(logicalCond.getLeftCond(), env) && evaluate(logicalCond.getRightCond(), env);
                case LogicalCond.OR:
                    return evaluate(logicalCond.getLeftCond(), env) || evaluate(logicalCond.getRightCond(), env);
                case LogicalCond.NOT:
                    return !evaluate(logicalCond.getLeftCond(), env);
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