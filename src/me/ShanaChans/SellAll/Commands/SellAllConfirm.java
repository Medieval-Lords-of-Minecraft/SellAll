package me.ShanaChans.SellAll.Commands;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class SellAllConfirm extends Subcommand
{
	public SellAllConfirm(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
	}
	
	@Override
	public void run(CommandSender sender, String[] args) 
	{
		Player player = (Player) sender;
		if(SellAllManager.getPlayerConfirmInv().get(player.getUniqueId()) != null)
		{
			if(!(player.getGameMode() == GameMode.CREATIVE))
			{
				SellAllManager.getPlayers().get(player.getUniqueId()).sellAll(SellAllManager.getPlayerConfirmInv().get(player.getUniqueId()), player, true);
				SellAllManager.getPlayerConfirmInv().remove(player.getUniqueId());
				return;
			}
			player.sendMessage("§6You can not sell in creative!");
		}
	}
}
