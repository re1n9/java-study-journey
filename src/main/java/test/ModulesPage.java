package test;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class representing a page for managing modules in a GUI application.
 * This page displays a table of module data and allows user to add, remove,
 * search for a module, and calculate total GPA.
 */
class ModulesPage extends JPanel {

    private JTable modulesTable;
    private DefaultTableModel model;
    private JTextField searchBar;
    private List<Module> moduleData;
    private File dataFile;
    private JLabel totalGPALabel;

    /**
     * Constructor for ModulesPage class.
     * Initializes the GUI components.
     */
    public ModulesPage() {
        setupUI();
    }

    /**
     * Sets up the user interface for the panel.
     * Adds all components to the panel.
     */
    private void setupUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setupHeaderPanel();
        setupTable();
        setupSearchBar();
        setupAddModuleButton();
        setupRemoveModuleButton();
    }

    /**
     * Sets up the header panel.
     * This includes a label, GPA calculator button, and total GPA label.
     */
    private void setupHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel modulesLabel = new JLabel("Modules & Skills  ");
        modulesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(modulesLabel, BorderLayout.WEST);

        setupCalculateButton(headerPanel);
        setupTotalGPALabel(headerPanel);

        this.add(headerPanel);
    }
    
    /**
     * Sets up the calculate total GPA button.
     * When clicked, the button calculates the total GPA and updates the label.
     * @param headerPanel the panel to add the button to.
     */
    private void setupCalculateButton(JPanel headerPanel) {
        JButton calculateTotalGPAButton = new JButton("Calculate Total GPA");
        calculateTotalGPAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double totalGPA = calculateTotalGPA();
                totalGPALabel.setText("Total GPA: " + String.format("%.2f", totalGPA));
            }
        });
        headerPanel.add(calculateTotalGPAButton, BorderLayout.EAST);
    }

    /**
     * Sets up the total GPA label.
     * @param headerPanel the panel to add the label to.
     */
    private void setupTotalGPALabel(JPanel headerPanel) {
        totalGPALabel = new JLabel("Total GPA: 0.00");
        totalGPALabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(totalGPALabel, BorderLayout.CENTER);
    }

    /**
     * Sets up the module data table.
     * This includes setting up the table model and reading data from file.
     */
    private void setupTable() {
        initializeTableModel();
        initializeModuleData();

        JScrollPane scrollPane = new JScrollPane(modulesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add(scrollPane);
    }

    /**
     * Initializes the table model for the modules table.
     */
    private void initializeTableModel() {
        String[] columnNames = {"Module Name", "Semester", "Module Grade", "Module GPA", "Credit", "Skills"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modulesTable = new JTable(model);
    }


    /**
     * Reads module data from file and initializes the data list.
     */
    private void initializeModuleData() {
        dataFile = new File("module_data.txt");
        moduleData = readModuleDataFromFile(dataFile);

        for (Module module : moduleData) {
            model.addRow(new Object[]{
                    module.getModuleName(),
                    module.getSemester(),
                    module.getModuleGrade(),
                    module.getModuleGPA(),
                    module.getCredit(),
                    String.join(",", module.getSkill()) // convert the array of skills back into a string
            });
        }
    }

    /**
     * Sets up the search bar panel.
     * When text is entered, the table is filtered based on the input.
     */
    private void setupSearchBar() {
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel searchLabel = new JLabel("Search: ");
        searchBarPanel.add(searchLabel);
        
        searchBar = new JTextField(20);

        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }

            private void filter() {
                String keyword = searchBar.getText().toLowerCase();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                modulesTable.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
            }
        });
        searchBarPanel.add(searchBar);

        this.add(searchBarPanel);
    }

    /**
     * Sets up the Add Module button.
     * When clicked, opens a dialog to add a new module to the table.
     */
    private void setupAddModuleButton() {
        JButton addModuleButton = new JButton("Add Module");
        addModuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewModule();
            }
        });
        this.add(addModuleButton);
    }
    
    /**
     * Sets up the Remove Module button.
     * When clicked, deletes the selected module from the table.
     */
    private void setupRemoveModuleButton() {
        JButton removeModuleButton = new JButton("Remove Module");
        removeModuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeModule();
            }
        });
        add(removeModuleButton);
    }


    /**
     * Shows a dialog to add a new module to the table.
     * The data is validated before adding to the table.
     */
    private void addNewModule() {
        JTextField moduleNameField = new JTextField();
        JComboBox<Module.Semester> semesterComboBox = new JComboBox<>(Module.Semester.values());
        JTextField moduleGradeField = new JTextField();
        JTextField creditField = new JTextField();
        JTextField skillsField = new JTextField();
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Module Name:"));
        panel.add(moduleNameField);
        panel.add(new JLabel("Semester:"));
        panel.add(semesterComboBox);
        panel.add(new JLabel("Module Grade:"));
        panel.add(moduleGradeField);
        panel.add(new JLabel("Credit:"));
        panel.add(creditField);
        panel.add(new JLabel("Skills (separate multiple with commas):"));
        panel.add(skillsField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add a New Module",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String moduleName = moduleNameField.getText().trim();
            Module.Semester semester = (Module.Semester) semesterComboBox.getSelectedItem();
            String skills = skillsField.getText().trim();
            String[] skillArray = skills.split(",");
            double moduleGrade, credit;
             
            // Check if a module with the same name already exists
            if (moduleExists(moduleName)) {
                JOptionPane.showMessageDialog(null, "A module with this name already exists. Please enter a different name.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                moduleGrade = Double.parseDouble(moduleGradeField.getText().trim());
                credit = Double.parseDouble(creditField.getText().trim());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Invalid grade or credit entry. Please enter a valid number.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Module newModule = new Module(moduleName, semester, moduleGrade, credit, skillArray);
            moduleData.add(newModule);
            model.addRow(new Object[]{moduleName, semester, moduleGrade, newModule.getModuleGPA(), credit, skills});
            writeModuleDataToFile(dataFile, moduleData);
        }
    }
    
    /**
     * Removes the selected module from the table.
     * Shows an error dialog if no row is selected.
     */
    private void removeModule() {
        int selectedRow = modulesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a row to delete.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String moduleName = (String) modulesTable.getValueAt(selectedRow, 0);
        for (Module module : moduleData) {
            if (module.getModuleName().equals(moduleName)) {
                moduleData.remove(module);
                break;
            }
        }

        model.removeRow(selectedRow);
        writeModuleDataToFile(dataFile, moduleData);
    }

    /**
     * Checks if a module with the same name already exists in the table.
     * @param moduleName the name of the module to check for.
     * @return true if the module exists, false otherwise.
     */
    boolean moduleExists(String moduleName) {
        for (Module module : moduleData) {
            if (module.getModuleName().equals(moduleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the total GPA from all modules in the table.
     * @return the calculated total GPA.
     */
    double calculateTotalGPA() {
        double totalCredits = 0.0;
        double totalGPACredits = 0.0;
        for (Module module : moduleData) {
            totalCredits += module.getCredit();
            totalGPACredits += module.getModuleGPA() * module.getCredit();
        }
        return totalGPACredits / totalCredits;
    }

    /**
     * Reads module data from a file.
     * @param file the file to read the module data from.
     * @return a list of Module objects read from the file.
     */
    List<Module> readModuleDataFromFile(File file) {
        List<Module> moduleData = new ArrayList<>();
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                moduleData = (List<Module>) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (EOFException e) {
                System.out.println("End of file reached. Returning an empty module list.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return moduleData;
    }
    /**
     * Writes module data to a file.
     * @param file the file to write the module data to.
     * @param moduleData the list of Module objects to write to the file.
     */
    void writeModuleDataToFile(File file, List<Module> moduleData) {
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(moduleData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}