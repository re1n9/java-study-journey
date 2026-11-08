package test;
//import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Activity implements Serializable {

    private String actName;
    private Date actDate;
    private String actPlace;
    private String actDescription;
    private String actMemo;

    // Constructor for creating new activities
    public Activity(String name, Date date, String place, String description, String memo) {
        this.actName = name;
        this.actDate = date;
        this.actPlace = place;
        this.actDescription = description;
        this.actMemo = memo;
    }

    // Constructor for loading activities from file
    public Activity(String line) throws ParseException {
        String[] parts = line.split(",");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.actName = parts[0];
        this.actDate = dateFormat.parse(parts[1]);
        this.actPlace = parts[2];
        this.actDescription = parts[3];
        this.actMemo = parts[4];
    }

    // Getters and setters
    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public Date getActDate() {
        return actDate;
    }

    public void setActDate(Date actDate) {
        this.actDate = actDate;
    }

    public String getActPlace() {
        return actPlace;
    }

    public void setActPlace(String actPlace) {
        this.actPlace = actPlace;
    }

    public String getActDescription() {
        return actDescription;
    }

    public void setActDescription(String actDescription) {
        this.actDescription = actDescription;
    }

    public String getActMemo() {
        return actMemo;
    }

    public void setActMemo(String actMemo) {
        this.actMemo = actMemo;
    }

    // Convert activity to CSV string for writing to file
    public String toCSVString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return actName + "," + dateFormat.format(actDate) + "," + actPlace + "," + actDescription + "," + actMemo;
    }
}




