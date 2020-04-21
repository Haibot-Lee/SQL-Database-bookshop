import javax.swing.*;
import java.awt.*;

public class OrderMakingWindow {
    DBConn dbConn;
    String oid;
    String[] payInfo;
    JFrame omPage;
    Container omc;
    JTextArea orderInfo;
    Panel addBooks;
    JLabel bookL;
    JTextField bookT;
    JLabel qtyL;
    JTextField qtyT;
    Button b1, b2;
    JLabel ifAdd;

    public OrderMakingWindow(DBConn dbConn, String oid) {
        this.dbConn = dbConn;
        this.oid = oid;
        initialize();
    }

    private void initialize() {
        omPage = new JFrame("Order Making");
        omPage.setLayout(new GridLayout(1, 2));
        omPage.setSize(1000, 800);
        omPage.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        omc = omPage.getContentPane();
        orderInfo = new JTextArea();
        orderInfo.setSize(500, 800);

        String books = "Your books in this order (Order No.:" + oid + "):\n" +
                "-----------------------------------------------------------------------------------------------\n";
        //将books的信息转换为string --TODO
        orderInfo.setText(books);

        omc.add(orderInfo);
        addBooks = new Panel();
        addBooks.setLayout(null);
        omc.add(addBooks);

        bookL = new JLabel("Input book No. here:");
        bookT = new JTextField(100);
        qtyL = new JLabel("Input quantity here:");
        qtyT = new JTextField(100);
        bookL.setBounds(50, 50, 200, 20);
        bookT.setBounds(50, 80, 100, 20);
        qtyL.setBounds(50, 120, 200, 20);
        qtyT.setBounds(50, 150, 100, 20);

        b1 = new Button("Add");
        b2 = new Button("Confirm");
        b1.setBounds(50, 200, 100, 40);
        b2.setBounds(50, 600, 100, 40);

        addBooks.add(bookL);
        addBooks.add(bookT);
        addBooks.add(qtyL);
        addBooks.add(qtyT);
        addBooks.add(b1);
        addBooks.add(b2);

        ifAdd = new JLabel();
        ifAdd.setBounds(50, 260, 500, 20);
        addBooks.add(ifAdd);
        ifAdd.setVisible(false);

        omPage.setVisible(true);

        // Action settings
        bookT.addActionListener(e -> bookT.requestFocusInWindow());
        qtyT.addActionListener(e -> bookT.requestFocusInWindow());
        b1.addActionListener(e -> {
            String book_no = bookT.getText();
            String qty = qtyT.getText();
            bookT.setText("");
            qtyT.setText("");

            int conditions = addBook(oid, book_no, qty);
            if (conditions == -1)
                ifAdd.setText("Add books successfully!");
            else if (conditions == -2)
                ifAdd.setText("Fail to add books: Please input book No. and quantity!");
            else if (conditions == -3)
                ifAdd.setText("Fail to add books: Book does not exists!");
            else
                ifAdd.setText("Fail to add books: This book(book No:" + book_no + ") is out of stock! Remaining quantity: " + conditions);

            ifAdd.setVisible(true);
        });
        b2.addActionListener(e -> omPage.dispose());
    }

    private int addBook(String order_no, String book_no, String qty) {
        if (book_no.equals("") || qty.equals(""))
            return -2;
        else {
            int stock = dbConn.selectStock(book_no);
            if (stock == -1)
                return -3;
            else if (stock < Integer.parseInt(qty))
                return stock;
        }
        dbConn.addBook(order_no, book_no, Integer.parseInt(qty));
        return -1;
    }


}
