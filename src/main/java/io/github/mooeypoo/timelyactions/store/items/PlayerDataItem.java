package io.github.mooeypoo.timelyactions.store.items;

import java.time.LocalDateTime;
import java.util.HashMap;

public class PlayerDataItem {
	private String playerName;
	
	private HashMap<String, LocalDateTime> intervalRecord = new HashMap<>();
	
	public PlayerDataItem(String playerName) {
		this.playerName = playerName;
	}
	
	public String getPlayerName() {
		return this.playerName;
	}
	
	public LocalDateTime getRecord(String intervalName) {
		return this.intervalRecord.get(intervalName);
	}
	
	public void addRecord(String intervalName, LocalDateTime timestamp) {
		this.intervalRecord.put(intervalName, timestamp);
	}
	
	public void addRecordNow(String intervalName) {
		LocalDateTime localdate = LocalDateTime.now();
		this.intervalRecord.put(intervalName, localdate);
	}
}
