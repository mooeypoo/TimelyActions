package io.github.mooeypoo.timelyactions.store;

import java.time.LocalDateTime;
import java.util.HashMap;
import org.bukkit.entity.Player;

import io.github.mooeypoo.timelyactions.store.items.PlayerDataItem;

public class PlayerStore {
	private HashMap<String, PlayerDataItem> store = new HashMap<String, PlayerDataItem>();
	
	public PlayerStore() {}

	public void reset() {
		// TODO: Store all data in DB
		this.store.clear();
	}
	
	public void addPlayer(Player player) {
		// look to see if the player is already in the store
		PlayerDataItem data = this.store.get(player.getName());
		if (data == null) {
			// Player isn't in store; add
			data = new PlayerDataItem(player.getName());
			this.store.put(player.getName(), data);
		}
	}
	
	public void removePlayer(Player player) {
		// TODO: Store info in DB
		
		// Remove the player
		this.store.remove(player.getName());
	}
	
	public LocalDateTime getIntervalRecordForPlayer(Player player, String intervalName) {
		PlayerDataItem data = this.store.get(player.getName());
		if (data == null) {
			return null;
		}
		return data.getRecord(intervalName);
	}
	
	public void updateIntervalForPlayer(Player player, String intervalName) {
		PlayerDataItem data = this.store.get(player.getName());
		if (data == null) {
			return;
		}
		data.addRecordNow(intervalName);
	}
}
