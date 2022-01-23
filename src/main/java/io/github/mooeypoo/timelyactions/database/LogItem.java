package io.github.mooeypoo.timelyactions.database;

import java.time.LocalDateTime;

public record LogItem(String player, String interval, LocalDateTime runTime) {
}
