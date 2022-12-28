package control_flow.quaternion;

public class Alloc extends Single{
    public Alloc(String arg1, String arg2) {
        super(arg1, arg2, QuaternionKind.Alloc);
    }

    public int getSize() {
        return Integer.parseInt(arg2);
    }


}
