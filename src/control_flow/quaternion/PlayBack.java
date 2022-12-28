package control_flow.quaternion;

import java.util.ArrayList;

public class PlayBack extends Quaternion{

    public PlayBack() {
        super(QuaternionKind.PlayBack);
    }

    @Override
    public void assembly() {
        // 啥都不写,环境恢复需要知道函数的信息,这些都在Func类,所以这里没啥用,转汇编的时候发现是这个,func自己解决
    }

    @Override
    public void printIR() {
        System.out.println(this.getQuaternionKind());
    }

    @Override
    public String getDefine() {
        return null;
    }

    @Override
    public ArrayList<String> getVars() {
        return new ArrayList<>();
    }
}
