package objects;

import java.util.Date;

public class Order {
    public final String orderNo;
    public final String stuNo;
    public final Date orderDate;
    public final int status;
    public final float totalPrice;
    public final String payMethod;
    public final String cardNo;
    // About "final": the data in the object should be retrieved from online DB.
    //      Thus, local update is not allowed.

    public Order(String orderNo, String stuNo, Date orderDate, int status, float totalPrice, String payMethod, String cardNo) {
        this.orderNo = orderNo;
        this.stuNo = stuNo;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.payMethod = payMethod;
        this.cardNo = cardNo;
    }


    @Override
    public String toString() {
        return "objects.Order{" +
                "orderNo='" + orderNo + '\'' +
                ", stuNo='" + stuNo + '\'' +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                ", payMethod='" + payMethod + '\'' +
                ", cardNo='" + cardNo + '\'' +
                '}';
    }
}
