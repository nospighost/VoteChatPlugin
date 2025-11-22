package org;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static String prefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        prefix = getConfig().getString("prefix");
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new VoteCommand());
        Bukkit.getLogger().info("[VoteChatPlugin] Plugin geladen!");
    }

    @Override
    public void onDisable() {
    }
}
