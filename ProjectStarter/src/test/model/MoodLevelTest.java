package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoodLevelTest {

    private MoodLevel verySad;
    private MoodLevel sad;
    private MoodLevel neutral;
    private MoodLevel happy;
    private MoodLevel veryHappy;

    @BeforeEach
    void runBefore() {
        verySad = MoodLevel.VERY_SAD;
        sad = MoodLevel.SAD;
        neutral = MoodLevel.NEUTRAL;
        happy = MoodLevel.HAPPY;
        veryHappy = MoodLevel.VERY_HAPPY;
    }

    @Test
    void testEnumFieldsHaveCorrectValues() {
        assertEquals(1, verySad.getValue());
        assertEquals(2, sad.getValue());
        assertEquals(3, neutral.getValue());
        assertEquals(4, happy.getValue());
        assertEquals(5, veryHappy.getValue());
    }

    @Test
    void testEnumLabelsAreCorrect() {
        assertEquals("😭 Very Sad", verySad.toString());
        assertEquals("☹️ Sad", sad.toString());
        assertEquals("😐 Neutral", neutral.toString());
        assertEquals("😊 Happy", happy.toString());
        assertEquals("😍 Very Happy", veryHappy.toString());
    }

    @Test
    void testGetEmojiReturnsCorrectEmoji() {
        assertEquals("😭", verySad.getEmoji());
        assertEquals("☹️", sad.getEmoji());
        assertEquals("😐", neutral.getEmoji());
        assertEquals("😊", happy.getEmoji());
        assertEquals("😍", veryHappy.getEmoji());
    }

    @Test
    void testGetLabelReturnsCorrectTextLabel() {
        assertEquals("Very Sad", verySad.getLabel());
        assertEquals("Sad", sad.getLabel());
        assertEquals("Neutral", neutral.getLabel());
        assertEquals("Happy", happy.getLabel());
        assertEquals("Very Happy", veryHappy.getLabel());
    }

    @Test
    void testFromValueReturnsCorrectEnum() {
        assertEquals(MoodLevel.VERY_SAD, MoodLevel.fromValue(1));
        assertEquals(MoodLevel.SAD, MoodLevel.fromValue(2));
        assertEquals(MoodLevel.NEUTRAL, MoodLevel.fromValue(3));
        assertEquals(MoodLevel.HAPPY, MoodLevel.fromValue(4));
        assertEquals(MoodLevel.VERY_HAPPY, MoodLevel.fromValue(5));
    }

    @Test
    void testFromValueThrowsExceptionForZero() {
        assertThrows(IllegalArgumentException.class, () -> MoodLevel.fromValue(0));
    }

    @Test
    void testFromValueThrowsExceptionForSix() {
        assertThrows(IllegalArgumentException.class, () -> MoodLevel.fromValue(6));
    }

    @Test
    void testFromValueThrowsExceptionForNegative() {
        assertThrows(IllegalArgumentException.class, () -> MoodLevel.fromValue(-1));
    }

    @Test
    void testFromValueThrowsExceptionForLargeNumber() {
        assertThrows(IllegalArgumentException.class, () -> MoodLevel.fromValue(999));
    }

    @Test
    void testFromValueExceptionMessageContainsInvalidValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> MoodLevel.fromValue(10));
        assertTrue(exception.getMessage().contains("10"));
        assertTrue(exception.getMessage().contains("Invalid mood value"));
    }

    @Test
    void testAllEnumValuesExistAndAreOrdered() {
        MoodLevel[] allLevels = MoodLevel.values();
        assertEquals(5, allLevels.length);
        assertEquals(MoodLevel.VERY_SAD, allLevels[0]);
        assertEquals(MoodLevel.SAD, allLevels[1]);
        assertEquals(MoodLevel.NEUTRAL, allLevels[2]);
        assertEquals(MoodLevel.HAPPY, allLevels[3]);
        assertEquals(MoodLevel.VERY_HAPPY, allLevels[4]);
    }
}