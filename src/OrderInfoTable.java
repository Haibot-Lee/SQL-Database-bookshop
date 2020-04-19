/*
 * References: https://www.youtube.com/watch?v=UDLr_LYnLv0
 */

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



    public OrderInfoTable(List<Order> orders, List<BookInOrder>[] bookInOrders) {
        root = new Node(new String[] {"root"});
        for (int i=0; i<orders.size(); i++) {
            // Append current order info to root
            Order o = orders.get(i);
            Node order = new Node(new String[] {o.orderNo, o.stuNo, o.orderDate.toString(), String.valueOf(o.status),
                    String.valueOf(o.totalPrice), o.payMethod, o.cardNo});
            root.add(order);
            // Add books under current order
            String[] subheadings = {"Order No.", "Book No.", "Title", "Quantity", "Delivery"};
            order.add(new Node(subheadings));
            for (BookInOrder b : bookInOrders[i]) {
                Node book = new Node (new String[] {o.orderNo, b.bookNo, b.title, String.valueOf(b.qty),
                        b.deliverDate == null ? "Pending Delivery" : b.deliverDate.toString()});
                order.add(book);
            }
        }

        model = new DefaultTreeTableModel(root, Arrays.asList(headings));
        table = new JXTreeTable(model);
        table.setShowGrid(true, true);
        // TODO: Change colors of the subheadings

        table.packAll();
        // TODO: Disable selection for subheadings
    }

    public JXTreeTable getTreeTable() {
        return table;
    }

    public Node getNode(TreePath path) {
        return (Node) path.getLastPathComponent();
    }
//    public static void main(String[] args) {
//        JFrame testFrame = new JFrame();
//
//        List<String[]> strss = new ArrayList<>();
//        strss.add(new String[] {"Order 1"});
//        strss.add(new String[] {"Book 1", "book1_attr_1", "book1_attr_2", "book1_attr_3"});
//        strss.add(new String[] {"Book 2", "book2_attr_1", "book2_attr_2", "book2_attr_3"});
//        strss.add(new String[] {"Order 2"});
//        strss.add(new String[] {"Book 1", "book1_attr_1", "book1_attr_2", "book1_attr_3"});
//        strss.add(new String[] {"Book 2", "book2_attr_1", "book2_attr_2", "book2_attr_3"});
//        strss.add(new String[] {"Order 3"});
//        strss.add(new String[] {"Book 1", "book1_attr_1", "book1_attr_2", "book1_attr_3"});
//        strss.add(new String[] {"Book 2", "book2_attr_1", "book2_attr_2", "book2_attr_3"});
//        OrderInfoTable orderInfoTable = new OrderInfoTable(strss);
//        testFrame.setSize(500, 500);
//        testFrame.setLayout(new BorderLayout());
//        testFrame.add(new JScrollPane(orderInfoTable.getTreeTable()), BorderLayout.CENTER);
//        testFrame.setVisible(true);
//    }

}
