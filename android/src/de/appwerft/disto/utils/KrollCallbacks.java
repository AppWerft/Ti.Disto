package de.appwerft.disto.utils;

import java.util.Iterator;
import java.util.Map;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;

public class KrollCallbacks {
	KrollDict callbacks = new KrollDict();
	KrollModule module;

	/**
	 * collect all callbacks from opts
	 * */
	public KrollCallbacks(KrollModule module, KrollDict opts) {
		this.module = module;
		Iterator<?> it = opts.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry) it.next();
			if (pair.getValue() instanceof KrollFunction) {
				if (pair.getKey() instanceof String)
					callbacks.put((String) pair.getKey(),
							(KrollFunction) (pair.getValue()));
			}
			it.remove();
		}
	}

	public void call(String event, KrollDict payload) {
		if (callbacks.containsKeyAndNotNull(event)) {
			KrollFunction kf = (KrollFunction) callbacks.get(event);
			kf.call(module.getKrollObject(), payload);
		}
	}

}