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

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

public class DBConn {
    private Connection conn;
    // Database Host
    private final String dbHost = "orasrv1.comp.hkbu.edu.hk";
    // Database Port
    private final int dbPort = 1521;

    private final String database = "pdborcl.orasrv1.comp.hkbu.edu.hk";
    private String jdbcHost = dbHost;
    private int jdbcPort = dbPort;

    private Session proxySession;
    private String fwHost = "localhost";
    private int fwPort;

    public DBConn(String dbuser, String dbpw) {
        loginDB(dbuser, dbpw);
    }

    public DBConn(String dbUser, String dbPw, String proxyHost, int proxyPort, String proxyUser, String proxyPw) {
        loginProxy(proxyHost, proxyPort, proxyUser, proxyPw);
        loginDB(dbUser, dbPw);

        // TODO: Handle exceptions, such as network failure. Consider GUI. Maybe let the
        //      two method throw exceptions instead of returning booleans?
    }

    public boolean loginDB(String dbuser, String dbpw) {
        String url = "jdbc:oracle:thin:@" + jdbcHost + ":" + jdbcPort + "/" + database;
        System.out.println("Logging in database: " + url);
        try {
            conn = DriverManager.getConnection(url, dbuser, dbpw);
            System.out.println("Database connected: " + database);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean loginProxy(String host, int port, String user, String pw) {
        System.out.println("Logging in proxy: " + host + ":" + port);
        try {
            proxySession = new JSch().getSession(user, host, port);
            proxySession.setPassword(pw);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            proxySession.setConfig(config);

            proxySession.connect();
            proxySession.setPortForwardingL(fwHost, 0, dbHost, dbPort);
            fwPort = Integer.parseInt(proxySession.getPortForwardingL()[0].split(":")[0]);

        } catch (JSchException e) {
            e.printStackTrace();
            return false;
        }
        jdbcHost = fwHost;
        jdbcPort = fwPort;
        return true;
    }


    public void orderMaking(String orderNo, String stuNo, Date orderDate, String payMethod, String cardNo) {
        try {
            Statement stm = conn.createStatement();
            String sql = "INSERT INTO ORDERS(order_no, stu_no, order_date, pay_method, card_no) VALUES('"
                    + orderNo + "','"
                    + stuNo + "','"
                    + new SimpleDateFormat("dd-MMM-yyyy").format(orderDate) + "\',\'"
                    + payMethod + "','"
                    + cardNo + "')";
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
            cs.close();
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

    public List<Book> listBooks() {
        List<Book> books = new ArrayList<>();
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT book_no, title, author, price, stock FROM BOOKS";
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                books.add(new Book(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getFloat(4), rs.getInt(5)));
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
            CallableStatement cs = conn.prepareCall("{CALL update_status_books_and_order(?,?,?)}");
            cs.setString(1, orderNo);
            cs.setString(2, bookNo);
            cs.setDate(3, new java.sql.Date(new Date().getTime()));
            System.out.println("call prepared");

            cs.execute();
            System.out.println("executed");
            cs.close();
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

    public int selectStock(String bookNo) {
        int stock = -1;
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT stock\n" +
                    "FROM BOOKS\n" +
                    "WHERE book_no = '" + bookNo + "'";
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                stock = rs.getInt(1);
            }
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stock;   // -1: Book does not exist  0: Out of stock  others: Book in stocks
    }

    public void cancelOrder(String orderNo) {
        try {
            CallableStatement cs = conn.prepareCall("{CALL cancel_status_books_and_order(?,?,?)}");
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM BOOK_IN_ORDERS WHERE order_no = '" + orderNo + "'";
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                cs.setString(1, rs.getString(1));
                cs.setString(2, rs.getString(2));
                cs.setString(3, rs.getString(3));
                System.out.println("call prepared");
                cs.execute();
                System.out.println("executed");
            }
            String sql2 = "UPDATE ORDERS SET status = 3 WHERE ORDERS.order_no = '" + orderNo + "' AND status = 0";
            stm.executeQuery(sql2);
            cs.close();
            rs.close();
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}