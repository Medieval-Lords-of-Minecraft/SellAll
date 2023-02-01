package me.ShanaChans.SellAll.Commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ShanaChans.SellAll.SellAllManager;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class SellAllCap extends Subcommand {
	public SellAllCap(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("player", false), new Arg("page #", false));
		aliases = new String[] { "limit" };
	}

	@Override
	public void run(CommandSender s, String[] args) {
		Player p = (Player) s;

		if (args.length == 0) {
			SellAllManager.getPlayers().get(p.getUniqueId()).getSellCap(p, p, 0,
					SellAllManager.getPlayerSort(p));
			return;
		}
		else if (args.length == 1) {
			if (StringUtils.isNumeric(args[0])) {
				SellAllManager.getPlayers().get(p.getUniqueId()).getSellCap(p, p,
						Integer.parseInt(args[0]) - 1, SellAllManager.getPlayerSort(p));
			}
			else {
				p = Bukkit.getPlayer(args[0]);
				if (p != null) {
					SellAllManager.getPlayers().get(p.getUniqueId()).getSellCap(s,
							p, 0, SellAllManager.getPlayerSort((Player) s));
				}
				else {
					Util.msg(s, "&cThat player is not online!");
				}
			}
		}
		else if (args.length == 2) {
			p = Bukkit.getPlayer(args[0]);
			if (p != null) {
				SellAllManager.getPlayers().get(p.getUniqueId()).getSellCap(s,
						p, Integer.parseInt(args[1]) - 1,
						SellAllManager.getPlayerSort((Player) s));
			}
			else {
				Util.msg(s, "&cThat player is not online!");
			}
		}
	}
}
