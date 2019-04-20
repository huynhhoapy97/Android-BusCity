package truonghuynhhoa.ptit.model;

import java.io.Serializable;

public class DetailInstruction implements Serializable {
    private int image;
    private String WalkOrBus;
    private String FromTo;

    public DetailInstruction() {
    }

    public DetailInstruction(int image, String walkOrBus, String fromTo) {
        this.image = image;
        WalkOrBus = walkOrBus;
        FromTo = fromTo;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getWalkOrBus() {
        return WalkOrBus;
    }

    public void setWalkOrBus(String walkOrBus) {
        WalkOrBus = walkOrBus;
    }

    public String getFromTo() {
        return FromTo;
    }

    public void setFromTo(String fromTo) {
        FromTo = fromTo;
    }
}
