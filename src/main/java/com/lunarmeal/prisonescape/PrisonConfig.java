package com.lunarmeal.prisonescape;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrisonConfig {
    PrisonEscape plugin;
    FileConfiguration config;
    FileConfiguration langConfig;
    public Map<String,String> message;

    public PrisonConfig() {
        this.plugin = PrisonEscape.getInstance();
        this.config = plugin.getConfig();
        this.message = new HashMap<>();
    }
    public void LoadConfig(){
        List<String> worldBL = config.getStringList("worldBlackList");
        plugin.worldBlackList.clear();
        for(String blackWorld:worldBL){
            plugin.worldBlackList.add(plugin.getServer().getWorld(blackWorld));
        }
        plugin.escapeTime = config.getInt("escapeTime");
        plugin.counter = (float) config.getDouble("counter");
        plugin.playerNum = config.getInt("playerNum");
    }
    public void LoadLang(){
        saveLang();
        getTranslation();
        message.put("usage",langConfig.getString("usage"));
        message.put("ErrorMsg",langConfig.getString("ErrorMsg"));
        message.put("plzCompleteMsg",langConfig.getString("plzCompleteMsg"));
        message.put("worldBlacklistMsg",langConfig.getString("worldBlacklistMsg"));
        message.put("MustResCreateMsg",langConfig.getString("MustResCreateMsg"));
        message.put("OnlyResOwnerCreateMsg",langConfig.getString("OnlyResOwnerCreateMsg"));
        message.put("ResHasPrisonMsg",langConfig.getString("ResHasPrisonMsg"));
        message.put("ResNameOnlyMsg",langConfig.getString("ResNameOnlyMsg"));
        message.put("SetSpawnTipMsg",langConfig.getString("SetSpawnTipMsg"));
        message.put("SetSpawnInResMsg",langConfig.getString("SetSpawnInResMsg"));
        message.put("MustOwnerSetSpawnMsg",langConfig.getString("MustOwnerSetSpawnMsg"));
        message.put("OwnerChallengeMsg",langConfig.getString("OwnerChallengeMsg"));
        message.put("PlayerChallengeMsg",langConfig.getString("PlayerChallengeMsg"));
        message.put("CantSetSpawnChallengingMsg",langConfig.getString("CantSetSpawnChallengingMsg"));
        message.put("OwnerChallengeResetMsg",langConfig.getString("OwnerChallengeResetMsg"));
        message.put("MustCreateFirstMsg",langConfig.getString("MustCreateFirstMsg"));
        message.put("NotHavePrisonMsg",langConfig.getString("NotHavePrisonMsg"));
        message.put("NotHaveOPMsg",langConfig.getString("NotHaveOPMsg"));
        message.put("MustResEditMsg",langConfig.getString("MustResEditMsg"));
        message.put("OnlyResOwnerEditMsg",langConfig.getString("OnlyResOwnerEditMsg"));
        message.put("CantEditChallengingMsg",langConfig.getString("CantEditChallengingMsg"));
        message.put("RemoveInResMsg",langConfig.getString("RemoveInResMsg"));
        message.put("MustOwnerRemoveMsg",langConfig.getString("MustOwnerRemoveMsg"));
        message.put("StopForRemovePrisonMsg",langConfig.getString("StopForRemovePrisonMsg"));
        message.put("RemovePrisonOverMsg",langConfig.getString("RemovePrisonOverMsg"));
        message.put("OwnerCantChallengeMsg",langConfig.getString("OwnerCantChallengeMsg"));
        message.put("AtMostPlayersMsg",langConfig.getString("AtMostPlayersMsg"));
        message.put("PlayerLackMoneyMsg",langConfig.getString("PlayerLackMoneyMsg"));
        message.put("BankLackMoneyMsg",langConfig.getString("BankLackMoneyMsg"));
        message.put("ExitChallengeMsg",langConfig.getString("ExitChallengeMsg"));
        message.put("NotChallengingMsg",langConfig.getString("NotChallengingMsg"));
        message.put("NotConsoleCommandMsg",langConfig.getString("NotConsoleCommandMsg"));
        message.put("FailCreateOverTimeMsg",langConfig.getString("FailCreateOverTimeMsg"));
        message.put("FailCreateDeathMsg",langConfig.getString("FailCreateDeathMsg"));
        message.put("SuccessCreateMsg",langConfig.getString("SuccessCreateMsg"));
        message.put("FailChallengeTimeMsg",langConfig.getString("FailChallengeTimeMsg"));
        message.put("FailChallengeDeathMsg",langConfig.getString("FailChallengeDeathMsg"));
        message.put("SuccessChallengeMsg",langConfig.getString("SuccessChallengeMsg"));
        message.put("NewPrisonNameTipMsg",langConfig.getString("NewPrisonNameTipMsg"));
        message.put("NewCounterTipMsg",langConfig.getString("NewCounterTipMsg"));
        message.put("NewEscapeTimeTipMsg",langConfig.getString("NewEscapeTimeTipMsg"));
        message.put("NewPlayerNumTipMsg",langConfig.getString("NewPlayerNumTipMsg"));
        message.put("CantCommandChallengingMsg",langConfig.getString("CantCommandChallengingMsg"));
        message.put("NewPrisonNameCompleteMsg",langConfig.getString("NewPrisonNameCompleteMsg"));
        message.put("IllegalNewPrisonNameMsg",langConfig.getString("IllegalNewPrisonNameMsg"));
        message.put("NewCounterCompleteMsg",langConfig.getString("NewCounterCompleteMsg"));
        message.put("NewCounterAtLeastMsg",langConfig.getString("NewCounterAtLeastMsg"));
        message.put("NewEscapeTimeCompleteMsg",langConfig.getString("NewEscapeTimeCompleteMsg"));
        message.put("NewEscapeTimeAtLeastMsg",langConfig.getString("NewEscapeTimeAtLeastMsg"));
        message.put("NewPlayerNumCompleteMsg",langConfig.getString("NewPlayerNumCompleteMsg"));
        message.put("NewPlayerNumAtMostMsg",langConfig.getString("NewPlayerNumAtMostMsg"));
        message.put("NewPlayerNumAtLeastMsg",langConfig.getString("NewPlayerNumAtLeastMsg"));
        message.put("ResChangeOwnerMsg",langConfig.getString("ResChangeOwnerMsg"));
        message.put("ResCreateSubMsg",langConfig.getString("ResCreateSubMsg"));
        message.put("ResChangeFlagMsg",langConfig.getString("ResChangeFlagMsg"));
        message.put("ResRemoveNoticeMsg",langConfig.getString("ResRemoveNoticeMsg"));
        message.put("ResRemoveTipMsg",langConfig.getString("ResRemoveTipMsg"));
        message.put("ResChangeSizeMsg",langConfig.getString("ResChangeSizeMsg"));

        message.put("PrisonListTitle",langConfig.getString("PrisonListTitle"));
        message.put("BeginChallengeTitle",langConfig.getString("BeginChallengeTitle"));
        message.put("SuccessChallengeTitle",langConfig.getString("SuccessChallengeTitle"));
        message.put("EscapeTimeTitle",langConfig.getString("EscapeTimeTitle"));

        message.put("ResListRow",langConfig.getString("ResListRow"));

        message.put("EditMenu",langConfig.getString("EditMenu"));
        message.put("ChallengeMenu",langConfig.getString("ChallengeMenu"));

        message.put("RenamePrisonItem",langConfig.getString("RenamePrisonItem"));
        message.put("SetSpawnPrisonItem",langConfig.getString("SetSpawnPrisonItem"));
        message.put("ChangeCounterItem",langConfig.getString("ChangeCounterItem"));
        message.put("ChangeEscapeTimeItem",langConfig.getString("ChangeEscapeTimeItem"));
        message.put("ChangePlayerNumItem",langConfig.getString("ChangePlayerNumItem"));

        message.put("RenamePrisonLore1",langConfig.getString("RenamePrisonLore1"));
        message.put("SetSpawnPrisonLore1",langConfig.getString("SetSpawnPrisonLore1"));
        message.put("ChangeCounterLore1",langConfig.getString("ChangeCounterLore1"));
        message.put("ChangeEscapeTimeLore1",langConfig.getString("ChangeEscapeTimeLore1"));
        message.put("ChangePlayerNumLore1",langConfig.getString("ChangePlayerNumLore1"));

        message.put("RenamePrisonLore2",langConfig.getString("RenamePrisonLore2"));
        message.put("ChangeCounterLore2",langConfig.getString("ChangeCounterLore2"));
        message.put("ChangeEscapeTimeLore2",langConfig.getString("ChangeEscapeTimeLore2"));
        message.put("ChangePlayerNumLore2",langConfig.getString("ChangePlayerNumLore2"));

        message.put("ChangeCounterLore3",langConfig.getString("ChangeCounterLore3"));

        message.put("ChallengeLore1",langConfig.getString("ChallengeLore1"));
        message.put("ChallengeLore2",langConfig.getString("ChallengeLore2"));
        message.put("ChallengeLore3",langConfig.getString("ChallengeLore3"));
        message.put("ChallengeLore4",langConfig.getString("ChallengeLore4"));
        message.put("ChallengeLore5",langConfig.getString("ChallengeLore5"));
        message.put("ChallengeLore6",langConfig.getString("ChallengeLore6"));
        message.put("ChallengeLore7",langConfig.getString("ChallengeLore7"));
    }
    public void saveLang(){
        plugin.saveResource("lang/Chinese.yml",true);
        //plugin.saveResource("lang/English.yml",true);
    }
    private void getTranslation() {
        String selLanguage = config.getString("language");
        File langFolder = new File("plugins/PrisonEscape", "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        File langFile = new File(langFolder, selLanguage + ".yml");
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }
}
