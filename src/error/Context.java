package error;

import control_flow.BasicBlock;

public class Context {
    public boolean isConst, isFuncDecl,isLVal,isGlobal,hasElse;
    public BasicBlock outBlock,inBlock;
    public BasicBlock breakBlock,continueBlock;

    public Context() {
        isConst = false;
        isFuncDecl = false;
        isLVal = false;
        isGlobal = false;
        hasElse = false;
        breakBlock = null;
        continueBlock = null;
        outBlock = null;
        inBlock = null;
    }

    public void copy(Context context) {

    }
}
