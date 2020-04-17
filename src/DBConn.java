import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

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

    public ResultSet orderSearch(String sid) {
        try {
            Statement stm = conn.createStatement();
            String sql = "SELECT *\n" +
                    "FROM ORDERS O NATURAL JOIN BOOK_IN_ORDERS BO\n" +
                    "WHERE O.stu_no = '" + sid + "'";
            return stm.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
}

    public void orderUpdate() {

    }

    public static void main(String[] args) {
        orderSearchTest();
    }



    /************* TESTING AREA ***************/
    private static void orderSearchTest() {
        // On-campus test
//        DBConn dbConn = new DBConn("e825xxxx", "e825xxxx");

        // Off-campus test
        DBConn dbConn = new DBConn("e825xxxx", "e825xxxx",
                "faith.comp.hkbu.edu.hk", 22, "e825xxxx", "******");
        // TODO: 给队友的温馨提示：Commit 前记得删掉密码😂😂

        try {
            ResultSet rs = dbConn.orderSearch("22222222");
            String[] heads = {"order_no", "stu_no", "order_date", "status", "total_price", "payment_method",
                    "card_no", "book_no", "qty", "deliver_date"};
            for (int i=0; i<10; i++) {
                System.out.print(heads[i] + "\t");
            }
            System.out.println();
            while (rs.next()) {
                for (int i=1; i<=10; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
