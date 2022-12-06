package me.ShanaChans.SellAll.Inventories;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import me.ShanaChans.SellAll.SellAllManager;


public abstract class CustomInventory
{
	protected Inventory inv;
	public abstract void handleInventoryClick(InventoryClickEvent e);
	public abstract void handleInventoryDrag(InventoryDragEvent e);
	public abstract void handleInventoryClose(InventoryCloseEvent e);
	public Inventory getInventory() {
		return this.inv;
	}
	public void setupInventory(Player p, Inventory inv, CustomInventory pinv) {
		SellAllManager.viewingInventory.put(p, this);
		this.inv = inv;
		p.openInventory(inv);
	}
}
