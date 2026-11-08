package test;

import com.github.lgooddatepicker.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask; 

/**
 * HomePage class represents the main panel for the application.
 * It displays the calendar, the tasks for the selected date and the reminders for the current date.
 */
public class HomePage extends JPanel {

    private JButton newTaskButton;
    private CalendarPanel calendarPanel;
    private Timer reminderTimer;
    private JTable reminderTable;
    private DefaultTableModel reminderTableModel;


    // Task map stores tasks by their due date.
    private Map<LocalDate, ArrayList<Task>> taskMap;

    // All tasks stored in an array list.
    private ArrayList<Task> tasks;

    // TaskDateHighlightPolicy is used to highlight dates in the calendar.
    private TaskDateHighlightPolicy taskDateHighlightPolicy;

    /**
     * Constructs a new HomePage object and initializes its components.
     */
    public HomePage() {
        initializeComponents();
        loadTasksFromFile();
        configureCalendar();
        startReminderTimer();
    }

    /**
     * Initializes the components for the HomePage.
     */
    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Create and add new task button.
        newTaskButton = new JButton("New Task");
        add(newTaskButton, BorderLayout.NORTH);
        
     // Create and add reminder table.
        String[] columnNames = {"Task", "Due date", "Description", "Complete"};
        reminderTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 3: return JButton.class;
                    default: return String.class;
                }
            }
        };
        reminderTable = new JTable(reminderTableModel) {
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }

            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 3) {
                    return new TableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            return (JButton) value;
                        }
                    };
                }
                return super.getCellRenderer(row, column);
            }
        };
        JScrollPane scrollPane = new JScrollPane(reminderTable);
        add(scrollPane, BorderLayout.SOUTH);
        addMouseListenerToTable();
        
        // Initialize tasks data structures.
        tasks = new ArrayList<>();
        taskMap = new HashMap<>();
    }

    /**
     * Loads tasks from a file.
     * If the file does not exist, it initializes an empty task list.
     */
    private void loadTasksFromFile() {
        File file = new File("tasks.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                tasks = (ArrayList<Task>) ois.readObject();
                updateTaskMap();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Configures the calendar panel.
     */
    private void configureCalendar() {
        DatePickerSettings datePickerSettings = new DatePickerSettings();
        calendarPanel = new CalendarPanel(datePickerSettings);

        // Set task date highlight policy.
        taskDateHighlightPolicy = new TaskDateHighlightPolicy(taskMap);
        datePickerSettings.setHighlightPolicy(taskDateHighlightPolicy);

        // Add calendar panel.       
        add(calendarPanel, BorderLayout.CENTER);
        
        JLabel tipLabel = new JLabel("<html>If you want to check the tasks on certain date,"
        		+ "<br> please click the date block first,"
        		+ "<br> and then click the blank area.</html>");
        add(tipLabel, BorderLayout.EAST);

        // Add mouse click listener to the calendar.
        calendarPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleDateSelection();
            }
        });
        

        // Add new task button listener.
        newTaskButton.addActionListener(e -> handleNewTaskButtonPress());
    }

    /**
     * Handles date selection in the calendar.
     */
    private void handleDateSelection() {
        LocalDate selectedDate = calendarPanel.getSelectedDate();
        if (selectedDate != null) {
            handleSelectedDateTasks(selectedDate);
        }
    }
    
    /**
     * Handles the tasks for a selected date.
     * @param SelectedDate the selected date
     */
    private void handleSelectedDateTasks(LocalDate SelectedDate) {
    	 ArrayList<Task> tasksOnSelectedDate = getTasksOnDate(SelectedDate);                    
         System.out.println("Selected date: " + SelectedDate);
         System.out.println("Tasks on selected date: " + tasksOnSelectedDate);
         if (tasksOnSelectedDate != null && !tasksOnSelectedDate.isEmpty()) {
             TaskListDialog taskListDialog = new TaskListDialog(SwingUtilities.getWindowAncestor(HomePage.this), tasksOnSelectedDate);
             taskListDialog.setVisible(true);
             Task selectedTask = taskListDialog.getSelectedTask();
             System.out.println("selectedTask1:"+selectedTask);
             
             if (selectedTask != null) {
             	System.out.println("selectedTaskDate2:"+selectedTask.getDueDate());
                 TaskDialog taskDialog = new TaskDialog(SwingUtilities.getWindowAncestor(HomePage.this), selectedTask,HomePage.this);
                 taskDialog.setVisible(true);
                 if (taskDialog.isDeleteTask()) {
                     tasks.remove(selectedTask);
                     System.out.println("removed sucessfully");
                     LocalDate localDueDate = selectedTask.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                     ArrayList<Task> taskList = taskMap.get(localDueDate);
                     if (taskList != null) {
                     	
                         taskList.remove(selectedTask);
                         if (taskList.isEmpty()) {
                             taskMap.remove(localDueDate);
                         }
                     }
                     saveTasksToFile();
                     refreshCalendarPanel();
                 }else {
                 // Handle the updated task
                 Task updatedTask = taskDialog.getTask();
                 System.out.println("Updated task due date: " + updatedTask.getDueDate());
                 if (updatedTask != null) {
                     // Replace the old task with the updated task
                     System.out.println("selectedTaskDate3:"+selectedTask.getDueDate());
                     updateTaskMap(); 
                     saveTasksToFile();
                     refreshCalendarPanel();
                 }
             }
             }
         }else {
             System.out.println("No tasks on selected date.");
         }
     }

    /**
     * Handles the creation of a new task.
     */
    private void handleNewTaskButtonPress() {
        // Create and display task dialog.
        TaskDialog taskDialog = new TaskDialog(SwingUtilities.getWindowAncestor(this), this);
        taskDialog.setVisible(true);

        // Add new task to task list and update task map.
        Task newTask = taskDialog.getTask();
        if (newTask != null) {
            tasks.add(newTask);
            updateTaskMap();

            // Save tasks to file.
            saveTasksToFile();

            // Refresh the calendar panel.
            refreshCalendarPanel();
        }
    }

    /**
     * Refreshes the calendar panel.
     */
    private void refreshCalendarPanel() {
        DatePickerSettings datePickerSettings = calendarPanel.getSettings();
        taskDateHighlightPolicy.setTaskMap(taskMap);
        datePickerSettings.setHighlightPolicy(taskDateHighlightPolicy);
        calendarPanel.repaint();
    }

    /**
     * Updates the task map with the tasks currently in the task list.
     */
    private void updateTaskMap() {
        // Clear the task map.
        taskMap.clear();

        // For each task in the task list, add it to the map under its due date.
        for (Task task : tasks) {
            LocalDate localDueDate = task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Get the task list for the due date, or create it if it doesn't exist.
            ArrayList<Task> taskList = taskMap.get(localDueDate);
            if (taskList == null) {
                taskList = new ArrayList<>();
                taskMap.put(localDueDate, taskList);
            }

            // Add the task to the task list.
            taskList.add(task);
        }
    }

    /**
     * Saves tasks to a file.
     */
    protected void saveTasksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tasks.dat"))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the reminder timer.
     * The timer is set to update the reminder text once per day.
     */
    private void startReminderTimer() {
        reminderTimer = new Timer();
        TimerTask reminderTask = new TimerTask() {
            @Override
            public void run() {
                updateReminderText();
            }
        };

        // Schedule the task to run once every day
        //reminderTimer.scheduleAtFixedRate(reminderTask, 0, 24 * 60 * 60 * 1000);
        reminderTimer.scheduleAtFixedRate(reminderTask, 0, 3 * 1000);
    }
    
    private void addMouseListenerToTable() {
        reminderTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = reminderTable.getColumnModel().getColumnIndexAtX(e.getX());
                int row    = e.getY() / reminderTable.getRowHeight();

                if (row < reminderTable.getRowCount() && row >= 0 && column < reminderTable.getColumnCount() && column >= 0) {
                    Object value = reminderTable.getValueAt(row, column);
                    if (value instanceof JButton) {
                        ((JButton) value).doClick();
                    }
                }
            }
        });
    }

    /**
     * Updates the reminder text.
     * This method checks the tasks for the current date and updates the reminder text area.
     */
    private void updateReminderText() {
        LocalDate today = LocalDate.now();
        reminderTableModel.setRowCount(0);

        for (Task task : tasks) {
            LocalDate taskRemindDate = task.getRemindDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate taskDueDate = task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int remindInterval = task.getRemindInterval();

            if (!task.getIsCompleted() && !taskRemindDate.isAfter(today) && !taskDueDate.isBefore(today)) {
                int daysBetween = (int) ChronoUnit.DAYS.between(taskRemindDate, today);
                if (daysBetween % remindInterval == 0) {
                	 JButton completeButton = new JButton("Complete");
                     completeButton.addActionListener(new ActionListener() {
                         public void actionPerformed(ActionEvent e) {
                        	 System.out.println("Complete button clicked");
                             task.setIsCompleted(true);
                             updateReminderText();
                             refreshCalendarPanel();
                             saveTasksToFile();
                             System.out.println("Task status: "+task.getIsCompleted());
                         }
                     });
                     reminderTableModel.addRow(new Object[] {task.getTaskName(), taskDueDate, task.getTaskDescription(),completeButton});
                
                }
            }
        }

        //SwingUtilities.invokeLater(() -> reminderTextArea.setText(reminderText.toString()));       
    }
    
    /**
     * Get the task list on selected date.
     * @param date the selected date
     */
    private ArrayList<Task> getTasksOnDate(LocalDate date) {
        ArrayList<Task> tasksOnDate = new ArrayList<>();
        for (Task task : tasks) {
            LocalDate taskDueDate = task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            System.out.println("Task due date: " + taskDueDate);
            if (taskDueDate.equals(date)) {
                tasksOnDate.add(task);
            }
        }
        return tasksOnDate;
    }
}
