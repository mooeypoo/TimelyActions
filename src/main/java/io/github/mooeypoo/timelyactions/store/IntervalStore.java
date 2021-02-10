package io.github.mooeypoo.timelyactions.store;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import io.github.mooeypoo.timelyactions.store.items.IntervalDataItem;

public class IntervalStore {
	private HashMap<String, IntervalDataItem> store = new HashMap<String, IntervalDataItem>();

	public IntervalStore() {}
	
	public void reset() {
		this.store.clear();
	}
	
	public Set<String> getIntervalNames() {
		return this.store.keySet();
	}
	
	public void addInterval(
		String name, Integer every_minutes, Set<String> commands,
		String permission,
		String message_to_user
	) {
		IntervalDataItem data = this.store.get(name);
		if (data == null) {
			// Interval isn't in store; add
			data = new IntervalDataItem(name, every_minutes, commands, permission, message_to_user);
			this.store.put(name, data);
		}
	}
	
	public Set<String> getIntervalCommands(String name) {
		IntervalDataItem data = this.store.get(name);
		if (data == null) {
			return new HashSet<String>();
		}

		return data.getCommands();
	}
	
	public Integer getIntervalMinutes(String name) {
		IntervalDataItem data = this.store.get(name);
		if (data == null) {
			return null;
		}
		
		return data.getEveryMinutes();
	}
	
	public String getIntervalUserMessage(String name) {
		IntervalDataItem data = this.store.get(name);
		if (data == null) {
			return null;
		}
		
		return data.getMessageToUser();
	}
	
	public Boolean doesPlayerHaveIntervalPermission(Player player, String name) {
		IntervalDataItem data = this.store.get(name);
		if (data != null) {
			return player.hasPermission(data.getPermission());
		}
		return true;
	}
}
