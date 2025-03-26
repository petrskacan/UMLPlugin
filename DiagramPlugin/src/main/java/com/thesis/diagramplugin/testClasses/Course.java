package com.thesis.diagramplugin.testClasses;

public class Course {
    private String courseName;

    public Course(String courseName) {
        this.courseName = courseName;
    }

    // Vztah "Student – účastní se – Course"
    // Není vlastnictví, pouze "student" chodí na "kurz"
    // Ve třídě Student bychom mohli mít: private Course enrolledCourse;
    // Nebo naopak v Course seznam Studentů
    // Zde je pouze ukázka prosté asociace:
    public void enrollStudent(Student student) {
        System.out.println("Student " + student + " enrolled in course " + courseName);
    }

    // další metody...
}