package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;


/**
 * The dialog for editing tasks.
 */
public class TaskDialog extends JDialog {
	private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton buttonDelete;
    private JTextField taskNameField;
    private JComboBox<String> priorityComboBox;
    private JComboBox<String> categoryComboBox;
    private JTextArea taskDescriptionArea;
    private JTextField remindIntervalField;
    private DatePicker dueDatePicker;
    private DatePicker remindDatePicker;
    private Task task;
    private boolean deleteTask = false;
    private HomePage homePage;

    /**
     * Create a dialog for creating a new task.
     */
    public TaskDialog(Window parent, HomePage homePage) {
        this(parent, null, homePage);
    }

    /**
     * Create a dialog for editing an existing task.
     */
    public TaskDialog(Window parent, Task task, HomePage homePage) {
        super(parent, "Task Editor", ModalityType.APPLICATION_MODAL);
        this.task = task;
        this.homePage = homePage;

        initComponents();
        packFields();
        initButtons();
        pack();

        if (task != null) {
            fillFieldsFromTask();
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    /**
     * Initialize the components used in the dialog.
     */
    private void initComponents() {
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK = new JButton("OK");
        buttonCancel = new JButton("Cancel");
        buttonDelete = new JButton("Delete");

        taskNameField = new JTextField();
        priorityComboBox = new JComboBox<>();
        categoryComboBox = new JComboBox<>();
        taskDescriptionArea = new JTextArea();
        remindIntervalField = new JTextField();

        DatePickerSettings dueDatePickerSettings = new DatePickerSettings();
        dueDatePickerSettings.setAllowKeyboardEditing(false);
        dueDatePicker = new DatePicker(dueDatePickerSettings);
        dueDatePicker.setDateToToday();

        DatePickerSettings remindDatePickerSettings = new DatePickerSettings();
        remindDatePickerSettings.setAllowKeyboardEditing(false);
        remindDatePicker = new DatePicker(remindDatePickerSettings);
        remindDatePickerSettings.setVetoPolicy(date -> date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now()));
        dueDatePickerSettings.setVetoPolicy(date -> date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now()));

        priorityComboBox.addItem("Low");
        priorityComboBox.addItem("Medium");
        priorityComboBox.addItem("High");

        categoryComboBox.addItem("Personal");
        categoryComboBox.addItem("Academic");
        categoryComboBox.addItem("Others");
    }

    /**
     * Pack the fields into the content pane.
     */
    private void packFields() {
        JPanel fieldsPanel = new JPanel(new GridLayout(7, 2));
        fieldsPanel.add(new JLabel("Task Name:"));
        fieldsPanel.add(taskNameField);
        fieldsPanel.add(new JLabel("Start Remind at:"));
        fieldsPanel.add(remindDatePicker);
        fieldsPanel.add(new JLabel("Due Date:"));
        fieldsPanel.add(dueDatePicker);
        fieldsPanel.add(new JLabel("Priority:"));
        fieldsPanel.add(priorityComboBox);
        fieldsPanel.add(new JLabel("Category:"));
        fieldsPanel.add(categoryComboBox);
        fieldsPanel.add(new JLabel("Task Description:"));
        fieldsPanel.add(taskDescriptionArea);
        fieldsPanel.add(new JLabel("Remind Interval:"));
        fieldsPanel.add(remindIntervalField);

        contentPane.add(fieldsPanel, BorderLayout.CENTER);
    }

    /**
     * Initialize and add listeners to the buttons.
     */
    private void initButtons() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(buttonOK);
        buttonsPanel.add(buttonCancel);
        buttonsPanel.add(buttonDelete);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

        buttonOK.addActionListener(e -> {
            onOK();
            homePage.saveTasksToFile();
        });

        buttonCancel.addActionListener(e -> {
            onCancel();
            homePage.saveTasksToFile();
        });

        buttonDelete.addActionListener(e -> onDelete());
    }

    /**
     * Fill the fields with the data from the existing task.
     */
    private void fillFieldsFromTask() {
        if (task.getDueDate() != null) {
            LocalDate localDueDate = task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            dueDatePicker.setDate(localDueDate);
        }
        if (task.getRemindDate() != null) {
            LocalDate localRemindDate = task.getRemindDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            remindDatePicker.setDate(localRemindDate);
        }
        taskNameField.setText(task.getTaskName());
        priorityComboBox.setSelectedItem(task.getPriority());
        categoryComboBox.setSelectedItem(task.getCategory());
        taskDescriptionArea.setText(task.getTaskDescription());
        remindIntervalField.setText(String.valueOf(task.getRemindInterval()));
    }

    /**
     * The action performed when the OK button is clicked.
     */
    private void onOK() {

        if (task == null) {
            task = new Task();
        }

        // Check if all required fields are filled
        if (areFieldsEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }

        // Check if remind date is before or equal to due date
        if (!isRemindDateValid()) {
            JOptionPane.showMessageDialog(this, "Remind date should be before or equal to due date.");
            return;
        }

        // Check if remind interval is an integer
        if (!isRemindIntervalValid()) {
            JOptionPane.showMessageDialog(this, "Remind interval should be an integer.");
            return;
        }

        System.out.println("Iscompleted:"+task.getIsCompleted());
    	if(task.getIsCompleted()!=null && task.getIsCompleted()) {
    		JOptionPane.showMessageDialog(this, "Completed task cannot be changed.");
    		return;
    	}
    	updateTaskFromFields();
        dispose();
    }

    /**
     * The action performed when the Cancel button is clicked.
     */
    private void onCancel() {
        task = null;
        dispose();
    }

    /**
     * The action performed when the Delete button is clicked.
     */
    private void onDelete() {
        deleteTask = true;
        dispose();
    }

    /**
     * Check if all required fields are filled.
     */
    private boolean areFieldsEmpty() {
        return taskNameField.getText().isEmpty() 
            || dueDatePicker.getDate() == null 
            || remindDatePicker.getDate() == null 
            || priorityComboBox.getSelectedItem() == null 
            || categoryComboBox.getSelectedItem() == null 
            || remindIntervalField.getText().isEmpty();
    }

    /**
     * Check if remind date is before or equal to due date.
     */
    private boolean isRemindDateValid() {
        return remindDatePicker.getDate().isBefore(dueDatePicker.getDate()) 
            || remindDatePicker.getDate().isEqual(dueDatePicker.getDate());
    }

    /**
     * Check if remind interval is an integer.
     */
    private boolean isRemindIntervalValid() {
        try {
            Integer.parseInt(remindIntervalField.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Update the task from the fields.
     */
    private void updateTaskFromFields() {
        Date dueDate = Date.from(dueDatePicker.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date remindDate = Date.from(remindDatePicker.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

        task.setTaskName(taskNameField.getText());
        task.setRemindDate(remindDate);
        task.setDueDate(dueDate);
        task.setPriority((String) priorityComboBox.getSelectedItem());
        task.setCategory((String) categoryComboBox.getSelectedItem());
        task.setTaskDescription(taskDescriptionArea.getText());
        task.setRemindInterval(Integer.parseInt(remindIntervalField.getText()));
        if(task.getIsCompleted()==null) task.setIsCompleted(false);
        homePage.saveTasksToFile();
    }

    public Task getTask() {
        return task;
    }

    public boolean isDeleteTask() {
        return deleteTask;
    }
}
