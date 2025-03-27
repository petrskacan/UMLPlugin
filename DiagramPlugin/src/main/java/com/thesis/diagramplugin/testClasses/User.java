package com.thesis.diagramplugin.testClasses;

public class User {
    private String name;

    public User(String name) {
        this.name = name;
    }

    // Metoda využívá (závisí na) třídě Message, ale nevlastní ji jako atribut.
    // Jde tedy o Dependency – dočasné použití ve vstupním parametru.
    public void postMessage(String text, NotificationService service) {
        Message msg = new Message(text);  // Vzniká objekt Message
        service.sendNotification(msg);    // Odeslání notifikace
    }

    public static void main(String[] args) {
        User user = new User("Alice");
        NotificationService service = new NotificationService();

        user.postMessage("Hello!", service);
    }
}
