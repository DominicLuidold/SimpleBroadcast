package net.simplebroadcast.broadcasts;

/**
 * Different status of broadcast.
 */
public enum BroadcastStatus {
	/**
	 * Broadcast is running.
	 */
	RUNNING, 
	/**
	 * Broadcast is waiting for a player to join ("requireOnlinePlayers").
	 */
	WAITING, 
	/**
	 * Broadcast is stopped ("/sb stop" or "/sb bossbar stop").
	 */
	STOPPED, 
	/**
	 * Broadcast is disabled (configured in config file).
	 */
	DISABLED, 
	/**
	 * Broadcast is not available due to missing dependency ("BarAPI").
	 */
	NOT_AVAILABLE
}