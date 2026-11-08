package test;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.io.*;
import java.util.*;

/**
 * ActivityLogPanel class represents the panel for logging activities.
 * This panel includes functions for adding, viewing, and deleting activities.
 */
public class ActivityLogPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String FILE_PATH = "activity_log.txt";

    private JTextField actNameField;
    private JTextField actDateField;
    private JTextField actPlaceField;
    private JTextArea actDescriptionArea;
    private JTextArea actMemoArea;
    private JTable activityLogTable;
    DefaultTableModel tableModel;

    private JButton addActivityButton;
    private JButton viewActivitiesButton;
    private JButton clearButton;
    // List to store all activities
    private List<Activity> activityList = new ArrayList<>();
    
    public ActivityLogPanel() {
        setLayout(new BorderLayout());
        add(setupInputPanel(), BorderLayout.CENTER);
        add(setupViewPanel(), BorderLayout.SOUTH);
        setPreferredSize(new Dimension(400, 400));
    }

    /**
     * Set up the panel for activity input.
     * @return the panel for activity input
     */
    private JPanel setupInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createTitledBorder("New Activity"));

        inputPanel.add(new JLabel("Name: "));
        actNameField = new JTextField(20);
        inputPanel.add(actNameField);

        inputPanel.add(new JLabel("Date (yyyy-MM-dd): "));
        actDateField = new JTextField(10);
        inputPanel.add(actDateField);

        inputPanel.add(new JLabel("Place: "));
        actPlaceField = new JTextField(20);
        inputPanel.add(actPlaceField);

        inputPanel.add(new JLabel("Description: "));
        actDescriptionArea = new JTextArea(4, 20);
        JScrollPane descriptionScrollPane = new JScrollPane(actDescriptionArea);
        inputPanel.add(descriptionScrollPane);

        inputPanel.add(new JLabel("Memo: "));
        actMemoArea = new JTextArea(4, 20);
        JScrollPane memoScrollPane = new JScrollPane(actMemoArea);
        inputPanel.add(memoScrollPane);

        addActivityButton = new JButton("Add Activity");
        addActivityButton.addActionListener(this);
        inputPanel.add(addActivityButton);
        return inputPanel;
    }

    /**
     * Set up the panel for activity viewing.
     * @return the panel for activity viewing
     */
    private JPanel setupViewPanel() {
        JPanel viewPanel = new JPanel();
        viewPanel.setBorder(BorderFactory.createTitledBorder("View Activities"));

        clearButton = new JButton("Clear Activity");
        clearButton.addActionListener(this);
        viewPanel.add(clearButton);

        tableModel = new DefaultTableModel(new Object[]{"Name", "Date", "Place", "Description", "Memo"}, 0);
        activityLogTable = new JTable(tableModel);
        activityLogTable.setPreferredScrollableViewportSize(new Dimension(350, 300));
        activityLogTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(activityLogTable);
        viewPanel.add(scrollPane);

        viewActivitiesButton = new JButton("View All Activities");
        viewActivitiesButton.addActionListener(this);
        viewPanel.add(viewActivitiesButton);

        return viewPanel;
    }
    
    /**
     * Called when a button is clicked.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addActivityButton) {
            addActivity();
        } else if (e.getSource() == viewActivitiesButton) {
            viewActivities();
        } else if (e.getSource() == clearButton) {
            clearActivity();
        }
    }

    /**
     * Add a new activity.
     */
    private void addActivity() {
        try {
            String actName = actNameField.getText();
            Date actDate = DATE_FORMAT.parse(actDateField.getText());
            String actPlace = actPlaceField.getText();
            String actDescription = actDescriptionArea.getText();
            String actMemo = actMemoArea.getText();

            Activity activity = new Activity(actName, actDate, actPlace, actDescription, actMemo);
            
            // add the new activity to the activityList
            activityList.add(activity);

            writeActivityToFile();

            JOptionPane.showMessageDialog(this, "Activity added successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding activity: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clear an activity.
     */
    private void clearActivity() {
        int selectedRow = activityLogTable.getSelectedRow();

        if (selectedRow >= 0) {
            String activityName = activityLogTable.getValueAt(selectedRow, 0).toString();
            String activityDate = activityLogTable.getValueAt(selectedRow, 1).toString();

            Activity toRemove = null;

            for (Activity activity : activityList) {
                if (activity.getActName().equals(activityName) && DATE_FORMAT.format(activity.getActDate()).equals(activityDate)) {
                    toRemove = activity;
                    break;
                }
            }

            if (toRemove != null) {
                activityList.remove(toRemove);
                // Removing from tableModel as well
                tableModel.removeRow(selectedRow);
                writeActivityToFile();
                JOptionPane.showMessageDialog(this, "Activity cleared successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Activity not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No row selected", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Read activities from file.
     * @return a list of activities read from file
     */
    private List<Activity> readActivitiesFromFile() {
        List<Activity> activities = new ArrayList<>();
        
        File file = new File(FILE_PATH);
        // If the file does not exist or is empty, return an empty list
        if (!file.exists() || file.length() == 0) {
            return activities;
        }
        
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            Activity activity;
            while ((activity = (Activity) ois.readObject()) != null) {
                activities.add(activity);
            }
        } catch (EOFException e) {
            // end of file reached
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An error occurred while reading activities from file.");
            e.printStackTrace();
        }
        
        // update the activityList with the activities read from the file
        activityList = activities;
        
        return activities;
    }

    /**
     * View all activities.
     */
    public void viewActivities() {
        List<Activity> activities = readActivitiesFromFile();

        // clear the table before adding new rows
        tableModel.setRowCount(0);
        
        for (Activity activity : activities) {
            Vector<String> data = new Vector<>();
            data.add(activity.getActName());
            data.add(DATE_FORMAT.format(activity.getActDate()));
            data.add(activity.getActPlace());
            data.add(activity.getActDescription());
            data.add(activity.getActMemo());

            tableModel.addRow(data);
        }
    }

    /**
     * Write activities to file.
     */
    private void writeActivityToFile() {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            for (Activity activity : activityList) {
                out.writeObject(activity);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing activities to file.");
            e.printStackTrace();
        }
    }
}