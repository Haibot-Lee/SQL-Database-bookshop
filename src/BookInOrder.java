import java.util.Date;

public class BookInOrder {
    final String orderNo;
    final String bookNo;
    final int qty;
    final Date deliverDate;
    // About "final": the data in the object should be retrieved from online DB.
    //      Thus, local update is not allowed.

    public BookInOrder(String orderNo, String bookNo, int qty, Date deliverDate) {
        this.orderNo = orderNo;
        this.bookNo = bookNo;
        this.qty = qty;
        this.deliverDate = deliverDate;
    }


    @Override
    public String toString() {
        return "BookInOrder{" +
                "orderNo='" + orderNo + '\'' +
                ", bookNo='" + bookNo + '\'' +
                ", qty=" + qty +
                ", deliverDate=" + deliverDate +
                '}';
    }
}
