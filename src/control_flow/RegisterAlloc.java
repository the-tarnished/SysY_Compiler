package control_flow;

import java.util.*;

public class RegisterAlloc {
    public static final int REGISTER_NUM = 18;
    public static final int REGISTER_BEGIN = 8;
    private HashMap<String, Integer> var2Reg;
    private HashMap<String, Integer> arrayInMem;
    private HashMap<String,Integer> varInMem;
    private Func func;

    public RegisterAlloc(Func func) {
        var2Reg = new HashMap<>();
        arrayInMem = func.getArrayInMem();
        varInMem = func.getVarInMem();
        this.func = func;
    }

    public void initRegisterAllocate(HashSet<String> selectedVars) {
        int i = 0;
        for (String var:selectedVars) {
            var2Reg.put(var,i+REGISTER_BEGIN);
            if (arrayInMem.containsKey(var)) {
                System.out.printf("addiu $%s $sp %d\n", i+REGISTER_BEGIN , getOffset(arrayInMem.get(var)));
            }
            i++;
        }
    }

    public void OptRegisterAllocate(ConflictGraph conflictGraph) {
        conflictGraph.run(REGISTER_NUM);
        HashMap<String,ConflictGraphNode> V = conflictGraph.getNewV();
        HashSet<String> keys = new HashSet<>(V.keySet());
        for (String name:keys) {
            var2Reg.put(name,V.get(name).getColor()+REGISTER_BEGIN-1);
        }
    }

    public String registerAllocate(String var) {
        if (!isLocalVar(var)) {
            return var;
        }
        if (!var2Reg.containsKey(var)) {
            if (varInMem.containsKey(var)) {
                return "sp"+String.valueOf(getOffset(varInMem.get(var)));
            } else {
                return "ar"+String.valueOf(getOffset(arrayInMem.get(var)));
            }
        }
        return "$"+var2Reg.get(var);
    }

    private boolean isLocalVar(String var) {
        return varInMem.containsKey(var)||arrayInMem.containsKey(var);
    }

    private int getOffset(int offset) {
        return func.getOffset(offset);
    }
}
