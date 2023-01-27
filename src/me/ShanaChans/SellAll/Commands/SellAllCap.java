package me.ShanaChans.SellAll.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class SellAllCap extends Subcommand
{
	public SellAllCap(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player", false), new Arg("page #", false));
		aliases = new String[] {"limit"};
	}

	@Override
	public void run(CommandSender sender, String[] args) 
	{
		Player player = (Player) sender;
		
		if(args.length > 0)
		{
			if(Bukkit.getPlayer(args[0]) != null)
			{
				if(Bukkit.getPlayer(args[0]).isOnline())
				{
					if(args.length > 1)
					{
						SellAllManager.getPlayers().get(Bukkit.getPlayer(args[0]).getUniqueId()).getSellCap(player, Bukkit.getPlayer(args[0]), Integer.parseInt(args[1]) - 1, SellAllManager.getPlayerSort(player));
					}
					else
					{
						SellAllManager.getPlayers().get(Bukkit.getPlayer(args[0]).getUniqueId()).getSellCap(player, Bukkit.getPlayer(args[0]), 0, SellAllManager.getPlayerSort(player));
					}
				}
			}
			else
			{
				SellAllManager.getPlayers().get(player.getUniqueId()).getSellCap(player, player, Integer.parseInt(args[0]) - 1, SellAllManager.getPlayerSort(player));
			}
		}
		else
		{
			SellAllManager.getPlayers().get(player.getUniqueId()).getSellCap(player, player, 0, SellAllManager.getPlayerSort(player));
		}
	}
}
