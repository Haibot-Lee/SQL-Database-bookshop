import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

public class OrderSearchWindow {
    DBConn dbConn;
    String sid;
    JFrame osPage;
    List<Order> orders;
    List<BookInOrder>[] bookInOrders;
    Container osc;
    JScrollPane pane;
    OrderInfoTable table;
    JXTreeTable jxTable;
    Button b1;
    Button b2;

    public OrderSearchWindow(DBConn dbConn, String sid) {
        this.dbConn = dbConn;
        this.sid = sid;
        getData();
        initialize();
    }

    private void getData() {
        orders = dbConn.searchOrder(sid);
        bookInOrders = new List[orders.size()];
        // Get bookInOrder for every order
        for (int i = 0; i < orders.size(); i++) {
            bookInOrders[i] = dbConn.searchBookInOrder(orders.get(i).orderNo);
        }
    }

    private void initialize() {
        osPage = new JFrame("All of your orders");
        osPage.setLayout(null);
        osPage.setSize(800, 1000);
        osc = osPage.getContentPane();
        table = new OrderInfoTable(orders, bookInOrders);
        jxTable = table.getTreeTable();
        pane = new JScrollPane(table.getTreeTable());
        pane.setBounds(0, 0, 600, 1000);

        // Row selection
        int selectedType;   // 0: invalid; 1: Order; 2: Book
        final String[] orderNo = new String[1];
        final String[] bookNo = new String[1];
        jxTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);    // Single selection allowed only

        jxTable.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                int selectedRow = jxTable.getSelectedRow();
                TreePath path = jxTable.getPathForRow(selectedRow);
                Node selectedNode = table.getNode(path);

                orderNo[0] = jxTable.getStringAt(selectedRow, 0);
                bookNo[0] = jxTable.getStringAt(selectedRow, 1);

                System.out.println(orderNo[0]);
                System.out.println(bookNo[0]);
                if (selectedNode.isLeaf()) {    // Book selected
                    b1.setEnabled(true);
                    b2.setEnabled(false);
                } else {    // Order selected
                    b1.setEnabled(false);
                    b2.setEnabled(true);
                }
            }
        });

        b1 = new Button("Order Update");
        b2 = new Button("Order Cancelling");
        b1.setEnabled(false);
        b2.setEnabled(false);
        b1.setBounds(650, 50, 100, 40);
        b2.setBounds(650, 150, 100, 40);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateOrder(orderNo[0], bookNo[0]);
                refresh();
                // TODO: REFRESH AFTER UPDATE!
            }
        });

        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                orderCancelling(sid);
            }
        });

        osc.add(b1);
        osc.add(b2);
        osc.add(pane);

        osPage.setVisible(true);
    }

    public void refresh() {
        getData();
        osc.remove(pane);
        table = new OrderInfoTable(orders, bookInOrders);
        jxTable = table.getTreeTable();
        pane = new JScrollPane(table.getTreeTable());
        pane.setBounds(0, 0, 600, 1000);
        osc.add(pane);
        osc.setVisible(true);
    }

    private void updateOrder(String orderNo, String bookNo) {
        dbConn.orderUpdate(orderNo, bookNo, new Date());
        // TODO Handle exceptions
    }

    private void orderCancelling(String sid) {

    }
}
