package model;

/*
 * Represents the 5 mood levels with:
 *  - a numeric value (1–5)
 *  - an emoji symbol
 *  - a descriptive text label.
 */
public enum MoodLevel {
    VERY_SAD(1, "😭", "Very Sad"),
    SAD(2, "☹️", "Sad"),
    NEUTRAL(3, "😐", "Neutral"),
    HAPPY(4, "😊", "Happy"),
    VERY_HAPPY(5, "😍", "Very Happy");

    private final int value;     // numeric value for the mood (1–5)
    private final String emoji;  // emoji symbol for this mood level
    private final String label;  // text label for display

    // REQUIRES: 1 <= value <= 5
    // EFFECTS: constructs a MoodLevel constant with given numeric value, emoji and label
    MoodLevel(int value, String emoji, String label) {
        this.value = value;
        this.emoji = emoji;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getLabel() {
        return label;
    }

    // EFFECTS: returns a combined label "emoji + space + text"
    @Override
    public String toString() {
        return emoji + " " + label;
    }

    // EFFECTS: returns the MoodLevel constant that matches the given numeric value;
    //          throws IllegalArgumentException if no matching MoodLevel exists
    public static MoodLevel fromValue(int value) {
        for (MoodLevel m : values()) {
            if (m.value == value) {
                return m;
            }
        }
        throw new IllegalArgumentException("Invalid mood value: " + value);
    }
}