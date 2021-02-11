package io.github.mooeypoo.timelyactions.store.items;

import java.time.LocalDateTime;

public class DatabaseItem {
	private String player;
	private String interval;
	private LocalDateTime last_run;

	public DatabaseItem(
		String player,
		String interval,
		LocalDateTime last_run
	) {
		this.player = player;
		this.interval = interval;
		this.last_run = last_run;
	}
	
	public String getPlayer() {
		return this.player;
	}
	
	public String getInterval() {
		return this.interval;
	}
	
	public LocalDateTime getLastRun() {
		return this.last_run;
	}
}
