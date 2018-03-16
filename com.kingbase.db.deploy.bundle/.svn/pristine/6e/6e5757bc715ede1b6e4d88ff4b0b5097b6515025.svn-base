/**
 * 
 */
package com.kingbase.db.deploy.bundle.graphical.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.pentaho.di.graphical.model.AbstractActivityNode;

import com.kingbase.db.deploy.bundle.graphical.action.CheckLogAction;
import com.kingbase.db.deploy.bundle.graphical.action.CommonMasterAction;
import com.kingbase.db.deploy.bundle.graphical.action.UpdateConfAction;
import com.kingbase.db.deploy.bundle.graphical.model.DeploySourceNode;
import com.kingbase.db.deploy.bundle.graphical.model.DeployTargetNode;
import com.kingbase.db.deploy.bundle.graphical.parts.DeployContetntsPart;
import com.kingbase.db.deploy.bundle.model.tree.CNodeEntity;

/**
 * @author jpliu
 *
 */
public class MasterMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;
	private CreateMasterStatusEditor editor;
	private CNodeEntity entity;

	public MasterMenuProvider(EditPartViewer viewer, ActionRegistry registry, CreateMasterStatusEditor editor) {
		super(viewer);
		if (registry == null) {
			throw new IllegalArgumentException();
		}
		actionRegistry = registry;
		this.editor = editor;
	}

	protected Object getSelection() {
		ISelection iSelection = editor.getSite().getSelectionProvider().getSelection();
		if (!(iSelection instanceof StructuredSelection))
			return null;
		StructuredSelection structuredSelection = (StructuredSelection) iSelection;
		Object obj = structuredSelection.getFirstElement();
		return obj;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);
		Object obj = getSelection();

		if (obj instanceof DeployContetntsPart) {
			DeployContetntsPart part = (DeployContetntsPart) obj;
			AbstractActivityNode node = (AbstractActivityNode) part.getModel();
			if (node instanceof DeploySourceNode) {
				entity = ((DeploySourceNode) node).getNode();
			} else if (node instanceof DeployTargetNode) {
				entity = ((DeployTargetNode) node).getNode();
			}
			CommonMasterAction DBStart = new CommonMasterAction(editor.getEditDomain().getEditorPart(), entity, "DB_start", editor);
			actionRegistry.registerAction(DBStart);

			CommonMasterAction DBDisable = new CommonMasterAction(editor.getEditDomain().getEditorPart(), entity, "DB_disable", editor);
			actionRegistry.registerAction(DBDisable);

			CommonMasterAction DBRestart = new CommonMasterAction(editor.getEditDomain().getEditorPart(), entity, "DB_restart", editor);
			actionRegistry.registerAction(DBRestart);

			CommonMasterAction clusterStart = new CommonMasterAction(editor.getEditDomain().getEditorPart(), entity, "cluster_start", editor);
			actionRegistry.registerAction(clusterStart);

			CommonMasterAction clusterDisable = new CommonMasterAction(editor.getEditDomain().getEditorPart(), entity, "cluster_disable", editor);
			actionRegistry.registerAction(clusterDisable);

			CommonMasterAction clusterRestart = new CommonMasterAction(editor.getEditDomain().getEditorPart(), entity, "cluster_restart", editor);
			actionRegistry.registerAction(clusterRestart);

			CommonMasterAction allstart = new CommonMasterAction(editor.getEditDomain().getEditorPart(), entity, "all_start", editor);
			actionRegistry.registerAction(allstart);

			CommonMasterAction allDisable = new CommonMasterAction(editor.getEditDomain().getEditorPart(), entity, "all_disable", editor);
			actionRegistry.registerAction(allDisable);

			CommonMasterAction allRestart = new CommonMasterAction(editor.getEditDomain().getEditorPart(), entity, "all_restart", editor);
			actionRegistry.registerAction(allRestart);

			UpdateConfAction updateConf1 = new UpdateConfAction(editor.getEditDomain().getEditorPart(), entity, "kingbase.conf", editor);
			actionRegistry.registerAction(updateConf1);
			UpdateConfAction updateConf2 = new UpdateConfAction(editor.getEditDomain().getEditorPart(), entity, "kingbasecluster.conf", editor);
			actionRegistry.registerAction(updateConf2);
			
			CheckLogAction checklog1 = new CheckLogAction(editor.getEditDomain().getEditorPart(), entity, "failover.log", editor);
			actionRegistry.registerAction(checklog1);
			CheckLogAction checklog2 = new CheckLogAction(editor.getEditDomain().getEditorPart(), entity, "recovery.log", editor);
			actionRegistry.registerAction(checklog2);
			CheckLogAction checklog3 = new CheckLogAction(editor.getEditDomain().getEditorPart(), entity, "pool_restart.log", editor);
			actionRegistry.registerAction(checklog3);

			if (entity.getLibrary().contains("DB")) {

//				if (!editor.getContainerModel().getService().isShutdown()) {//在一键停止后，即停止了定时刷新，所以DB 不应该出现右键
					
					MenuManager DBMenu = new MenuManager("DB");
					DBMenu.add(DBStart);
					DBMenu.add(DBDisable);
					DBMenu.add(DBRestart);
					menu.add(DBMenu);
//				}
				
				MenuManager allMenu = new MenuManager("集群");
				allMenu.add(allstart);
				allMenu.add(allDisable);
				allMenu.add(allRestart);
				menu.add(allMenu);
				
				MenuManager logMenu = new MenuManager("日志查看");
				logMenu.add(checklog1);
				logMenu.add(checklog2);
				logMenu.add(checklog3);
				menu.add(logMenu);
				
				MenuManager confMenu = new MenuManager("配置下发");
				confMenu.add(updateConf1);
				menu.add(confMenu);
			} else if (entity.getLibrary().contains("cluster")) {
				MenuManager logMenu = new MenuManager("日志查看");
				logMenu.add(checklog1);
				logMenu.add(checklog2);
				logMenu.add(checklog3);
				menu.add(logMenu);
				
				MenuManager confMenu = new MenuManager("配置下发");
				confMenu.add(updateConf2);
				menu.add(confMenu);
			}

		}
	}
}
