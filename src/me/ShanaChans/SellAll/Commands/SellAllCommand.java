package me.ShanaChans.SellAll.Commands;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.Inventories.SellAllInventory;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class SellAllCommand extends Subcommand
{

	public SellAllCommand(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}
	
	@Override
	public void run(CommandSender sender, String[] args) 
	{
		Player player = (Player) sender;
		
		if(!(player.getGameMode() == GameMode.CREATIVE))
		{
			new SellAllInventory(player);
			return;
		}
		player.sendMessage("§6You can not sell in creative!");
	}

}
