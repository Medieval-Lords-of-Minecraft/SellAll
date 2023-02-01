package me.ShanaChans.SellAll.Commands;

import org.bukkit.command.CommandSender;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class SellAdminReload extends Subcommand
{
	public SellAdminReload(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}
	
	@Override
	public void run(CommandSender sender, String[] args) 
	{	
		sender.sendMessage("§6SellAll Config reloaded!");
		SellAllManager.inst().loadConfigs();
	}

}
