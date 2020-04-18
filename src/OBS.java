import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class OBS {
    DBConn dbConn = new DBConn("e8252125", "e8252125");

    public OBS() {
        List<String> sids = dbConn.selectSid();
//        List<String> sids = new ArrayList<>();
//        sids.add("1");

        JFrame homePage = new JFrame("Online University Bookshop");
        homePage.setSize(600, 400);
        homePage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = homePage.getContentPane();
        c.setLayout(new GridLayout(3, 1,20,20));
        c.add(new JLabel("Welcome to Online University Bookshop! Please choose one function!", SwingConstants.CENTER));

        Button b1 = new Button("Order Search");
        Button b2 = new Button("Order Making");
        b1.setBounds(50, 300, 200, 50);
        b1.setBounds(200, 300, 100, 50);
        c.add(b1);
        c.add(b2);

        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sid = login(sids);
                orderSearching(sid);

            }
        });

        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sid = login(sids);
                orderMaking(sid);
            }
        });

        homePage.setVisible(true);
    }

    public String login(List<String> sids) {
        boolean ifexist = false;
        String sid;
        do {
            JPanel panel = new JPanel();
            final TextField sidField = new TextField();
            panel.setLayout(new GridLayout(2, 1));
            panel.add(new JLabel("Please login with your student ID:"));
            panel.add(sidField);

            JOptionPane pane = new JOptionPane(panel) {
                public void selectInitialValue() {
                    sidField.requestFocusInWindow();
                }
            };

            JDialog dialog = pane.createDialog("Login");
            dialog.setVisible(true);
            dialog.dispose();

            sid = sidField.getText();

            for (String i : sids) {
                if (i.equals(sid)) {
                    ifexist = true;
                    break;
                }
            }

            if (!ifexist) {
                System.out.println("Students does not exist!"); //TODO 改为弹窗的形式
            }

        } while (!ifexist);

        return sid;
    }

    public void orderMaking(String sid) {
        List<Order> orders = dbConn.searchOrder(sid);
        for (Order i : orders) {
            if (i.status == 0 || i.status == 1) {
                System.out.println("You have outstanding orders!");
                return;
            }
        }

        payMethod();

        JFrame omPage = new JFrame("Order Making");
        omPage.setSize(800, 1000);
        omPage.setLayout(new GridLayout(1, 2));
        Container omc = omPage.getContentPane();
        JTextArea orderInfo = new JTextArea();
        orderInfo.setSize(400, 1000);

        String books = "1111\n2222\n3333\n4444";
        //将books的信息转换为string --TODO

        orderInfo.setText(books);
        omc.add(orderInfo);

        Panel addbooks = new Panel();
        Button b1 = new Button("Add");
        Button b2 = new Button("Confirm");
        Button b3 = new Button("Cancel");
        b1.setBounds(650, 50, 100, 40);
        b2.setBounds(650, 150, 50, 40);
        b3.setBounds(650, 250, 50, 40);

        omPage.setVisible(true);
    }

    public void payMethod() {

    }

    public void orderSearching(String sid) {
        JFrame osPage = new JFrame("All of your orders");
        osPage.setLayout(null);
        osPage.setSize(800, 1000);
        Container osc = osPage.getContentPane();
        JTextArea orderInfo = new JTextArea();
        orderInfo.setBounds(0, 0, 600, 1000);

        String orders = "1111\n2222\n3333\n4444";
        //将order的信息转换为string --TODO

        orderInfo.setText(orders);
        osc.add(orderInfo);

        Button b1 = new Button("Order Update");
        Button b2 = new Button("Order Cancelling");
        b1.setBounds(650, 50, 100, 40);
        b2.setBounds(650, 150, 100, 40);
        osc.add(b1);
        osc.add(b2);

        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                orderUpdate(sid);
            }
        });

        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                orderCancelling(sid);
            }
        });

        osPage.setVisible(true);

    }

    public void orderUpdate(String sid) {

    }

    public void orderCancelling(String sid) {

    }

    public static void main(String[] args) {
        OBS obs = new OBS();
    }

}