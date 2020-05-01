package objects;

public class Book {
    public final String bookNo;
    public final String title;
    public final String author;
    public final float price;
    public final int stock;

    public Book(String bookNo, String title, String author, float price, int stock) {
        this.bookNo = bookNo;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
    }
}
