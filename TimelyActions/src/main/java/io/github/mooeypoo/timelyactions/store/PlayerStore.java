package io.github.mooeypoo.timelyactions.store;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

import io.github.mooeypoo.timelyactions.store.items.PlayerDataItem;

public class PlayerStore {
	private HashMap<String, PlayerDataItem> store = new HashMap<String, PlayerDataItem>();
	
	public PlayerStore() {}

	public void reset() {
		// TODO: Store all data in DB
		this.store.clear();
	}
	
	public Set<String> getPlayerNames() {
		return this.store.keySet();
	}
	
	public void addPlayer(String player) {
		// look to see if the player is already in the store
		PlayerDataItem data = this.store.get(player);
		if (data == null) {
			// Player isn't in store; add
			data = new PlayerDataItem(player);
			this.store.put(player, data);
		}
	}
	
	public void removePlayer(String playerName) {
		// TODO: Store info in DB
		
		// Remove the player
		this.store.remove(playerName);
	}
	
	public Boolean doesPlayerExist(String playerName) {
		return this.getPlayerNames().contains(playerName);
	}
	
	public LocalDateTime getIntervalRecordForPlayer(String playerName, String intervalName) {
		PlayerDataItem data = this.store.get(playerName);
		if (data == null) {
			return null;
		}
		return data.getRecord(intervalName);
	}
	
	public void updateIntervalForPlayer(String playerName, String intervalName) {
		PlayerDataItem data = this.store.get(playerName);
		if (data == null) {
			return;
		}
		data.addRecordNow(intervalName);
	}

	public void setIntervalForPlayer(String playerName, String intervalName, LocalDateTime last_run) {
		PlayerDataItem data = this.store.get(playerName);
		if (data == null) {
			return;
		}
		data.addRecord(intervalName, last_run);
	}
}
