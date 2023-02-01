package me.ShanaChans.SellAll.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.Items.Items;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class SellAdminGive extends Subcommand
{
	public SellAdminGive(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player", false));
	}
	
	@Override
	public void run(CommandSender sender, String[] args) 
	{
		if(args.length > 0)
		{
			if(Bukkit.getPlayer(args[0]) != null)
			{
				Bukkit.getPlayer(args[0]).getInventory().addItem(Items.getChestSellStick());
			}
		}
		else {
			((Player) sender).getInventory().addItem(Items.getChestSellStick());
		}
	}
}
