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
import java.io.*;
import java.util.*;

/**
 * This class represents a PositionLogPanel where users can add, view and clear positions.
 * The positions are stored in a list and persisted to a file.
 */
public class PositionLogPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String FILE_PATH = "position_log.txt";

    // Input fields
    private JTextField posNameField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextArea posDescriptionArea;
    private JTextArea posMemoArea;
    // Table for displaying positions
    private JTable positionLogTable;
    private DefaultTableModel tableModel;
    // Buttons for adding, viewing and clearing positions
    private JButton addPositionButton;
    private JButton viewPositionsButton;
    private JButton clearButton;
    // List to store all positions
    private List<Position> positionList = new ArrayList<>();
    
    /**
     * Constructor for PositionLogPanel.
     * Sets up the layout and adds the input and view panels.
     */
    public PositionLogPanel() {
        setLayout(new BorderLayout());
        add(setupInputPanel(), BorderLayout.CENTER);
        add(setupViewPanel(), BorderLayout.SOUTH);
        setPreferredSize(new Dimension(400, 400));
    }

    /**
     * Creates and returns the input panel for adding new positions.
     */
    private JPanel setupInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createTitledBorder("New Position"));

        inputPanel.add(new JLabel("Name: "));
        posNameField = new JTextField(20);
        inputPanel.add(posNameField);

        inputPanel.add(new JLabel("Start Date (yyyy-MM-dd): "));
        startDateField = new JTextField(10);
        inputPanel.add(startDateField);

        inputPanel.add(new JLabel("End Date (yyyy-MM-dd): "));
        endDateField = new JTextField(10);
        inputPanel.add(endDateField);

        inputPanel.add(new JLabel("Description: "));
        posDescriptionArea = new JTextArea(4, 20);
        JScrollPane descriptionScrollPane = new JScrollPane(posDescriptionArea);
        inputPanel.add(descriptionScrollPane);

        inputPanel.add(new JLabel("Memo: "));
        posMemoArea = new JTextArea(4, 20);
        JScrollPane memoScrollPane = new JScrollPane(posMemoArea);
        inputPanel.add(memoScrollPane);

        addPositionButton = new JButton("Add Position");
        addPositionButton.addActionListener(this);
        inputPanel.add(addPositionButton);
        return inputPanel;
    }

    /**
     * Creates and returns the view panel for viewing all positions.
     */
    private JPanel setupViewPanel() {
        JPanel viewPanel = new JPanel();
        viewPanel.setBorder(BorderFactory.createTitledBorder("View Positions"));

        clearButton = new JButton("Clear Position");
        clearButton.addActionListener(this);
        viewPanel.add(clearButton);

        tableModel = new DefaultTableModel(new Object[]{"Name", "Start Date", "End Date", "Description", "Memo"}, 0);
        positionLogTable = new JTable(tableModel);
        positionLogTable.setPreferredScrollableViewportSize(new Dimension(350, 300));
        positionLogTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(positionLogTable);
        viewPanel.add(scrollPane);

        viewPositionsButton = new JButton("View All Positions");
        viewPositionsButton.addActionListener(this);
        viewPanel.add(viewPositionsButton);

        return viewPanel;
    }

    /**
     * Called when a button is clicked.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPositionButton) {
            addPosition();
        } else if (e.getSource() == viewPositionsButton) {
            viewPositions();
        } else if (e.getSource() == clearButton) {
            clearPosition();
        }
    }

    /**
     * Adds a new position based on the input fields and saves it to the file.
     */
    private void addPosition() {
        try {
            String posName = posNameField.getText();
            Date startDate = DATE_FORMAT.parse(startDateField.getText());
            Date endDate = DATE_FORMAT.parse(endDateField.getText());
            //Check that if end date is after start date
            if (endDate.before(startDate)) {
                JOptionPane.showMessageDialog(this, "End date should be after start date", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String posDescription = posDescriptionArea.getText();
            String posMemo = posMemoArea.getText();

            Position position = new Position(posName, startDate, endDate, posDescription, posMemo);
            positionList.add(position);
            writePositionToFile();

            JOptionPane.showMessageDialog(this, "Position added successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding position: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Removes a selected position from the list and file.
     */
    private void clearPosition() {
        int selectedRow = positionLogTable.getSelectedRow();

        if (selectedRow >= 0) {
            String positionName = positionLogTable.getValueAt(selectedRow, 0).toString();
            String startDate = positionLogTable.getValueAt(selectedRow, 1).toString();

            Position toRemove = null;

            for (Position position : positionList) {
                if (position.getPosName().equals(positionName) && DATE_FORMAT.format(position.getStartDate()).equals(startDate)) {
                    toRemove = position;
                    break;
                }
            }

            if (toRemove != null) {
                positionList.remove(toRemove);
                // Removing from tableModel as well
                tableModel.removeRow(selectedRow);
                writePositionToFile();
                JOptionPane.showMessageDialog(this, "Position cleared successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Position not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No row selected", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reads positions from the file and returns them as a list.
     */
    private List<Position> readPositionsFromFile() {
        List<Position> positions = new ArrayList<>();
        
        File file = new File(FILE_PATH);
        // If the file does not exist or is empty, return an empty list
        if (!file.exists() || file.length() == 0) {
            return positions;
        }
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            Position position;
            while ((position = (Position) ois.readObject()) != null) {
                positions.add(position);
            }
        } catch (EOFException e) {
            // end of file reached
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An error occurred while reading positions from file.");
            e.printStackTrace();
        }
        
        // update the activityList with the activities read from the file
        positionList = positions;
        
        return positions;
    }

    /**
     * Reads positions from the file and displays them in the table.
     */
    public void viewPositions() {
        List<Position> positions = readPositionsFromFile();
    	// clear the table before adding new rows
        tableModel.setRowCount(0);
        for (Position position : positions) {
            Vector<String> data = new Vector<>();
            data.add(position.getPosName());
            data.add(DATE_FORMAT.format(position.getStartDate()));
            data.add(DATE_FORMAT.format(position.getEndDate()));
            data.add(position.getPosDescription());
            data.add(position.getPosMemo());

            tableModel.addRow(data);
        }
    }
    /**
     * Write positions to file.
     */
    private void writePositionToFile() {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            for (Position position : positionList) {
                out.writeObject(position);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing positions to file.");
            e.printStackTrace();
        }
    }
}