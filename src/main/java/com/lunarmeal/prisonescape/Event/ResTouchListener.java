package com.lunarmeal.prisonescape.Event;

import com.bekvon.bukkit.residence.economy.ResidenceBank;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.lunarmeal.prisonescape.PrisonData;
import com.lunarmeal.prisonescape.PrisonEscape;
import com.lunarmeal.prisonescape.PrisonTask;
import com.lunarmeal.prisonescape.Utils.PlaceholderFormat;
import com.lunarmeal.prisonescape.Utils.SchemManager;
import com.lunarmeal.prisonescape.Utils.TitleMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;

import static com.bekvon.bukkit.residence.api.ResidenceApi.getResidenceManager;

public class ResTouchListener implements Listener {
    PrisonEscape plugin;

    public ResTouchListener() {
        this.plugin = PrisonEscape.getInstance();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // 在这里检查玩家的位置是否在领地内
        // 你需要编写逻辑来判断玩家是否进入或离开领地

        // 获取玩家位置
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        ClaimedResidence res = getResidenceManager().getByLoc(loc);
        if(!plugin.prisonerList.containsKey(player))
            return;
        // 获取领地信息并判断玩家是否在目标领地内
        if (!isPlayerInResidence(res)) {
            // 玩家进入领地
            // 处理进入领地的逻辑
            TitleMessage.sendTitle(player,plugin.prisonConfig.message.get("SuccessChallengeTitle"),"",20,60,20);
            PrisonData prisonData = plugin.prisonerList.get(player);
            int timeScore = 0;
            if(prisonData.getTaskList().containsKey(player)){
                PrisonTask task = prisonData.getTaskList().get(player);
                timeScore = task.getMaxCountdown() - task.getCountdown();
                task.cancel();
                prisonData.getTaskList().remove(player);
            }
            prisonData.setFree(true);
            plugin.prisonerList.remove(player,prisonData);
            ClaimedResidence myRes = getResidenceManager().getByName(prisonData.getResName());
            if(plugin.prisonTempList.containsKey(prisonData.getPrisonName())){
                //典狱长挑战完成
                myRes.getPermissions().setOwner(player,true);
                for(Map.Entry<String, Boolean> entry:prisonData.getResFlags().entrySet())
                    myRes.getPermissions().setFlag(entry.getKey(), entry.getValue() ? FlagPermissions.FlagState.TRUE : FlagPermissions.FlagState.FALSE);
                myRes.getPermissions().setFlag("bank", FlagPermissions.FlagState.TRUE);
                prisonData.getRankingList().clear();
                //从临时列表里移除
                plugin.prisonTempList.remove(prisonData.getPrisonName());
                //添加到实际列表
                plugin.prisonDataList.put(prisonData.getPrisonName(),prisonData);
                CuboidArea mainArea = myRes.getMainArea();
                prisonData.getRankingList().put(player.getName(),timeScore);
                if(prisonData.getPrisonID()!=-1) {
                    plugin.databaseManager.updateDataWithPname(prisonData);
                    //AreaDataHandler.saveAreaData("plugins/PrisonEscape/schems/prison_"+prisonData.getPrisonID()+".pdata",mainArea.getLowLocation(),mainArea.getHighLocation());
                    SchemManager.saveSchematic("plugins/PrisonEscape/schems/prison_"+prisonData.getPrisonID()+".schem",mainArea.getLowLocation(),mainArea.getHighLocation());
                }
                else {
                    int prisonID = plugin.databaseManager.insertData(prisonData);
                    prisonData.setPrisonID(prisonID);
                    SchemManager.saveSchematic("plugins/PrisonEscape/schems/prison_"+prisonID+".schem",mainArea.getLowLocation(),mainArea.getHighLocation());
                }
                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("SuccessCreateMsg"));
                component.setColor(ChatColor.GREEN); // 设置文本颜色
                player.spigot().sendMessage(component);
            }else{
                //玩家挑战完成
                if(plugin.economyManager.hasEconomy()) {
                    plugin.economyManager.setMoney(player, prisonData.getCounter());
                    ResidenceBank bank = myRes.getBank();
                    bank.setStoredMoney(bank.getStoredMoneyD()-prisonData.getCounter());
                }
                prisonData.getRankingList().put(player.getName(),timeScore);
                String tip = new PlaceholderFormat(plugin.prisonConfig.message.get("SuccessChallengeMsg")).format(prisonData.getCounter());
                TextComponent component = new TextComponent(tip);
                component.setColor(ChatColor.GREEN); // 设置文本颜色
                player.spigot().sendMessage(component);
            }
            plugin.prisonerList.remove(player);
        }
    }

    // 编写逻辑来判断玩家是否在领地内
    private boolean isPlayerInResidence(ClaimedResidence res) {
        // 在这里编写逻辑来检查玩家是否在领地内
        // 可以使用 Residence 插件的 API 或其他方法来进行判断
        // 返回 true 表示玩家在领地内，返回 false 表示玩家不在领地内
        if(res == null)
            return false;
        for(PrisonData i: plugin.prisonerList.values()){
            if(i.getResName().equals(res.getResidenceName())){
                return true;
            }
        }
        return false;
    }
}
