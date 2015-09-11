package ca.mdietr.achieved.model;

import java.util.Date;

/**
 * Created by Matt on 2015-09-01.
 * Model class for a goal
 */
public class Goal {
    private long id;
    private String text;
    private Date date;
    private boolean achieved;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }
}
