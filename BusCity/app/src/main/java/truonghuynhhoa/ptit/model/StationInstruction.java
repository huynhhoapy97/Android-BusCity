package truonghuynhhoa.ptit.model;

import java.io.Serializable;

public class StationInstruction implements Serializable {
    private int image;
    private String name;

    public StationInstruction() {
    }

    public StationInstruction(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
