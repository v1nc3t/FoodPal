package services;

import client.config.ConfigManager;
import client.scenes.Internationalizable;
import client.services.LocaleManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

public class LocaleManagerTest {

    private LocaleManager localeManager;
    private ConfigManager configManager;
    private TestInternationalizable testCtrl1;
    private TestInternationalizable testCtrl2;
    private Locale testLocale = LocaleManager.DE;

    @TempDir
    Path tempDir;

    private String testCfgPath;

    @BeforeEach
    public void setUp() {
        localeManager = new LocaleManager();

        testCfgPath = tempDir.resolve("test-config.json").toString();
        configManager = new ConfigManager(testCfgPath);

        testCtrl1 = new TestInternationalizable();
        testCtrl2 = new TestInternationalizable();
    }

    @Test
    public void initDefaultTest() {
        configManager.load();

        localeManager.init(configManager);

        assertEquals(LocaleManager.EN, localeManager.getCurrentLocale());
    }

    @Test
    public void initDutchFromConfigTest() {
        configManager.load();
        configManager.getConfig().setLanguagePreference("nl-NL");
        configManager.save();

        ConfigManager newManager = new ConfigManager(testCfgPath);
        newManager.load();

        localeManager.init(newManager);

        assertEquals(LocaleManager.NL, localeManager.getCurrentLocale());
        assertEquals("nl-NL", localeManager.getCurrentLocale().toLanguageTag());
    }

    @Test
    public void initGermanFromConfigTest() {
        configManager.load();
        configManager.getConfig().setLanguagePreference("DE");
        configManager.save();

        ConfigManager newManager = new ConfigManager(testCfgPath);
        newManager.load();

        localeManager.init(newManager);

        assertEquals(LocaleManager.DE, localeManager.getCurrentLocale());
        assertEquals("de", localeManager.getCurrentLocale().toLanguageTag());
    }

    @Test
    public void getCorrectBundleTest() {
        assertEquals("client.language", localeManager.getBundleName());
    }

    @Test
    public void changeOfLocaleGetsPropagatedTest() {
        configManager.load();
        localeManager.init(configManager);

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

    @Test
    public void getNullConfigManagerTest() {
        assertNull(localeManager.getConfigManager(),
                "Expected localeManager to have no config manager before initialization.");
    }

    @Test
    public void getConfigManagerTest() {
        configManager.load();
        localeManager.init(configManager);

        assertEquals(configManager, localeManager.getConfigManager(),
                "Expected localeManager to have a different config manager.");
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
