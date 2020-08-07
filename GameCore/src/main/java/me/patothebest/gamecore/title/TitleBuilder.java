package me.patothebest.gamecore.title;

import org.bukkit.ChatColor;

public final class TitleBuilder {

    ChatColor titleColor = ChatColor.WHITE;
    /* Subtitle text and color */ String subtitle = "";
    ChatColor subtitleColor = ChatColor.WHITE;
    /* Title timings */ int fadeInTime = -1;
    int stayTime = -1;
    int fadeOutTime = -1;
    /* Title text and color */ String title = "";

    private TitleBuilder() {
    }

    public static TitleBuilder newTitle() {
        return new TitleBuilder();
    }

    public TitleBuilder withTitleColor(ChatColor titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public TitleBuilder withSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public TitleBuilder withSubtitleColor(ChatColor subtitleColor) {
        this.subtitleColor = subtitleColor;
        return this;
    }

    public TitleBuilder withFadeInTime(int fadeInTime) {
        this.fadeInTime = fadeInTime;
        return this;
    }

    public TitleBuilder withStayTime(int stayTime) {
        this.stayTime = stayTime;
        return this;
    }

    public TitleBuilder withFadeOutTime(int fadeOutTime) {
        this.fadeOutTime = fadeOutTime;
        return this;
    }

    public TitleBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public Title build() {
        Title title = TitleManager.newInstance(this.title);
        title.setTitleColor(titleColor);
        title.setSubtitle(subtitle);
        title.setSubtitleColor(subtitleColor);
        title.setFadeInTime(fadeInTime);
        title.setStayTime(stayTime);
        title.setFadeOutTime(fadeOutTime);
        return title;
    }
}
