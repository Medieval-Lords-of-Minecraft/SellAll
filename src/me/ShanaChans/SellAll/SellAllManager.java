package me.ShanaChans.SellAll;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Container;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.tr7zw.nbtapi.NBTItem;
import me.ShanaChans.SellAll.Commands.*;
import me.ShanaChans.SellAll.Inventories.CustomInventory;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.bungee.BungeeAPI;
import me.neoblade298.neocore.bukkit.commands.SubcommandManager;
import me.neoblade298.neocore.bukkit.io.IOComponent;
import me.neoblade298.neocore.bukkit.player.PlayerTags;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;
import me.neoblade298.neocore.bukkit.scheduler.SchedulerAPI;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.PaginatedList;
import net.md_5.bungee.api.ChatColor;

public class SellAllManager extends JavaPlugin implements Listener, IOComponent {
	private static TreeMap<Material, Double> itemPrices = new TreeMap<Material, Double>();
	private static TreeMap<Material, Double> itemPricesAlphabetical;
	private static TreeMap<Material, Integer> itemCaps = new TreeMap<Material, Integer>();
	private static HashMap<UUID, SellAllPlayer> players = new HashMap<UUID, SellAllPlayer>();
	private static TreeMap<Double, String> permMultipliers = new TreeMap<Double, String>();
	private static TreeMap<Double, String> permBoosters = new TreeMap<Double, String>();
	private static HashMap<UUID, Inventory> playerConfirmInv = new HashMap<UUID, Inventory>();
	public static HashMap<Player, CustomInventory> viewingInventory = new HashMap<Player, CustomInventory>();
	private static HashSet<Material> containers = new HashSet<Material>();
	private static double moneyCap;
	private static double tierMultiplier;
	private static double tierPriceMultiplier;
	private static int tierAmount;
	private static YamlConfiguration cfg;
	public static PlayerTags settings;
	private static SellAllManager inst;
	
	public void onEnable() {
		Bukkit.getServer().getLogger().info("SellAll Enabled");
		getServer().getPluginManager().registerEvents(this, this);
		settings = NeoCore.createPlayerTags("SellAll", this, true);
		Comparator<Material> comp = new Comparator<Material>(){
			@Override
			public int compare(Material m1, Material m2)
			{
				return m1.name().compareTo(m2.name());
			}
		};
		itemPricesAlphabetical = new TreeMap<Material, Double>(comp);
		initCommands();
		loadConfigs();
		SchedulerAPI.scheduleRepeating("sellall-resetcaps", ScheduleInterval.DAILY, new Runnable() {
		    public void run() {
		        resetPlayers();
		    }
		});
		inst = this;
		NeoCore.registerIOComponent(this, this, "SellAllManager");
	}

	public void onDisable() {
		org.bukkit.Bukkit.getServer().getLogger().info("SellAll Disabled");
		super.onDisable();
	}

	private void initCommands() {
		SubcommandManager sellAll = new SubcommandManager("sellall", null, ChatColor.RED, this);
		sellAll.registerCommandList("help");
		sellAll.register(new SellAllCommand("", "Sell your items!", null, SubcommandRunner.PLAYER_ONLY));
		sellAll.register(new SellAllCap("cap", "Check the sell limits for items", null, SubcommandRunner.PLAYER_ONLY));
		sellAll.register(new SellAllList("list", "Lists prices for materials you can sell", null, SubcommandRunner.PLAYER_ONLY));
		sellAll.register(new SellAllQuick("quick", "Sell items instantly with no confirm inventory", null, SubcommandRunner.PLAYER_ONLY));
		sellAll.register(new SellAllConfirm("confirm", "Confirm your sell", null, SubcommandRunner.PLAYER_ONLY));
		
		SubcommandManager value = new SubcommandManager("value", null, ChatColor.RED, this);
		value.register(new SellAllValue("", "Checks the value of the item in hand", null, SubcommandRunner.PLAYER_ONLY));
		
		SubcommandManager sellAdmin = new SubcommandManager("selladmin", "sellall.admin", ChatColor.DARK_RED, this);
		sellAdmin.registerCommandList("");
		sellAdmin.register(new SellAdminSet("set", "Set amount sold for player", null, SubcommandRunner.BOTH));
		sellAdmin.register(new SellAdminGive("give", "Gives a sell wand", null, SubcommandRunner.BOTH));
		sellAdmin.register(new SellAdminReload("reload", "Reload the plugin", null, SubcommandRunner.BOTH));
		sellAdmin.register(new SellAdminReset("reset", "Reset sellall", null, SubcommandRunner.BOTH));
	}

