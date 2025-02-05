package com.thesis.diagramplugin.rendering.kopenogram.treepainter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * usefull methods for painter
 */
public class PainterUtils {

    private static final Graphics GRAPHICS = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR).createGraphics();

    private PainterUtils() {
    }

    /**
     * return size of text in pixels
     */
    public static int getTextWidth(String text, Font font) {
        FontMetrics metrics = GRAPHICS.getFontMetrics(font);
        return metrics.stringWidth(text);
    }

    /**
     * if object is null return error message
     */
    public static void validateNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void saveToFile(Serializable object, String file) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(file)));
            out.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static Object readFromFile(String file) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(new File(file)));
            try {
                return in.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }
}
