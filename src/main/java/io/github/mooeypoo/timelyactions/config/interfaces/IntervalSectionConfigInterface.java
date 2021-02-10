package io.github.mooeypoo.timelyactions.config.interfaces;

import java.util.Set;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;

public interface IntervalSectionConfigInterface {
	@ConfKey("every_minutes")
	@ConfComments({"An interval in real-world minutes to trigger these commands."})
	@DefaultInteger(60)
	Integer every_minutes();

	@ConfKey("message_to_user")
	@ConfComments({"Message sent to the user when the command is invoked."})
	@DefaultString("")
	String message_to_user();

	@ConfKey("commands")
	@ConfComments({"Commands to run."})
	@DefaultStrings({"give %player% minecraft:iron_ingot"})
	Set<String> commands();

	@ConfKey("permission")
	@ConfComments({"Permission the user must have to have the command invoked in the time period."})
	@DefaultString("")
	String permission();
}
