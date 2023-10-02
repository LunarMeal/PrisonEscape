package com.lunarmeal.prisonescape;

import com.lunarmeal.prisonescape.Enum.EditEnum;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PrisonData {
    PrisonEscape plugin;
    private EditEnum editEnum;
    private String prisonName;
    private String resName;
    private Location prisonSpawn;
    private boolean isFree;
    private int prisonID;
    private int playerNum;
    private int escapeTime;
    private Map<String,Integer> rankingList;
    private Map<String,Boolean> resFlags;
    private Map<Player, PrisonTask> taskList;
    private String prisonOwner;
    private float counter;

    public PrisonData(String prisonName, String resName, Location prisonSpawn, Map<String,Boolean> resFlags) {
        this.plugin = PrisonEscape.getInstance();
        this.editEnum = EditEnum.EMPTY;
        this.prisonName = prisonName;
        this.resName = resName;
        this.prisonSpawn = prisonSpawn;
        this.isFree = true;
        this.prisonID = -1;
        this.playerNum = plugin.playerNum;
        this.escapeTime = plugin.escapeTime;
        this.taskList = new HashMap<>();
        this.rankingList = new HashMap<>();
        Map<String, Boolean> deepCopy = new HashMap<>();
        deepCopy.putAll(resFlags);
        this.resFlags = deepCopy;
        this.prisonOwner = null;
        this.counter = plugin.counter;
    }

    public EditEnum getEditEnum() {
        return editEnum;
    }

    public void setEditEnum(EditEnum editEnum) {
        this.editEnum = editEnum;
    }

    public String getPrisonName() {
        return prisonName;
    }

    public void setPrisonName(String prisonName) {
        this.prisonName = prisonName;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public Location getPrisonSpawn() {
        return prisonSpawn;
    }

    public void setPrisonSpawn(Location prisonSpawn) {
        this.prisonSpawn = prisonSpawn;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public Map<String, Boolean> getResFlags() {
        return resFlags;
    }

    public void setResFlags(Map<String, Boolean> resFlags) {
        Map<String, Boolean> deepCopy = new HashMap<>();
        deepCopy.putAll(resFlags);
        this.resFlags = deepCopy;
    }

    public Map<Player, PrisonTask> getTaskList() {
        return taskList;
    }

    public void addTaskToList(Player player, PrisonTask task) {
        this.taskList.put(player,task);
    }

    public String getPrisonOwner() {
        return prisonOwner;
    }

    public void setPrisonOwner(String prisonOwner) {
        this.prisonOwner = prisonOwner;
    }

    public float getCounter() {
        return counter;
    }

    public void setCounter(float counter) {
        this.counter = counter;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public int getPrisonID() {
        return prisonID;
    }

    public void setPrisonID(int prisonID) {
        this.prisonID = prisonID;
    }

    public int getEscapeTime() {
        return escapeTime;
    }

    public void setEscapeTime(int escapeTime) {
        this.escapeTime = escapeTime;
    }

    public Map<String, Integer> getRankingList() {
        return rankingList;
    }
}
