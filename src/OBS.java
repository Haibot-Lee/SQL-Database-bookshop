import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import org.jdesktop.swingx.JXTreeTable;

public class OBS {
    DBConn dbConn = new DBConn("e8252125", "e8252125");

//    DBConn dbConn = new DBConn("e8250009", "e8250009", "faith.comp.hkbu.edu.hk", 22,
//            "e825xxxx", "********");

    public OBS() {
        List<String> sids = dbConn.selectSid();

        JFrame homePage = new JFrame("Online University Bookshop");
        homePage.setSize(600, 400);
        homePage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = homePage.getContentPane();
        c.setLayout(new GridLayout(4, 1, 20, 20));
        c.add(new JLabel("Welcome to Online University Bookshop! Please choose one function!", SwingConstants.CENTER));

        Button b1 = new Button("Order Search");
        Button b2 = new Button("Order Making");
        b1.setBounds(50, 300, 200, 50);
        b1.setBounds(200, 300, 100, 50);
        c.add(b1);
        c.add(b2);

        JLabel jl = new JLabel("You have outstanding orders! Can't make a new order!", SwingConstants.CENTER);
        c.add(jl);
        jl.setVisible(false);

        b1.addActionListener(e -> {
            String sid = login(sids);
            new OrderSearchWindow(dbConn, sid);

        });

        b2.addActionListener(e -> {
            jl.setVisible(false);
            String sid = login(sids);
            jl.setVisible(!orderMaking(sid));
        });

        homePage.setVisible(true);
    }

    public String login(List<String> sids) {
        boolean ifexist = true;
        String sid;
        do {
            JPanel panel = new JPanel();
            final TextField sidField = new TextField();
            panel.setLayout(new GridLayout(3, 1));
            panel.add(new JLabel("Please login with your student ID:"));
            panel.add(sidField);
            JLabel jl = new JLabel("Students does not exist!");
            jl.setVisible(!ifexist);
            panel.add(jl);

            JOptionPane pane = new JOptionPane(panel) {
                public void selectInitialValue() {
                    sidField.requestFocusInWindow();
                }
            };

            JDialog dialog = pane.createDialog("Login");
            dialog.setVisible(true);
            dialog.dispose();

            sid = sidField.getText();

            ifexist = false;
            for (String i : sids) {
                if (i.equals(sid)) {
                    ifexist = true;
                    break;
                }
            }

        } while (!ifexist);

        return sid;
    }

    public boolean orderMaking(String sid) {
        List<Order> orders = dbConn.searchOrder(sid);
        for (Order i : orders) {
            if (i.status == 0 || i.status == 1) {
                return false;
            }
        }

        String oid = "" + sid.charAt(6) + sid.charAt(7) + String.format("%02d", orders.size() % 100) + new Date().getTime() % 1000000;
        String[] payInfo = payMethod();
        dbConn.orderMaking(oid, sid, new Date(), payInfo[0], payInfo[1]);
        new OrderMakingWindow(dbConn, oid);

        return true;
    }

    public String[] payMethod() {
        String[] payInfo = {null, null};

        do {
            JComboBox<String> pay = new JComboBox<>();
            JLabel jl = new JLabel("Please choose one method to pay");
            pay.addItem("Credit card");
            pay.addItem("Bank transfer");
            pay.addItem("Cash");
            pay.addItem("Octopus");
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 1));
            panel.add(jl);
            panel.add(pay);

            JOptionPane pane = new JOptionPane(panel) {
                public void selectInitialValue() {
                    pay.requestFocusInWindow();
                }
            };

            JDialog dialog = pane.createDialog("Pay method");
            dialog.setVisible(true);
            dialog.dispose();

            payInfo[0] = (String) pay.getSelectedItem();

            if (payInfo[0].equals("Credit card")) {

                JPanel panel2 = new JPanel();
                final TextField cardNo = new TextField();
                panel2.setLayout(new GridLayout(2, 1));
                panel2.add(new JLabel("Please input your credit card No.:"));
                panel2.add(cardNo);

                JOptionPane pane2 = new JOptionPane(panel2) {
                    public void selectInitialValue() {
                        cardNo.requestFocusInWindow();
                    }
                };

                JDialog dialog2 = pane2.createDialog("Credit Card No");
                dialog2.setVisible(true);
                dialog2.dispose();

                payInfo[1] = cardNo.getText();
            }

        } while (payInfo[0].equals("Credit card") && payInfo[1].equals(""));

        return payInfo;
    }

    public void orderCancelling(String sid) {

    }

    public static void main(String[] args) {
        OBS obs = new OBS();
    }

}