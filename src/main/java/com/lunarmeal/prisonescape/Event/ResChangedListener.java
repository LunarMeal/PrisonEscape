package com.lunarmeal.prisonescape.Event;

import com.bekvon.bukkit.residence.event.*;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.lunarmeal.prisonescape.PrisonData;
import com.lunarmeal.prisonescape.PrisonEscape;
import com.lunarmeal.prisonescape.PrisonTask;
import com.lunarmeal.prisonescape.Utils.SchemManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResChangedListener implements Listener {
    PrisonEscape plugin;
    public ResChangedListener() {
        this.plugin = PrisonEscape.getInstance();
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onResidenceOwnerChange(ResidenceOwnerChangeEvent event) {
        ClaimedResidence res = event.getResidence();
        String newOwner = event.getNewOwner();
        for(PrisonData i : plugin.prisonTempList.values()){
            if(i.getResName().equals(res.getResidenceName()))
                if(!i.isFree())
                    return;
        }
        Map<Player, PrisonData> map = new ConcurrentHashMap<>();
        map.putAll(plugin.prisonerList);
        for(Map.Entry<Player, PrisonData> entry:map.entrySet()){
            Player prisonPlayer = entry.getKey();
            PrisonData prisonData = entry.getValue();
            if(prisonData.getResName().equals(res.getResidenceName())){
                //1.杀死定时器
                if(prisonData.getTaskList().containsKey(prisonPlayer)){
                    PrisonTask task = prisonData.getTaskList().get(prisonPlayer);
                    task.cancel();
                    prisonData.getTaskList().remove(prisonPlayer);
                }
                //2.移除游戏进行列表
                plugin.prisonerList.remove(prisonPlayer);
                //3.提醒玩家领地被删除
                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("ResChangeOwnerMsg"));
                component.setColor(ChatColor.RED); // 设置文本颜色
                prisonPlayer.spigot().sendMessage(component);
            }
        }
        map.clear();
        map.putAll(plugin.editorList);
        for(Map.Entry<Player, PrisonData> entry:map.entrySet()){
            Player editor = entry.getKey();
            PrisonData prisonData = entry.getValue();
            if(prisonData.getResName().equals(res.getResidenceName()))
                plugin.editorList.remove(editor);
        }
        for(PrisonData i : plugin.prisonDataList.values()){
            if(i.getResName().equals(res.getResidenceName()))
                if(i.isFree())
                    i.setPrisonOwner(newOwner);
        }
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onResidenceSubzoneCreate(ResidenceSubzoneCreationEvent event) {
        ClaimedResidence res = event.getResidence();
        Player player = event.getPlayer();
        if(player == null)
            return;
        for(PrisonData i : plugin.prisonDataList.values()){
            if(i.getResName().equals(res.getResidenceName())){
                event.setCancelled(true);
                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("ResCreateSubMsg"));
                component.setColor(ChatColor.RED); // 设置文本颜色
                player.spigot().sendMessage(component);
            }
        }
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onResidenceFlagChange(ResidenceFlagChangeEvent event) {
        ClaimedResidence res = event.getResidence();
        Player player = event.getPlayer();
        if(player == null)
            return;
        for(PrisonData i : plugin.prisonDataList.values()){
            if(i.getResName().equals(res.getResidenceName())){
                event.setCancelled(true);
                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("ResChangeFlagMsg"));
                component.setColor(ChatColor.RED); // 设置文本颜色
                player.spigot().sendMessage(component);
            }
        }
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onResidenceDelete(ResidenceDeleteEvent event) {
        ClaimedResidence res = event.getResidence();
        Player player = event.getPlayer();
        Map<Player, PrisonData> map = new ConcurrentHashMap<>();
        map.putAll(plugin.prisonerList);
        for(Map.Entry<Player, PrisonData> entry:map.entrySet()){
            Player prisonPlayer = entry.getKey();
            PrisonData prisonData = entry.getValue();
            if(prisonData.getResName().equals(res.getResidenceName())){
                //1.杀死定时器
                if(prisonData.getTaskList().containsKey(prisonPlayer)){
                    PrisonTask task = prisonData.getTaskList().get(prisonPlayer);
                    task.cancel();
                    prisonData.getTaskList().remove(prisonPlayer);
                }
                //2.移除游戏进行列表
                plugin.prisonerList.remove(prisonPlayer);
                //3.提醒玩家领地被删除
                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("ResRemoveNoticeMsg"));
                component.setColor(ChatColor.RED); // 设置文本颜色
                prisonPlayer.spigot().sendMessage(component);
            }
        }
        map.clear();
        map.putAll(plugin.editorList);
        for(Map.Entry<Player, PrisonData> entry:map.entrySet()){
            Player editor = entry.getKey();
            PrisonData prisonData = entry.getValue();
            if(prisonData.getResName().equals(res.getResidenceName()))
                plugin.editorList.remove(editor);
        }
        Map<String, PrisonData> map1 = new ConcurrentHashMap<>();
        map1.putAll(plugin.prisonTempList);
        for(PrisonData i : map1.values()){
            if(i.getResName().equals(res.getResidenceName())){
                if(player != null) {
                    TextComponent component = new TextComponent(plugin.prisonConfig.message.get("ResRemoveTipMsg"));
                    component.setColor(ChatColor.RED); // 设置文本颜色
                    player.spigot().sendMessage(component);
                }
                if(i.getPrisonID()!=-1) {
                    plugin.databaseManager.deleteData(i.getPrisonName());
                    SchemManager.deleteSchematic("plugins/PrisonEscape/schems/prison_"+i.getPrisonID()+".schem");
                }
                plugin.prisonTempList.remove(i.getPrisonName());
                i = null;
            }
        }
        map1.clear();
        map1.putAll(plugin.prisonDataList);
        for(PrisonData i : map1.values()){
            if(i.getResName().equals(res.getResidenceName())){
                if(player != null) {
                    TextComponent component = new TextComponent(plugin.prisonConfig.message.get("ResRemoveTipMsg"));
                    component.setColor(ChatColor.RED); // 设置文本颜色
                    player.spigot().sendMessage(component);
                }
                plugin.databaseManager.deleteData(i.getPrisonName());
                SchemManager.deleteSchematic("plugins/PrisonEscape/schems/prison_"+i.getPrisonID()+".schem");
                plugin.prisonDataList.remove(i.getPrisonName());
                i = null;
            }
        }
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onResidenceSizeChange(ResidenceSizeChangeEvent event){
        ClaimedResidence res = event.getResidence();
        Player player = event.getPlayer();
        if(player == null)
            return;
        for(PrisonData i : plugin.prisonDataList.values()){
            if(i.getResName().equals(res.getResidenceName())){
                event.setCancelled(true);
                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("ResChangeSizeMsg"));
                component.setColor(ChatColor.RED); // 设置文本颜色
                player.spigot().sendMessage(component);
            }
        }
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onResidenceRename(ResidenceRenameEvent event){
        String newResName = event.getNewResidenceName();
        for(PrisonData i : plugin.prisonTempList.values()){
            if(i.getResName().equals(event.getOldResidenceName())){
                i.setResName(newResName);
                if(i.getPrisonID()!=-1)
                    plugin.databaseManager.updateDataWithPname(i);
            }
        }
        for(PrisonData i : plugin.prisonDataList.values()){
            if(i.getResName().equals(event.getOldResidenceName())){
                i.setResName(newResName);
                if(i.getPrisonID()!=-1)
                    plugin.databaseManager.updateDataWithPname(i);
            }
        }
    }
}
