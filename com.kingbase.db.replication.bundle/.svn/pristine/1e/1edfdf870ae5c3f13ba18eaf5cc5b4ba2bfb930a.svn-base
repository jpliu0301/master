/**
 * 
 */
package com.kingbase.db.replication.bundle.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;

import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;
import com.kingbase.db.replication.bundle.views.ReplicationView;

/**
 * @author Duke
 *
 */
public class NavigaterActionGroup extends ActionGroup {

	private ReplicationView view;

	public NavigaterActionGroup(ReplicationView view) {
		this.view = view;
	}

	public void fillActionBars(IActionBars paramIActionBars) {
		IToolBarManager localIToolBarManager = paramIActionBars.getToolBarManager();
		localIToolBarManager.add(new CollapseallDBConnection());
		localIToolBarManager.add(new Separator());
		//localIToolBarManager.add(new CreateDBConnection());
	}

	class CollapseallDBConnection extends Action {
		public CollapseallDBConnection() {
			super(Messages.NavigaterActionGroup_Collapse_All, IAction.AS_UNSPECIFIED);
			setToolTipText(Messages.NavigaterActionGroup_Collapse_All); //$NON-NLS-1$
			this.setImageDescriptor(ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.collapseall));
		}

		public void run() {
			view.CollapseallTree();
		}
	}
}
