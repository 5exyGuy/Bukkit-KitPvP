package com.escapeg.kitpvp.api.language;

import com.escapeg.kitpvp.KitPvP;

import java.util.ArrayList;

public class LanguageAPI {

    private KitPvP plugin;
    private ArrayList<Language> languages;
    private Language activeLanguage;

    public LanguageAPI(KitPvP plugin) {
        this.plugin = plugin;
        this.languages = new ArrayList<>();
        this.activeLanguage = null;
    }

    public void unregisterLanguages() {
        languages.clear();
    }

    public void registerLanguage(Language language) {
        if (languages.isEmpty()) {
            setActiveLanguage(language);
        }
        if (!languages.contains(language)) {
            languages.add(language);
        }
    }

    public void setActiveLanguage(Language language) {
        activeLanguage = language;
    }

    public Language getActiveLanguage() {
        return activeLanguage;
    }

    public String replaceKeys(String msg) {
        return getActiveLanguage().replaceKeys(msg);
    }

    public String replaceColoredKeys(String msg){
        return getActiveLanguage().replaceColoredKeys(msg);
    }

}
