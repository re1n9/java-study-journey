package test;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    private String taskName;
    private Date dueDate;
    private String priority;
    private String category;
    private String taskDescription;
    private int remindInterval;
    private Date remindDate;
    private Boolean isCompleted;

    // Getters
    public String getTaskName() {
        return taskName;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public String getCategory() {
        return category;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public int getRemindInterval() {
        return remindInterval;
    }

    public Date getRemindDate() {
        return remindDate;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    // Setters
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setRemindInterval(int remindInterval) {
        this.remindInterval = remindInterval;
    }

    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
