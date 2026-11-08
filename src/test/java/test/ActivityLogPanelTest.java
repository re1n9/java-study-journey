package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ActivityLogPanelTest {
    private ActivityLogPanel testPanel;
    private List<Activity> mockActivities;

    @BeforeEach
    public void setUp() {
        // Create a list of mock activities for testing
        mockActivities = new ArrayList<>();
        mockActivities.add(new Activity("Test Activity 1", new Date(), "Test Place 1", "Test Description 1", "Test Memo 1"));
        mockActivities.add(new Activity("Test Activity 2", new Date(), "Test Place 2", "Test Description 2", "Test Memo 2"));

        // Create a test panel with mocked readActivitiesFromFile method
        testPanel = new ActivityLogPanel() {
            protected List<Activity> readActivitiesFromFile() {
                return mockActivities;
            }
        };
    }

    @Test
    public void testViewActivities() {
        // Call the method to test
        testPanel.viewActivities();

        // Verify the table model is correctly populated
        assertEquals(3, testPanel.tableModel.getRowCount());
        assertEquals("Test Activity 1", testPanel.tableModel.getValueAt(0, 0));
        assertEquals("Test Activity 2", testPanel.tableModel.getValueAt(1, 0));
    }
}

