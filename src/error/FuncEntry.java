package error;

import java.util.ArrayList;
import java.util.Objects;

public class FuncEntry{
    private String name;
    private boolean isVoid;
    private ArrayList<ArrayList<Integer>> paramsDimension;

    public FuncEntry(String name, boolean isVoid, ArrayList<ArrayList<Integer>> paramsDimension) {
        this.name = name;
        this.isVoid = isVoid;
        this.paramsDimension = paramsDimension;
    }

    public FuncEntry() {}

    public int matchParams(ArrayList<ArrayList<Integer>> input) {
        if (input.size() != paramsDimension.size()) {
            return -1;
        }
        for (int i = 0;i < input.size();i++) {
            if (input.get(i).size() != paramsDimension.get(i).size()) { // 维度不同
                return -2;
            }
            if (input.get(i).size() > 1 && !Objects.equals(input.get(i).get(input.get(i).size() - 1), paramsDimension.get(i).get(input.get(i).size() - 1))) { // 最后一维长度不同
                return -2;
            }
        }
        return 0;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public boolean needParam() {
        return paramsDimension.size() != 0;
    }
}
