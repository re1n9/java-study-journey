package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TaskListDialog extends JDialog {
    private JList<String> taskList;
    private Task selectedTask;

    public TaskListDialog(Window parent, List<Task> tasks) {
        super(parent, "Task List", ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Task task : tasks) {
            listModel.addElement(task.getTaskName());
        }
        taskList = new JList<>(listModel);

        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    int index = taskList.locationToIndex(evt.getPoint());
                    selectedTask = tasks.get(index);
                    dispose();
                }
            }
        });

        add(new JScrollPane(taskList), BorderLayout.CENTER);
        pack();
    }

    public Task getSelectedTask() {
        return selectedTask;
    }
}
