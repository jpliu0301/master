package com.kingbase.db.replication.bundle.graphical.editor;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.DomainEventDispatcher;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.pentaho.di.graphical.editor.FileCreationFactory;
import org.pentaho.di.graphical.editor.FlowDomainEventDispatcher;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.graphical.factory.ReplicationEditPartFactory;
import com.kingbase.db.replication.bundle.graphical.model.ReplicationAllProcess;

public class AllSubscribeStatusEditor extends GraphicalEditor{

	public static final String ID = "com.kingbase.db.replication.application.editor.AllSubscribeStatusEditor";
	private ReplicationAllProcess allprocess;
	private DataBaseInput input;

	public AllSubscribeStatusEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		setPartName("订阅状态监控图");
		setTitleImage(ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.replication));
		this.input = (DataBaseInput) input;;
		allprocess = new ReplicationAllProcess(this.input);
	}

	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(allprocess);
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer) getGraphicalViewer();
		ScalableRootEditPart root = new ScalableRootEditPart();// 提供缩放功能
		viewer.setRootEditPart(root);
		

		ActionRegistry registry = getActionRegistry();
		ZoomManager manager = root.getZoomManager();// 实现手动选择是否显示网格背景

		// 手动添加缩放比例； 缩放比例是从 25 ％－300 ％
		double[] zoomLevels = new double[] { 0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0, 2.25, 2.5, 3.0 };
		manager.setZoomLevels(zoomLevels);
		
		IAction inAction = new ZoomInAction(manager);
		registry.registerAction(inAction);

		IAction outAction = new ZoomOutAction(manager);
		registry.registerAction(outAction);

		IAction showGrid = new ToggleGridAction(viewer);// 实现手动选择是否显示网格背景
		registry.registerAction(showGrid);

		viewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, true);
		viewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, true);
		
		viewer.setEditPartFactory(new ReplicationEditPartFactory());

	}

	public DefaultEditDomain getEditDomain() {
		return super.getEditDomain();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		getCommandStack().markSaveLocation();
	}
	
	public boolean isDirty(){
		return false;
	}

	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
			}

			protected void hookPaletteViewer(PaletteViewer viewer) {
				super.hookPaletteViewer(viewer);
			}
		};
	}

	protected void createGraphicalViewer(Composite parent) {
		GraphicalViewer viewer = new ScrollingGraphicalViewer() {
			private FlowDomainEventDispatcher eventDispatcher;

			protected DomainEventDispatcher getEventDispatcher() {
				return eventDispatcher;
			}

			public void setEditDomain(EditDomain domain) {
				super.setEditDomain(domain);
				eventDispatcher = new FlowDomainEventDispatcher(domain, this);
				getLightweightSystem().setEventDispatcher(eventDispatcher);
			}
		};
		viewer.createControl(parent);
		setGraphicalViewer(viewer);
		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();
	}

	protected GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	public Object getAdapter(Class type) {
		if (type == ZoomManager.class)
			return ((ScalableRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		return super.getAdapter(type);
	}

	protected FileCreationFactory createFactory() {
		return null;
	}

	public void dispose() {
		super.dispose();
		if (allprocess != null && allprocess.getService() != null) {
			ScheduledExecutorService service = allprocess.getService();
			service.shutdown();
			try {
				if (!service.awaitTermination(500, TimeUnit.MILLISECONDS)) {
					service.shutdownNow();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("定时器关闭成功");
		}
		System.out.println("定时器关闭成功");
	}

}
