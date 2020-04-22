import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * This class is extended by:
 *      * StockTable, which shows the information of Books in stock;
 *      * BookInOrderTable, which shows the information of BookInOrders
 *          in current order;
 *      (I admit that the naming is a bit confusing...)
 */
public abstract class BookTable {
    String[] headings;
    DefaultTableModel model;
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
        model = new DefaultTableModel(data, headings);
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
        model.setDataVector(data, null);  // todo check?
        table.updateUI();
    }
}
