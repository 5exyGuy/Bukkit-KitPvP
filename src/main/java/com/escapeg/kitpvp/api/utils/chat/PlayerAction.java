package com.escapeg.kitpvp.api.utils.chat;

import com.escapeg.kitpvp.api.API;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerAction {

    private UUID uuid;
    private API api;

    private TextComponent message;
    private ClickAction clickAction;
    private boolean discard;

    public PlayerAction(API api, Player player, ClickData clickData) {
        this.uuid = player.getUniqueId();
        this.api = api;
        this.message = new TextComponent(API.translateColorCodes(api.getLanguageAPI().getActiveLanguage().replaceKeys(clickData.getMessage())));
        this.clickAction = clickData.getClickAction();
        this.discard = clickData.isDiscard();
    }

    public void run(Player player) {
        if (clickAction != null) {
            clickAction.run(api, player);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public API getAPI() {
        return api;
    }

    public void setApi(API api) {
        this.api = api;
    }

    public TextComponent getMessage() {
        return message;
    }

    public void setMessage(TextComponent message) {
        this.message = message;
    }

    public ClickAction getClickAction() {
        return clickAction;
    }

    public void setClickAction(ClickAction clickAction) {
        this.clickAction = clickAction;
    }

    public boolean isDiscard() {
        return discard;
    }
}
