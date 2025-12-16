package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class LanguageEnumTest {
    private Language languageEN;
    private Language languageNL;
    private Language languageDE;

    @BeforeEach
    void setup() {
        languageEN = Language.EN;
        languageNL = Language.NL;
        languageDE = Language.DE;
    }

    @Test
    void constructorTest() {
        assertNotNull(languageEN, "Initialized language should not be null.");
        assertNotNull(languageNL, "Initialized language should not be null.");
        assertNotNull(languageDE, "Initialized language should not be null.");
    }

    @Test
    void properTest() {
        String expected = "English";
        String actual = languageEN.proper();
        assertEquals(expected, actual, "Expected proper default language name of EN to be English.");
    }

    @Test
    void loadProperLabelTest() {
        var resourceBundle = ResourceBundle.getBundle("commons.language", Locale.GERMAN);
        languageDE.loadProperLabel(resourceBundle);
        String expected = "Deutsch";
        String actual = languageDE.proper();
        assertEquals(expected, actual, "Expected proper language name of DE to be Deutsch in the German locale.");
    }

    @Test
    void reloadLocaleTest() {
        List<String> expectedPropers = new ArrayList<>(List.of("Englisch", "Niederländisch", "Deutsch"));
        List<String> actualPropers = new ArrayList<>();

        var resourceBundle = ResourceBundle.getBundle("commons.language", Locale.GERMAN);
        Language.reloadLocale(resourceBundle);

        for (Language language : Language.values()) {
            actualPropers.add(language.proper());
        }
        expectedPropers.sort(String::compareTo);
        actualPropers.sort(String::compareTo);

        assertEquals(expectedPropers, actualPropers, "Expected proper language names to be in German after" +
                " reloading the locale.");
    }

    @Test
    void valueOfProperInEnglishTest() {
        var resourceBundle = ResourceBundle.getBundle("commons.language", Locale.ENGLISH);
        Language.reloadLocale(resourceBundle);
        Language actual = Language.valueOfProper("German");
        assertEquals(languageDE, actual, "Expected the proper language name German to return a DE language enum.");
    }

    @Test
    void valueOfProperInGermanTest() {
        var resourceBundle = ResourceBundle.getBundle("commons.language", Locale.GERMAN);
        Language.reloadLocale(resourceBundle);
        Language actual = Language.valueOfProper("Deutsch");
        assertEquals(languageDE, actual, "Expected the proper language name Deutsch to return a DE language enum.");
    }
}