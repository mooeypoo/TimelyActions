package io.github.mooeypoo.timelyactions.config.interfaces;

import java.util.HashMap;
import java.util.Map;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault.DefaultObject;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

public interface MainConfigInterface {
	static Map<String, @SubSection IntervalSectionConfigInterface> sectionsSetDefaults(IntervalSectionConfigInterface defaultGiftsSection) {
		Map<String, @SubSection IntervalSectionConfigInterface> map = new HashMap<String, @SubSection IntervalSectionConfigInterface>();
		map.put("sample_gift", defaultGiftsSection);
		return map;
	}

	@ConfKey("intervals")
	@ConfComments({"A definition of the periodic commands to run at given intervals."})
	@DefaultObject("sectionsSetDefaults")
	Map<String, @SubSection IntervalSectionConfigInterface> intervals();
}
