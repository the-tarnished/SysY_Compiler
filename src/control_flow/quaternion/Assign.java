package control_flow.quaternion;

import node.Node;


public class Assign extends Single{
    public Assign(String arg1, String arg2) {
        super(arg1, arg2, QuaternionKind.Assign);
    }

}
