package com.lunarmeal.prisonescape;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class PrisonTask extends BukkitRunnable {
    protected Player timerPlayer;
    protected ClaimedResidence residence;
    protected BossBar bossBar;
    protected PrisonData pData;
    protected ItemStack[] savedInventory;
    protected int countdown;
    protected int maxCountdown;
    public void cancel(){
        bossBar.removeAll();
        timerPlayer.getInventory().setContents(savedInventory);
        super.cancel();
    }
    public int getCountdown(){ return countdown;}
    public int getMaxCountdown(){ return maxCountdown;}

    public ItemStack[] getSavedInventory() {
        return savedInventory;
    }
}
