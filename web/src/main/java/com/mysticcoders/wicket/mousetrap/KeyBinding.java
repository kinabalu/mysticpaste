package com.mysticcoders.wicket.mousetrap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * KeyBinding for mousetrap.js
 *
 * @author Andrew Lombardi
 */
public class KeyBinding implements Serializable {
    private static final long serialVersionUID = 1L;

    // Modifiers
    public static final String CTRL = "ctrl";
    public static final String SHIFT = "shift";
    public static final String ALT = "alt";
    public static final String OPTION = "option";
    public static final String META = "meta";
    public static final String COMMAND = "command";

    // Special Keys
    public static final String BACKSPACE = "backspace";
    public static final String TAB = "tab";
    public static final String ENTER = "enter";
    public static final String RETURN = "return";
    public static final String CAPSLOCK = "capslock";
    public static final String ESC = "esc";
    public static final String SPACE = "space";
    public static final String PAGEUP = "pageup";
    public static final String PAGEDOWN = "pagedown";
    public static final String END = "end";
    public static final String HOME = "home";
    public static final String LEFT = "left";
    public static final String UP = "up";
    public static final String RIGHT = "right";
    public static final String DOWN = "down";
    public static final String INS = "ins";
    public static final String DEL = "del";

    // Additional specification for key events
    public static final String EVENT_KEYPRESS = "keypress";
    public static final String EVENT_KEYDOWN = "keydown";
    public static final String EVENT_KEYUP = "keyup";

    private String eventType = null;

    private List<String> keysOptions = new ArrayList<String>();

    public KeyBinding() { }

    /**
     * If we want to capture a specific key event
     *
     * @param eventType one of keypress, keydown, keyup
     */
    public KeyBinding(String eventType) {
        if (EVENT_KEYPRESS.equals(eventType) || EVENT_KEYDOWN.equals(eventType) || EVENT_KEYUP.equals(eventType)) {
            this.eventType = eventType;
        }
    }

    /**
     * Convenience method for adding a formatted String for use with mousetrap.js
     *
     * @param combo is this a key combo or sequence
     * @param keys a vararg of keys
     * @return KeyBinding object
     */
    private KeyBinding addKeys(boolean combo, String... keys) {
        if (keys == null || keys.length == 0) return this;

        StringBuilder watchKeys = new StringBuilder();
        for (String key : keys) {
            watchKeys.append(key).append(combo ? "+" : " ");
        }
        watchKeys.deleteCharAt(watchKeys.length() - 1);
        keysOptions.add(watchKeys.toString());

        return this;
    }

    /**
     * Add a key combination such as:
     * <p/>
     * ctrl+shift+up
     *
     * @param keys vararg of keys to add to a binding
     * @return KeyBinding object
     */
    public KeyBinding addKeyCombo(String... keys) {
        return addKeys(true, keys);
    }

    /**
     * Add a key sequence such as:
     * <p/>
     * g a
     *
     * @param keys vararg of keys to bind to a sequence
     * @return KeyBinding object
     */
    public KeyBinding addKeySequence(String... keys) {
        return addKeys(false, keys);
    }


    public String getEventType() {
        return eventType;
    }

    /**
     * Put together a String for use with mousetrap.js
     *
     * @return String formatted for use with mousetrap.js
     */
    public String toString() {
        StringBuilder mtKeys = new StringBuilder();
        if (keysOptions.size() > 1) {
            mtKeys.append("[");
        }

        for (String key : keysOptions) {
            mtKeys.append("'").append(key).append("',");
        }
        mtKeys.deleteCharAt(mtKeys.length() - 1);

        if (keysOptions.size() > 1) {
            mtKeys.append("]");
        }
        return mtKeys.toString();
    }
}