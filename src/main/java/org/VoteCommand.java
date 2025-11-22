package org;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("abstimmung")
public class VoteCommand extends BaseCommand {


    int yes = 0;
    int no = 0;
    List<String> votedUser = new ArrayList<>();
    boolean isVoting = false;
    @Subcommand("create")
    @CommandPermission("abstimmung.create")
    public void onDefault(Player player, @Syntax("<Frage>") String question) {
        if(isVoting){
            player.sendMessage(Main.prefix + "§cEs läuft bereits eine Abstimmung!");
            return;
        }
        sendVoteMessage(question);
        isVoting = true;
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
            Bukkit.broadcastMessage(Main.prefix + "Abstimmung abgeschlossen! Ergebnis:");
            Bukkit.broadcastMessage(Main.prefix + "Ja: " + yes);
            Bukkit.broadcastMessage(Main.prefix + "Nein: " + no);
            if(yes == no){
                Bukkit.broadcastMessage(Main.prefix + "Es ist unentschieden!");
            } else {
                boolean yesWinner = yes > no;
                if (yesWinner) {
                    Bukkit.broadcastMessage(Main.prefix + "§aJa hat gewonnen!");
                } else {
                    Bukkit.broadcastMessage(Main.prefix + "§cNein hat gewonnen!");
                }
            }

            isVoting = false;
            yes = 0;
            no = 0;
            votedUser.clear();
        }, 20 * Main.getPlugin(Main.class).getConfig().getInt("abstimmung.duration"));
    }

    public void sendVoteMessage(String question) {
        TextComponent message = new TextComponent("§bStimme ab: ");

        TextComponent yes = new TextComponent("[Ja]");
        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/abstimmung internalvote yes"));
        yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.ComponentBuilder("§a§lKlicke um Ja zu stimmen").create()));
        yes.setColor(net.md_5.bungee.api.ChatColor.GREEN);

        TextComponent no = new TextComponent("[Nein]");
        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/abstimmung internalvote no"));
        no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.ComponentBuilder("§c§l    Klicke um für Nein zu stimmen").create()));
        no.setColor(net.md_5.bungee.api.ChatColor.RED);

        message.addExtra(yes);
        message.addExtra(" ");
        message.addExtra(no);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(Main.prefix);
            p.sendMessage(Main.prefix + question);
            p.sendMessage(Main.prefix);
            p.spigot().sendMessage(message);
        }
    }

    @CommandAlias("internalvote")
    public void onInternalVote(CommandSender sender, String choice) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if( votedUser.contains(player.getName())){
            player.sendMessage(Main.prefix + "Du hast bereits abgestimmt!");
            return;
        }
        votedUser.add(player.getName());

        if (choice.equals("yes")) {
            yes++;
        } else {
            no++;
        }

        player.sendMessage(Main.prefix + "Du hast abgestimmt!: " + choice);
    }

}
