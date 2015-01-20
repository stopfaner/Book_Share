package ua.stopfan.bookshare.Library;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by stopfan on 12/14/14.
 */
public class Book {

    private String bookName;
    private List<Author> authors;
    private String authorName;
    private int yearOfProduction;
    private int countOfPages;
    private String publisher;
    private Genre genreOfBook;
    private Drawable image;
    private Color color;

    public Book() {
        bookName = null;
        authors = null;
        genreOfBook = null;
        image = null;
        color = null;
    }

    /**
     *
     * @param bookName name of book
     * @param authorName name of author of book to create new Author class
     */
    public Book(String bookName, String authorName, Drawable image) {
        this.bookName = bookName;
        this.authorName = authorName;
        this.image = image;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Drawable getImage() {
        return image;
    }

}
