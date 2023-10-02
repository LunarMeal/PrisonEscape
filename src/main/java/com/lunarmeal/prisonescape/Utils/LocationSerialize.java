package com.lunarmeal.prisonescape.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class LocationSerialize {
    public static String LocToStr(Location loc){
        return Objects.requireNonNull(loc.getWorld()).getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
    }
    public static Location StrToLoc(String strLoc){
        String[] parts = strLoc.split(":");

        if (parts.length != 6) {
            return null; // 格式不正确，返回 null 或者抛出异常
        }

        String worldName = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);

        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return null; // 指定的世界不存在，返回 null 或者抛出异常
        }

        return new Location(world, x, y, z, yaw, pitch);
    }
}
