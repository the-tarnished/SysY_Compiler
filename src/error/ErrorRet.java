package error;

import java.util.ArrayList;

public class ErrorRet {
    public ArrayList<Pair<ErrorKind>> errorList;
    public ArrayList<Integer> value;
    public String str;
    public ArrayList<Integer> dimension;
    public boolean hasReturn;
    public ArrayList<ArrayList<Integer>> paramDimension;
    public int rBraceLine;

    public ErrorRet() {
        errorList = new ArrayList<>();
        value = new ArrayList<>();
        str = null;
        dimension = new ArrayList<>();
        hasReturn = false;
        paramDimension = new ArrayList<>();
        rBraceLine = 0;
    }
}
