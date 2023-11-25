package com.example.tenanteye;

import android.graphics.Color;

public enum PasswordStrength {

    WEAK(R.string.password_strength_weak, Color.RED),
    MEDIUM(R.string.password_strength_medium, Color.argb(255, 220, 185, 0)),
    STRONG(R.string.password_strength_strong, Color.GREEN),
    VERY_STRONG(R.string.password_strength_very_strong, Color.BLUE);

    //--------REQUIREMENTS--------
    private static final int REQUIRED_LENGTH = 8;
    private static final boolean REQUIRE_SPECIAL_CHARACTERS = true;

    private final int resId;
    private final int color;

    PasswordStrength(int resId, int color) {
        this.resId = resId;
        this.color = color;
    }

    public static PasswordStrength calculateStrength(String password) {
        int currentScore = 0;
        boolean sawUpper = false;
        boolean sawLower = false;
        boolean sawDigit = false;
        boolean sawSpecial = false;


        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (!sawSpecial && !Character.isLetterOrDigit(c)) {
                currentScore += 1;
                sawSpecial = true;
            } else {
                if (!sawDigit && Character.isDigit(c)) {
                    currentScore += 1;
                    sawDigit = true;
                } else {
                    if (!sawUpper || !sawLower) {
                        if (Character.isUpperCase(c))
                            sawUpper = true;
                        else
                            sawLower = true;
                        if (sawUpper && sawLower)
                            currentScore += 1;
                    }
                }
            }

        }

        if (password.length() > REQUIRED_LENGTH) {
            boolean REQUIRE_DIGITS = true;
            boolean REQUIRE_LOWER_CASE = true;
            boolean REQUIRE_UPPER_CASE = false;
            if ((REQUIRE_SPECIAL_CHARACTERS && !sawSpecial)
                    || (REQUIRE_UPPER_CASE && !sawUpper)
                    || (REQUIRE_LOWER_CASE && !sawLower)
                    || (REQUIRE_DIGITS && !sawDigit)) {
                currentScore = 1;
            } else {
                currentScore = 2;
                int MAXIMUM_LENGTH = 15;
                if (password.length() > MAXIMUM_LENGTH) {
                    currentScore = 3;
                }
            }
        } else {
            currentScore = 0;
        }

        switch (currentScore) {
            case 0:
                return WEAK;
            case 1:
                return MEDIUM;
            case 2:
                return STRONG;
            case 3:
                return VERY_STRONG;
            default:
        }

        return VERY_STRONG;
    }

    public CharSequence getText(android.content.Context ctx) {
        return ctx.getText(resId);
    }

    public int getColor() {
        return color;
    }

}
