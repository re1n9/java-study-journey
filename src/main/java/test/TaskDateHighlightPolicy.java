package test;

import com.github.lgooddatepicker.optionalusertools.DateHighlightPolicy;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;
import java.awt.Color;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class TaskDateHighlightPolicy implements DateHighlightPolicy {

    private Map<LocalDate, ArrayList<Task>> taskMap;

    public TaskDateHighlightPolicy(Map<LocalDate, ArrayList<Task>> taskMap) {
        this.taskMap = taskMap;
    }

    @Override
    public HighlightInformation getHighlightInformationOrNull(LocalDate date) {
        if (!taskMap.containsKey(date)) {
            return null;
        }

        ArrayList<Task> tasksForDate = taskMap.get(date);
        String highestPriority = "Low";
        for (Task task : tasksForDate) {
            if ("High".equals(task.getPriority()) || ("Medium".equals(task.getPriority()) && !"High".equals(highestPriority))) {
                highestPriority = task.getPriority();
            }
        }

        switch (highestPriority) {
            case "High":
                return new HighlightInformation(Color.red, null, "High priority tasks due!");
            case "Medium":
                return new HighlightInformation(Color.orange, null, "Medium priority tasks due!");
            case "Low":
                return new HighlightInformation(Color.green, null, "Low priority tasks due!");
            default:
                return new HighlightInformation(Color.gray, null, "Low priority tasks due!");
        }
    }
    public void setTaskMap(Map<LocalDate, ArrayList<Task>> taskMap) {
        this.taskMap = taskMap;
    }
}


