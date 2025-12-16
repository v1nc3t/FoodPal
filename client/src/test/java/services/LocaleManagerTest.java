package services;

import client.scenes.Internationalizable;
import client.services.LocaleManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class LocaleManagerTest {

    private LocaleManager localeManager;
    private TestInternationalizable testCtrl1;
    private TestInternationalizable testCtrl2;
    private Locale testLocale = Locale.GERMAN;

    @BeforeEach
    public void setUp() {
        localeManager = new LocaleManager();

        testCtrl1 = new TestInternationalizable();
        testCtrl2 = new TestInternationalizable();
    }

    @Test
    public void localeIsEnglishTest() {
        assertEquals(Locale.ENGLISH, localeManager.getCurrentLocale());
    }

    @Test
    public void getCorrectBundleTest() {
        assertEquals("client.language", localeManager.getBundleName());
    }

    @Test
    public void changeOfLocaleGetsPropagatedTest() {
        localeManager.register(testCtrl1);
        localeManager.register(testCtrl2);

        localeManager.setAllLocale(testLocale);

        assertEquals(testLocale, localeManager.getCurrentLocale());

        assertEquals(testLocale, testCtrl1.getLocale());
        assertEquals(testLocale, testCtrl2.getLocale());
    }

    @Test
    public void getBundleUpdatedTest() {
        localeManager.setAllLocale(testLocale);

        ResourceBundle bundle = localeManager.getCurrentBundle();

        assertEquals(testLocale, bundle.getLocale());
    }

    @Test
    public void getBundleThrowNoExceptionTest() {
        Locale defaultLocale = Locale.ENGLISH;
        localeManager.setAllLocale(defaultLocale);

        assertDoesNotThrow(() -> {
            ResourceBundle bundle = localeManager.getCurrentBundle();
            assertEquals(defaultLocale, bundle.getLocale());
        });
    }

    static class TestInternationalizable implements Internationalizable {
        private Locale locale;

        @Override
        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        public Locale getLocale() {
            return locale;
        }
    }
}
