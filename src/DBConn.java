import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.CallableStatement;

import java.util.ArrayList;
import java.util.List;

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

    public void orderMaking(String orderNo, String stuNo, Date orderDate, String payMethod, String cardNo) {
        try {
            Statement stm = conn.createStatement();
            String sql = "INSERT INTO ORDERS(order_no, stu_no, order_date, pay_method, card_no) VALUES(\'"
                    + orderNo + "\',\'"
                    + stuNo + "\',\'"
                    + new SimpleDateFormat("dd-MMM-yyyy").format(orderDate) + "\',\'"
                    + payMethod + "\',"
                    + cardNo + ")";
            stm.executeUpdate(sql);
            stm.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add %qty% books into the designated order.
     *
     * @param orderNo ORDER_NO
     * @param bookNo  BOOK_NO
     * @param qty     quantity to add
     */
    public void addBook(String orderNo, String bookNo, int qty) {
        try {
            CallableStatement cs = conn.prepareCall("{CALL ADD_BOOK_IN_ORDER('" + orderNo +
                        "', '" + bookNo + "', " + qty + ")}");
            cs.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> searchOrder(String stuNo) {
        List<Order> orders = new ArrayList<Order>();
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT *\n" +
                    "FROM ORDERS O\n" +
                    "WHERE O.stu_no = '" + stuNo + "'";
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                orders.add(new Order(rs.getString(1), rs.getString(2), rs.getDate(3),
                        rs.getInt(4), rs.getFloat(5), rs.getString(6), rs.getString(7)));
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<BookInOrder> searchBookInOrder(String orderNo) {
        List<BookInOrder> books = new ArrayList<BookInOrder>();
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT book_no, title, qty, deliver_date\n" +
                    "FROM BOOK_IN_ORDERS BO NATURAL JOIN BOOKS\n" +
                    "WHERE BO.order_no = '" + orderNo + "'";
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                books.add(new BookInOrder(rs.getString(1), rs.getString(2),
                        rs.getInt(3), rs.getDate(4)));
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public void orderUpdate(String orderNo, String bookNo, Date date) {
        try {
            Statement stm = conn.createStatement();
            String sql = "UPDATE BOOK_IN_ORDERS\n" +
                    "SET DELIVER_DATE = '" + new SimpleDateFormat("dd-MMM-yyyy").format(date) +
                    "'\n WHERE order_no = '" + orderNo + "' AND book_no = '" + bookNo + "'";
            System.out.println(sql);
            stm.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> selectSid() {
        List<String> sid = new ArrayList<String>();
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT stu_no FROM STUDENTS";
            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()) {
                sid.add(rs.getString(1));
            }
            rs.close();
            stm.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sid;
    }

    public static void main(String[] args) {
        orderMakingTest();
    }


    /************* TESTING AREA ***************/
    private static void orderSearchTest() {
        DBConn dbConn = new DBConn("e8252125", "e8252125");
        List<Order> orders = dbConn.searchOrder("22222222");
        List<BookInOrder> books = dbConn.searchBookInOrder("222222221");

        for (Order i : orders) {
            System.out.println(i);
        }

        for (BookInOrder i : books) {
            System.out.println(i);
        }

        dbConn.orderUpdate("222222221", "002", new Date());
        System.out.println();

        orders = dbConn.searchOrder("22222222");
        books = dbConn.searchBookInOrder("222222221");

        for (Order i : orders) {
            System.out.println(i);
        }

        for (BookInOrder i : books) {
            System.out.println(i);
        }


    }

    private static void orderMakingTest() {
        DBConn dbConn = new DBConn("e8250009", "e8250009");
        String cardNo = "'asd'";
        dbConn.orderMaking("234", "11111111", new Date(), "CASH", cardNo);
    }
}

// TODO LIST
//  * add close() method