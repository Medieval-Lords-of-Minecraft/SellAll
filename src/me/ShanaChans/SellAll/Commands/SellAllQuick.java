package me.ShanaChans.SellAll.Commands;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class SellAllQuick extends Subcommand
{
	public SellAllQuick(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		aliases = new String[] { "q" };
	}
	
	@Override
	public void run(CommandSender sender, String[] args) 
	{
		Player player = (Player) sender;
		
		if(!(player.getGameMode() == GameMode.CREATIVE))
		{
			if(!SellAllManager.settings.exists("SellAllNoConfirm", player.getUniqueId()))
			{
				SellAllManager.getPlayers().get(player.getUniqueId()).sellAll(player.getInventory(), player, false);
				return;
			}
			
			SellAllManager.getPlayers().get(player.getUniqueId()).sellAll(player.getInventory(), player, true);
			return;
		}
		player.sendMessage("§6You can not sell in creative!");
	}

}
