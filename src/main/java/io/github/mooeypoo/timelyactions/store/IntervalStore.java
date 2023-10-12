package io.github.mooeypoo.timelyactions.store;

import static java.util.Collections.emptySet;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import io.github.mooeypoo.timelyactions.store.items.IntervalDataItem;
import io.github.mooeypoo.timelyactions.utils.ValidityHelper;

public class IntervalStore {
	private final HashMap<String, IntervalDataItem> store = new HashMap<>();

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
			return emptySet();
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
		
		if (data == null) {
			// Interval doesn't exist
			return false;
		}

		if (player.hasPermission("timelyactions.ignore")) {
			// Player has global ignore permission, skip always
			return false;
		}
		
		if (ValidityHelper.isStringEmpty(data.getPermission())) {
			// Permission is empty (= everyone)
			return true;
		}
		return player.hasPermission(data.getPermission());
	}
}
