package me.ShanaChans.SellAll.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class SellAllList extends Subcommand
{
	public SellAllList(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("page #"));
		aliases = new String[] { "price", "prices" };
	}

	@Override
	public void run(CommandSender sender, String[] args) 
	{
		Player player = (Player) sender;
		
		if(args.length > 0)
		{
			try {
				SellAllManager.getItemList(player, Integer.parseInt(args[0]) - 1, SellAllManager.getPlayerSort(player));
			} catch(NumberFormatException e){
				
			}
		}
		else
		{
			SellAllManager.getItemList(player, 0, SellAllManager.getPlayerSort(player));
		}
	}

}
