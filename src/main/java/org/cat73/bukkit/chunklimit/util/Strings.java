package org.cat73.bukkit.chunklimit.util;

public class Strings {
    protected Strings() {
        throw new UnsupportedOperationException();
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs == null) {
            return true;
        }

        for (int i = cs.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean notBlank(CharSequence cs) {
        return !isBlank(cs);
    }
}
