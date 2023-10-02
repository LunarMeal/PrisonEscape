package com.lunarmeal.prisonescape;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.lunarmeal.prisonescape.Event.MenuEventListener;
import com.lunarmeal.prisonescape.Event.PlayerListener;
import com.lunarmeal.prisonescape.Event.ResChangedListener;
import com.lunarmeal.prisonescape.Event.ResTouchListener;
import com.lunarmeal.prisonescape.Utils.DatabaseManager;
import com.lunarmeal.prisonescape.Utils.EcoManager;
import com.lunarmeal.prisonescape.Utils.PrisonTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class PrisonEscape extends JavaPlugin {

    private static PrisonEscape instance; // 声明一个静态的 PrisonEscape 类型变量 instance
    public ResidenceApi residenceApi;
    public EcoManager economyManager;
    public Residence residencePlugin;
    public PrisonConfig prisonConfig;
    public Map<String, PrisonData> prisonTempList;
    public Map<String, PrisonData> prisonDataList;
    public Map<Player, PrisonData> prisonerList;
    public Map<Player, PrisonData> editorList;
    public List<World> worldBlackList;
    public int escapeTime;
    public float counter;
    public int playerNum;
    ResTouchListener resTouchListener;
    PlayerListener playerListener;
    ResChangedListener resChangedListener;
    public MenuEventListener menuEventListener;
    public DatabaseManager databaseManager;
    public Player waitingForChange = null;

    @Override
    public void onEnable() {
        // 初始化 instance 变量，使其指向当前对象
        instance = this;
        residencePlugin = (Residence) getServer().getPluginManager().getPlugin("Residence");
        economyManager = new EcoManager();
        prisonTempList = new HashMap<>();
        prisonerList = new HashMap<>();
        editorList = new HashMap<>();
        worldBlackList = new ArrayList<>();
        escapeTime = 600;
        counter = 100;
        playerNum = 2;
        prisonConfig = new PrisonConfig();

        // 加载配置文件
        saveDefaultConfig();
        prisonConfig.LoadConfig();
        prisonConfig.LoadLang();

        resTouchListener = new ResTouchListener();
        playerListener = new PlayerListener();
        resChangedListener = new ResChangedListener();
        menuEventListener = new MenuEventListener();

        databaseManager = new DatabaseManager("plugins/PrisonEscape/prison-database.db");
        if (databaseManager.getConnection() != null)
            databaseManager.createTable();
        prisonDataList = databaseManager.retrieveAllData();

        initPrisons();

        this.residenceApi = residencePlugin.getAPI();

        Bukkit.getConsoleSender().sendMessage("§b[PrisonEscape] 监狱逃脱插件，启动！！！");
        Bukkit.getConsoleSender().sendMessage("§b[PrisonEscape] 有问题请加作者QQ1745266439咨询");

        Objects.requireNonNull(Bukkit.getPluginCommand("prisonescape")).setExecutor(new PrisonEscapeCommandListener());
        Objects.requireNonNull(Bukkit.getPluginCommand("prisonescape")).setTabCompleter(new PrisonTabCompleter());

        registerEvents();
    }

    @Override
    public void onDisable() {
        savePrisonOwners();
        databaseManager.closeConnection();
        Bukkit.getConsoleSender().sendMessage("§a[PrisonEscape] 监狱逃脱插件，结束！！！");
    }

    private void initPrisons() {
        for (PrisonData i : prisonDataList.values()) {
            ClaimedResidence res = ResidenceApi.getResidenceManager().getByName(i.getResName());
            if (!res.getOwner().equals(i.getPrisonOwner())) {
                res.getPermissions().setOwner(i.getPrisonOwner(), true);
                for (Map.Entry<String, Boolean> entry : i.getResFlags().entrySet())
                    res.getPermissions().setFlag(entry.getKey(), entry.getValue() ? FlagPermissions.FlagState.TRUE : FlagPermissions.FlagState.FALSE);
            }
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(resTouchListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(resChangedListener, this);
        getServer().getPluginManager().registerEvents(menuEventListener, this);
    }

    private void savePrisonOwners() {
        for (PrisonData i : prisonTempList.values()) {
            ClaimedResidence res = ResidenceApi.getResidenceManager().getByName(i.getResName());
            res.getPermissions().setOwner(i.getPrisonOwner(), true);
            for (Map.Entry<String, Boolean> entry : i.getResFlags().entrySet())
                res.getPermissions().setFlag(entry.getKey(), entry.getValue() ? FlagPermissions.FlagState.TRUE : FlagPermissions.FlagState.FALSE);
        }
    }

    // 声明静态的 getInstance() 方法，用于获取唯一实例
    public static PrisonEscape getInstance() {
        return instance;
    }
}
