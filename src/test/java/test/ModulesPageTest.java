package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

class ModulesPageTest {

    private ModulesPage modulesPage;

    @BeforeEach
    void setUp() {
        modulesPage = new ModulesPage();
    }

    @Test
    void testModuleExists() {
        List<Module> moduleData = new ArrayList<>();
        moduleData.add(new Module("Module1", Module.Semester.FALL_2020, 80, 2, new String[]{"Skill1", "Skill2"}));
        modulesPage.writeModuleDataToFile(new File("module_data.txt"), moduleData);

        assertTrue(modulesPage.moduleExists("Module1"));
        assertFalse(modulesPage.moduleExists("Module2"));
    }

    @Test
    void testReadModuleDataFromFile() {
        List<Module> moduleData = new ArrayList<>();
        moduleData.add(new Module("Module1", Module.Semester.FALL_2020, 80, 2, new String[]{"Skill1", "Skill2"}));
        modulesPage.writeModuleDataToFile(new File("module_data1.txt"), moduleData);

        List<Module> readData = modulesPage.readModuleDataFromFile(new File("module_data1.txt"));
        assertEquals(moduleData.get(0).getModuleName(), readData.get(0).getModuleName());
    }

    @Test
    void testWriteModuleDataToFile() {
        List<Module> moduleData = new ArrayList<>();
        moduleData.add(new Module("Module1", Module.Semester.FALL_2020, 80, 2, new String[]{"Skill1", "Skill2"}));
        modulesPage.writeModuleDataToFile(new File("module_data2.txt"), moduleData);

        List<Module> readData = modulesPage.readModuleDataFromFile(new File("module_data2.txt"));
        assertEquals(moduleData.get(0).getModuleName(), readData.get(0).getModuleName());
    }
}
