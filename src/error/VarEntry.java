package error;

import java.util.ArrayList;

public class VarEntry{
    private String name;
    private int id;
    private boolean isConst;
    private ArrayList<Integer> dimension;
    private ArrayList<Integer> initVal;
    private boolean isGlobal;

    public VarEntry(String name, boolean isConst, ArrayList<Integer> dimension,ArrayList<Integer> initVal,int id) {
        this.name = name;
        this.isConst = isConst;
        this.dimension = dimension;
        this.initVal = initVal;
        this.id = id;
        this.isGlobal = false;
    }

    public void setGlobal() {
        this.isGlobal = true;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public Integer getValue(ArrayList<Integer> index) {
//        int offset = getOffset(index);
//        if (initVal.size() == 0) {
//            return 0;
//        } else if (offset >= initVal.size()) {
//            System.out.println("REG");
//            return null;
//        }
//        return initVal.get(offset);
        return 1;
    }

    public ArrayList<Integer> getDimension() {
        return dimension;
    }

    private int getOffset(ArrayList<Integer> index) {
        int ret = 0,base = 1;
        if (index.size() == 0) {
            return 0;
        }
        // get base
        for (int i = dimension.size()-1;i>=index.size();i--) {
            base *= dimension.get(i);
        }
        // get ret
        for (int i = index.size()-1;i>0;i--) {
            ret += index.get(i)*base;
            base *= dimension.get(i);
        }
        return ret + base*index.get(0);
    }


}
