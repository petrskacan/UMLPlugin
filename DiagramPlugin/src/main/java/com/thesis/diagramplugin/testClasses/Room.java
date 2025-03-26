package com.thesis.diagramplugin.testClasses;

class Room {
    private String roomName;

    public Room(String roomName) {
        this.roomName = roomName;
    }

    public void describe() {
        System.out.println("This is the " + roomName);
    }
}

