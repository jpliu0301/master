package com.kingbase.db.console.bundle.views;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;
import org.pentaho.di.viewer.CBasicTreeViewer;
import org.pentaho.di.viewer.CTableTreeLabelProvider;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.CTreeStruredContentProvider;

import com.kingbase.db.console.bundle.KBConsoleCore;
import com.kingbase.db.console.bundle.model.tree.ConsoleRoot;
import com.kingbase.db.console.bundle.model.tree.ExportOrImportEntity;
import com.kingbase.db.console.bundle.model.tree.InstanceInstallEntity;
import com.kingbase.db.console.bundle.model.tree.InstanceUnInstallEntity;
import com.kingbase.db.console.bundle.model.tree.ServiceStatusEntity;
import com.kingbase.db.console.bundle.newEditor.ExportDatainfoEditor;
import com.kingbase.db.console.bundle.newEditor.ImportDatainfoEditor;
import com.kingbase.db.console.bundle.newEditor.InstanceInstallEditor;
import com.kingbase.db.console.bundle.newEditor.InstanceUninstallEditor;
import com.kingbase.db.console.bundle.newEditor.ServerInfoEditor;
import com.kingbase.db.console.bundle.newEditor.ServiceStatusEditor;
import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.PlatformUtil;
import com.kingbase.db.core.util.UIUtils;

/**
 * 控制台工具
 * 
 * @author jpliu
 *
 */
public class ConsoleView extends ViewPart {

	public static final String ID = "com.kingbase.db.console.bundle.views.ConsoleView";
	private CBasicTreeViewer dbConsoleTree;
	private List<CTableTreeNode> list = new ArrayList<CTableTreeNode>();
	private Object object = null;

	public ConsoleView() {
		this.initWorkspace();
	}

	private void initWorkspace() {
		ConsoleRoot colonyRoot = new ConsoleRoot();
		list.add(colonyRoot);

	}

	@Override
	public void createPartControl(Composite parent) {

		dbConsoleTree = new CBasicTreeViewer(parent);
		GridData data = new GridData(GridData.FILL_BOTH);
		Tree tree = dbConsoleTree.getTree();
		tree.setLayoutData(data);

		dbConsoleTree.setLabelProvider(new LabelProvider());
		dbConsoleTree.setContentProvider(new ContentProvider());
		dbConsoleTree.setInput(list);
		
		MenuManager menuMgr = addPopupMenu();
		Menu fContextMenu = menuMgr.createContextMenu(tree);
		tree.setMenu(fContextMenu);

		dbConsoleTree.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection selectObj = ((StructuredSelection) dbConsoleTree.getSelection());
				object = selectObj.getFirstElement();
				if (object != null && object instanceof ServiceStatusEntity) {
					ServiceStatusEntity backupSet = (ServiceStatusEntity) object;

					PlatformUtil.openEditor(new DataBaseInput(backupSet, "服务状态", dbConsoleTree),
							ServiceStatusEditor.ID);
				} else if (object != null && object instanceof InstanceInstallEntity) {
					InstanceInstallEntity service = (InstanceInstallEntity) object;

					PlatformUtil.openEditor(new DataBaseInput(service, "数据加载", dbConsoleTree),
							InstanceInstallEditor.ID);
				} else if (object != null && object instanceof InstanceUnInstallEntity) {
					InstanceUnInstallEntity service = (InstanceUnInstallEntity) object;

					PlatformUtil.openEditor(new DataBaseInput(service, "实例卸载", dbConsoleTree),
							InstanceUninstallEditor.ID);
				}

			}
		});
	}

	protected MenuManager addPopupMenu() {
		final MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				if (dbConsoleTree != null) {
					StructuredSelection selectObj = ((StructuredSelection) dbConsoleTree.getSelection());
					object = selectObj.getFirstElement();
					if (object instanceof ExportOrImportEntity) {
						ExportOrImportEntity tfFolder = (ExportOrImportEntity) object;
						if (tfFolder.getConnection() == null) {
							exportAction.setEnabled(false);
							importAction.setEnabled(false);
						}else{
							exportAction.setEnabled(true);
							importAction.setEnabled(true);
						}
						manager.add(exportAction);
						manager.add(new Separator());
						manager.add(importAction);
						manager.add(new Separator());
						manager.add(serverAction);
					}
				}
			}
		});
		return menuMgr;
	}

	private Action exportAction = new Action("导出数据") {
		public void run() {
			if (object != null && object instanceof ExportOrImportEntity) {
				ExportOrImportEntity tfFolder = (ExportOrImportEntity) object;

				PlatformUtil.openEditor(new DataBaseInput(tfFolder, "导出数据", dbConsoleTree), ExportDatainfoEditor.ID);
			}
		}
	};
	private Action importAction = new Action("导入数据") {
		public void run() {
			if (object != null && object instanceof ExportOrImportEntity) {
				ExportOrImportEntity tfFolder = (ExportOrImportEntity) object;

				PlatformUtil.openEditor(new DataBaseInput(tfFolder, "导入数据", dbConsoleTree), ImportDatainfoEditor.ID);
			}
		}
	};
	private Action serverAction = new Action("服务器信息") {
		public void run() {
			if (object != null && object instanceof ExportOrImportEntity) {
				ExportOrImportEntity tfFolder = (ExportOrImportEntity) object;

				PlatformUtil.openEditor(new DataBaseInput(tfFolder, "服务器信息", dbConsoleTree), ServerInfoEditor.ID);
			}
		}
	};

	@Override
	public void setFocus() {
	}

	public void CollapseallTree() {
		this.dbConsoleTree.collapseAll();
	}

	class LabelProvider extends CTableTreeLabelProvider {
		public Image getImage(Object element) {
			if (element instanceof IResource) {
				return ImageURL.createImage(KBConsoleCore.PLUGIN_ID, ImageURL.replication);
			}
			return super.getImage(element);
		}

		public String getText(Object element) {
			if (element instanceof IResource) {
				return ((IResource) element).getName();
			}
			return super.getText(element);
		}
	}

	class ContentProvider extends CTreeStruredContentProvider {

		public Object[] getChildren(Object parentElement) {
			return super.getChildren(parentElement);
		}

		@Override
		public boolean hasChildren(Object element) {
			return super.hasChildren(element);
		}
	}

	public static Connection getConnection(String address, String port, String userName, String password,String database) {
		String sqlUrl = "jdbc:kingbase://" + address + ":" + port + "/" +database ;
		String driverName = "com.kingbase.Driver";
		String driverPath = PlatformUtil.getConfigurationFile(KBConsoleCore.PLUGIN_ID, "driver/kingbasejdbc4.jar")
				.toString();
		File file = PlatformUtil.getConfigurationFile(KBConsoleCore.PLUGIN_ID, driverPath);
		Connection connection = null;
		try {
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			ClassLoader loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
			Class driverClassName = loader != null ? loader.loadClass(driverName) : Class.forName(driverName);
			if (driverClassName != null) {
				Driver driver = (Driver) driverClassName.newInstance();
				DriverManager.registerDriver(driver);
				if (driver.acceptsURL(sqlUrl)) {
					Properties props = new Properties();
					props.put("user", userName); //$NON-NLS-1$
					props.put("password", password); //$NON-NLS-1$
					connection = driver.connect(sqlUrl, props);
				}
			}
		} catch (Exception e1) {
			return null;
		}
		return connection;
	}
}
