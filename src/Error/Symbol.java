package Error;

import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

public class Symbol { // 全局符号表,所有的符号表查询都要通过这个类,单例模式
    private final static Symbol instance = new Symbol();
    private HashMap<String,SymbolEntry> words2Entry;
    private Stack<Stack<Set<String>>> stackSymbolTables;
    private boolean inLoop; // 是否是在循环里面
    private boolean isVoid; // 是否在 void 函数里面
    private boolean isFuncDecl; // 是否是函数声明

    private Symbol () {
        words2Entry = new HashMap<>();
        stackSymbolTables = new Stack<>();
        inLoop = false;
        isVoid = true;
        isFuncDecl = false;
    }

    private static Symbol getInstance() {
        return instance;
    }

    private void startBlock
}
