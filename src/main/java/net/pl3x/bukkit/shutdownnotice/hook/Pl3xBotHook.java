package net.pl3x.bukkit.shutdownnotice.hook;

import net.pl3x.bukkit.pl3xbot.Pl3xBot;

public class Pl3xBotHook {
    public void sendToDiscord(String message) {
        Pl3xBot.getPlugin().sendToDiscord(message);
    }
}
