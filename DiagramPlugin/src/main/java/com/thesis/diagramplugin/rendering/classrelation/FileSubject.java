/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thesis.diagramplugin.rendering.classrelation;

/**
 *
 * @author vojta
 */
public interface FileSubject {
    public void notifyListeners(String newFilePath);
    public void addListener(FileListener listener);
    public void removeListener(FileListener listener);
}
