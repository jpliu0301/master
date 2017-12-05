/**
 * 
 */
package com.kingbase.db.console.bundle;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author duke
 *
 */
public class KBConsoleCore extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "com.kingbase.db.console.bundle";

	private static KBConsoleCore plugin;

	public KBConsoleCore() {
		super();
	}

	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static KBConsoleCore getDefault() {
		return plugin;
	}

}
