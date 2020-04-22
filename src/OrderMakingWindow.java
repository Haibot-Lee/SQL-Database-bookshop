import javax.swing.*;
import java.awt.Container;
import java.awt.Panel;
import java.awt.Button;
import java.awt.GridLayout;
import java.util.List;

public class OrderMakingWindow {
    DBConn dbConn;
    String oid;
    String[] payInfo;
    JFrame omPage;
    JScrollPane bookPane;
    Container omc;

    BookTable bookTable;
    BookInOrderTable bookInOrderTable;

    Panel addBooks;
    JLabel bookL;
    JTextField bookT;
    JLabel qtyL;
    JTextField qtyT;
    Button b1, b2;
    JLabel ifAdd;
    List<Book> books;
    List<BookInOrder> bookInOrder;

    public OrderMakingWindow(DBConn dbConn, String oid) {
        this.dbConn = dbConn;
        this.oid = oid;
        getData();
        initialize();
    }

    private void initialize() {
        omPage = new JFrame("Order Making");
        omPage.setLayout(new GridLayout(1, 2));
        omPage.setSize(1000, 800);
        omPage.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        omc = omPage.getContentPane();

        bookTable = new BookTable(books);
        bookPane = new JScrollPane(bookTable.getTable());
        bookPane.setSize(500, 800);
        omc.add(bookPane);

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
            addBook();
            // refresh tables
        });
        b2.addActionListener(e -> omPage.dispose());
    }

    private void addBook() {
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

    private void getData() {
        books = dbConn.listBooks();
        bookInOrder = dbConn.searchBookInOrder(oid);
    }

    public void refresh() {
        getData();

    }
}
