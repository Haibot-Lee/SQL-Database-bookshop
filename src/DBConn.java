import java.text.SimpleDateFormat;
import java.util.Date;

public class DBConn {

    public void login() {

    }

    public void orderMaking(String stuNo) {
        //check whether he can make order or not
        if (ifAddOrder(stuNo)) {
            // create orderNo, orderDate
            String orderNo = "";

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-YYYY");
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

    }
}
