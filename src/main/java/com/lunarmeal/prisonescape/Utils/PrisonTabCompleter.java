package com.lunarmeal.prisonescape.Utils;

import com.lunarmeal.prisonescape.PrisonData;
import com.lunarmeal.prisonescape.PrisonEscape;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PrisonTabCompleter implements TabCompleter {
    PrisonEscape plugin;
    public PrisonTabCompleter(){
        this.plugin = PrisonEscape.getInstance();
    }
    @Override
    public List<String> onTabComplete(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String alias, @NonNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if(commandSender instanceof ConsoleCommandSender)
            return null;
        // 在这里根据参数前缀生成自动补全的建议
        // 例如，你可以根据玩家输入的前缀来从数据库或配置文件中获取建议
        Player player = (Player) commandSender;
        if (args.length == 1) {
            suggestions.add("create");
            suggestions.add("setspawn");
            suggestions.add("edit");
            suggestions.add("remove");
            suggestions.add("list");
            suggestions.add("challenge");
            suggestions.add("quit");
            suggestions.add("rule");
            suggestions.add("confirm");
        }
        if(args.length == 2 && "challenge".startsWith(args[0])){
            for(PrisonData i: plugin.prisonDataList.values()){
                suggestions.add(i.getPrisonName());
            }
        } else if (args.length == 2 && ("remove".startsWith(args[0]) || "edit".startsWith(args[0]))){
            for(PrisonData i: plugin.prisonDataList.values()){
                if(i.getPrisonOwner().equals(player.getName()))
                    suggestions.add(i.getPrisonName());
            }
        } else if (args.length == 2 && "setspawn".startsWith(args[0])) {
            for(PrisonData i: plugin.prisonDataList.values())
                if(i.getPrisonOwner()!=null)
                    if(i.getPrisonOwner().equals(player.getName()))
                        suggestions.add(String.valueOf(i.getEscapeTime()));
            for(PrisonData i: plugin.prisonTempList.values())
                if(i.getPrisonOwner()!=null)
                    if(i.getPrisonOwner().equals(player.getName()))
                        suggestions.add(String.valueOf(i.getEscapeTime()));
        }

        return suggestions;
    }
}
