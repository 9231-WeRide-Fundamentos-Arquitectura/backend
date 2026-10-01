package org.example.backendweride.platform.profile.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
public class Preferences {
    private String language;
    private boolean notifications;
    private String theme;

    public Preferences(String language, boolean notifications, String theme) {
        this.language = language != null ? language : "es";
        this.notifications = notifications;
        this.theme = theme != null ? theme : "light";
    }

    public static Preferences defaultPreferences() {
        return new Preferences("es", true, "light");
    }

    public Preferences updateLanguage(String language) {
        return new Preferences(language, this.notifications, this.theme);
    }

    public Preferences updateNotifications(boolean notifications) {
        return new Preferences(this.language, notifications, this.theme);
    }

    public Preferences updateTheme(String theme) {
        return new Preferences(this.language, this.notifications, theme);
    }
}