package com.lunarmeal.prisonescape.Event;

import com.bekvon.bukkit.residence.economy.ResidenceBank;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.lunarmeal.prisonescape.Enum.EditEnum;
import com.lunarmeal.prisonescape.PrisonData;
import com.lunarmeal.prisonescape.PrisonEscape;
import com.lunarmeal.prisonescape.Utils.PlaceholderFormat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.bekvon.bukkit.residence.api.ResidenceApi.getResidenceManager;
import static com.lunarmeal.prisonescape.Utils.MapSortUtil.getTopThreeEntries;

public class MenuEventListener implements Listener {
    PrisonEscape plugin;
    Map<Player,Inventory> editMenus;
    Map<Player,Inventory> challengeMenus;
    public MenuEventListener(){
        this.plugin = PrisonEscape.getInstance();
        // 创建一个包含 9 个槽位的菜单
        editMenus = new HashMap<>();
        challengeMenus = new HashMap<>();
    }
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
        }

        item.setItemMeta(meta);

        return item;
    }
    public void openEditMenu(Player player) {
        Inventory editMenu = Bukkit.createInventory(null, 9, plugin.prisonConfig.message.get("EditMenu"));
        editMenus.put(player,editMenu);
        PrisonData prisonData = null;
        if(plugin.editorList.containsKey(player))
            prisonData = plugin.editorList.get(player);
        if(prisonData == null)
            return;
        // 向菜单添加物品
        ClaimedResidence res = getResidenceManager().getByName(prisonData.getResName());
        ResidenceBank bank = res.getBank();

        String renameLore2 = new PlaceholderFormat(plugin.prisonConfig.message.get("RenamePrisonLore2")).format(prisonData.getPrisonName());
        String changeCounterLore2 = new PlaceholderFormat(plugin.prisonConfig.message.get("ChangeCounterLore2")).format(prisonData.getCounter());
        String changeCounterLore3 = new PlaceholderFormat(plugin.prisonConfig.message.get("ChangeCounterLore3")).format(bank.getStoredMoneyD());
        String changeEscapeTimeLore2 = new PlaceholderFormat(plugin.prisonConfig.message.get("ChangeEscapeTimeLore2")).format(prisonData.getEscapeTime());
        String changePlayerNumLore2 = new PlaceholderFormat(plugin.prisonConfig.message.get("ChangePlayerNumLore2")).format(prisonData.getPlayerNum());

        editMenu.addItem(createGuiItem(Material.NAME_TAG,plugin.prisonConfig.message.get("RenamePrisonItem"),plugin.prisonConfig.message.get("RenamePrisonLore1"),renameLore2));
        editMenu.addItem(createGuiItem(Material.COMPASS,plugin.prisonConfig.message.get("SetSpawnPrisonItem"),plugin.prisonConfig.message.get("SetSpawnPrisonLore1")));
        editMenu.addItem(createGuiItem(Material.PAPER,plugin.prisonConfig.message.get("ChangeCounterItem"),plugin.prisonConfig.message.get("ChangeCounterLore1"),changeCounterLore2,changeCounterLore3));
        editMenu.addItem(createGuiItem(Material.CLOCK,plugin.prisonConfig.message.get("ChangeEscapeTimeItem"),plugin.prisonConfig.message.get("ChangeEscapeTimeLore1"),changeEscapeTimeLore2));
        editMenu.addItem(createGuiItem(Material.PLAYER_HEAD,plugin.prisonConfig.message.get("ChangePlayerNumItem"),plugin.prisonConfig.message.get("ChangePlayerNumLore1"),changePlayerNumLore2));
        // 设置更多槽位...
        // 打开菜单
        player.openInventory(editMenu);
    }
    public void openChallengeMenu(Player player) {
        Inventory challengeMenu = Bukkit.createInventory(null, 9, plugin.prisonConfig.message.get("ChallengeMenu"));
        challengeMenus.put(player,challengeMenu);
        for(PrisonData i:plugin.prisonDataList.values()){
            ClaimedResidence res = getResidenceManager().getByName(i.getResName());
            ResidenceBank bank = res.getBank();
            List<String> lores = new ArrayList<>();
            //统计游玩人数
            int sum = 0;
            for(PrisonData j:plugin.prisonerList.values()){
                if(j.equals(i))
                    sum++;
            }

            String Lore1 = new PlaceholderFormat(plugin.prisonConfig.message.get("ChallengeLore1")).format(i.getPrisonOwner());
            String Lore2 = new PlaceholderFormat(plugin.prisonConfig.message.get("ChallengeLore2")).format(i.getCounter());
            String Lore3 = new PlaceholderFormat(plugin.prisonConfig.message.get("ChallengeLore3")).format(bank.getStoredMoneyD());
            String Lore4 = new PlaceholderFormat(plugin.prisonConfig.message.get("ChallengeLore4")).format(sum,i.getPlayerNum());
            String Lore6 = new PlaceholderFormat(plugin.prisonConfig.message.get("ChallengeLore6")).format(i.getPrisonOwner(),i.getRankingList().get(i.getPrisonOwner()));

            lores.add(Lore1);
            lores.add(Lore2);
            lores.add(Lore3);
            lores.add(Lore4);
            lores.add(plugin.prisonConfig.message.get("ChallengeLore5"));
            lores.add(Lore6);

            List<Map.Entry<String, Integer>> sortedEntries = getTopThreeEntries(i.getRankingList());
            int j = 1;
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                String Lore7 = new PlaceholderFormat(plugin.prisonConfig.message.get("ChallengeLore7")).format(j,entry.getKey(),entry.getValue());
                lores.add(Lore7);
                j++;
            }
            challengeMenu.addItem(createGuiItem(Material.IRON_BARS,i.getPrisonName(),lores.toArray(new String[0])));
        }
        // 设置更多槽位...
        // 打开菜单
        player.openInventory(challengeMenu);
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 检查事件是否发生在玩家的菜单中
        Player player = (Player) event.getWhoClicked();

        if (editMenus.containsValue(event.getInventory())) {
            event.setCancelled(true);
            // 检查点击的物品是否为 null 且包含自定义标签
            if (plugin.editorList.containsKey(player)) {
                // 根据物品的名称执行不同的操作
                PrisonData prisonData = plugin.editorList.get(player);
                switch (event.getRawSlot()) {
                    case 0: {
                        TextComponent component = new TextComponent(plugin.prisonConfig.message.get("NewPrisonNameTipMsg"));
                        component.setColor(ChatColor.YELLOW); // 设置文本颜色
                        player.spigot().sendMessage(component);
                        prisonData.setEditEnum(EditEnum.PRISONNAME);
                        plugin.waitingForChange = player;
                        player.closeInventory();
                        break;
                    }
                    case 1: {
                        player.performCommand("pecp setspawn");
                        player.closeInventory();
                        break;
                    }
                    case 2: {
                        TextComponent component = new TextComponent(plugin.prisonConfig.message.get("NewCounterTipMsg"));
                        component.setColor(ChatColor.YELLOW); // 设置文本颜色
                        player.spigot().sendMessage(component);
                        prisonData.setEditEnum(EditEnum.COUNTER);
                        plugin.waitingForChange = player;
                        player.closeInventory();
                        break;
                    }
                    case 3: {
                        TextComponent component = new TextComponent(plugin.prisonConfig.message.get("NewEscapeTimeTipMsg"));
                        component.setColor(ChatColor.YELLOW); // 设置文本颜色
                        player.spigot().sendMessage(component);
                        prisonData.setEditEnum(EditEnum.ESCAPETIME);
                        plugin.waitingForChange = player;
                        player.closeInventory();
                        break;
                    }
                    case 4:{
                        TextComponent component = new TextComponent(plugin.prisonConfig.message.get("NewPlayerNumTipMsg"));
                        component.setColor(ChatColor.YELLOW); // 设置文本颜色
                        player.spigot().sendMessage(component);
                        prisonData.setEditEnum(EditEnum.PLAYERNUM);
                        plugin.waitingForChange = player;
                        player.closeInventory();
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        } else if (challengeMenus.containsValue(event.getInventory())) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if(item!=null) {
                player.performCommand("pecp challenge " + Objects.requireNonNull(item.getItemMeta()).getDisplayName());
                player.closeInventory();
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Collection<Inventory> editCollect = editMenus.values();
        Collection<Inventory> challengeCollect = challengeMenus.values();
        editCollect.remove(event.getInventory());
        if (challengeCollect.contains((event.getInventory())))
            challengeCollect.remove(event.getInventory());
    }

}
