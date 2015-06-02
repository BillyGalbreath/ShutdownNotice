package net.pl3x.bukkit.shutdownnotice.task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.pl3x.bukkit.shutdownnotice.Main;
import net.pl3x.bukkit.shutdownnotice.ServerStatus;
import net.pl3x.bukkit.shutdownnotice.ServerStatus.State;
import net.pl3x.bukkit.shutdownnotice.configuration.Config;
import net.pl3x.bukkit.shutdownnotice.configuration.Lang;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Shutdown extends BukkitRunnable {
	private Main plugin;

	public Shutdown(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		ServerStatus status = plugin.getStatus();
		State state = status.getState();
		String reason = status.getReason();

		if (reason == null) {
			reason = "";
		}

		if (state.equals(State.RESTART)) {
			// create blank restart file
			try {
				new File(plugin.getDataFolder(), "restart").createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// run configured commands before shutting down
		List<String> shutdownCommands = Config.SHUTDOWN_COMMANDS.getStringList();
		for (String command : shutdownCommands) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		}

		// always perform save-all command
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "save-all");

		// always kick all players
		String action = state.equals(State.SHUTDOWN) ? Lang.SHUTTING_DOWN.get() : Lang.RESTARTING.get();
		String kickMessage = ChatColor.translateAlternateColorCodes('&', Lang.KICK_MESSAGE.get().replace("{action}", action).replace("{reason}", reason));

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.kickPlayer(kickMessage);
		}

		// finally stop the server
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
	}
}