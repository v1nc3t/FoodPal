package client.services;

import client.scenes.Internationalizable;

import java.util.*;

public class LocaleManager {

    private static final String BUNDLE_NAME = "client.language";

    private Locale currentLocale = Locale.ENGLISH;

    private final List<Internationalizable> registeredCtrls = new ArrayList<>();

    public void register(Internationalizable ctrl) {
        registeredCtrls.add(ctrl);
    }

    public void setAllLocale(Locale locale) {
        currentLocale = locale;

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