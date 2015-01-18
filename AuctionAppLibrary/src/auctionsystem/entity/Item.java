
package auctionsystem.entity;

import java.io.Serializable;

public class Item implements Serializable{

    private Integer id;
    private String description;
    private String image;
    private static int countId = 0;
    
    public Item() {
        countId++;
        this.id = countId;
    }

    public Item(String description, String image) {
        this();
        this.description = description;
        this.image = image;
        this.id = countId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

}
