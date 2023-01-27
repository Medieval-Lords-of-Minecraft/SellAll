package me.ShanaChans.SellAll.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class SellAdminReset extends Subcommand
{
	public SellAdminReset(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}

	@Override
	public void run(CommandSender sender, String[] args) 
	{
		if(args.length > 0)
		{
			if(args[0].toLowerCase().equals("all"))
			{
				SellAllManager.resetPlayers();
				sender.sendMessage("�6All player limits reset!");
			}
			else if(Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).isOnline())
			{
				SellAllManager.resetPlayer(Bukkit.getPlayer(args[0]));
				sender.sendMessage("§6Player's limits reset!");
			}
		}
	}

}