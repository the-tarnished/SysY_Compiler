package error;

import java.util.*;

public class Symbol { // 全局符号表,所有的符号表查询都要通过这个类,单例模式
    private final static Symbol instance = new Symbol();
    private HashMap<String,Stack<VarEntry>> string2VarStack;
    private Stack<Set<String>> stackSymbolTables;
    private HashMap<String,FuncEntry> string2Func;
    private HashMap<String,VarEntry> globalVar;
    private boolean inLoop; // 是否是在循环里面
    private boolean isVoid; // 是否在 void 函数里面
    private boolean isConst; // 是否是 const
    private boolean isFuncDecl; // 是否是函数声明
    private static EnumMap<ErrorKind,String> error2Signal = new EnumMap<>(ErrorKind.class);
    private int cnt; // var id cnt
    private boolean isLVal;
    private boolean inRealParam;
    private boolean needParam;

    static {
        error2Signal.put(ErrorKind.Invalid_Character, "a");
        error2Signal.put(ErrorKind.Redefined, "b");
        error2Signal.put(ErrorKind.Undefined, "c");
        error2Signal.put(ErrorKind.Params_Num_Mismatch, "d");
        error2Signal.put(ErrorKind.Params_Type_MisMatch, "e");
        error2Signal.put(ErrorKind.Void_Return_Invalid, "f");
        error2Signal.put(ErrorKind.Return_Absence, "g");
        error2Signal.put(ErrorKind.Modify_Const_Error, "h");
        error2Signal.put(ErrorKind.Semicolon_Absence, "i");
        error2Signal.put(ErrorKind.RParent_Absence, "j");
        error2Signal.put(ErrorKind.RBrack_Absence, "k");
        error2Signal.put(ErrorKind.Format_Params_Mismatch, "l");
        error2Signal.put(ErrorKind.Break_Continue_Out_Loop, "m");
    }

    private Symbol () {
        string2VarStack = new HashMap<>();
        stackSymbolTables = new Stack<>();
        string2Func = new HashMap<>();
        globalVar = new HashMap<>();
        inLoop = false;
        isVoid = true;
        isFuncDecl = false;
        cnt = 0;
        isConst = false;
        isLVal = false;
        needParam = false;
    }

    public static Symbol getInstance() {
        return instance;
    }

    public static void printError(ErrorKind kind,int line) {
//        System.out.printf("%s in line %d\n",error2Signal.get(kind),line);
        System.out.printf("%d %s\n",line,error2Signal.get(kind));
    }

    public boolean addVar(boolean isConst, String name, ArrayList<Integer> dimension,ArrayList<Integer> initVal) {
        // 判断是否重复定义
        Set<String> cur = stackSymbolTables.pop();
        if (cur.contains(name)) {
            stackSymbolTables.push(cur);
            return false;
        }
        ++cnt;
        VarEntry var = new VarEntry(name,isConst,dimension,initVal,cnt);
        // 维护符号表
        cur.add(name);
        if (stackSymbolTables.size() == 0) {
            globalVar.put(name,var);
            var.setGlobal();
        }

        // 维护索引
        if (!string2VarStack.containsKey(name)) {
            Stack<VarEntry>  tmp = new Stack<>();
            tmp.push(var);
            string2VarStack.put(name,tmp);
        } else {
            string2VarStack.get(name).push(var);
        }
        stackSymbolTables.push(cur);
        return true;
    }

    public boolean addVar(boolean isConst, String name, ArrayList<Integer> dimension) {
        ArrayList<Integer> initVal = new ArrayList<>();
        return addVar(isConst,name,dimension,initVal);
    }

    public boolean addFunc(String name,boolean isVoid,ArrayList<ArrayList<Integer>> paramsDimension) {
        if (string2Func.containsKey(name)) {
            return false;
        }
        FuncEntry func = new FuncEntry(name,isVoid,paramsDimension);
        string2Func.put(name,func);
        return true;
    }

    public void startBlock() {
        this.stackSymbolTables.push(new HashSet<String>());
    }

    public void endBlock() {
        Set<String> tmp = this.stackSymbolTables.pop();
        for (String name : tmp) {
            string2VarStack.get(name).pop();
            if (string2VarStack.get(name).size() == 0) {
                string2VarStack.remove(name);
            }
        }
    }

    public void setConst(boolean input) {
        isConst = input;
    }

    public boolean isInLoop() {
        return inLoop;
    }

    public void setInLoop(boolean inLoop) {
        this.inLoop = inLoop;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public void setVoid(boolean aVoid) {
        isVoid = aVoid;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isFuncDecl() {
        return isFuncDecl;
    }

    public void setFuncDecl(boolean funcDecl) {
        isFuncDecl = funcDecl;
    }

    public VarEntry getVar(String name) {
        if (string2VarStack.containsKey(name)) {
            return string2VarStack.get(name).peek();
        } else {
            return null;
        }
    }

    public FuncEntry getFunc(String name) {
        return string2Func.getOrDefault(name, null);
    }

    public boolean isLVal() {
        return isLVal;
    }

    public void setLVal(boolean LVal) {
        isLVal = LVal;
    }

    public boolean isInRealParam() {
        return inRealParam;
    }

    public void setInRealParam(boolean inRealParam) {
        this.inRealParam = inRealParam;
    }

    public boolean isNeedParam() {
        return needParam;
    }

    public void setNeedParam(boolean needParam) {
        this.needParam = needParam;
    }
}
