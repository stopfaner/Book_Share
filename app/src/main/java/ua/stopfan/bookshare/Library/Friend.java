package ua.stopfan.bookshare.Library;

import com.parse.ParseFile;

/**
 * Created by denys on 4/23/15.
 */
public class Friend {

    private String name;
    private int count;
    private String id;
    private String image;

    public Friend(String name, String id, String image) {
        this.name = name;
        //this.count = count;
        this.id = id;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }
}
