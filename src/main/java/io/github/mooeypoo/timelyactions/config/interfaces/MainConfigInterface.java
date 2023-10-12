package io.github.mooeypoo.timelyactions.config.interfaces;

import java.util.HashMap;
import java.util.Map;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultObject;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

public interface MainConfigInterface {
	static Map<String, @SubSection IntervalSectionConfigInterface> sectionsSetDefaults(IntervalSectionConfigInterface defaultGiftsSection) {
		Map<String, @SubSection IntervalSectionConfigInterface> map = new HashMap<>();
		map.put("sample_gift", defaultGiftsSection);
		return map;
	}

	@ConfKey("intervals")
	@ConfComments({"A definition of the periodic commands to run at given intervals."})
	@DefaultObject("sectionsSetDefaults")
	Map<String, @SubSection IntervalSectionConfigInterface> intervals();

	@ConfKey("log_everything")
	@ConfComments({"If set to true, log messages will be more verbose."})
	@DefaultBoolean(false)
	Boolean log_everything();
}
