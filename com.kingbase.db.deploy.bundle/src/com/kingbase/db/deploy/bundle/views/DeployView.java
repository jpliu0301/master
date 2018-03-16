package com.kingbase.db.deploy.bundle.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.pentaho.di.model.IContainerModel;
import org.pentaho.di.viewer.CBasicTreeViewer;
import org.pentaho.di.viewer.CTableTreeLabelProvider;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.CTreeStruredContentProvider;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.PlatformUtil;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.deploy.bundle.KBDeployCore;
import com.kingbase.db.deploy.bundle.graphical.editor.CreateMasterStatusEditor;
import com.kingbase.db.deploy.bundle.graphical.editor.CreateReadWriteStatusEditor;
import com.kingbase.db.deploy.bundle.model.tree.CNodeEntity;
import com.kingbase.db.deploy.bundle.model.tree.CReadWriteEntity;
import com.kingbase.db.deploy.bundle.model.tree.DeployRoot;
import com.kingbase.db.deploy.bundle.model.tree.MasterStandbyEntity;
import com.kingbase.db.deploy.bundle.model.tree.NodeEntity;
import com.kingbase.db.deploy.bundle.model.tree.ReadWriteEntity;

/**
 * 集群管理
 * 
 * @author feng
 *
 */
public class DeployView extends ViewPart {

	public static final String ID = "com.kingbase.db.tools.application.view.DeployView";
	private CBasicTreeViewer dbDeployTree;
	private List<CTableTreeNode> list = new ArrayList<CTableTreeNode>();
	private Object object = null;

	public DeployView() {
		this.initWorkspace();
	}

	private void initWorkspace() {
		IProject proejct = PlatformUtil.getColonyProject("Colony");
		DeployRoot colonyRoot = new DeployRoot(proejct);
		list.add(colonyRoot);

	}

