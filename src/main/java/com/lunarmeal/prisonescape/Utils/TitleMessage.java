package com.lunarmeal.prisonescape.Utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class TitleMessage {

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        // 清除现有的标题
        player.resetTitle();

        // 设置标题内容、渐显时间、停留时间和渐隐时间（以tick为单位）
        player.sendTitle(ChatColor.translateAlternateColorCodes('&', title), ChatColor.translateAlternateColorCodes('&', subtitle), fadeIn, stay, fadeOut);
    }
}

