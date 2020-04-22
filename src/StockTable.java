import java.util.List;

public class StockTable extends BookTable {


    public StockTable(List<Book> books) {
        super(books, new String[] {"Book No.", "Book Title", "Author", "Price", "Stock"});
    }

    void importData() {
        super.data = new String[super.items.size()][5];
        System.out.println("books.size(): " + super.items.size());
        for (int i=0; i<super.items.size(); i++) {
            Book b = (Book) super.items.get(i);
            data[i] = new String[] {b.bookNo, b.title, b.author,
                    String.valueOf(b.price), String.valueOf(b.stock)};
            for (String s : data[i]) {
                System.out.print(s);
            }
            System.out.println();
        }
    }

}
