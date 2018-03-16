/**
 * 
 */
package com.kingbase.db.replication.bundle;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author duke
 *
 */
public class KBReplicationCore extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.kingbase.db.replication.bundle";

	private static KBReplicationCore plugin;

	public KBReplicationCore() {
		super();
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static KBReplicationCore getDefault() {
		return plugin;
	}

}
