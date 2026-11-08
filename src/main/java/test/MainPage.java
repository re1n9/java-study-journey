package test;

import com.github.lgooddatepicker.components.CalendarPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main application class for the Learning Journey Tracker.
 * This class sets up the main GUI and handles navigation between pages.
 */
public class MainPage {
	
    /**
     * Main method to launch the application.
     * @param args command-line arguments. Not used.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	// Setup main frame
            JFrame frame = new JFrame("Learning Journey Tracker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // Main application panel
            JPanel mainPanel = new JPanel(new BorderLayout());
            frame.add(mainPanel);

            // Setup left navigation bar
            JPanel navPanel = new JPanel();
            navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
            navPanel.setPreferredSize(new Dimension(200, 600));

            // Navigation buttons
            JButton homeBtn = new JButton("Home");
            JButton modulesBtn = new JButton("Modules & Skills");
            JButton tasksBtn = new JButton("Tasks");
            JButton activityLogBtn = new JButton("Activity Log");
            JButton positionLogBtn = new JButton("Position Log");

            // Add buttons to navigation panel
            navPanel.add(homeBtn);
            navPanel.add(modulesBtn);
            navPanel.add(activityLogBtn);
            navPanel.add(positionLogBtn);

            mainPanel.add(navPanel, BorderLayout.WEST);

            // Setup right panel with CardLayout for page navigation
            JPanel rightPanel = new JPanel(new CardLayout());
            mainPanel.add(rightPanel, BorderLayout.CENTER);

            // Initialize individual pages
            HomePage homePage = new HomePage();
            ModulesPage modulesPage = new ModulesPage();
            ActivityLogPanel activityLogPage =new ActivityLogPanel();
            PositionLogPanel positionLogPanel =new PositionLogPanel();
            
            // Tasks Page
            JPanel tasksPage = new JPanel();
            JLabel tasksLabel = new JLabel("Tasks Page");
            tasksPage.add(tasksLabel);

            // Add pages to rightPanel
            rightPanel.add(homePage, "Home");
            rightPanel.add(modulesPage, "Modules");
            rightPanel.add(activityLogPage, "ActivityLog");
            rightPanel.add(positionLogPanel, "PositionLog");

            // Setup navigation listener to handle page switches
            ActionListener navigationListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CardLayout cl = (CardLayout) rightPanel.getLayout();
                    if (e.getSource() == homeBtn) {
                        cl.show(rightPanel, "Home");
                    } else if (e.getSource() == modulesBtn) {
                        cl.show(rightPanel, "Modules");
                    } else if (e.getSource() == tasksBtn) {
                        cl.show(rightPanel, "Tasks");
                    } else if (e.getSource() == activityLogBtn) {
                        cl.show(rightPanel, "ActivityLog");
                    }else if (e.getSource() == positionLogBtn) {
                        cl.show(rightPanel, "PositionLog");
                    }

                }
            };

            // Assign the ActionListener to buttons
            homeBtn.addActionListener(navigationListener);
            modulesBtn.addActionListener(navigationListener);
            tasksBtn.addActionListener(navigationListener);
            activityLogBtn.addActionListener(navigationListener);
            positionLogBtn.addActionListener(navigationListener);

            // Show the calendar view when the application starts
            CardLayout cl = (CardLayout) rightPanel.getLayout();
            cl.show(rightPanel, "Home");

            frame.setVisible(true);
        });
    }
}