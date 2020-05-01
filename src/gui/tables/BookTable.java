package gui.tables;

import gui.tables.BookTableModel;

import javax.swing.*;
import java.util.List;

/**
 * This class is extended by:
 *      * gui.tables.StockTable, which shows the information of Books in stock;
 *      * gui.tables.BookInOrderTable, which shows the information of BookInOrders
 *          in current order;
 *      (I admit that the naming is a bit confusing...)
 */
public abstract class BookTable {
    String[] headings;
    BookTableModel model;
    JTable table;
    List items;
    String[][] data;

    public BookTable(List items, String[] headings) {
        this.items = items;
        this.headings = headings;
        importData();
        createModel();
        createTable();
    }

    abstract void importData();

    private void createModel() {
        model = new BookTableModel(data, headings);
    }

    private void createTable() {
        table = new JTable(model);
        table.setShowGrid(true);
    }

    public JTable getTable() {
        return table;
    }

    public void refresh(List items) {
        this.items = items;
        importData();
        model.setDataVector(data, headings);
    }
}
