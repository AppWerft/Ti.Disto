package de.appwerft.disto;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

@Kroll.proxy(creatableInModule = TidistoModule.class, propertyAccessors = {
		"then", "error" })
public class TestProxy extends KrollProxy {

	public TestProxy() {
		super();
	}

	/* JS_
	 * var test = Module.createTest();
	 * test.doIt().then(function(e){s});
	 * */
	@Kroll.method
	public TestProxy doIt(String message) {
		dispatchThen(new KrollDict());
		return this;
	}

	private void dispatchThen(KrollDict payload) {
		KrollFunction Then = (KrollFunction) getProperty("then");
		if (Then != null) {
			Object res = Then.call(getKrollObject(), payload);
		}
	}
}
