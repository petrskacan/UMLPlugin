package com.thesis.diagramplugin.testClasses;

import java.util.ArrayList;
import java.util.List;

class House {
    // Kompozice: House vlastní Room. Room existuje pouze v kontextu House.
    private List<Room> rooms = new ArrayList<>();

    public House() {
        // V konstruktoru vytvoříme konkrétní Room objekty
        rooms.add(new Room("Kitchen"));
        rooms.add(new Room("Living Room"));
        rooms.add(new Room("Bedroom"));
    }

    public void describeHouse() {
        for (Room room : rooms) {
            room.describe();
        }
    }

    // Když zanikne House, tak zaniknou i Room (nemají smysl existovat bez House)
    // V čistém modelu by se to projevilo třeba tak, že reference na Room zmizí a nelze ji "mimo" zachovat.
}
