package test;

import java.io.Serializable;
import java.util.Date;

public class Module implements Serializable {
    private String moduleName;
    private double moduleGrade;
    private double moduleGPA;
    private double credit;
    private String[] skill;
    private Semester semester;
    
    public Module() {};
    public enum Semester {
        FALL_2020, SPRING_2021, FALL_2021, SPRING_2022, FALL_2022, SPRING_2023, FALL_2023, SPRING_2024, FALL_2024
    }
    
    public Module(String moduleName, Semester semester, double moduleGrade, double credit, String[] skill) {
        this.moduleName = moduleName;
        this.semester = semester;
        this.moduleGrade = moduleGrade;
        this.moduleGPA = calculateGPA(moduleGrade);
        this.credit = credit;
        this.skill = skill;
    }
    
    private double calculateGPA(double moduleGrade) {
        double gpa;
        if (moduleGrade < 60) {
            gpa = 0;
        } else if (moduleGrade >= 100) {
            gpa = 4;
        } else {
            gpa = 4 - 3 * Math.pow((100 - moduleGrade), 2) / 1600;
        }

        // Round to 2 decimal places
        gpa = Math.round(gpa * 100.0) / 100.0;

        return gpa;
    }
    
    public Semester getSemester() {
        return semester;
    }

    // Getters
    public String getModuleName() {
        return moduleName;
    }


    public double getModuleGrade() {
        return moduleGrade;
    }

    public double getModuleGPA() {
        return moduleGPA;
    }

    public double getCredit() {
        return credit;
    }

    public String[] getSkill() {
        return skill;
    }

    // Setters
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setModuleGrade(double moduleGrade) {
        this.moduleGrade = moduleGrade;
    }

    public void setModuleGPA(double moduleGPA) {
        this.moduleGPA = moduleGPA;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public void setSkill(String[] skill) {
        this.skill = skill;
    }
   

}