package gui.windows;

import connection.DBConn;
import gui.tables.BookInOrderTable;
import gui.tables.StockTable;
import objects.Book;
import objects.BookInOrder;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Container;
import java.awt.Panel;
import java.awt.Button;
import java.awt.GridLayout;
import java.util.List;

public class OrderMakingWindow {
    DBConn dbConn;
    String oid;
    JFrame omPage;
    JScrollPane bookPane;
    JScrollPane bookInOrderPane;
    Container omc;

    StockTable stockTable;
    BookInOrderTable bookInOrderTable;

    Panel addBooks, options;
    JLabel bookL;
    JTextField bookT;
    JLabel qtyL;
    JSpinner qtyS;
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
        omPage = new JFrame("objects.Order Making");
        omPage.setLayout(new GridLayout(1, 2));
        omPage.setSize(1000, 800);
        omPage.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        omc = omPage.getContentPane();

        // objects.Book in stock Table
        stockTable = new StockTable(books);
        bookPane = new JScrollPane(stockTable.getTable());
        omc.add(bookPane);
        ListSelectionModel selectionModel = stockTable.getTable().getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                int selectedRow = stockTable.getTable().getSelectedRow();
                if (selectedRow != -1)  // to prevent trigger this listener when refreshing the table
                    bookT.setText((String) stockTable.getTable().getValueAt(selectedRow, 0));
            }
        });

        addBooks = new Panel();
        addBooks.setLayout(new GridLayout(2, 1));
        omc.add(addBooks);

        // objects.Book in order Table
        bookInOrderTable = new BookInOrderTable(bookInOrder);
        bookInOrderPane = new JScrollPane(bookInOrderTable.getTable());
        addBooks.add(bookInOrderPane);

        // AddBook Operation Panel
        options = new Panel();
        options.setLayout(null);
        addBooks.add(options);

        bookL = new JLabel("objects.Book No.:");
        bookT = new JTextField(100);
        qtyL = new JLabel("Quantity:");
        qtyS = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        bookL.setBounds(50, 50, 200, 20);
        bookT.setBounds(50, 80, 100, 20);
        qtyL.setBounds(50, 120, 200, 20);
        qtyS.setBounds(50, 150, 100, 20);

        b1 = new Button("Add");
        b2 = new Button("Confirm");
        b1.setBounds(300, 100, 100, 40);
        b2.setBounds(150, 300, 200, 40);

        options.add(bookL);
        options.add(bookT);
        options.add(qtyL);
        options.add(qtyS);
        options.add(b1);
        options.add(b2);

        ifAdd = new JLabel();
        ifAdd.setBounds(50, 200, 500, 40);
        options.add(ifAdd);
        ifAdd.setVisible(false);

        omPage.setVisible(true);

        // Action settings
        b1.addActionListener(e -> {
            addBook();
            refresh();
        });
        b2.addActionListener(e -> {
            dbConn.confirmOrder(oid);
            omPage.dispose();
        });
    }

    private void addBook() {
        String book_no = bookT.getText();
        int qty = (int) qtyS.getValue();
        bookT.setText("");
        qtyS.setValue(1);

        if (book_no.equals("")) {
            ifAdd.setText("Fail to add books: Please input book No. and quantity!");
            ifAdd.setVisible(true);
            return;
        } else {
            int stock = dbConn.selectStock(book_no);
            if (stock == -1) {
                ifAdd.setText("Fail to add books: This book(book No:" + book_no + ") does not exists!");
                ifAdd.setVisible(true);
                return;
            } else if (stock < qty) {
                ifAdd.setText("<html>Fail to add books: This book(book No:" + book_no + ") is out of stock!<br>Remaining quantity: " + stock + "</html>");
                ifAdd.setVisible(true);
                return;
            }
        }

        dbConn.addBook(oid, book_no, qty);
        ifAdd.setText("Add book(s) successfully!");
        ifAdd.setVisible(true);
    }

    private void getData() {
        books = dbConn.listBooks();
        bookInOrder = dbConn.searchBookInOrder(oid);
    }

    public void refresh() {
        getData();
        stockTable.refresh(books);
        bookInOrderTable.refresh(bookInOrder);
    }
}