import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OBS {

    public OBS() {
        DBConn db = new DBConn("e8252125", "e8252125");
        JFrame homePage=new JFrame();
        homePage.setTitle("Online University Bookshop");
        homePage.setSize(800, 400);
        homePage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = homePage.getContentPane();
        c.setLayout(new GridLayout(8,1));
        c.add(new JLabel("Welcome to Online University Bookshop! Please choose one function!", SwingConstants.CENTER));

        Button b1 = new Button("Order Search");
        Button b2 = new Button("Order Making");
        b1.setBounds(50,300,200,50);
        b1.setBounds(200,300,100,50);
        c.add(b1);
        c.add(b2);

        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] students={"123"};
                String sid=login(students);
                orderSearching(sid);
            }
        });

        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] students={"123"};
                String sid=login(students);
                orderMaking(sid);
            }
        });

        homePage.setVisible(true);
    }

    public void orderSearching(String sid){

    }

    public void orderMaking(String sid){

    }

    public String login(String[] students) {
        boolean ifexist = false;
        String sid;
        do {
            JPanel panel = new JPanel();
            final TextField sidField = new TextField();
            panel.setLayout(new GridLayout(2, 1));
            panel.add(new JLabel("Please input your student ID:"));
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

            for (String i : students) {
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

    public static void main(String[] args) {
        OBS obs = new OBS();
    }

}