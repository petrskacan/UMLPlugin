/*
 This file is part of the BlueJ program. 
 Copyright (C) 1999-2009,2010  Michael Kolling and John Rosenberg 
 
 This program is free software; you can redistribute it and/or 
 modify it under the terms of the GNU General Public License 
 as published by the Free Software Foundation; either version 2 
 of the License, or (at your option) any later version. 
 
 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of 
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 GNU General Public License for more details. 
 
 You should have received a copy of the GNU General Public License 
 along with this program; if not, write to the Free Software 
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. 
 
 This file is subject to the Classpath exception as provided in the  
 LICENSE.txt file that accompanied this code.
 */
package com.thesis.diagramplugin.rendering.classrelation.bluej.pkgmgr;


import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;


/**
 * Manages the printing of package assets eg. Class Diagram, Readme and Source
 * files. All source files are printed as one large batch. Individual printing
 * occurs through the source code editor
 *
 * @author Bruce Quig
 */
public class PackagePrintManager extends Thread {

    private PageFormat pageFormat;
    private Package pkg;

    /**
     * Constructor for PackagePrinter.
     *
     * @param pkg package to be printed
     * @param pageFormat pageformat of printer job
     * @param dialog the project print dialog used by user to select which
     * assets to print.
     */
    public PackagePrintManager(Package pkg, PageFormat pageFormat) {
        this.pkg = pkg;
        this.pageFormat = pageFormat;
    }

    /**
     * Overridden run method called as part of the usage of this class as a
     * background operation via a thread with lower priority.
     */
    public void run() {
        PrinterJob printer = PrinterJob.getPrinterJob();
        printClassDiagram(printer);
    }

    /**
     * Prints the graphical representation of classes in the package.
     *
     * @param printJob the printer job to print the diagram to.
     */
    public void printClassDiagram(PrinterJob printJob) {
//        ClassDiagramPrinter diagramPrinter = new ClassDiagramPrinter(printJob,
//                pkg,
//                pageFormat);
//        diagramPrinter.printPackage();
    }
}
