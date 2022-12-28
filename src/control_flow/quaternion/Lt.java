package control_flow.quaternion;

import node.Node;

public class Lt extends Binary {
    public Lt(String arg1, String arg2, String arg3) {
        super(arg1, arg2, arg3, QuaternionKind.Lt);
    }
}
