package com.lunarmeal.prisonescape;

import com.bekvon.bukkit.residence.economy.ResidenceBank;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.lunarmeal.prisonescape.Utils.PlaceholderFormat;
import com.lunarmeal.prisonescape.Utils.SchemManager;
import com.lunarmeal.prisonescape.Utils.StringUtil;
import com.lunarmeal.prisonescape.Utils.TitleMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static com.bekvon.bukkit.residence.api.ResidenceApi.getResidenceManager;


public class PrisonEscapeCommandListener implements CommandExecutor {

    PrisonEscape plugin;
    Player player ;
    String playerName;
    Location loc;
    TextComponent component;
    ClaimedResidence res;

    public PrisonEscapeCommandListener() {
        this.plugin = PrisonEscape.getInstance();
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if(commandSender instanceof Player){
            player = (Player) commandSender;
            playerName = player.getName();
            loc = player.getLocation();
            component = new TextComponent(plugin.prisonConfig.message.get("usage"));
            res = getResidenceManager().getByLoc(loc);
            if(args.length == 0) {
                component.setColor(ChatColor.GREEN); // 设置文本颜色
                player.spigot().sendMessage(component);
            }else{
                String arg1 = args[0];
                switch (arg1){
                    case "create":{
                        if(plugin.prisonerList.containsKey(player)){
                            component.setText(plugin.prisonConfig.message.get("plzCompleteMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        if(plugin.worldBlackList.contains(player.getWorld())){
                            component.setText(plugin.prisonConfig.message.get("worldBlacklistMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        if(res==null){
                            component.setText(plugin.prisonConfig.message.get("MustResCreateMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        UUID ownerUUID = res.getOwnerUUID();
                        if(ownerUUID==null){
                            component.setText(plugin.prisonConfig.message.get("ErrorMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        if(!ownerUUID.equals(player.getUniqueId())){
                            component.setText(plugin.prisonConfig.message.get("OnlyResOwnerCreateMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        String prisonName = res.getResidenceName();
                        if(args.length > 1)
                            prisonName = args[1];

                        PrisonData prisonData = new PrisonData(prisonName,res.getResidenceName(),null,res.getPermissions().getFlags());
                        prisonData.setCounter(plugin.counter);

                        //同一个领地不能有多个监狱
                        for(PrisonData i: plugin.prisonTempList.values()){
                            if(i.getResName().equals(res.getResidenceName())){
                                component.setText(plugin.prisonConfig.message.get("ResHasPrisonMsg"));
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                player.spigot().sendMessage(component);
                                return true;
                            }
                        }
                        for(PrisonData i: plugin.prisonDataList.values()){
                            if(i.getResName().equals(res.getResidenceName())){
                                component.setText(plugin.prisonConfig.message.get("ResHasPrisonMsg"));
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                player.spigot().sendMessage(component);
                                return true;
                            }
                        }
                        if(plugin.prisonDataList.containsKey(prisonName)){
                            component.setText(plugin.prisonConfig.message.get("ResNameOnlyMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        plugin.prisonTempList.put(prisonName,prisonData);
                        component.setText(plugin.prisonConfig.message.get("SetSpawnTipMsg"));
                        component.setColor(ChatColor.GREEN); // 设置文本颜色
                        player.spigot().sendMessage(component);
                        break;
                    }
                    case "setspawn":{
                        if(plugin.prisonerList.containsKey(player)){
                            component.setText(plugin.prisonConfig.message.get("plzCompleteMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        if(res==null){
                            component.setText(plugin.prisonConfig.message.get("SetSpawnInResMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        UUID ownerUUID = res.getOwnerUUID();
                        if(ownerUUID==null){
                            component.setText(plugin.prisonConfig.message.get("ErrorMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        if(!ownerUUID.equals(player.getUniqueId())){
                            component.setText(plugin.prisonConfig.message.get("MustOwnerSetSpawnMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        if(Bukkit.getOperators().isEmpty()){
                            component.setText(plugin.prisonConfig.message.get("NotHaveOPMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        boolean hasResult = false;
                        for(PrisonData i: plugin.prisonTempList.values()){
                            if(i.getResName().equals(res.getResidenceName())){
                                int escapeTime;
                                if(args.length > 1)
                                    if(StringUtil.isNumeric(args[1])) {
                                        escapeTime = Integer.parseInt(args[1]);
                                        if(escapeTime < plugin.escapeTime){
                                            String tip = new PlaceholderFormat(plugin.prisonConfig.message.get("NewEscapeTimeAtLeastMsg")).format(plugin.escapeTime);
                                            component = new TextComponent(tip);
                                            component.setColor(ChatColor.RED); // 设置文本颜色
                                            player.spigot().sendMessage(component);
                                            return true;
                                        }
                                        i.setEscapeTime(escapeTime);
                                    }
                                String msg = new PlaceholderFormat(plugin.prisonConfig.message.get("OwnerChallengeMsg")).format(i.getEscapeTime());
                                component.setText(msg);
                                component.setColor(ChatColor.GREEN); // 设置文本颜色
                                player.spigot().sendMessage(component);
                                TitleMessage.sendTitle(player,plugin.prisonConfig.message.get("BeginChallengeTitle"),"",20,60,20);

                                PrisonTask task = changeResOrder(i);
                                i.addTaskToList(player,task);
                                hasResult = true;
                            }
                        }
                        Map<String, PrisonData> map = new ConcurrentHashMap<>();
                        map.putAll(plugin.prisonDataList);
                        for(PrisonData i: map.values()){
                            if(i.getResName().equals(res.getResidenceName())){
                                if(plugin.prisonerList.containsValue(i)){
                                    component.setText(plugin.prisonConfig.message.get("CantSetSpawnChallengingMsg"));
                                    component.setColor(ChatColor.RED); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                    return true;
                                }
                                int escapeTime;
                                if(args.length > 1)
                                    if(StringUtil.isNumeric(args[1])) {
                                        escapeTime = Integer.parseInt(args[1]);
                                        if(escapeTime < plugin.escapeTime){
                                            String tip = new PlaceholderFormat(plugin.prisonConfig.message.get("NewEscapeTimeAtLeastMsg")).format(plugin.escapeTime);
                                            component = new TextComponent(tip);
                                            component.setColor(ChatColor.RED); // 设置文本颜色
                                            player.spigot().sendMessage(component);
                                            return true;
                                        }
                                        i.setEscapeTime(escapeTime);
                                    }
                                String msg = new PlaceholderFormat(plugin.prisonConfig.message.get("OwnerChallengeResetMsg")).format(i.getEscapeTime());
                                component.setText(msg);
                                component.setColor(ChatColor.GREEN); // 设置文本颜色
                                player.spigot().sendMessage(component);
                                TitleMessage.sendTitle(player,plugin.prisonConfig.message.get("BeginChallengeTitle"),"",20,60,20);

                                //从实际列表中暂时移到临时列表
                                plugin.prisonTempList.put(i.getPrisonName(),i);
                                plugin.prisonDataList.remove(i.getPrisonName());
                                PrisonTask task = changeResOrder(i);
                                i.addTaskToList(player,task);
                                hasResult = true;
                            }
                        }
                        if(!hasResult){
                            component.setText(plugin.prisonConfig.message.get("MustCreateFirstMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                        }
                        break;
                    }
                    case "edit":{
                        if(plugin.prisonerList.containsKey(player)){
                            component.setText(plugin.prisonConfig.message.get("plzCompleteMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        if(res==null){
                            if(args.length > 1){
                                if(plugin.prisonDataList.containsKey(args[1])){
                                    PrisonData prisonData = plugin.prisonDataList.get(args[1]);
                                    res = getResidenceManager().getByName(prisonData.getResName());
                                }else{
                                    component.setText(plugin.prisonConfig.message.get("NotHavePrisonMsg"));
                                    component.setColor(ChatColor.RED); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                    return true;
                                }
                            }else {
                                component.setText(plugin.prisonConfig.message.get("MustResEditMsg"));
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                player.spigot().sendMessage(component);
                                return true;
                            }
                        }
                        UUID ownerUUID = res.getOwnerUUID();
                        if(ownerUUID==null){
                            component.setText(plugin.prisonConfig.message.get("ErrorMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        if(!ownerUUID.equals(player.getUniqueId())){
                            component.setText(plugin.prisonConfig.message.get("OnlyResOwnerEditMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        for(PrisonData i : plugin.prisonDataList.values()){
                            if(i.getResName().equals(res.getResidenceName())){
                                if(plugin.prisonerList.containsValue(i)){
                                    component.setText(plugin.prisonConfig.message.get("CantEditChallengingMsg"));
                                    component.setColor(ChatColor.RED); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                    return true;
                                }
                                plugin.editorList.put(player,i);
                                plugin.menuEventListener.openEditMenu(player);
                            }
                        }
                        break;
                    }
                    case "remove":{
                        if(plugin.prisonerList.containsKey(player)){
                            component.setText(plugin.prisonConfig.message.get("plzCompleteMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        if(res==null){
                            if(args.length > 1){
                                if(plugin.prisonDataList.containsKey(args[1])){
                                    PrisonData prisonData = plugin.prisonDataList.get(args[1]);
                                    res = getResidenceManager().getByName(prisonData.getResName());
                                }else{
                                    component.setText(plugin.prisonConfig.message.get("NotHavePrisonMsg"));
                                    component.setColor(ChatColor.RED); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                    return true;
                                }
                            }else {
                                component.setText(plugin.prisonConfig.message.get("RemoveInResMsg"));
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                player.spigot().sendMessage(component);
                                return true;
                            }
                        }
                        UUID ownerUUID = res.getOwnerUUID();
                        if(ownerUUID==null){
                            component.setText(plugin.prisonConfig.message.get("ErrorMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        if(!ownerUUID.equals(player.getUniqueId())){
                            component.setText(plugin.prisonConfig.message.get("MustOwnerRemoveMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
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
                                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("StopForRemovePrisonMsg"));
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                prisonPlayer.spigot().sendMessage(component);
                            }
                        }
                        Map<String, PrisonData> map1 = new ConcurrentHashMap<>();
                        map1.putAll(plugin.prisonTempList);
                        for(PrisonData i : map1.values()){
                            if(i.getResName().equals(res.getResidenceName())){
                                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("RemovePrisonOverMsg"));
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                player.spigot().sendMessage(component);
                                if(i.getPrisonID()!=-1)
                                    plugin.databaseManager.deleteData(i.getPrisonName());
                                plugin.prisonTempList.remove(i.getPrisonName());
                            }
                        }
                        map1.clear();
                        map1.putAll(plugin.prisonDataList);
                        for(PrisonData i : map1.values()){
                            if(i.getResName().equals(res.getResidenceName())){
                                TextComponent component = new TextComponent(plugin.prisonConfig.message.get("RemovePrisonOverMsg"));
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                player.spigot().sendMessage(component);
                                plugin.databaseManager.deleteData(i.getPrisonName());
                                plugin.prisonDataList.remove(i.getPrisonName());
                            }
                        }
                        break;
                    }
                    case "list": {
                        String title = plugin.prisonConfig.message.get("PrisonListTitle");
                        StringBuilder prisonListBuilder = new StringBuilder("========================\n")
                                .append(title).append("\n");
                        int num = 1;
                        for (PrisonData i : plugin.prisonDataList.values()) {
                            String row = new PlaceholderFormat(plugin.prisonConfig.message.get("ResListRow")).format(i.getResName());
                            prisonListBuilder.append(num).append(".").append(i.getPrisonName()).append(row).append("\n");
                            num++;
                        }
                        prisonListBuilder.append("========================");
                        component.setText(prisonListBuilder.toString());
                        component.setColor(ChatColor.AQUA);
                        player.spigot().sendMessage(component);
                        break;
                    }

                    case "challenge":{
                        if(plugin.prisonerList.containsKey(player)){
                            component.setText(plugin.prisonConfig.message.get("plzCompleteMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                            return true;
                        }
                        if(args.length > 1){
                            if(plugin.prisonDataList.containsKey(args[1])){
                                PrisonData prisonData = plugin.prisonDataList.get(args[1]);
                                res = getResidenceManager().getByName(prisonData.getResName());
                                UUID ownerUUID = res.getOwnerUUID();
                                if(ownerUUID==null){
                                    component.setText(plugin.prisonConfig.message.get("ErrorMsg"));
                                    component.setColor(ChatColor.RED); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                    return true;
                                }
                                if(ownerUUID.equals(player.getUniqueId())){
                                    component.setText(plugin.prisonConfig.message.get("OwnerCantChallengeMsg"));
                                    component.setColor(ChatColor.RED); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                    return true;
                                }
                                //统计游玩人数
                                int sum = 0;
                                for(PrisonData i:plugin.prisonerList.values()){
                                    if(i.equals(prisonData))
                                        sum++;
                                }
                                if(sum>= prisonData.getPlayerNum()){
                                    String msg = new PlaceholderFormat(plugin.prisonConfig.message.get("AtMostPlayersMsg")).format(plugin.playerNum);
                                    component.setText(msg);
                                    component.setColor(ChatColor.RED); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                    return true;
                                }
                                if(plugin.economyManager.hasEconomy()) {
                                    if(plugin.economyManager.getMoney(player) < prisonData.getCounter()){
                                        component.setText(plugin.prisonConfig.message.get("PlayerLackMoneyMsg"));
                                        component.setColor(ChatColor.RED); // 设置文本颜色
                                        player.spigot().sendMessage(component);
                                        return true;
                                    }
                                    if (res.getBank().getStoredMoneyD() < prisonData.getCounter() * prisonData.getPlayerNum()) {
                                        component.setText(plugin.prisonConfig.message.get("BankLackMoneyMsg"));
                                        component.setColor(ChatColor.RED); // 设置文本颜色
                                        player.spigot().sendMessage(component);
                                        return true;
                                    }
                                }
                                res.getPermissions().setPlayerFlag(playerName,"move", FlagPermissions.FlagState.TRUE);
                                res.getPermissions().setPlayerFlag(playerName,"tp", FlagPermissions.FlagState.TRUE);
                                player.teleport(prisonData.getPrisonSpawn());

                                String msg = new PlaceholderFormat(plugin.prisonConfig.message.get("PlayerChallengeMsg")).format(prisonData.getEscapeTime());
                                component.setText(msg);
                                component.setColor(ChatColor.GREEN); // 设置文本颜色
                                player.spigot().sendMessage(component);
                                TitleMessage.sendTitle(player,plugin.prisonConfig.message.get("BeginChallengeTitle"),"",20,60,20);

                                PrisonTask task = ChallengePrison(prisonData);
                                prisonData.addTaskToList(player,task);
                            }else{
                                component.setText(plugin.prisonConfig.message.get("NotHavePrisonMsg"));
                                component.setColor(ChatColor.RED); // 设置文本颜色
                                player.spigot().sendMessage(component);
                            }
                        }else{
                            plugin.menuEventListener.openChallengeMenu(player);
                            return true;
                        }
                        break;
                    }
                    case "quit":{
                        //离开当前监狱
                        if(plugin.prisonerList.containsKey(player)){
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
                            Map<String, PrisonData> map = new ConcurrentHashMap<>();
                            map.putAll(plugin.prisonTempList);
                            for(PrisonData i: map.values()){
                                //临时挑战
                                ClaimedResidence res = getResidenceManager().getByName(i.getResName());
                                if(player.getName().equals(i.getPrisonOwner())){
                                    res.getPermissions().setOwner(player,true);
                                    for(Map.Entry<String, Boolean> entry:prisonData.getResFlags().entrySet())
                                        res.getPermissions().setFlag(entry.getKey(), entry.getValue() ? FlagPermissions.FlagState.TRUE : FlagPermissions.FlagState.FALSE);
                                    plugin.prisonTempList.remove(prisonData.getPrisonName());
                                    TextComponent component = new TextComponent(plugin.prisonConfig.message.get("ExitChallengeMsg"));
                                    component.setColor(ChatColor.RED); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                }
                                if(i.equals(prisonData)){
                                    Location spawnLoc = Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation();
                                    // 使用玩家的teleport方法将其传送到出生点
                                    player.teleport(spawnLoc);
                                }
                            }
                            //非典狱长挑战，执行失败逻辑
                            for(PrisonData i: plugin.prisonDataList.values()){
                                //实际挑战
                                if(i.equals(prisonData)){
                                    //非典狱长挑战，执行失败逻辑
                                    TextComponent component = new TextComponent(plugin.prisonConfig.message.get("ExitChallengeMsg"));
                                    component.setColor(ChatColor.RED); // 设置文本颜色
                                    player.spigot().sendMessage(component);
                                    Location spawnLoc = Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation();
                                    player.teleport(spawnLoc);
                                }
                            }
                        }
                        else{
                            component.setText(plugin.prisonConfig.message.get("NotChallengingMsg"));
                            component.setColor(ChatColor.RED); // 设置文本颜色
                            player.spigot().sendMessage(component);
                        }
                        break;
                    }
                    case "rule":{
                        component.setText(plugin.prisonConfig.message.get("RuleMsg"));
                        component.setColor(ChatColor.YELLOW); // 设置文本颜色
                        player.spigot().sendMessage(component);
                        break;
                    }
                    default:{
                        component.setColor(ChatColor.GREEN); // 设置文本颜色
                        player.spigot().sendMessage(component);
                        return true;
                    }
                }
            }
        }
        if(commandSender instanceof ConsoleCommandSender){
            plugin.getLogger().log(Level.ALL,plugin.prisonConfig.message.get("NotConsoleCommandMsg"));
        }
        return true;
    }

    public PrisonTask changeResOrder(PrisonData prisonData){
        prisonData.setFree(false);
        prisonData.setPrisonSpawn(loc);
        prisonData.setResFlags(res.getPermissions().getFlags());
        prisonData.setPrisonOwner(player.getName());
        plugin.prisonerList.put(player,prisonData);

        // 切换领地主人
        Set<OfflinePlayer> operators = Bukkit.getOperators();
        // 给第一个管理员领地权限
        for (OfflinePlayer operator : operators) {
            if(operator.isOnline())
                res.getPermissions().setOwner(operator.getPlayer(),true);
            else{
                String operatorName = operator.getName();
                res.getPermissions().setOwner(operatorName,true);
            }
            break;
        }
        res.getPermissions().setPlayerFlag(playerName,"move", FlagPermissions.FlagState.TRUE);
        for(Map.Entry<String, Boolean> entry:prisonData.getResFlags().entrySet())
            res.getPermissions().setFlag(entry.getKey(), entry.getValue() ? FlagPermissions.FlagState.TRUE : FlagPermissions.FlagState.FALSE);
        res.getPermissions().setFlag("move",FlagPermissions.FlagState.FALSE);
        res.getPermissions().setFlag("tp",FlagPermissions.FlagState.FALSE);
        res.getPermissions().setFlag("pvp",FlagPermissions.FlagState.FALSE);
        //过一段时间后恢复权限
        class ResOwnerTask extends PrisonTask {
            public ResOwnerTask(Player timerPlayer, int count, PrisonData pData) {
                this.timerPlayer = timerPlayer;
                this.countdown = count;
                this.maxCountdown = count;
                this.pData = pData;
                this.savedInventory = timerPlayer.getInventory().getContents().clone();
                String msg = new PlaceholderFormat(plugin.prisonConfig.message.get("EscapeTimeTitle")).format(countdown);
                this.bossBar = Bukkit.createBossBar(msg, BarColor.GREEN, BarStyle.SOLID);
                this.bossBar.addPlayer(timerPlayer);
                this.residence = res;
            }
            @Override
            public void run() {
                if (countdown <= 0) {
                    component.setText(plugin.prisonConfig.message.get("FailCreateOverTimeMsg"));
                    component.setColor(ChatColor.RED); // 设置文本颜色
                    timerPlayer.spigot().sendMessage(component);
                    residence.getPermissions().setOwner(timerPlayer, true);
                    for (Map.Entry<String, Boolean> entry : pData.getResFlags().entrySet())
                        residence.getPermissions().setFlag(entry.getKey(), entry.getValue() ? FlagPermissions.FlagState.TRUE : FlagPermissions.FlagState.FALSE);
                    pData.setFree(true);
                    pData.getTaskList().remove(timerPlayer);
                    plugin.prisonerList.remove(timerPlayer, pData);

                    if (pData.getPrisonID() != -1) {
                        plugin.prisonTempList.remove(pData.getPrisonName());
                        PrisonData orgPrisonData = plugin.databaseManager.retrieveData(pData.getPrisonName());
                        plugin.prisonDataList.put(orgPrisonData.getPrisonName(), orgPrisonData);
                    }
                    this.cancel();
                }else{
                    String msg = new PlaceholderFormat(plugin.prisonConfig.message.get("EscapeTimeTitle")).format(countdown);
                    bossBar.setTitle(msg);
                    bossBar.setProgress((double) countdown / maxCountdown); // 此例假设总时间是60秒
                }
                countdown--;
            }
        }
        // 创建一个一次性的定时任务，延迟10秒后执行
        PrisonTask task = new ResOwnerTask(player, prisonData.getEscapeTime(), prisonData);
        player.getInventory().clear();
        task.runTaskTimer(plugin,20L,20L);

        // 获取定时任务
        return task;
    }

    public PrisonTask ChallengePrison(PrisonData prisonData){
        prisonData.setResFlags(res.getPermissions().getFlags());

        ClaimedResidence myRes = getResidenceManager().getByName(prisonData.getResName());
        CuboidArea mainArea = myRes.getMainArea();
        if(!plugin.prisonerList.containsValue(prisonData)){
            SchemManager.clearEntitiesInRegion(prisonData.getPrisonSpawn(),mainArea.getLowVector(),mainArea.getHighVector());
            SchemManager.loadAndPasteSchematic("plugins/PrisonEscape/schems/prison_" + prisonData.getPrisonID() + ".schem",mainArea.getLowLocation());

            res.getPermissions().setFlag("move",FlagPermissions.FlagState.FALSE);
            res.getPermissions().setFlag("tp",FlagPermissions.FlagState.FALSE);
            res.getPermissions().setFlag("pvp",FlagPermissions.FlagState.FALSE);
        }
        plugin.prisonerList.put(player,prisonData);
        //过一段时间后恢复权限
        class ResChallengerTask extends PrisonTask {
            public ResChallengerTask(Player timerPlayer, int count,PrisonData pData) {
                this.timerPlayer = timerPlayer;
                this.countdown = count;
                this.maxCountdown = count;
                this.pData = pData;
                this.savedInventory = timerPlayer.getInventory().getContents().clone();
                String msg = new PlaceholderFormat(plugin.prisonConfig.message.get("EscapeTimeTitle")).format(countdown);
                this.bossBar = Bukkit.createBossBar(msg, BarColor.GREEN, BarStyle.SOLID);
                this.bossBar.addPlayer(timerPlayer);
                this.residence = res;
            }

            @Override
            public void run() {
                if (countdown <= 0) {
                    bossBar.removeAll();

                    String tip = new PlaceholderFormat(plugin.prisonConfig.message.get("FailChallengeTimeMsg")).format(pData.getCounter());
                    component.setText(tip);
                    component.setColor(ChatColor.RED); // 设置文本颜色
                    timerPlayer.spigot().sendMessage(component);

                    for (Map.Entry<String, Boolean> entry : pData.getResFlags().entrySet())
                        residence.getPermissions().setFlag(entry.getKey(), entry.getValue() ? FlagPermissions.FlagState.TRUE : FlagPermissions.FlagState.FALSE);

                    pData.setFree(true);
                    pData.getTaskList().remove(timerPlayer);
                    plugin.prisonerList.remove(timerPlayer, pData);

                    Location spawnLoc = Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation();

                    if(plugin.economyManager.hasEconomy()) {
                        plugin.economyManager.setMoney(timerPlayer, -pData.getCounter());
                        ResidenceBank bank = residence.getBank();
                        bank.setStoredMoney(bank.getStoredMoneyD()+pData.getCounter());
                    }
                    // 使用玩家的teleport方法将其传送到出生点
                    timerPlayer.teleport(spawnLoc);
                    this.cancel();
                }else{
                    String msg = new PlaceholderFormat(plugin.prisonConfig.message.get("EscapeTimeTitle")).format(countdown);
                    bossBar.setTitle(msg);
                    bossBar.setProgress((double) countdown / maxCountdown); // 此例假设总时间是60秒
                }
                countdown--;
            }
        }
        PrisonTask task = new ResChallengerTask(player, prisonData.getEscapeTime(),prisonData);
        player.getInventory().clear();
        task.runTaskTimer(plugin, 20L,20L);

        // 获取定时任务
        return task;
    }

}
