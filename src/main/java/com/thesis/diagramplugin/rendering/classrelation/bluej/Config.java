/*
 This file is part of the BlueJ program.
 Copyright (C) 1999-2009,2010,2011,2012,2013,2014,2015  Michael Kolling and John Rosenberg

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
package com.thesis.diagramplugin.rendering.classrelation.bluej;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Properties;

import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Class to handle application configuration for BlueJ.
 * The configuration information is spread over several files: <BR>
 * <BR>
 *  &lt;bluej_home>/lib/bluej.defs <BR>
 *  &lt;user_home>/.bluej/bluej.properties <BR>
 *  command line arguments in form -D&lt;prop>=&lt;val> <BR>
 * <BR>
 * bluej.defs - contains system definitions which are not user specific<BR>
 * bluej.properties - contains user specific settings.
 *    Settings here override settings in bluej.defs <BR>
 * command line arguments - contains per-launch specific settings.
 *    Settings here override settings in bluej.properties <BR>
 * <BR>
 * There is also a set of language specific labels
 * in a directory named after the language
 *  &lt;bluej_home>/lib/&lt;language>/labels
 *
 * @author Michael Cahill
 * @author Michael Kolling
 * @author Andrew Patterson
 */
public final class Config
{
    public static final String nl = System.getProperty("line.separator");

    public static Properties moeSystemProps;  // moe (editor) properties
    public static Properties moeUserProps;    // moe (editor) properties

    public static String compilertype = "javac";  // current compiler (javac, jikes)
    public static String language;      // message language (english, ...)

    public static Rectangle screenBounds; // maximum dimensions of screen

    public static final String DEFAULT_LANGUAGE = "english";
    public static final String BLUEJ_OPENPACKAGE = "bluej.openPackage";

    public static final Color ENV_COLOUR = new Color(152,32,32);


    // a border for components with keyboard focus
    public static final Border focusBorder = new CompoundBorder(new LineBorder(Color.BLACK),
            new BevelBorder(BevelBorder.LOWERED,
                    new Color(195, 195, 195),
                    new Color(240, 240, 240),
                    new Color(195, 195, 195),
                    new Color(124, 124, 124)));

    // a border for components without keyboard focus
    public static final Border normalBorder = new CompoundBorder(new EmptyBorder(1,1,1,1),
            new BevelBorder(BevelBorder.LOWERED,
                    new Color(195, 195, 195),
                    new Color(240, 240, 240),
                    new Color(124, 124, 124),
                    new Color(195, 195, 195)));


    // bluej configuration properties hierarchy
    // (command overrides user which overrides system)

    private static Properties commandProps;     // specified on the command line


    protected static final int SHORTCUT_MASK =
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    // Bit ugly having it here, but it's needed by MiscPrefPanel (which may just be in BlueJ)
    // and by Greenfoot
    public static final KeyStroke GREENFOOT_SET_PLAYER_NAME_SHORTCUT =
        KeyStroke.getKeyStroke(KeyEvent.VK_P, SHORTCUT_MASK | InputEvent.SHIFT_DOWN_MASK);

    private static Color selectionColour;
    private static Color selectionColour2;
    private static Color highlightColour;
    private static Color highlightColour2;

    /**
     * Get the screen size information
     */
    public static Rectangle calculateScreenBounds()
    {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        return new Rectangle(d);
    }

    /**
     * Return a color value from the bluej properties.
     */
    public static Color getItemColour(String itemname)
    {
        try {
            String rgbStr = getPropString(itemname, "255,0,255");
            String rgbVal[] = Utility.split(rgbStr, ",");

            if (rgbVal.length < 3)
                System.out.println("Error reading colour ["+itemname+"]");
            else {
                int r = Integer.parseInt(rgbVal[0].trim());
                int g = Integer.parseInt(rgbVal[1].trim());
                int b = Integer.parseInt(rgbVal[2].trim());

                return new Color(r, g, b);
            }
        }
        catch(Exception e) {
            System.out.println("Could not get colour for " + itemname);
        }

        return null;
    }

