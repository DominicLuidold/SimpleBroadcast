package net.simplebroadcast.util;

import java.io.IOException;
import java.util.logging.Level;

import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

import net.simplebroadcast.Main;

public class MetricsUtil {

	/**
	 * Generates data for mcstats.org.
	 */
	public void generateData() {
		try {
			Metrics metrics = new Metrics(Main.getInstance());
			Graph enabledFeatures = metrics.createGraph("Enabled Features");
			if (Main.getInstance().getConfig().getBoolean("chat.prefix.enabled")) {
				enabledFeatures.addPlotter(new Metrics.Plotter("Prefix") {
					@Override
					public int getValue() {
						return 1;
					}
				});
			}
			if (Main.getInstance().getConfig().getBoolean("chat.suffix.enabled")) {
				enabledFeatures.addPlotter(new Metrics.Plotter("Suffix") {
					@Override
					public int getValue() {
						return 1;
					}
				});
			}
			if (Main.getInstance().getConfig().getBoolean("general.updater.checkForUpdates")) {
				enabledFeatures.addPlotter(new Metrics.Plotter("Update checking") {
					@Override
					public int getValue() {
						return 1;
					}
				});
			}
			if (Main.getInstance().getConfig().getBoolean("chat.randomizeMessages")) {
				enabledFeatures.addPlotter(new Metrics.Plotter("Message randomizing") {
					@Override
					public int getValue() {
						return 1;
					}
				});
			}
			if (Main.getInstance().getConfig().getBoolean("chat.requireOnlinePlayers")) {
				enabledFeatures.addPlotter(new Metrics.Plotter("Online players required") {
					@Override
					public int getValue() {
						return 1;
					}
				});
			}
			if (Main.getInstance().getConfig().getBoolean("chat.showMessagesInConsole")) {
				enabledFeatures.addPlotter(new Metrics.Plotter("Console messages") {
					@Override
					public int getValue() {
						return 1;
					}
				});
			}
			if (Main.getInstance().getBossBarConfig().getBoolean("bossbar.enabled")) {
				enabledFeatures.addPlotter(new Metrics.Plotter("Bossbar") {
					@Override
					public int getValue() {
						return 1;
					}
				});
				if (Main.getInstance().getBossBarConfig().getBoolean("bossbar.reduceHealthBar")) {
					enabledFeatures.addPlotter(new Metrics.Plotter("Reduce health bar") {
						@Override
						public int getValue() {
							return 1;
						}
					});
				}
			}
			metrics.start();
		} catch (IOException e) {
			Main.getInstance().getLogger().log(Level.WARNING, "An error occured while generating stats for mcstats.org");
		}
	}
}