	@Override
	public void createPartControl(Composite parent) {

		dbDeployTree = new CBasicTreeViewer(parent);
		GridData data = new GridData(GridData.FILL_BOTH);
		Tree tree = dbDeployTree.getTree();
		tree.setLayoutData(data);

		dbDeployTree.setLabelProvider(new LabelProvider());
		dbDeployTree.setContentProvider(new ContentProvider());
		dbDeployTree.setInput(list);

		// contributeToActionBars();
		MenuManager menuMgr = addPopupMenu();
		Menu fContextMenu = menuMgr.createContextMenu(tree);
		tree.setMenu(fContextMenu);

		dbDeployTree.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection selectObj = ((StructuredSelection) dbDeployTree.getSelection());
				object = selectObj.getFirstElement();
				if (object != null && object instanceof CReadWriteEntity) {
					CReadWriteEntity dataInfo = (CReadWriteEntity) object;
					String parentName = dataInfo.getParentModel().getName();
					if (parentName.equals("读写分离")) {

						PlatformUtil.openEditor(new DataBaseInput(dataInfo, dataInfo.getName(), dbDeployTree),
								CreateReadWriteStatusEditor.ID);
					} else if (parentName.equals("主备同步")) {

						PlatformUtil.openEditor(new DataBaseInput(dataInfo, dataInfo.getName(), dbDeployTree),
								CreateMasterStatusEditor.ID);
					}
				}else if(object != null && object instanceof CNodeEntity){
					CNodeEntity dataInfo = (CNodeEntity) object;
					
					IWorkbenchPage PAGE = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						IEditorPart editor = PAGE.findEditor(new DataBaseInput(dataInfo, "修改节点"+" "+dataInfo.getName(), dbDeployTree));
						if (editor != null) {
							PAGE.bringToTop(editor);
						} else {
							PAGE.openEditor(new DataBaseInput(dataInfo, "修改节点"+" "+dataInfo.getName(), dbDeployTree),
									"com.kingbase.db.replication.application.editor.CreateNodeEditor");
						}
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				
				}
			}
		});
	}

	/**
	 * 右键菜单
	 * 
	 * @return
	 */
	protected MenuManager addPopupMenu() {
		final MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				if (dbDeployTree != null) {
					StructuredSelection selectObj = ((StructuredSelection) dbDeployTree.getSelection());
					object = selectObj.getFirstElement();
					if (object instanceof DeployRoot) {
						manager.add(actionRefresh);
					} else if (object instanceof NodeEntity) {
						manager.add(createNodeAction);
						manager.add(new Separator());
						manager.add(actionRefresh);
					} else if (object instanceof CNodeEntity) {
						manager.add(updateNodeAction);
						manager.add(new Separator());
						manager.add(deleteNodeAction);
					} else if (object instanceof MasterStandbyEntity) {
						manager.add(createMasterAction);
						manager.add(new Separator());
						manager.add(actionRefresh);
					} else if (object instanceof ReadWriteEntity) {
						manager.add(createReadWriteAction);
						manager.add(new Separator());
						manager.add(actionRefresh);
					} else if (object instanceof CReadWriteEntity) {
						IContainerModel parentModel = ((CReadWriteEntity) object).getParentModel();

						if (parentModel instanceof MasterStandbyEntity) {
							
							manager.add(deleteMasterAction);
							manager.add(new Separator());
							manager.add(updateMasterAction);
						} else {

							manager.add(deleteReadAction);
							manager.add(new Separator());
							manager.add(updateReadAction);
						}
					}
				}
			}
		});
		return menuMgr;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	class LabelProvider extends CTableTreeLabelProvider {
		public Image getImage(Object element) {
			if (element instanceof IResource) {
				return ImageURL.createImage(KBDeployCore.PLUGIN_ID, ImageURL.replication);
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
	}

	private Action actionRefresh = new Action("刷新",
			ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_refresh)) {
		public void run() {
			if (object != null && object instanceof DeployRoot) {
				DeployRoot tRoot = (DeployRoot) object;
				CollapseallTree();
				tRoot.refresh();
				dbDeployTree.expandToLevel(2);
			} else if (object != null && object instanceof NodeEntity) {
				NodeEntity tfFolder = (NodeEntity) object;
				CollapseallTree();
				tfFolder.refresh();
				dbDeployTree.expandToLevel(2);
			} else if (object != null && object instanceof MasterStandbyEntity) {
				MasterStandbyEntity sourceMeta = (MasterStandbyEntity) object;
				CollapseallTree();
				sourceMeta.refresh();
				dbDeployTree.expandToLevel(2);
			} else if (object != null && object instanceof ReadWriteEntity) {
				ReadWriteEntity sourceMeta = (ReadWriteEntity) object;
				CollapseallTree();
				sourceMeta.refresh();
				dbDeployTree.expandToLevel(2);
			}
		}
	};

	public void CollapseallTree() {
		this.dbDeployTree.collapseAll();
	}

	private Action createNodeAction = new Action("新建节点",
			ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_add)) {
		public void run() {
			if (object != null && object instanceof NodeEntity) {
				NodeEntity tfFolder = (NodeEntity) object;
				IWorkbenchPage PAGE = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IEditorPart editor = PAGE.findEditor(new DataBaseInput(tfFolder, "新建节点", dbDeployTree));
					if (editor != null) {
						PAGE.bringToTop(editor);
					} else {
						PAGE.openEditor(new DataBaseInput(tfFolder, "新建节点", dbDeployTree),
								"com.kingbase.db.replication.application.editor.CreateNodeEditor");
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private Action updateNodeAction = new Action("修改节点",
			ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_update)) {
		public void run() {
			if (object != null && object instanceof CNodeEntity) {
				CNodeEntity source = (CNodeEntity) object;

				IWorkbenchPage PAGE = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IEditorPart editor = PAGE.findEditor(new DataBaseInput(source, "修改节点"+" "+source.getName(), dbDeployTree));
					if (editor != null) {
						PAGE.bringToTop(editor);
					} else {
						PAGE.openEditor(new DataBaseInput(source, "修改节点"+" "+source.getName(), dbDeployTree),
								"com.kingbase.db.replication.application.editor.CreateNodeEditor");
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private Action deleteNodeAction = new Action("删除节点",
			ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_delete)) {
		public void run() {
			if (object != null && object instanceof CNodeEntity) {

				CNodeEntity sourceMeta = (CNodeEntity) object;
				NodeEntity parentMeta = (NodeEntity) sourceMeta.getParentModel();
				boolean openConfirm = MessageDialog.openConfirm(UIUtils.getActiveShell(), "提示", "是否要删除这个节点?");
				if (openConfirm) {
					parentMeta.removeChild(sourceMeta);
					dbDeployTree.refresh();
					DeployRoot rootFolder = (DeployRoot) parentMeta.getParentModel();
					IFolder folder = ((NodeEntity) (rootFolder.getChildren())[0]).getFolder();
					IFile file = (IFile) folder.findMember("cnode.xml");
					deleteXmlNode(file, sourceMeta.getName(), "name");
					
					PlatformUtil.closeEditor(new DataBaseInput(sourceMeta, "修改节点"+" "+sourceMeta.getName(), dbDeployTree),true);

				}

			}
		}
	};

	private Action createMasterAction = new Action("新建主备同步",
			ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_add)) {
		public void run() {
			if (object != null && object instanceof MasterStandbyEntity) {
				MasterStandbyEntity tfFolder = (MasterStandbyEntity) object;
				IWorkbenchPage PAGE = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IEditorPart editor = PAGE.findEditor(new DataBaseInput(tfFolder, "新建主备同步", dbDeployTree));
					if (editor != null) {
						PAGE.bringToTop(editor);
					} else {
						PAGE.openEditor(new DataBaseInput(tfFolder, "新建主备同步", dbDeployTree),
								"com.kingbase.db.deploy.bundle.editor.CreateMasterstandEditor");
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
	};
	private Action deleteMasterAction = new Action("删除主备同步",
			ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_delete)) {
		public void run() {
			if (object != null && object instanceof CReadWriteEntity) {

				CReadWriteEntity sourceMeta = (CReadWriteEntity) object;
				MasterStandbyEntity parentMeta = (MasterStandbyEntity) sourceMeta.getParentModel();
				boolean openConfirm = MessageDialog.openConfirm(UIUtils.getActiveShell(), "提示", "是否要删除这个主备同步?");
				if (openConfirm) {
					parentMeta.removeChild(sourceMeta);
					dbDeployTree.refresh();
					DeployRoot rootFolder = (DeployRoot) parentMeta.getParentModel();
					IFolder folder = ((MasterStandbyEntity) (rootFolder.getChildren())[1]).getFolder();
					IFile file = (IFile) folder.findMember("master.xml");
					deleteXmlNode(file, sourceMeta.getName(), "name");
					
					PlatformUtil.closeEditor(new DataBaseInput(sourceMeta, "属性", dbDeployTree),true);
					PlatformUtil.closeEditor(new DataBaseInput(sourceMeta, sourceMeta.getName(), dbDeployTree),true);
				}

			}
		}
	};
	private Action createReadWriteAction = new Action("新建读写分离",
			ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_add)) {
		public void run() {
			if (object != null && object instanceof ReadWriteEntity) {
				ReadWriteEntity tfFolder = (ReadWriteEntity) object;
				IWorkbenchPage PAGE = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IEditorPart editor = PAGE.findEditor(new DataBaseInput(tfFolder, "新建读写分离", dbDeployTree));
					if (editor != null) {
						PAGE.bringToTop(editor);
					} else {
						PAGE.openEditor(new DataBaseInput(tfFolder, "新建读写分离", dbDeployTree),
								"com.kingbase.db.deploy.bundle.editor.ReadWriteEditor");
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
	};
	private Action deleteReadAction = new Action("删除读写分离",
			ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_delete)) {
		public void run() {
			if (object != null && object instanceof CReadWriteEntity) {

				CReadWriteEntity sourceMeta = (CReadWriteEntity) object;
				ReadWriteEntity parentMeta = (ReadWriteEntity) sourceMeta.getParentModel();
				boolean openConfirm = MessageDialog.openConfirm(UIUtils.getActiveShell(), "提示", "是否要删除这个读写分离?");
				if (openConfirm) {
					parentMeta.removeChild(sourceMeta);
					dbDeployTree.refresh();
					DeployRoot rootFolder = (DeployRoot) parentMeta.getParentModel();
					IFolder folder = ((ReadWriteEntity) (rootFolder.getChildren())[2]).getFolder();
					IFile file = (IFile) folder.findMember("read.xml");
					deleteXmlNode(file, sourceMeta.getName(), "name");
					
					PlatformUtil.closeEditor(new DataBaseInput(sourceMeta, "属性", dbDeployTree),true);
					PlatformUtil.closeEditor(new DataBaseInput(sourceMeta, sourceMeta.getName(), dbDeployTree),true);
				}

			}
		}
	};
	private Action updateReadAction = new Action("属性",
			ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_update)) {
		public void run() {
			if (object != null && object instanceof CReadWriteEntity) {

				CReadWriteEntity source = (CReadWriteEntity) object;

				IWorkbenchPage PAGE = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IEditorPart editor = PAGE.findEditor(new DataBaseInput(source, "属性", dbDeployTree));
					if (editor != null) {
						PAGE.bringToTop(editor);
					} else {
						PAGE.openEditor(new DataBaseInput(source, "属性", dbDeployTree),
								"com.kingbase.db.deploy.bundle.editor.ReadWriteEditor");
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}


		}
	};
	private Action updateMasterAction = new Action("属性",
			ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_update)) {
		public void run() {
			if (object != null && object instanceof CReadWriteEntity) {
				
				CReadWriteEntity source = (CReadWriteEntity) object;
				
				IWorkbenchPage PAGE = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IEditorPart editor = PAGE.findEditor(new DataBaseInput(source, "属性", dbDeployTree));
					if (editor != null) {
						PAGE.bringToTop(editor);
					} else {
						PAGE.openEditor(new DataBaseInput(source, "属性", dbDeployTree),
								"com.kingbase.db.deploy.bundle.editor.CreateMasterstandEditor");
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
			
			
		}
	};


	/**
	 * 删除xml节点
	 */
	private void deleteXmlNode(IFile file, String dbName, String eleName) {
		SAXReader reader = new SAXReader();
		List<Element> listEle = null;
		Document document = null;
		try {
			document = reader.read(
					new BufferedReader(new InputStreamReader(new FileInputStream(file.getLocation().toFile()), "utf-8")));
			Element root = document.getRootElement();
			listEle = root.elements();
			if (listEle == null || listEle.size() == 0) {
				return;
			}
			for (Element element : listEle) {
				if (element.element(eleName).getStringValue().equals(dbName)) {
					root.elements().remove(element);
				}
			}
			try {
				OutputFormat xmlFormat = UIUtils.xmlFormat();
				File fileLocal = file.getLocation().toFile();
				XMLWriter output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
				output.write(document);
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	public static Connection getConnection(CNodeEntity node) {
		File file = PlatformUtil.getConfigurationFile(KBDeployCore.PLUGIN_ID, "driver/kingbase8-8.2.0.jar");
		Connection connection = null;
		try {
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			String driverName = "com.kingbase8.Driver";
			String sqlUrl = "jdbc:kingbase8://" + node.getIp() + ":" + node.getListenerPost() + "/"+UIUtils.getDatabase();
			String userName = node.getUser();
			ClassLoader loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
			Class driverClassName = loader != null ? loader.loadClass(driverName) : Class.forName(driverName);
			if (driverClassName != null) {
				Driver driver = (Driver) driverClassName.newInstance();
				DriverManager.registerDriver(driver);
				if (driver.acceptsURL(sqlUrl)) {
					Properties props = new Properties();
					props.put("user", userName); //$NON-NLS-1$
					props.put("password", ""); //$NON-NLS-1$
					props.put("preferQueryMode", "simple");
					connection = driver.connect(sqlUrl, props);
				}
			}
		} catch (MalformedURLException e1) {
			e1.getMessage();
			return null;
		} catch (ClassNotFoundException e1) {
			e1.getMessage();
			return null;
		} catch (InstantiationException e1) {
			e1.getMessage();
			return null;
		} catch (IllegalAccessException e1) {
			e1.getMessage();
			return null;
		} catch (SQLException e1) {
			e1.getMessage();
			return null;
		}
		return connection;
	}

}