	public void loadConfigs() 
	{
		itemPrices.clear();
		itemCaps.clear();
		permMultipliers.clear();
		File cfg = new File(getDataFolder(), "config.yml");

		// Save config if doesn't exist
		if (!cfg.exists()) {
			saveResource("config.yml", false);
		}

		SellAllManager.cfg = YamlConfiguration.loadConfiguration(cfg);
		ConfigurationSection sec = SellAllManager.cfg.getConfigurationSection("price-list");

		for (String key : sec.getKeys(false)) {
			try {
				if (Material.valueOf(key) == null) {
					Bukkit.getLogger().warning("Item failed to load: " + key);
				}
				else 
				{
					itemPrices.put(Material.valueOf(key), sec.getDouble(key));
					itemPricesAlphabetical.put(Material.valueOf(key), sec.getDouble(key));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		moneyCap = SellAllManager.cfg.getDouble("money-cap");
		tierMultiplier = SellAllManager.cfg.getDouble("tier-multiplier");
		tierPriceMultiplier = SellAllManager.cfg.getDouble("tier-price-multiplier");
		tierAmount = SellAllManager.cfg.getInt("tier-amount");
		
		sec = SellAllManager.cfg.getConfigurationSection("item-cap");
		
		for(Material mat : itemPrices.keySet())
		{
			if(sec.contains(mat.name()))
			{
				itemCaps.put(mat, sec.getInt(mat.name()));
			}
			else
			{
				int defaultValue = (int) Math.round(SellAllManager.getMoneyCap() / SellAllManager.getItemPrices().get(mat));
				itemCaps.put(mat, defaultValue);
			}
		}
		
        List<String> containerList = SellAllManager.cfg.getStringList("containers");
		
		for(int i = 0; i < containerList.size(); i++)
		{
			if(Material.valueOf(containerList.get(i)) != null)
			{
				containers.add(Material.valueOf(containerList.get(i)));
			}
		}
		
		sec = SellAllManager.cfg.getConfigurationSection("multipliers");
		
		for (String key : sec.getKeys(false)) 
		{
			permMultipliers.put(sec.getDouble(key), key.replace("-", "."));
		}
		
		sec = SellAllManager.cfg.getConfigurationSection("boosters");
		
		for (String key : sec.getKeys(false)) 
		{
			permBoosters.put(sec.getDouble(key), key.replace("-", "."));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void rightClick(PlayerInteractEvent e) 
	{
		Player player = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND
				&& containers.contains(e.getClickedBlock().getType()) && e.getItem() != null) 
		{
			NBTItem heldItem = new NBTItem(e.getItem());
			if (heldItem.hasKey("sellStick") && NeoCore.isLoaded(player)) {
				e.setCancelled(true);
				Container container = (Container) e.getClickedBlock().getState();
				Inventory inv = container.getInventory();
				if(!SellAllManager.settings.exists("SellAllNoConfirm", player.getUniqueId()))
				{
					SellAllManager.getPlayers().get(player.getUniqueId()).sellAll(inv, player, false);
					return;
				}
				SellAllManager.getPlayers().get(player.getUniqueId()).sellAll(inv, player, true);
				return;
			}
		}
	}

	public static HashMap<UUID, SellAllPlayer> getPlayers() {
		return players;
	}

	public static TreeMap<Material, Double> getItemPrices() {
		return itemPrices;
	}
	
	public static TreeMap<Material, Integer> getItemCaps() {
		return itemCaps;
	}
	
	public static double getMultiplier(Player p) 
	{
		Iterator<Double> iter = permMultipliers.descendingKeySet().iterator();
		while (iter.hasNext()) 
		{
			double mult = iter.next();
			String perm = permMultipliers.get(mult);
			if (p.hasPermission(perm)) 
			{
				return mult;	
			}
		}
		return 1.0;
	}
	
	public static double getBooster(Player p) 
	{
		Iterator<Double> iter = permBoosters.descendingKeySet().iterator();
		while (iter.hasNext()) 
		{
			double mult = iter.next();
			String perm = permBoosters.get(mult);
			if (p.hasPermission(perm)) 
			{
				return mult;	
			}
		}
		return 1.0;
	}
	
	public static TreeMap<Material, Double> getPlayerSort(Player p)
	{
		if(settings.exists("SellAllSort", p.getUniqueId()))
		{
			return itemPricesAlphabetical;
		}
		
		return itemPrices;
	}
    
	/**
	 * Lists out material price list
	 * @param player
	 */
	public static void getItemList(Player player, int pageNumber, TreeMap<Material, Double> sort)
	{
		PaginatedList<String> list = new PaginatedList<String>();
		DecimalFormat df = new DecimalFormat("0.00");
		double multiplier = SellAllManager.getMultiplier(player);
		double booster = SellAllManager.getBooster(player);
		
		double boosterMultiplier = (multiplier - 1) + (booster - 1) + 1;
		
		for(Material mat : sort.keySet())
		{
			double price = SellAllManager.getItemPrices().get(mat);
			list.add("§7" + mat.name() + ":§e " + df.format(price) + "g §7| §c" + df.format(price * boosterMultiplier) + "g");
		}
		if(-1 < pageNumber && pageNumber < list.pages())
		{
			player.sendMessage("§6O---={ Price List }=---O");
			player.sendMessage("§eBase Price §7| §cMultiplier (" + multiplier + "x) + Booster (" + booster + "x)");
			for(String output : list.get(pageNumber))
			{
				player.sendMessage(output);
			}
			String nextPage ="/sellall list " + (pageNumber + 2); 
			String prevPage = "/sellall list " + (pageNumber); 
			player.spigot().sendMessage(list.getFooter(pageNumber, nextPage, prevPage));
			return;
		}
		player.sendMessage("§7Invalid page");
	}
	
    public static void resetPlayers()
    {
    	
    	try (Connection con = NeoCore.getConnection("SellAllManager");
    			Statement stmt = con.createStatement();){
			stmt.executeUpdate("DELETE FROM sellall_players;");
			Bukkit.getLogger().info("[Sellall] Reset all players!");
	    	BungeeAPI.broadcast("§6Sell All Limits have been refreshed!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	ArrayList<UUID> toRemove = new ArrayList<UUID>();
    	for(UUID uuid : players.keySet())
    	{
    		if (players.get(uuid) == null) {
    			toRemove.add(uuid);
    			continue;
    		}
    		players.get(uuid).resetSold();
    	}
    	for (UUID uuid : toRemove) {
    		players.remove(uuid);
    		Bukkit.getLogger().warning("[SellAll] Removed uuid " + uuid + ", null account");
    	}
    }
    
    public static void resetPlayer(Player p)
    {
    	try (Connection con = NeoCore.getConnection("SellAllManager");
    			Statement stmt = con.createStatement();){
    		stmt.executeUpdate("DELETE FROM sellall_players WHERE uuid = '" + p.getUniqueId() + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	players.get(p.getUniqueId()).resetSold();
    }

	@Override
	public void cleanup(Statement arg0, Statement arg1) {}

	@Override
	public void loadPlayer(Player arg0, Statement arg1) {}

	@Override
	public void preloadPlayer(OfflinePlayer p, Statement stmt) {
		if (!players.containsKey(p.getUniqueId())) {
			HashMap<Material, Integer> sold = new HashMap<Material, Integer>();
			
			try {
				ResultSet rs = stmt.executeQuery("SELECT * FROM sellall_players WHERE uuid = '" + p.getUniqueId() + "';");
				while (rs.next()) {
					Material key = Material.valueOf(rs.getString(2));
					int value = rs.getInt(3);
					sold.put(key, value);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			players.put(p.getUniqueId(), new SellAllPlayer(sold));
		}
	}

	@Override
	public void savePlayer(Player p, Statement insert, Statement delete) {
		if (players.containsKey(p.getUniqueId())) {
			HashMap<Material, Integer> sold = players.get(p.getUniqueId()).getItemAmountSold();
			try {
				for (Entry<Material, Integer> e : sold.entrySet()) {
						insert.addBatch("REPLACE INTO sellall_players VALUES ('" + p.getUniqueId() + "','"
								+ e.getKey() + "'," + e.getValue() + ");");
				}
				insert.executeBatch();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			players.remove(p.getUniqueId());
		}
	}
	
	public static void getValue(Player p)
	{
		ItemStack item = p.getInventory().getItemInMainHand();
        
        if (item == null || item.getType().isAir()) 
        {
            p.sendMessage("§6You're not holding anything!");
            return;
        }
        
        NBTItem nbti = new NBTItem(item);
        double value = 0;
        
        if (!nbti.getString("value").isBlank()) 
        {
            value = Double.parseDouble(nbti.getString("value"));
        }
        else 
        {
            value = nbti.getDouble("value");
        }
        
        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name();
        
        if(value == 0)
        {
        	if(itemPrices.containsKey(item.getType()))
        	{
        		name = item.getType().name();
        		value = itemPrices.get(item.getType());
        	}
        	else
        	{
        		p.sendMessage("§6This item does not have a price!");
        		return;
        	}
        }
        
        p.sendMessage("§6Value of §7" + name + "§7: §e" + value + "g");
	}
	
	public static HashMap<UUID, Inventory> getPlayerConfirmInv() 
	{
		return playerConfirmInv;
	}
	
	public static SellAllManager inst()
	{
		return inst;
	}

	public static double getMoneyCap() 
	{
		return moneyCap;
	}
	
	public static double getTierMultiplier() 
	{
		return tierMultiplier;
	}
	
	public static double getTierPriceMultiplier() 
	{
		return tierPriceMultiplier;
	}
	
	public static int getTierAmount() 
	{
		return tierAmount;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (viewingInventory.containsKey(p)) {
			viewingInventory.get(p).handleInventoryClick(e);
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (viewingInventory.containsKey(p)) {
			viewingInventory.get(p).handleInventoryDrag(e);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (viewingInventory.containsKey(p) && e.getInventory() != null && e.getInventory() == viewingInventory.get(p).getInventory()) {
			viewingInventory.get(p).handleInventoryClose(e);
			viewingInventory.remove(p);
		}
	}

	public static HashSet<Material> getContainers() {
		return containers;
	}
}
