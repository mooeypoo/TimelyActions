package io.github.mooeypoo.timelyactions.store.items;

import java.util.HashSet;
import java.util.Set;

public class IntervalDataItem {
	private String name;
	
	private Integer every_minutes;
	
	private String permission;
	
	private String message_to_user;
	
	private Set<String> commands = new HashSet<String>();
	
	public IntervalDataItem(
		String name,
		Integer every_minutes,
		Set<String> commands,
		String permission,
		String message_to_user
	) {
		this.name = name;
		this.every_minutes = every_minutes;
		this.commands = commands;
		this.permission = permission;
		this.message_to_user = message_to_user;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Integer getEveryMinutes() {
		return this.every_minutes;
	}

	public Set<String> getCommands() {
		return this.commands;
	}
		
	public String getPermission() {
		return this.permission;
	}
	
	public String getMessageToUser() {
		return this.message_to_user;
	}
}
