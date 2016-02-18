package ca.mattdietrich.achieved.model;

import java.util.Date;

/**
 * Created by Matt on 2015-09-01.
 * Model class for a goal reminder
 */
public class Reminder {
    private long id;
    private Date dateTime;
    private boolean enabled;
    private long goalId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getGoalId() {
        return goalId;
    }

    public void setGoalId(long goalId) {
        this.goalId = goalId;
    }
}
