package client.services;

import client.scenes.Internationalizable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleManager {

    private static final String BUNDLE_NAME = "client.language";

    private Locale currentLocale = Locale.ENGLISH;

    private static final List<Internationalizable> registeredCtrls = new ArrayList<>();

    public LocaleManager() {
        //TODO load initial locale from config
    }

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