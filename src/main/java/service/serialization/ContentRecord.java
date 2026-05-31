package service.serialization;

import java.io.Serializable;

public class ContentRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String title;
    private final int year;
    private final int duration;
    private final String type;

    public ContentRecord(String title, int year, int duration, String type) {
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public int getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }
}
