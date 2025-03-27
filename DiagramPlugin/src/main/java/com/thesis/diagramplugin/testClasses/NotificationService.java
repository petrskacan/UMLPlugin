package com.thesis.diagramplugin.testClasses;

public class NotificationService {
    public void sendNotification(Message message) {
        System.out.println("Sending notification: " + message.getContent());
    }
}
