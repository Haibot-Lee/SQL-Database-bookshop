package gui.tables;

import gui.tables.BookTable;
import objects.BookInOrder;

import java.util.List;

public class BookInOrderTable extends BookTable {

    public BookInOrderTable(List<BookInOrder> bookInOrders) {
        super(bookInOrders, new String[] {"objects.Book No.", "Title", "Quantity"});
    }
    @Override
    void importData() {
        super.data = new String[super.items.size()][3];
        System.out.println("items.size(): " + super.items.size());
        for (int i=0; i<super.items.size(); i++) {
            BookInOrder b = (BookInOrder) super.items.get(i);
            data[i] = new String[] {b.bookNo, b.title, String.valueOf(b.qty)};
            for (String s : data[i]) {
                System.out.print(s);
            }
            System.out.println();
        }
    }
}
