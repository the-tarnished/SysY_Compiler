package error;

import control_flow.ControlFlowBuilder;
import control_flow.quaternion.Add;
import control_flow.quaternion.Assign;
import control_flow.quaternion.Mul;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static control_flow.ControlFlowBuilder.IR;

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
        int offset = getOffset(index);
        if (initVal.size() == 0) {
            return 0;
        } else if (offset >= initVal.size()) {
            System.out.println("REG");
            return null;
        }
        return initVal.get(offset);
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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String calOffset(ArrayList<String> offset) {
        if (getDimension().size() == 0)
            return "0";
        ControlFlowBuilder controlFlowBuilder = ControlFlowBuilder.getInstance();
        String base = controlFlowBuilder.getTmpVar();
        controlFlowBuilder.insertQuaternion(new Assign(base,"1"));
        // get base
//        for (int i = dimension.size()-1;i>=offset.size();i--) {
//            controlFlowBuilder.insertQuaternion(new Mul(base,base,String.valueOf(dimension.get(i))));
//        }
        // get ret and ret
        String ret = controlFlowBuilder.getTmpVar();
        controlFlowBuilder.insertQuaternion(new Assign(ret,"0"));
        for (int i = dimension.size()-1;i>=0;i--) {
            if (i < offset.size()) {
                String tmp = controlFlowBuilder.getTmpVar();
                controlFlowBuilder.insertQuaternion(new Mul(tmp, base, offset.get(i)));
                controlFlowBuilder.insertQuaternion(new Add(ret, ret, tmp));
            }
            controlFlowBuilder.insertQuaternion(new Mul(base,base,String.valueOf(dimension.get(i))));
        }
        return ret;
    }

    public void print() {
        AtomicInteger size = new AtomicInteger(1);
        dimension.forEach(tmp -> size.updateAndGet(v -> v * tmp));
        if (IR) {
            System.out.printf("size:%d initVal:", size.get());
            initVal.forEach(tmp -> System.out.printf("%d ", tmp));
        } else {
            System.out.printf(".word ");
            if (initVal.size() == 0) {
                System.out.printf("0: %d",size.get());
            } else {
                initVal.forEach(tmp -> System.out.printf("%d ", tmp));
            }
        }
    }
}
