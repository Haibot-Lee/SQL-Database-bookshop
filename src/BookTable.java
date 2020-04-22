import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class BookTable {
    private String[] headings = {"Book No.", "Book Title", "Author", "Price", "Stock"};
    private DefaultTableModel model;
    private JTable table;
    private List<Book> books;
    private String[][] data;

    public BookTable(List<Book> books) {
        this.books = books;
        importData();
        createModel();
        createTable();
    }

    private void importData() {
        data = new String[books.size()][5];
        System.out.println("books.size(): " + books.size());
        for (int i=0; i<books.size(); i++) {
            Book b = books.get(i);
            data[i] = new String[] {b.bookNo, b.title, b.author,
                    String.valueOf(b.price), String.valueOf(b.stock)};
            for (String s : data[i]) {
                System.out.print(s);
            }
            System.out.println();
        }

    }

    private void createModel() {
        model = new DefaultTableModel(data, headings);
    }

    private void createTable() {
        table = new JTable(model);
        table.setShowGrid(true);
    }

    public JTable getTable() {
        return table;
    }

    public void refresh(List<Book> books) {
        this.books = books;
        importData();
        model.setDataVector(data, null);  // todo check?
        table.updateUI();
    }



}
