package error;

import java.util.ArrayList;

public class IRRet { // 不需要check了
    public ArrayList<String> initVal; // 初始值
    public ArrayList<String> args; // 参数名
    public boolean isArray; // 如果是数组需要给指针
    public String ret;
    public String offset;
    public static final String DEFAULT = "-YSY";
    public boolean hasReturn;

    public IRRet() {
        initVal = new ArrayList<>();
        args = new ArrayList<>();
        isArray = false;
        offset = DEFAULT;
        ret = DEFAULT;
        hasReturn = false;
    }
}
