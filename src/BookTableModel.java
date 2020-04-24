import javax.swing.table.DefaultTableModel;

public class BookTableModel extends DefaultTableModel {

    public BookTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
