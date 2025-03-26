package com.thesis.diagramplugin.testClasses;

class Engine {
    private String type;

    public Engine(String type) {
        this.type = type;
    }

    public void start() {
        System.out.println("Engine of type " + type + " started.");
    }

    // další metody...
}
