package me.ShanaChans.SellAll.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class SellAdminSet extends Subcommand {
	public SellAdminSet(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player"), new Arg("material"));
	}

	@Override
	public void run(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if (Bukkit.getPlayer(args[0]) != null) {
			if (Material.valueOf(args[1].toUpperCase()) != null) {
				int value = Integer.parseInt(args[2]);
				SellAllManager.getPlayers().get(Bukkit.getPlayer(args[0]).getUniqueId()).setSold(player,
						Material.valueOf(args[1].toUpperCase()), value);
			}
		}

	}

}
