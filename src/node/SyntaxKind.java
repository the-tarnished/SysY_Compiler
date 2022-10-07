package node;

public enum SyntaxKind {
    CompUnit,
    Decl, //不需要输出
    ConstDecl,
    BType, // 不需要输出
    ConstDef,
    ConstInitVal,
    VarDecl,
    VarDef,
    InitVal,
    FuncDef,
    MainFuncDef,
    FuncType,
    FuncFParams,
    FuncFParam,
    Block,
    BlockItem, // 不需要输出
    Stmt,
    AssignStmt, // 不需要输出
    IfStmt, // 不需要输出
    WhileStmt,  //不需要输出
    BreakStmt, // 不需要输出
    ContinueStmt, // 不需要输出
    ReturnStmt, // 不需要输出
    GetIntStmt, // 不需要输出
    PrintStmt, // 不需要输出
    Exp,
    Cond,
    LVal,
    PrimaryExp,
    Number,
    UnaryExp,
    UnaryOp,
    FuncRParams,
    MulExp,
    AddExp,
    RelExp,
    EqExp,
    LAndExp,
    LOrExp,
    ConstExp,
    TerminalTk,
}
