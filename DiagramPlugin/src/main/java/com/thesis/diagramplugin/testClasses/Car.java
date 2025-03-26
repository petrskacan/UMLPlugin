package com.thesis.diagramplugin.testClasses;

class Car {
    private String brand;
    private Engine engine;  // Agregace: Car obsahuje Engine, ale Engine může existovat samostatně

    public Car(String brand, Engine engine) {
        this.brand = brand;
        this.engine = engine;
    }

    public void startCar() {
        // Můžeme nastartovat motor, ale motor není na život vázán jen na auto
        engine.start();
        System.out.println("Car " + brand + " is starting.");
    }

    // další metody...
}
