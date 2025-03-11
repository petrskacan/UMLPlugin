/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thesis.diagramplugin.rendering.kopenogram.treepainter;


import lombok.Getter;

import java.awt.*;

/**
 *
 * loading property file
 */
public class Settings {

        public static Color decodeColorProperty(String prop) {
                String[] rgbSt = prop.split(",");
                int r = 0, g = 0, b = 0;
                try {
                        r = Integer.parseInt(rgbSt[0]);
                        g = Integer.parseInt(rgbSt[1]);
                        b = Integer.parseInt(rgbSt[2]);
                } catch (Exception e) {
                }

                return new Color(r, g, b);
        }

        @Getter
        public static enum Property {
        KOPENOGRAM_KEY_WORDS("Kopenogram Key Words", "Kopenogram_Key_Words", "1",
        PropertyType.NUMBER),
        NUMBER_OF_SYMBOLS_IN_EXPRESSION("Number Of Symbols In Expression", "Number_Of_Symbols_In_Expression", "35", PropertyType.NUMBER),
        VERTICAL_GAP("Vertical Gap", "Vertical_Gap", "15",
        PropertyType.NUMBER),
        HORIZONTAL_GAP("Horizontal Gap", "Horizontal_Gap", "5",
        PropertyType.NUMBER),
        FONT_SIZE("Font Size", "Font_Size", "20", PropertyType.NUMBER),
        EXPRESSION_COLOR("Expression Color", "Expression_Color", "255,153,154", PropertyType.COLOR),
        BREAK_COLOR("Break Color", "Break_Color", "248,3,0", PropertyType.COLOR),
        CONTINUE_COLOR("Continue Color", "Continue_Color", "102,255,102", PropertyType.COLOR),
        CYCLE_HEAD_COLOR("Cycle Head Color", "Cycle_Head_Color", "102,255,102", PropertyType.COLOR),
        CYCLE_BODY_COLOR("Cycle Body Color", "Cycle_Body_Color", "201,255,154", PropertyType.COLOR),
        SWITCH_HEAD_COLOR("Switch Head Color", "Switch_Head_Color", "99,205,253", PropertyType.COLOR),
        SWITCH_BODY_COLOR("Switch Body Color", "Switch_Body_Color", "204,253,255", PropertyType.COLOR),
        METHOD_HEAD_COLOR("Method Head Color", "Method_Head_Color", "255,255,0", PropertyType.COLOR),
        METHOD_BODY_COLOR("Method Body Color", "Method_Body_Color", "255,255,203", PropertyType.COLOR),
        BLOCK_BODY_COLOR("Block Body Color", "Block_Body_Color", "255,255,203", PropertyType.COLOR),
        LABEL_COLOR("Label Color", "Label_Color", "255,102,0", PropertyType.COLOR),
        SYNCHRONIZED_HEAD_COLOR("Synchronized Head Color", "Synchronized_Head_Color", "153,153,153",
        PropertyType.COLOR),
        SYNCHRONIZED_BODY_COLOR("Synchronized Body Color", "Synchronized_Body_Color", "204,204,204", PropertyType.COLOR),
        THROW_COLOR("Throw Color", "Throw_Color", "248,3,0", PropertyType.COLOR),
        TRY_HEAD_COLOR("Try Head Color", "Try_Head_Color", "255,255,255", PropertyType.COLOR),
        TRY_BODY_COLOR("Try Body Color", "Try_Body_Color", "255,52,255", PropertyType.COLOR),
        CATCH_HEAD_COLOR("Catch Head Color", "Catch_Head_Color", "255,255,255", PropertyType.COLOR),
        CATCH_BODY_COLOR("Catch Body Color", "Catch_Body_Color", "255,165,0", PropertyType.COLOR),
        FINALLY_HEAD_COLOR("Finally Head Color", "Finally_Head_Color", "255,255,255", PropertyType.COLOR),
        FINALLY_BODY_COLOR("Finally Body Color", "Finally_Body_Color", "255,165,0", PropertyType.COLOR);

        private Property(String displayName, String identifier, String defValue, PropertyType type) {
            this.identifier = identifier;
            this.displayName = displayName;
            this.value = defValue;
            this.propertyType = type;
        }

        private final String identifier;
        private final String displayName;
        private String value;
        private final PropertyType propertyType;

        private static enum PropertyType {
            NUMBER, COLOR;
        }
        public void setValue(String newValue) {
                this.value = newValue;
        }
    }
}