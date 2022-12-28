package control_flow;

import java.util.*;

public class ConflictGraph {
    private HashMap<String,ConflictGraphNode> V;
    private HashSet<ConflictGraphNode> spilledV;
    private HashMap<String,Integer> variableWeight;
    private HashMap<String,ConflictGraphNode> newV;
    private int REGISTER_NUM;

    public ConflictGraph(HashMap<String,Integer> variableWeight) {
        V = new HashMap<>();
        newV = new HashMap<>();
        spilledV = new HashSet<>();
        this.variableWeight = variableWeight;
    }

    public void addNode(String node) {
        V.put(node,new ConflictGraphNode(node));
    }

    public void connect(String node1, String node2) {
        if(node1.equals(node2))
            return;
        ConflictGraphNode n1 = V.getOrDefault(node1,new ConflictGraphNode(node1));
        ConflictGraphNode n2 = V.getOrDefault(node2,new ConflictGraphNode(node2));
        n1.connect(n2);
        n2.connect(n1);
        V.put(node1,n1);
        V.put(node2,n2);
    }

    public void run(int REGISTER_NUM) {
        this.REGISTER_NUM = REGISTER_NUM;
        // 摘点
        FetchNode();
        // 重建
        rebuild();
        // 涂色
        color();
    }

    private void FetchNode() {
        while (!V.isEmpty()) {
            boolean modified = false;
            ArrayList<String> keys = new ArrayList<>(V.keySet());
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String var = iterator.next();
                ConflictGraphNode node = V.get(var);
                if (node.getDegree() < REGISTER_NUM) {
                    modified = true;
                    // 放入新的图中,然后把别人对他的边减去,新图重建再搞回来
                    newV.put(var, node);
                    node.getNeighborhood().forEach(each -> each.disconnect(node));
                    // 旧图删掉
                    iterator.remove();
                    V.remove(var);
                }
            }
            if (!modified) {
                // spill一个吧,寄
                int min = 10000;
                String minNode = null;
                iterator = keys.iterator();
                while (iterator.hasNext()) {
                    String var = iterator.next();
                    if (variableWeight.get(var) < min) {
                        min = variableWeight.get(var);
                        minNode = var;
                    }
                }
                // 删除对应的node
                ConflictGraphNode node = V.get(minNode);
                node.getNeighborhood().forEach(each -> each.disconnect(node));
                V.remove(minNode);
            }
        }
    }

    private void rebuild() {
        // 遍历newV中每一个点,如果这个点对应的边存在,则重新connect,不然则删除这条边
        ArrayList<String> keys = new ArrayList<>(newV.keySet());
        for (String tmp : keys) {
            HashSet<ConflictGraphNode> neighborhood = newV.get(tmp).getNeighborhood();
            Iterator<ConflictGraphNode> iterator = neighborhood.iterator();
            while (iterator.hasNext()) {
                ConflictGraphNode tmpNode = iterator.next();
                if (keys.contains(tmpNode.getName())) {
                    tmpNode.connect(newV.get(tmp));
                } else {
                    iterator.remove();
                }
            }
        }
    }

    private void color() {
        ArrayList<String> keys = new ArrayList<>(newV.keySet());
        for (String tmp:keys) { // 对每一个node着色
            // 初始化颜色记录
            boolean[] record = new boolean[REGISTER_NUM+1];
            // 获取xd的颜色
            ConflictGraphNode node = newV.get(tmp);
            node.getNeighborhood().forEach((each -> record[each.getColor()] = true));
            for (int i = 1;i<record.length;i++) {
                if (!record[i]) {
                    node.setColor(i);
                    break;
                }
            }
        }
    }

    public HashMap<String, ConflictGraphNode> getNewV() {
        return newV;
    }
}
