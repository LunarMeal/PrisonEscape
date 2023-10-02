package com.lunarmeal.prisonescape.Utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Economy.CMIEconomyAcount;
import com.Zrips.CMI.Modules.Economy.EconomyManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import static org.bukkit.Bukkit.getServer;

public class EcoManager {
    private final Plugin CMIPlugin;
    private EconomyManager economyManager;

    public EcoManager() {
        PluginManager pluginManager = getServer().getPluginManager();
        this.CMIPlugin = pluginManager.getPlugin("CMI");
        if(CMIPlugin != null){
            CMI cmi = (CMI)CMIPlugin;
            economyManager = cmi.getEconomyManager();
        }
    }

    public void setMoney(Player player, double money){
        CMIEconomyAcount acount = CMIUser.getUser(player).getEconomyAccount();
        acount.setBalance(acount.getBalance()+money);
    }
    public double getMoney(Player player){
        CMIEconomyAcount acount = CMIUser.getUser(player).getEconomyAccount();
        return acount.getBalance();
    }
    public boolean hasEconomy(){
        if(CMIPlugin == null)
            return false;
        if(!CMIPlugin.isEnabled())
            return false;
        if(economyManager == null)
            return false;
        if(!economyManager.isEnabled())
            return false;
        return true;
    }
}
