package client.services;

import client.config.ConfigManager;
import client.scenes.Internationalizable;

import java.util.*;

public class LocaleManager {

    private static final String BUNDLE_NAME = "client.language";
    public static final Locale EN = Locale.ENGLISH;
    public static final Locale DE = Locale.GERMAN;
    public static final Locale NL = Locale.forLanguageTag("nl-NL");

    private Locale currentLocale = Locale.ENGLISH;

    private final List<Internationalizable> registeredCtrls = new ArrayList<>();

    private ConfigManager configManager;

    public void init(ConfigManager configManager) {
        this.configManager = configManager;

        String savedLang = configManager.getConfig().getLanguagePreference();

        if (savedLang != null && !savedLang.isEmpty()) {
            this.currentLocale = Locale.forLanguageTag(savedLang);
        } else {
            this.currentLocale = EN;
        }
    }

    public void register(Internationalizable ctrl) {
        registeredCtrls.add(ctrl);
    }

    public void setAllLocale(Locale locale) {
        currentLocale = locale;

        if (configManager != null) {
            configManager.getConfig().setLanguagePreference(locale.toLanguageTag());
            configManager.save();
        }

        for (Internationalizable ctrl : registeredCtrls) {
            ctrl.setLocale(locale);
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public String getBundleName() {
        return BUNDLE_NAME;
    }

    public ResourceBundle getCurrentBundle() {
        return ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
    }

}