package com.escapeg.kitpvp.api.language;

import com.escapeg.kitpvp.KitPvP;

import java.util.ArrayList;

public class LanguageAPI {

    private final KitPvP plugin;
    private final ArrayList<Language> languages;
    private Language activeLanguage;

    public LanguageAPI(final KitPvP plugin) {
        this.plugin = plugin;
        this.languages = new ArrayList<>();
        this.activeLanguage = null;
    }

    public void unregisterLanguages() {
        this.languages.clear();
    }

    public void registerLanguage(final Language language) {
        if (this.languages.isEmpty()) {
            setActiveLanguage(language);
        }
        if (!this.languages.contains(language)) {
            this.languages.add(language);
        }
    }

    public void setActiveLanguage(final Language language) {
        this.activeLanguage = language;
    }

    public Language getActiveLanguage() {
        return this.activeLanguage;
    }

    public String replaceKeys(final String msg) {
        return getActiveLanguage().replaceKeys(msg);
    }

    public String replaceColoredKeys(final String msg){
        return getActiveLanguage().replaceColoredKeys(msg);
    }

}
