package me.solarbakha.solarlevel.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.solarbakha.solarlevel.SolarLevel;
import me.solarbakha.solarlevel.events.XPGain_BlockBreak;
import me.solarbakha.solarlevel.manager.XPManager;
import me.solarbakha.solarlevel.manager.misc.Localization;
import me.solarbakha.solarlevel.manager.misc.PlaceholderAPIXP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class solarLevelCMD implements TabExecutor {
    private final List<String> actions = Arrays.asList("get", "add", "set", "reload");
    private final List<String> actionsOpt = Arrays.asList("points", "levels");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("solarlevel.use") || sender.isOp() || sender instanceof ConsoleCommandSender) {
// todo: rewrite this
            switch (testCommand(args)) {
                case -1 -> {
                    sender.sendMessage(Localization.getString("Command.usage"));
                    return true;
                }
                case 1 -> {
                    sender.sendMessage(Localization.getString("Errors.notEnoughArguments"));
                    return true;
                }
                case 2, 4, 5 -> {
                    sender.sendMessage(Localization.getString("Errors.wrongArgument"));
                    return true;
                }
                case 3 -> {
                    sender.sendMessage(Localization.getString("Errors.playerNotFound"));
                    return true;
                }
                case 11 -> {
                    Localization.reload();
                    SolarLevel.getInstance().reloadConfig();
                    XPManager.init();
                    if (SolarLevel.getCorePApi() != null) XPGain_BlockBreak.init();
                    if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) { //todo: improve ver check, like coreprot
                        new PlaceholderAPIXP(SolarLevel.getInstance()).register();
                    }
                    sender.sendMessage(Localization.getString("Command.reloadMessage"));

                    return true;
                }
                case 0, 10 -> {

                }  // ignore missing optional args error | todo: make use of it and remove checks below
                default -> {
//                    sender.sendMessage(Localisation.getString("Command.usage"));
                    return false; // should never happen, in case it does my brain hurts, send default help message
                }
            }

            int optIndex = (args[0].equals(actions.get(0))) ? 2 : 3;

            Player target = Bukkit.getServer().getPlayerExact(args[1]);
            assert target != null;
            String targetID = target.getUniqueId().toString();

            if (args[0].equals(actions.get(0))) {
                String message = Localization.parseString("Command.getMessage");
                int querry;
                if ((args.length == optIndex + 1) && Objects.equals(args[optIndex], actionsOpt.get(1))) {
                    querry = XPManager.getLevel(targetID);
                    message = message.formatted(target.getName(), querry, (querry == 1 || querry == -1) ? "level" : "levels");
                } else {
                    querry = XPManager.getXP(targetID);
                    message = message.formatted(target.getName(), querry, (querry == 1 || querry == -1) ? "exp point" : "exp points");
                }
                sender.sendMessage(message);
                return true;
            }
            if (args[0].equals(actions.get(1))) {
                int added = Integer.parseInt(args[2]);
                String message = Localization.parseString("Command.addMessage");
                if ((args.length == optIndex + 1) && Objects.equals(args[optIndex], actionsOpt.get(1))) {
                    XPManager.addLevel(targetID, added);
                    message = message.formatted(target.getName(), added, (added == 1 || added == -1) ? "level" : "levels");
                } else {
                    XPManager.addXP(targetID, added);
                    message = message.formatted(target.getName(), added, (added == 1 || added == -1) ? "exp point" : "exp points");
                }
                sender.sendMessage(message);
                return true;
            }
            if (args[0].equals(actions.get(2))) {
                int set = Integer.parseInt(args[2]);
                String message = Localization.parseString("Command.setMessage");
                if ((args.length == optIndex + 1) && Objects.equals(args[optIndex], actionsOpt.get(1))) {
                    XPManager.setLevel(targetID, set);
                    message = message.formatted(target.getName(), set, (set == 1 || set == -1) ? "level" : "levels");

                } else {
                    XPManager.setXP(targetID, set);
                    message = message.formatted(target.getName(), set, (set == 1 || set == -1) ? "exp point" : "exp points");
                }
                sender.sendMessage(message);
                return true;
            }
        }
        return false; //todo: you do not have permission
    }

    public int testCommand(@NotNull String[] args) {  // todo: rewrite this
        if (args.length == 0) return -1;
        if (!actions.contains(args[0].toLowerCase())) return 2;
        if (args[0].equals(actions.get(3))) return 11;
        if (args.length > 1 && Bukkit.getPlayerExact(args[1]) == null) return 3;
        int optIndex = (args[0].equals(actions.get(0))) ? 2 : 3;
        if (args.length < optIndex) return 1;
        if (args.length > 2 && optIndex == 3 && (args[optIndex - 1].matches("-?(0|[1-9]\\d*)") && //this haunts me in my sleep
                ((args[optIndex - 1].length() < (args[optIndex-1].startsWith("-") ? 12 : 11)) && (Long.parseLong(args[optIndex - 1]) > Integer.MAX_VALUE || Long.parseLong(args[optIndex - 1]) < Integer.MIN_VALUE))))
            return 5;
        if (args.length < optIndex + 1) return 10;
        if (!actionsOpt.contains(args[optIndex].toLowerCase()))
            return 4; //todo: fails on extra

        return 0;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        switch (testCommand(args)) {
            case 0, 10, 4 -> {
            }
            case 2 -> {
                if (args.length > 1) return suggestions;
            }
            case 3 -> {
                if (args.length == 2) return null;
            }
            default -> {
//                System.out.println(testCommand(args));
                return suggestions;
            }
        }

        if (args.length == 1) {
            for (String str : actions) {
                if (str.contains(args[0])) {
                    suggestions.add(str);
                }
            }
            if (suggestions.isEmpty()) return actions;
        } else {
            int optIndex = (args[0].equals(actions.get(0))) ? 2 : 3;
            if (args.length == optIndex + 1) {
                for (String str : actionsOpt) {
                    if (str.contains(args[optIndex])) {
                        suggestions.add(str);
                    }
                }
                if (suggestions.isEmpty()) return actionsOpt;
            }
        }
        return suggestions;
    }
}

