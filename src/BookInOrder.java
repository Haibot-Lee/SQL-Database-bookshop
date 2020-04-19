import java.util.Date;

public class BookInOrder {
    final String bookNo;
    final String title;
    final int qty;
    final Date deliverDate;
    // About "final": the data in the object should be retrieved from online DB.
    //      Thus, local update is not allowed.

    @Override
    public String toString() {
        return "BookInOrder{" +
                "bookNo='" + bookNo + '\'' +
                ", title='" + title + '\'' +
                ", qty=" + qty +
                ", deliverDate=" + deliverDate +
                '}';
    }

    public BookInOrder(String bookNo, String title, int qty, Date deliverDate) {
        this.bookNo = bookNo;
        this.title = title;
        this.qty = qty;
        this.deliverDate = deliverDate;
    }


}
