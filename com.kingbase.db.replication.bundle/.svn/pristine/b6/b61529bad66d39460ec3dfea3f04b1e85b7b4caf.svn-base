package com.kingbase.db.replication.bundle.graphical.editor;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.replication.bundle.KBReplicationCore;
/**
 * @author jpliu
 *
 */
public class FlowEditorContributor extends ActionBarContributor {

	public FlowEditorContributor() {
		super();
	}

	protected void buildActions() {
		addAction(ActionFactory.SAVE.create(this.getPage().getWorkbenchWindow()));
		RetargetAction action = new UndoRetargetAction();
		action.setText("撤销(&U)");
		action.setToolTipText("撤销(&U)");
		addRetargetAction(action);
		action = new RedoRetargetAction();
		action.setText("恢复(&R)");
		action.setToolTipText("恢复(&R)");
		addRetargetAction(action);

		addRetargetAction(new AlignmentRetargetAction(PositionConstants.LEFT));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.CENTER));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.RIGHT));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.TOP));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.MIDDLE));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.BOTTOM));

		// 网格显示
		addRetargetAction(new RetargetAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY, "", IAction.AS_CHECK_BOX)); 
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
		
//		addRetargetAction(new PriorityRetargetAction("PRIORITY_HIGH"));
//        addRetargetAction(new PriorityRetargetAction("PRIORITY_MEDIUM"));
//        addRetargetAction(new PriorityRetargetAction("PRIORITY_LOW"));
	}

//	public class PriorityRetargetAction extends LabelRetargetAction {
//		public PriorityRetargetAction(String string) {
//			super(null, null);
//			switch (string) {
//			case "PRIORITY_HIGH":
//				setId("PRIORITY_HIGH");
//				setText("High Priority");
//				break;
//			case "PRIORITY_MEDIUM":
//				setId("PRIORITY_MEDIUM");
//				setText("Medium Priority");
//				break;
//			case "PRIORITY_LOW":
//				setId("PRIORITY_LOW");
//				setText("Low Priority");
//				break;
//			default:
//				break;
//			}
//		}
//	}
	protected void declareGlobalActionKeys() {
	}

//	public void contributeToMenu(IMenuManager menuManager) {
//        IMenuManager mgr=new MenuManager("&Node","Node");
//        menuManager.insertAfter(IWorkbenchActionConstants.M_EDIT,mgr);
//        mgr.add(getAction("PRIORITY_HIGH"));
//        mgr.add(getAction("PRIORITY_MEDIUM"));
//        mgr.add(getAction("PRIORITY_LOW"));
//    }

	/**
	 * 往工具栏里面添加菜单
	 */
	public void contributeToToolBar(IToolBarManager toolBarManager) {

		IAction action = getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY);
		action.setImageDescriptor(ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.grid));
		action.setToolTipText("网格背景1");
		toolBarManager.add(action);

		toolBarManager.add(new Separator());
	    action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
	    action.setImageDescriptor(ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.undo));
	    action.setToolTipText("撤销");
		toolBarManager.add(action);
		action = getActionRegistry().getAction(ActionFactory.REDO.getId());
		action.setImageDescriptor(ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.redo));
		action.setToolTipText("恢复");
		toolBarManager.add(action);

		toolBarManager.add(new Separator());
		// 水平方向对齐按钮
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_LEFT));
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_CENTER));
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_RIGHT));

		toolBarManager.add(new Separator());
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_TOP));
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_MIDDLE));
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_BOTTOM));

		toolBarManager.add(new Separator());
		action = getAction(GEFActionConstants.ZOOM_IN);
		action.setToolTipText("放大");
		toolBarManager.add(action);
		action = getAction(GEFActionConstants.ZOOM_OUT);
		action.setToolTipText("缩小");
		toolBarManager.add(action);
		toolBarManager.add(new ZoomComboContributionItem(getPage()));

		toolBarManager.add(new Separator());

	}
}