    /**
     * Return a color value from the bluej properties.
     *
     * If the value is not present, return null
     */
    public static Color getOptionalItemColour(String itemname)
    {
        try {
            String rgbStr = getPropString(itemname, null);
            if (rgbStr == null) {
                return null;
            }

            String rgbVal[] = Utility.split(rgbStr, ",");
            if (rgbVal.length < 3) {
                System.out.println("Error reading colour ["+itemname+"]");
            }
            else {
                int r = Integer.parseInt(rgbVal[0].trim());
                int g = Integer.parseInt(rgbVal[1].trim());
                int b = Integer.parseInt(rgbVal[2].trim());

                return new Color(r, g, b);
            }
        }
        catch(Exception e) {
            System.out.println("Could not get colour for " + itemname);
        }

        return null;
    }

    /**
     * Return a color value for selections.
     */
    public static Color getSelectionColour()
    {
        if(selectionColour == null) {
            selectionColour = Config.getItemColour("colour.selection");
        }
        return selectionColour;
    }

    /**
     * Return the second (gradient) color value for selections.
     */
    public static Color getSelectionColour2()
    {
        if(selectionColour2 == null) {
            selectionColour2 = Config.getItemColour("colour.selection2");
        }
        return selectionColour2;
    }

    /**
     * Return a color value for selections.
     */
    public static Color getHighlightColour()
    {
        if(highlightColour == null) {
            highlightColour = Config.getItemColour("colour.highlight");
        }
        return highlightColour;
    }

    /**
     * Return the second (gradient) color value for selections.
     */
    public static Color getHighlightColour2()
    {
        if(highlightColour2 == null) {
            highlightColour2 = Config.getItemColour("colour.highlight2");
        }
        return highlightColour2;
    }

    /**
     * Get a font from a specified property, using the given default font name and
     * the given size. Font name can end with "-bold" to indicate bold style.
     */
    public static Font getFont(String propertyName, String defaultFontName, int size)
    {
        String fontName = getPropString(propertyName, defaultFontName);

        int style;
        if(fontName.endsWith("-bold")) {
            style = Font.BOLD;
            fontName = fontName.substring(0, fontName.length()-5);
        }
        else {
            style = Font.PLAIN;
        }

        return new Font(fontName, style, size);
    }

    /**
     * Return a point, read from the config files. The config properties
     * are formed by adding ".x" and ".y" to the itemPrefix.
     */
    public static Point getLocation(String itemPrefix)
    {
        try {
            int x = getPropInteger(itemPrefix + ".x", 16);
            int y = getPropInteger(itemPrefix + ".y", 16);

            if (x > (screenBounds.width - 16))
                x = screenBounds.width - 16;

            if (y > (screenBounds.height - 16))
                y = screenBounds.height - 16;

            return new Point(x,y);
        }
        catch(Exception e) {
            System.err.println("Could not get screen location for " + itemPrefix);
        }

        return new Point(16,16);
    }
    
        /**
     * Get a non-language dependant integer from the BlueJ properties
     * ("bluej.defs" or "bluej.properties") with a default value
     */
    public static int getPropInteger(String intname, int def)
    {
        int value;
        try {
            value = Integer.parseInt(getPropString(intname, String.valueOf(def)));
        }
        catch(NumberFormatException nfe) {
            return def;
        }
        return value;
    }
    
     /**
     * Get a non-language-dependent string from the BlueJ properties
     * ("bluej.defs" or "bluej.properties") with a default value. Variable
     * substitution ($varname) is performed on the value (and will be
     * performed on the default value if that is used).
     */
    public static String getPropString(String strname, String def)
    {
        return getPropString(strname, def, commandProps);
    }
    
    /**
     * Get a property string from the given properties map, using variable substitution.
     * If the variable is not defined the given default value is used (and variable
     * substitution is performed on it).
     * 
     * @param strname  The name of the property thats value is to be retrieved
     * @param def      The default value to use if the value is not defined
     * @param props    The properties to retrieve the value from
     * @return  The property value after variable substitution
     */
    public static String getPropString(String strname, String def, Properties props)
    {
        String propVal = props.getProperty(strname, def);
        if (propVal == null) {
            propVal = def;
        }
        if (propVal != null) {
            return PropParser.parsePropString(propVal, props);
        }
        return null;
    }
}