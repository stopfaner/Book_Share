package ua.stopfan.bookshare.Library;

/**
 * Created by stopfan on 12/14/14.
 */
public class Book {

    private String bookName;
    private Author author;

    public Book() {

        bookName = null;
        author = null;
    }

    /**
     *
     * @param bookName name of book
     * @param authorName name of author of book to create new Author class
     */
    public Book(String bookName, String authorName) {

        this.bookName = bookName;
    }


}
