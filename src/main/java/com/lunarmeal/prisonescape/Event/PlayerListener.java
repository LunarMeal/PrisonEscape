package com.lunarmeal.prisonescape.Event;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.lunarmeal.prisonescape.PrisonData;
import com.lunarmeal.prisonescape.PrisonEscape;
import com.lunarmeal.prisonescape.PrisonTask;
import com.lunarmeal.prisonescape.Utils.PlaceholderFormat;
import com.lunarmeal.prisonescape.Utils.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

import static com.bekvon.bukkit.residence.api.ResidenceApi.getResidenceManager;

public class PlayerListener implements Listener {
    PrisonEscape plugin;
    public PlayerListener(){
        this.plugin = PrisonEscape.getInstance();
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 获取退出游戏的玩家
        Player player = event.getPlayer();
        player.closeInventory();
        if(plugin.editorList.containsKey(player))
            plugin.editorList.remove(player);
        if(plugin.waitingForChange!=null)
            if(plugin.waitingForChange.equals(player))
                plugin.waitingForChange = null;
        // 在这里处理玩家退出游戏的逻辑
        // 例如，保存玩家数据或执行其他操作
        if(plugin.prisonerList.containsKey(player)) {
            //1.杀死定时器
            PrisonData prisonData = plugin.prisonerList.get(player);
            if(prisonData.getTaskList().containsKey(player)){
                PrisonTask task = prisonData.getTaskList().get(player);
                task.cancel();
                prisonData.getTaskList().remove(player);
            }
            prisonData.setFree(true);
            //2.移除游戏进行列表
            plugin.prisonerList.remove(player);
            //3.典狱长挑战，则恢复原样
            for(PrisonData i: plugin.prisonTempList.values()){
                //临时挑战
                ClaimedResidence res = getResidenceManager().getByName(i.getResName());
                if(player.getName().equals(i.getPrisonOwner())){
                    res.getPermissions().setOwner(player,true);
                    for(Map.Entry<String, Boolean> entry:prisonData.getResFlags().entrySet())
                        res.getPermissions().setFlag(entry.getKey(), entry.getValue() ? FlagPermissions.FlagState.TRUE : FlagPermissions.FlagState.FALSE);
                    plugin.prisonTempList.remove(prisonData.getPrisonName());

                    //数据库处理
                    if(prisonData.getPrisonID()!=-1){
                        PrisonData orgPrisonData = plugin.databaseManager.retrieveData(prisonData.getPrisonName());
                        plugin.prisonDataList.put(orgPrisonData.getPrisonName(),orgPrisonData);
                    }
                }
            }
            //非典狱长挑战，执行失败逻辑
            for(PrisonData i: plugin.prisonDataList.values()){
                //实际挑战
                ClaimedResidence res = getResidenceManager().getByName(i.getResName());
                if(i.equals(prisonData)){
                    Location spawnLoc = Bukkit.getWorld("world").getSpawnLocation();
                    // 使用玩家的teleport方法将其传送到出生点
                    player.teleport(spawnLoc);
                }
            }
        }else{
            //不挑战，则检测是否为典狱长
            for(PrisonData i: plugin.prisonTempList.values()){
                ClaimedResidence res = getResidenceManager().getByName(i.getResName());
                if(player.getName().equals(res.getOwner()))
                    plugin.prisonTempList.remove(i.getPrisonName());
            }
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // 获取死亡的玩家
        Player player = event.getEntity();
        if(plugin.editorList.containsKey(player))
            plugin.editorList.remove(player);
        if(plugin.waitingForChange!=null)
            if(plugin.waitingForChange.equals(player))
                plugin.waitingForChange = null;
        // 在这里处理玩家死亡事件的逻辑
        // 例如，你可以给予玩家一些特定的处理，或记录死亡事件的信息
        if(plugin.prisonerList.containsKey(player)) {
            //1.杀死定时器
            PrisonData prisonData = plugin.prisonerList.get(player);
            if(prisonData.getTaskList().containsKey(player)){
                PrisonTask task = prisonData.getTaskList().get(player);
                task.cancel();
                prisonData.getTaskList().remove(player);
            }
            prisonData.setFree(true);
            //2.移除游戏进行列表
            plugin.prisonerList.remove(player);
            //3.典狱长挑战，则恢复原样
            for(PrisonData i: plugin.prisonTempList.values()){
                //临时挑战
                ClaimedResidence res = getResidenceManager().getByName(i.getResName());
                if(player.getName().equals(i.getPrisonOwner())){
                    res.getPermissions().setOwner(player,true);
                    for(Map.Entry<String, Boolean> entry:prisonData.getResFlags().entrySet())
                        res.getPermissions().setFlag(entry.getKey(), entry.getValue() ? FlagPermissions.FlagState.TRUE : FlagPermissions.FlagState.FALSE);
                    plugin.prisonTempList.remove(prisonData.getPrisonName());
                    TextComponent component = new TextComponent(plugin.prisonConfig.message.get("FailCreateDeathMsg"));
                    component.setColor(ChatColor.RED); // 设置文本颜色
                    player.spigot().sendMessage(component);

                    //数据库处理
                    if(prisonData.getPrisonID()!=-1){
                        plugin.prisonTempList.remove(prisonData.getPrisonName());
                        PrisonData orgPrisonData = plugin.databaseManager.retrieveData(prisonData.getPrisonName());
                        plugin.prisonDataList.put(orgPrisonData.getPrisonName(),orgPrisonData);
                    }
                }
            }
            //非典狱长挑战，执行失败逻辑
            for(PrisonData i: plugin.prisonDataList.values()){
                //实际挑战
                ClaimedResidence res = getResidenceManager().getByName(i.getResName());
                if(i.equals(prisonData)){
                    //非典狱长挑战，执行失败逻辑
                    TextComponent component = new TextComponent(plugin.prisonConfig.message.get("FailChallengeDeathMsg"));
                    component.setColor(ChatColor.RED); // 设置文本颜色
                    player.spigot().sendMessage(component);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase(); // 将指令转换为小写
        if(plugin.editorList.containsKey(player))
            plugin.editorList.remove(player);
        if(plugin.waitingForChange!=null)
            if(plugin.waitingForChange.equals(player))
                plugin.waitingForChange = null;

        if (plugin.prisonerList.containsKey(player)) {
            if(!command.startsWith("/prisonescape") && !command.startsWith("/pecp") && !command.startsWith("/psecp")) {
                // 如果正在拦截指令，取消事件并存储指令
                event.setCancelled(true);
                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("CantCommandChallengingMsg"));
                component.setColor(ChatColor.RED); // 设置文本颜色
                player.spigot().sendMessage(component);
            }
        }
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        // 检查是否有玩家在等待输入
        if(plugin.waitingForChange != null)
            if (plugin.waitingForChange.equals(player)) {
                String input = event.getMessage(); // 获取玩家在聊天中输入的消息
                if(plugin.editorList.containsKey(player)) {
                    // 在这里处理输入的内容，例如执行相应的操作
                    PrisonData prisonData = plugin.editorList.get(player);
                    switch (prisonData.getEditEnum()){
                        case PRISONNAME: {
                            if(!StringUtil.isSpecialChar(input)) {
                                plugin.prisonDataList.remove(prisonData.getPrisonName());
                                prisonData.setPrisonName(input);
                                plugin.prisonDataList.put(input, prisonData);
                                if (prisonData.getPrisonID() != -1)
                                    plugin.databaseManager.updateDataWithRname(prisonData);
                                String newPrisonName = new PlaceholderFormat(plugin.prisonConfig.message.get("NewPrisonNameCompleteMsg")).format(input);
                                TextComponent component = new TextComponent(newPrisonName);
                                component.setColor(ChatColor.GREEN); // 设置文本颜色
                                player.spigot().sendMessage(component);
                            }else{
                                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("IllegalNewPrisonNameMsg"));
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                player.spigot().sendMessage(component);
                            }
                            break;
                        }
                        case COUNTER:{
                            if(StringUtil.isNumeric(input)){
                                float counter = Float.parseFloat(input);
                                if(counter < plugin.counter){
                                    String tip = new PlaceholderFormat(plugin.prisonConfig.message.get("NewCounterAtLeastMsg")).format(plugin.counter);
                                    TextComponent component = new TextComponent(tip);
                                    component.setColor(ChatColor.RED); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                }else {
                                    prisonData.setCounter(counter);
                                    if (prisonData.getPrisonID() != -1)
                                        plugin.databaseManager.updateDataWithRname(prisonData);
                                    String tip = new PlaceholderFormat(plugin.prisonConfig.message.get("NewCounterCompleteMsg")).format(input);
                                    TextComponent component = new TextComponent(tip);
                                    component.setColor(ChatColor.GREEN); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                }
                            }
                            break;
                        }
                        case ESCAPETIME:{
                            int escapeTime = Integer.parseInt(input);
                            if(escapeTime < plugin.escapeTime){
                                String tip = new PlaceholderFormat(plugin.prisonConfig.message.get("NewEscapeTimeAtLeastMsg")).format(plugin.escapeTime);
                                TextComponent component = new TextComponent(tip);
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                player.spigot().sendMessage(component);
                            }else {
                                if (StringUtil.isNumeric(input)) {
                                    prisonData.setEscapeTime(Integer.parseInt(input));
                                    if (prisonData.getPrisonID() != -1)
                                        plugin.databaseManager.updateDataWithRname(prisonData);
                                    String tip = new PlaceholderFormat(plugin.prisonConfig.message.get("NewEscapeTimeCompleteMsg")).format(input);
                                    TextComponent component = new TextComponent(tip);
                                    component.setColor(ChatColor.GREEN); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                }
                            }
                            break;
                        }
                        case PLAYERNUM:{
                            int playerNum = Integer.parseInt(input);
                            if(playerNum > plugin.playerNum){
                                String tip = new PlaceholderFormat(plugin.prisonConfig.message.get("NewPlayerNumAtMostMsg")).format(plugin.playerNum);
                                TextComponent component = new TextComponent(tip);
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                player.spigot().sendMessage(component);
                            } else if (playerNum < plugin.playerNum) {
                                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("NewPlayerNumAtLeastMsg"));
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                player.spigot().sendMessage(component);
                            } else{
                                if (StringUtil.isNumeric(input)) {
                                    prisonData.setEscapeTime(Integer.parseInt(input));
                                    if (prisonData.getPrisonID() != -1)
                                        plugin.databaseManager.updateDataWithRname(prisonData);
                                    String tip = new PlaceholderFormat(plugin.prisonConfig.message.get("NewPlayerNumCompleteMsg")).format(input);
                                    TextComponent component = new TextComponent(tip);
                                    component.setColor(ChatColor.GREEN); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                }
                            }
                            break;
                        }
                    }
                    // 清除等待输入状态
                    plugin.waitingForChange = null;
                    // 取消事件，防止消息显示在聊天栏中
                    event.setCancelled(true);
                }
            }
    }
}
