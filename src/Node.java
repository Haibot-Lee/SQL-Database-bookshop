import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;

class Node extends AbstractMutableTreeTableNode {
    public Node(Object[] data) {
        super(data);
    }

    public int getColumnCount() {
        return getData().length;
    }

    public Object getValueAt(int col) {
        return getData()[col];
    }

    public Object[] getData() {
        return (Object[]) getUserObject();
    }

}