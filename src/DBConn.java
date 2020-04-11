import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConn {
    private Connection conn;
    // Database Host
    private final String dbHost = "orasrv1.comp.hkbu.edu.hk";
    // Database Port
    private final int dbPort = 1521;

    private final String database = "pdborcl.orasrv1.comp.hkbu.edu.hk";
    private String jdbcHost = dbHost;
    private int jdbcPort = dbPort;

    public DBConn(String dbuser, String dbpw) {
        loginDB(dbuser, dbpw);
    }

    public boolean loginDB(String dbuser, String dbpw) {
        String url = "jdbc:oracle:thin:@" + jdbcHost + ":" + jdbcPort + "/" + database;
        try {
            conn = DriverManager.getConnection(url, dbuser, dbpw);
            System.out.println("Database connected: " + database);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void orderMaking(String stuNo) {
        //check whether he can make order or not
        if (ifAddOrder(stuNo)) {
            // create orderNo, orderDate
            String orderNo = "";

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-YYYY");  // TODO 时间-系统语言冲突
            String orderDate = dateFormat.format(date);

            //let user choose pay method: 1. cash 2.octopus 3.credit card
            String payMethod = "";
            String cardNo;
            if (payMethod.equals("credit card")) {
                cardNo = "let user input here";
                cardNo = "'" + cardNo + "'";
            } else
                cardNo = "NULL";

            // use this SQL to insert order
            String sql = "INSERT INTO ORDERS(order_no, stu_no, order_date, pay_method, card_no) VALUES(\'"
                    + orderNo + "\',\'"
                    + stuNo + "\',\'"
                    + orderDate + "\',\'"
                    + payMethod + "\',"
                    + cardNo + ")";

            // add books to order
            boolean ifAddBook;
            do {
                ifAddBook = false;
                addBook(orderNo);
                //if continue add, ifAddBook -> true.

            } while (ifAddBook == true);
        }
    }

    private boolean ifAddOrder(String stuNo) {

        return true;
    }

    private void addBook(String orderNo) {
        //let user choose book and quantity
        String bookNo = "";
        int qty = 1;

        // use this SQL to insert book
        String sql = "INSERT INTO ORDERS(order_no, book_no, qty) VALUES(\'"
                + orderNo + "\',\'" + bookNo + "\'," + qty + ")";
    }

    public void orderSearch() {

    }

    public void orderUpdate() {

    }

    public static void main(String[] args) {
        DBConn dbConn = new DBConn("e8250009", "e8250009");
    }
}
