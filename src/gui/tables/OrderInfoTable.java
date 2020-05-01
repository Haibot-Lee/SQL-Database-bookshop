package gui.tables;/*
 * References: https://www.youtube.com/watch?v=UDLr_LYnLv0
 */

import gui.tables.Node;
import objects.BookInOrder;
import objects.Order;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.List;

public class OrderInfoTable {

    private String[] headings = {"Order No.", "Student No.", "Order Date", "Status",
                                    "Total Price", "Payment Method", "Card No"};
    private Node root;
    private DefaultTreeTableModel model;
    private JXTreeTable table;
    private List<Order> orders;
    private List<BookInOrder>[] bookInOrders;



    public OrderInfoTable(List<Order> orders, List<BookInOrder>[] bookInOrders) {
        this.orders = orders;
        this.bookInOrders = bookInOrders;
        createTree();
        createModel();
        createTable();
    }

    private void createTree() {
        root = new Node(new String[] {"root"});
        for (int i=0; i<orders.size(); i++) {
            // Append current order info to root
            Order o = orders.get(i);
            String[] status = {"Confirmed", "Shipping", "Completed", "Cancelled", "Incomplete"};
            Node order = new Node(new String[] {o.orderNo, o.stuNo, o.orderDate.toString(), status[o.status],
                    String.valueOf(o.totalPrice), o.payMethod, o.cardNo});
            root.add(order);
            // Add books under current order
            String[] subheadings = {"<Order No.>", "<Book No.>", "<Title>", "<Quantity>", "<Delivery>", "---", "---"};
            order.add(new Node(subheadings));
            for (BookInOrder b : bookInOrders[i]) {
                Node book = new Node (new String[] {o.orderNo, b.bookNo, b.title, String.valueOf(b.qty),
                        b.deliverDate == null ? "Pending Delivery" : b.deliverDate.toString()});
                order.add(book);
            }
        }
    }

    public void createModel() {
        model = new DefaultTreeTableModel(root, Arrays.asList(headings));
    }

    public void createTable() {
        table = new JXTreeTable(model);
        table.setShowGrid(true, true);
    }

    public JXTreeTable getTreeTable() {
        return table;
    }

    public void refresh(List<Order> orders, List<BookInOrder>[] bookInOrders) {
        this.orders = orders;
        this.bookInOrders = bookInOrders;
        createTree();
        model.setRoot(root);
        table.updateUI();
    }

    public Node getNode(TreePath path) {
        return (Node) path.getLastPathComponent();
    }

}
