package de.appwerft.disto;

/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */

import java.io.IOException;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiSensorHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

// This proxy can be created by calling IconView.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = TidistoModule.class)
public class ViewProxy extends TiViewProxy {

	TiUIView view;
	private static final String LCAT = TidistoModule.LCAT;

	private Bitmap bitmap;
	private ImageView IconView;
	private String name;

	private class IconView extends TiUIView {
		public IconView(final TiViewProxy proxy) {
			super(proxy);
			LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			LinearLayout container = new LinearLayout(proxy.getActivity());
			container.setLayoutParams(lp);
			IconView = new ImageView(proxy.getActivity());
			IconView.setImageBitmap(bitmap);
			container.addView(IconView);
			setNativeView(container);
		}
	}

	@Override
	public TiUIView createView(Activity activity) {
		view = new IconView(this);
		view.getLayoutParams().autoFillsHeight = true;
		view.getLayoutParams().autoFillsWidth = true;
		return view;
	}

	private Bitmap loadImageFromApplication(String imageName) {
		Bitmap bitmap = null;
		String url = null;
		try {
			url = resolveUrl(null, imageName);
			TiBaseFile file = TiFileFactory.createTitaniumFile(
					new String[] { url }, false);
			bitmap = TiUIHelper.createBitmap(file.getInputStream());
		} catch (IOException e) {
			Log.e(LCAT, " WheelView only supports local image files " + url);
		}
		return bitmap;
	}

	// Constructor
	public ViewProxy() {
		super();
	}

	public ViewProxy(String name) {
		super();
		this.name = name;
	}

	// Handle creation options
	@Override
	public void handleCreationDict(KrollDict opts) {
		super.handleCreationDict(opts);

	}

}
