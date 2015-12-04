package net.simplebroadcast.broadcasts;

import org.bukkit.Bukkit;

import net.simplebroadcast.Main;

public class ChatBroadcast {
	
	
	/**
	 * Repeating scheduler.
	 */
	private int schedulerTask;

	/**
	 * TODO
	 */
	public void broadcast() {
		setSchedulerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				// TODO
			}
		}, 0L, Main.getInstance().getConfig().getInt("chat.delay") * 20L));
	}
	
	/**
	 * TODO
	 */
	public void randomBroadcast() {
		setSchedulerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				// TODO
			}
		}, 0L, Main.getInstance().getConfig().getInt("chat.delay") * 20L));
	}

	/**
	 * Returns scheduler task.
	 * 
	 * @return the schedulerTask
	 */
	public int getSchedulerTask() {
		return schedulerTask;
	}

	/**
	 * Sets scheduler task.
	 * 
	 * @param schedulerTask the schedulerTask to set
	 */
	public void setSchedulerTask(int schedulerTask) {
		this.schedulerTask = schedulerTask;
	}
}