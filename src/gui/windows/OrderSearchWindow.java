package gui.windows;

import connection.DBConn;
import gui.tables.Node;
import gui.tables.OrderInfoTable;
import objects.BookInOrder;
import objects.Order;
import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
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

    Node selectedNode;

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
        osPage.setSize(1100, 800);
        osc = osPage.getContentPane();
        table = new OrderInfoTable(orders, bookInOrders);
        jxTable = table.getTreeTable();
        pane = new JScrollPane(table.getTreeTable());
        pane.setBounds(0, 0, 800, 800);

        // Row selection
        final String[] orderNo = new String[1];
        final String[] bookNo = new String[1];
        jxTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);    // Single selection allowed only

        jxTable.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                int selectedRow = jxTable.getSelectedRow();
                TreePath path = jxTable.getPathForRow(selectedRow);
                if (path == null) return;      // to prevent trigger this listener when refreshing the table
                selectedNode = table.getNode(path);
                // Disable selection for subheading rows
                if (((String[]) selectedNode.getUserObject())[0].equals("<Order No.>")) {
                    jxTable.clearSelection();
                    b1.setEnabled(false);
                    b2.setEnabled(false);
                    return;
                }
                orderNo[0] = jxTable.getStringAt(selectedRow, 0);
                bookNo[0] = jxTable.getStringAt(selectedRow, 1);

                System.out.println(orderNo[0]);
                System.out.println(bookNo[0]);
                if (selectedNode.isLeaf()) {    // objects.Book selected
                    b1.setEnabled(true);
                    b2.setEnabled(false);
                } else {    // objects.Order selected
                    b1.setEnabled(false);
                    b2.setEnabled(true);
                }
            }
        });

        b1 = new Button("Mark Delivered");
        b2 = new Button("Cancel Order");
        b1.setEnabled(false);
        b2.setEnabled(false);
        b1.setBounds(840, 50, 100, 40);
        b2.setBounds(840, 150, 100, 40);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedNode.getParent() != null &&
                        !((String[]) selectedNode.getParent().getUserObject())[3].equals("Confirmed") &&
                        !((String[]) selectedNode.getParent().getUserObject())[3].equals("Shipping")) {
                    JOptionPane.showMessageDialog(null, "The order is already " + ((String[]) selectedNode.getParent().getUserObject())[3] + "!", "", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                updateOrder(orderNo[0], bookNo[0]);
                refresh();
            }
        });

        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                orderCancelling(orderNo[0]);
                refresh();
            }
        });

        osc.add(b1);
        osc.add(b2);
        osc.add(pane);
        osPage.setVisible(true);
    }

    public void refresh() {
        getData();
        List<Integer> expandedRows = new ArrayList<>();

        for (int i = 0; i < jxTable.getRowCount(); i++) {
            if (jxTable.isExpanded(i)) expandedRows.add(i);
        }
        table.refresh(orders, bookInOrders);
        for (int i : expandedRows) {
            jxTable.expandRow(i);
        }
    }

    private void updateOrder(String orderNo, String bookNo) {
        try {
            dbConn.orderUpdate(orderNo, bookNo, new Date());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void orderCancelling(String orderNo) {
        try {
            dbConn.cancelOrder(orderNo);
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
            if (e.getErrorCode() == 20001)
                JOptionPane.showMessageDialog(null, "Some book(s) in this order is already delivered!", "", JOptionPane.ERROR_MESSAGE);
            else if (e.getErrorCode() == 20002)
                JOptionPane.showMessageDialog(null, "Wrong status: The order is already completed!", "", JOptionPane.ERROR_MESSAGE);
            else if (e.getErrorCode() == 20003)
                JOptionPane.showMessageDialog(null, "Wrong status: The order is already cancelled!", "", JOptionPane.ERROR_MESSAGE);
            else if (e.getErrorCode() == 20004)
                JOptionPane.showMessageDialog(null, "Wrong status: The order is unconfirmed!", "", JOptionPane.ERROR_MESSAGE);
            else if (e.getErrorCode() == 20017)
                JOptionPane.showMessageDialog(null, "This order is made over 7 days ago!", "", JOptionPane.ERROR_MESSAGE);
            else
                JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
        }
    }
}
