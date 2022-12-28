package control_flow.quaternion;

import java.util.ArrayList;

public class GetReturn extends Quaternion{
    private String arg1;

    public GetReturn(String arg1) {
        super(QuaternionKind.GetReturn);
        this.arg1 = arg1;
    }

    @Override
    public void assembly() {
        if (defineName.charAt(0) == '$') {
            System.out.printf("move %s $v0\n", defineName);
        } else {
            System.out.printf("sw $v0 %s($sp)\n", defineName.substring(2));
        }
    }

    @Override
    public void printIR() {
        System.out.println(this.getQuaternionKind() + " " + arg1);
    }

    @Override
    public String getDefine() {
        return arg1;
    }

    @Override
    public ArrayList<String> getVars() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(arg1);
        return ret;
    }
}
