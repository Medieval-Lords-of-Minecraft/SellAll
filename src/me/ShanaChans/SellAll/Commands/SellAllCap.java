package me.ShanaChans.SellAll.Commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.commands.CommandArgument;
import me.neoblade298.neocore.commands.CommandArguments;
import me.neoblade298.neocore.commands.Subcommand;
import me.neoblade298.neocore.commands.SubcommandRunner;

public class SellAllCap implements Subcommand
{
	private static final CommandArguments args = new CommandArguments(Arrays.asList(new CommandArgument("player/page number", false),new CommandArgument("page number", false)));

	@Override
	public String getDescription() 
	{
		return "Check the sell limits for your materials!";
	}

	@Override
	public String getKey() 
	{
		return "limit";
	}
	
	@Override
	public CommandArguments getArgs()
	{
		return args;
	}
	
	@Override
	public String getPermission() 
	{
		return null;
	}

	@Override
	public SubcommandRunner getRunner() 
	{
		return SubcommandRunner.PLAYER_ONLY;
	}
	
	@Override
	public String[] getAliases() 
	{
		return new String[] {"cap"};
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
