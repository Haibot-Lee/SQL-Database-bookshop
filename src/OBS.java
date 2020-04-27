import com.jcraft.jsch.JSchException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class OBS {
    DBConn dbConn;

    public OBS(DBConn dbConn) {
        this.dbConn = dbConn;

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
            String sid = inputSID(sids);
            new OrderSearchWindow(dbConn, sid);

        });

        b2.addActionListener(e -> {
            jl.setVisible(false);
            String sid = inputSID(sids);
            jl.setVisible(!orderMaking(sid));
        });

        homePage.setVisible(true);
    }

    public String inputSID(List<String> sids) {
        boolean ifexist = true;
        String sid;
        do {
            JPanel panel = new JPanel();
            final TextField sidField = new TextField();
            panel.setLayout(new GridLayout(3, 1));
            panel.add(new JLabel("Please input your student ID:"));
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
        String[] payInfo = {null, ""};

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

            if (payInfo[1].equals("")) {
                JOptionPane.showMessageDialog(null, "Please Input your credit No.!", "", JOptionPane.ERROR_MESSAGE);
            }

        } while (payInfo[0].equals("Credit card") && payInfo[1].equals(""));

        return payInfo;
    }

    public static void main(String[] args) {
        JFrame login = new JFrame("Login");
        login.setSize(900, 300);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = login.getContentPane();
        c.setLayout(new GridLayout(2, 1));
        JPanel[] dbLogin = new JPanel[3];
        for (int i = 0; i < dbLogin.length; i++) {
            dbLogin[i] = new JPanel();
        }
        JPanel[] proxy = new JPanel[3];
        for (int i = 0; i < proxy.length; i++) {
            proxy[i] = new JPanel();
        }

        c.add(dbLogin[0]);
        c.add(proxy[0]);
        proxy[0].setVisible(false);

        //Login into database!
        dbLogin[0].setLayout(new GridLayout(2, 1));
        for (int i = 1; i < dbLogin.length; i++) {
            dbLogin[0].add(dbLogin[i]);
        }
        dbLogin[1].add(new JLabel("Database user name:"));
        JTextField dbUserName = new JTextField(25);
        dbLogin[1].add(dbUserName);
        dbLogin[1].add(new JLabel("Database user password:"));
        JPasswordField dbUserPasw = new JPasswordField(25);
        dbLogin[1].add(dbUserPasw);
        JCheckBox ifUseProxy = new JCheckBox("Use SSH proxy");
        dbLogin[2].add(ifUseProxy);
        JButton confirm = new JButton("Login");
        dbLogin[2].add(confirm);

        //Choose proxy
        dbLogin[0].setLayout(new GridLayout(2, 1));
        for (int i = 1; i < proxy.length; i++) {
            proxy[0].add(proxy[i]);
        }
        proxy[1].add(new Label("Proxy Host:"));
        JTextField proxyHost = new JTextField(50);
        proxy[1].add(proxyHost);
        proxy[1].add(new Label("Proxy Port:"));
        JTextField proxyPort = new JTextField(5);
        proxy[1].add(proxyPort);
        proxy[2].add(new Label("Proxy user name:"));
        JTextField proxyUserName = new JTextField(25);
        proxy[2].add(proxyUserName);
        proxy[2].add(new Label("Proxy user password:"));
        JPasswordField proxyUserPasw = new JPasswordField(25);
        proxy[2].add(proxyUserPasw);

        ifUseProxy.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ifUseProxy.isSelected())
                    proxy[0].setVisible(true);
                else
                    proxy[0].setVisible(false);
            }
        });
        login.setVisible(true);

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean ifLogin = false;
                if (ifUseProxy.isSelected()) {
                    try {
//                        OBS obs = new OBS(new DBConn(dbUserName.getText(), new String(dbUserPasw.getPassword()),
//                            proxyHost.getText(), Integer.parseInt(proxyPort.getText()), proxyUserName.getText(), new String(proxyUserPasw.getPassword())));
                        OBS obs = new OBS(new DBConn("e825215", "e8252125",
                                "faith.comp.hkbu.edu.hk", 22, "e8252125", ""));
                        ifLogin = true;
                    } catch (JSchException ex) {
                        String errorMess = "Fail to login proxy: ";
                        if (ex.getCause() instanceof java.net.UnknownHostException)
                            errorMess += "please check your proxy host address!";
                        else if (ex.getCause() instanceof java.net.ConnectException)
                            errorMess = errorMess + "please check your proxy port";
                        else
                            errorMess = errorMess + "please check your user name/password!";

                        JOptionPane.showMessageDialog(null, errorMess, "", JOptionPane.ERROR_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Fail to login database: please check your user name/password!", "", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    try {
//                        OBS obs = new OBS(new DBConn(dbUserName.getText(), dbUserPasw.getText());
                        OBS obs = new OBS(new DBConn("e8252125", "e8252125"));
                        ifLogin = true;
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Fail to login database: please check your user name/password!", "", JOptionPane.ERROR_MESSAGE);
                    }
                }

                if (ifLogin == true) {
                    login.dispose();
                }
            }
        });

    }

}