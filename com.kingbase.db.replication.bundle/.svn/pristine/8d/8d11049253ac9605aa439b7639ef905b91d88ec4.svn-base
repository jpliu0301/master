package com.kingbase.db.replication.bundle.views;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IProject;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;
import org.pentaho.di.viewer.CBasicTreeViewer;
import org.pentaho.di.viewer.CTableTreeLabelProvider;
import org.pentaho.di.viewer.CTreeStruredContentProvider;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.PlatformUtil;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.action.NavigaterActionGroup;
import com.kingbase.db.replication.bundle.dialog.CreateReleaseEditor;
import com.kingbase.db.replication.bundle.dialog.CreateSourceConnectionEditor;
import com.kingbase.db.replication.bundle.dialog.CreateSubscribeEditor;
import com.kingbase.db.replication.bundle.dialog.DDLdialog;
import com.kingbase.db.replication.bundle.dialog.ThisSubscribeDialog;
import com.kingbase.db.replication.bundle.graphical.editor.AllSubscribeStatusEditor;
import com.kingbase.db.replication.bundle.graphical.editor.CreateSubscribeStatusEditor;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataBase;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataInfo;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataSource;
import com.kingbase.db.replication.bundle.model.tree.ReplicationFile;
import com.kingbase.db.replication.bundle.model.tree.ReplicationRoot;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataBase;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataInfo;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataSource;
import com.kingbase.db.replication.bundle.model.tree.SubscribeTable;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;

/**
 * @author jpliu
 *
 */
public class ReplicationView extends ViewPart {

	public static final String ID = "com.kingbase.db.replication.application.view.ReplicationView"; //$NON-NLS-1$
	private CBasicTreeViewer dbReplicationTree;
	private List<ReplicationRoot> list = new ArrayList<ReplicationRoot>();
	private Object object = null;
	private NavigaterActionGroup actionGroup = null;
	
	public static final Image subscriber_enable = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.status_subscriber_enable);
	public static final Image publish = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.status_publish);
	public static final Image subscriber_disable = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.status_subscriber_disable);
	public static final Color lightGreen = ColorConstants.lightGreen;
	public static final Color red = ColorConstants.red;

	public ReplicationView() {
		this.initWorkspace();
	}

	private void initWorkspace() {
		IProject proejct = PlatformUtil.getProject("General"); //$NON-NLS-1$
		ReplicationRoot transferRoot = new ReplicationRoot(proejct);
		list.add(transferRoot);
	}

	@Override
	public void createPartControl(Composite parent) {

//		Composite container = new Composite(parent, SWT.NONE);
//		GridLayout layout1 = new GridLayout(1, false);
//		layout1.marginTop = 1;
//		layout1.marginLeft = 1;
//		layout1.marginRight = 1;
//		container.setLayout(layout1);
//		GridData data = new GridData(GridData.FILL_BOTH);
//		container.setLayoutData(data);

		dbReplicationTree = new CBasicTreeViewer(parent);
		GridData data = new GridData(GridData.FILL_BOTH);
		Tree tree = dbReplicationTree.getTree();
		tree.setLayoutData(data);

		dbReplicationTree.setLabelProvider(new LabelProvider());
		dbReplicationTree.setContentProvider(new ContentProvider());
		dbReplicationTree.setInput(list);

		contributeToActionBars();
		MenuManager menuMgr = addPopupMenu();
		Menu fContextMenu = menuMgr.createContextMenu(tree);
		tree.setMenu(fContextMenu);

		dbReplicationTree.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection selectObj = ((StructuredSelection) dbReplicationTree.getSelection());
				object = selectObj.getFirstElement();
				if (object != null && object instanceof SubscribeDataInfo) {
					SubscribeDataInfo dataInfo = (SubscribeDataInfo) object;

					List<ReleaseDataInfo> list = getSubscribeToRelease(dataInfo);
					dataInfo.setReleaseList(list);
					PlatformUtil.openEditor(new DataBaseInput(dataInfo, dataInfo.getSubscribeName(), dbReplicationTree),
							CreateSubscribeStatusEditor.ID);
				} else if (object != null && object instanceof ReplicationRoot) {
					ReplicationRoot dataInfo = (ReplicationRoot) object;

					PlatformUtil.openEditor(new DataBaseInput(dataInfo,
							Messages.ReplicationView_Subscription_status_diagram, dbReplicationTree),
							AllSubscribeStatusEditor.ID);
				}
			}
		});

	}

	protected MenuManager addPopupMenu() {
		final MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				if (dbReplicationTree != null) {
					StructuredSelection selectObj = ((StructuredSelection) dbReplicationTree.getSelection());
					object = selectObj.getFirstElement();
					if (object instanceof ReplicationRoot) {
						manager.add(actionRefresh);
					} else if (object instanceof ReplicationFile) {
						ReplicationFile tfFolder = (ReplicationFile) object;
						if (tfFolder.isRelease()) {
							manager.add(createReleaseSource);
							manager.add(new Separator());
							manager.add(actionRefresh);
						} else {
							manager.add(createSubscribeSource);
							manager.add(new Separator());
							manager.add(actionRefresh);
						}
					} else if (object instanceof ReleaseDataSource) {
						manager.add(updateReleaseServers);
						manager.add(new Separator());
						manager.add(deleteServers);
						manager.add(new Separator());
						manager.add(actionRefresh);
					} else if (object instanceof ReleaseDataBase) {
						ReleaseDataBase database = (ReleaseDataBase) object;
						if(database.isExistTable()){
							
							manager.add(createRelease);
							manager.add(new Separator());
							manager.add(syncDDL);
							manager.add(new Separator());
							manager.add(actionRefresh);
						}else{
							manager.add(syncEnable);
						}
					} else if (object instanceof ReleaseDataInfo) {
						manager.add(updateRelease);
						manager.add(new Separator());
						manager.add(deleteRelease);
						manager.add(new Separator());
						manager.add(actionRefresh);
					} else if (object instanceof SubscribeDataSource) {

						manager.add(updateSubscribeSource);
						manager.add(new Separator());
						manager.add(deleteServers);
						manager.add(new Separator());
						manager.add(actionRefresh);
					} else if (object instanceof SubscribeDataBase) {
						SubscribeDataBase database = (SubscribeDataBase) object;
						if(database.isExistTable()){
							
							manager.add(createSubscribe);
							manager.add(new Separator());
							manager.add(actionRefresh);
						}else{
							manager.add(syncEnable);
						}
					} else if (object instanceof SubscribeDataInfo) {
						SubscribeDataInfo dataInfo = (SubscribeDataInfo) object;
						manager.add(updateSubscribe);
						if (dataInfo.getSubscribeEnable() != null && dataInfo.getSubscribeEnable().equals("t")) { //$NON-NLS-1$
							manager.add(new Separator());
							manager.add(syncSubscribe);
							manager.add(new Separator());
							manager.add(disableSubscribe);
						} else if (dataInfo.getSubscribeEnable() != null && dataInfo.getSubscribeEnable().equals("f")) { //$NON-NLS-1$
							manager.add(new Separator());
							manager.add(deleteSubscribe);
							manager.add(new Separator());
							manager.add(enableSubscribe);
						}
						manager.add(new Separator());
						manager.add(actionRefresh);
					} else if (object instanceof SubscribeTable) {
						manager.add(new Separator());
						manager.add(syncTableSubscribe);
					}
				}
			}
		});
		return menuMgr;
	}

	private void contributeToActionBars() {
		this.actionGroup = new NavigaterActionGroup(this);
		MenuManager localMenuManager = new MenuManager();
		localMenuManager.setRemoveAllWhenShown(true);
		actionGroup.fillActionBars(getViewSite().getActionBars());
	}

	private Action deleteServers = new Action(Messages.ReplicationView_delete_server,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_delete)) {
		public void run() {
			if (object != null && object instanceof SubscribeDataSource) {

				SubscribeDataSource sourceMeta = (SubscribeDataSource) object;
				ReplicationFile parentMeta = (ReplicationFile) sourceMeta.getParentModel();
				boolean openConfirm = MessageDialog.openConfirm(UIUtils.getActiveShell(),
						Messages.ReplicationView_Prompt, Messages.ReplicationView_if_delete_this_server);
				if (openConfirm) {
					parentMeta.removeChild(sourceMeta);
					dbReplicationTree.refresh();
					File file = parentMeta.getFile();
					deleteXmlNode(file, sourceMeta.getDbName());
					PlatformUtil.closeEditor(new DataBaseInput(sourceMeta, Messages.ReplicationView_Update_subscriber_server+" "+sourceMeta.getDbName(), dbReplicationTree),true);
				}
			} else {

				ReleaseDataSource sourceMeta = (ReleaseDataSource) object;
				ReplicationFile parentMeta = (ReplicationFile) sourceMeta.getParentModel();
				boolean openConfirm = MessageDialog.openConfirm(UIUtils.getActiveShell(),
						Messages.ReplicationView_Prompt, Messages.ReplicationView_if_delete_this_server);
				if (openConfirm) {
					parentMeta.removeChild(sourceMeta);
					dbReplicationTree.refresh();
					File file = parentMeta.getFile();
					deleteXmlNode(file, sourceMeta.getDbName());
					PlatformUtil.closeEditor(new DataBaseInput(sourceMeta, Messages.ReplicationView_update_release_server+" "+sourceMeta.getDbName(), dbReplicationTree),true);
				}
			}
			 
		}
	};

	/**
	 * 删除xml节点
	 */
	private void deleteXmlNode(File file, String dbName) {
		SAXReader reader = new SAXReader();
		List<Element> listEle = null;
		Document document = null;
		try {
			document = reader.read(file);
			Element root = document.getRootElement();
			listEle = root.elements();
			if (listEle == null || listEle.size() == 0) {
				return;
			}
			for (Element element : listEle) {
				if (element.element("name").getStringValue().equals(dbName)) { //$NON-NLS-1$
					root.elements().remove(element);
				}
			}
			try {
				OutputFormat xmlFormat = UIUtils.xmlFormat();
				File fileLocal = file;
				XMLWriter output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
				output.write(document);
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private Action createSubscribe = new Action(Messages.ReplicationView_create_subscriber,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_add)) {
		public void run() {
			if (object != null && object instanceof SubscribeDataBase) {
				SubscribeDataBase sourceMeta = (SubscribeDataBase) object;

				PlatformUtil.openEditor(
						new DataBaseInput(sourceMeta, Messages.ReplicationView_create_subscriber, dbReplicationTree),
						CreateSubscribeEditor.ID);
			}
		}
	};
	private Action updateSubscribe = new Action(Messages.ReplicationView_update_subscriber,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_update)) {
		public void run() {
			if (object != null && object instanceof SubscribeDataInfo) {
				SubscribeDataInfo dataInfo = (SubscribeDataInfo) object;
				PlatformUtil.openEditor(
						new DataBaseInput(dataInfo, Messages.ReplicationView_update_subscriber+dataInfo.getDbName()+"."+dataInfo.getSubscribeName(), dbReplicationTree),
						CreateSubscribeEditor.ID);
			}
		}
	};
	private Action deleteSubscribe = new Action(Messages.ReplicationView_delete_subsciber,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_delete)) {
		public void run() {
			if (object != null && object instanceof SubscribeDataInfo) {
				SubscribeDataInfo sourceMeta = (SubscribeDataInfo) object;
				SubscribeDataBase parentMeta = (SubscribeDataBase) sourceMeta.getParentModel();
				boolean open = MessageDialog.openConfirm(UIUtils.getActiveShell(), Messages.ThisSubscribeDialog_delete_subscription, Messages.ThisSubscribeDialog_delete_current_subscription);
				if (open) {
					boolean confirm = confirm("delete", sourceMeta, null);
					if (!confirm) {
						return;
					}
					parentMeta.removeChild(sourceMeta);
					parentMeta.refresh();
					dbReplicationTree.refresh();
					PlatformUtil.closeEditor(new DataBaseInput(sourceMeta, Messages.ReplicationView_update_subscriber+sourceMeta.getDbName()+"."+sourceMeta.getSubscribeName(), dbReplicationTree),true);
				}
			}
		}
	};
	private Action enableSubscribe = new Action(Messages.ReplicationView_open_subscriber,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_enable)) {
		public void run() {
			if (object != null && object instanceof SubscribeDataInfo) {
				SubscribeDataInfo sourceMeta = (SubscribeDataInfo) object;
				boolean open = MessageDialog.openConfirm(UIUtils.getActiveShell(), Messages.ReplicationView_open_subscriber, Messages.ThisSubscribeDialog_open_current_subscription);
				if (open) {
					boolean confirm = confirm("enable", sourceMeta, null);
					if (!confirm) {
						return;
					}
					sourceMeta.setSubscribeEnable("t"); //$NON-NLS-1$
					MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.ReplicationView_Prompt,
							Messages.ReplicationView_Enable_subscription_success);
					dbReplicationTree.refresh();
				}
			}
		}
	};
	private Action disableSubscribe = new Action(Messages.ReplicationView_close_subscribe,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_disable)) {
		public void run() {
			if (object != null && object instanceof SubscribeDataInfo) {
				SubscribeDataInfo sourceMeta = (SubscribeDataInfo) object;
				boolean open = MessageDialog.openConfirm(UIUtils.getActiveShell(), Messages.ReplicationView_close_subscribe, Messages.ThisSubscribeDialog_close_current_subscription);
				if (open) {
					boolean confirm = confirm("disable", sourceMeta,null);
					if(!confirm){
						return;
					}
					sourceMeta.setSubscribeEnable("f"); //$NON-NLS-1$
					MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.ReplicationView_Prompt,
							Messages.ReplicationView_Disable_subscription_success);
					dbReplicationTree.refresh();
				}
			}
		}
	};
	private Action syncSubscribe = new Action(Messages.ReplicationView_sync_subscriber,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_sync)) {
		public void run() {
			if (object != null && object instanceof SubscribeDataInfo) {
				SubscribeDataInfo sourceMeta = (SubscribeDataInfo) object;
				ThisSubscribeDialog dialog = new ThisSubscribeDialog(UIUtils.getActiveShell(), sourceMeta, "sync"); //$NON-NLS-1$
				int result = dialog.open();
				if (result == IDialogConstants.OK_ID) {
					MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.ReplicationView_Prompt,
							Messages.ReplicationView_Sync_subscription_success);
					dbReplicationTree.refresh();
				}
			}
		}
	};
	private Action syncTableSubscribe = new Action(Messages.ReplicationView_sync_table,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_sync)) {
		public void run() {
			if (object != null && object instanceof SubscribeTable) {
				SubscribeTable tableMeta = (SubscribeTable) object;
				SubscribeDataInfo sourceMeta = (SubscribeDataInfo) tableMeta.getParentModel();
				boolean open = MessageDialog.openConfirm(UIUtils.getActiveShell(), Messages.ReplicationView_sync_table, Messages.ThisSubscribeDialog_if_syncTable);

				if (open) {
					boolean confirm = confirm("syncTable", sourceMeta, tableMeta);
					if (!confirm) {
						return;
					}
					MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.ReplicationView_Prompt,
							Messages.ReplicationView_Sync_table_success);
					SubscribeDataBase parentMeta = (SubscribeDataBase) sourceMeta.getParentModel();
					parentMeta.refresh();
					dbReplicationTree.refresh();
				}
			}
		}
	};
	private Action syncEnable = new Action(Messages.ReplicationView_syncEnable,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_sync)) {
		public void run() {
			if (object != null && object instanceof ReleaseDataBase) {
				ReleaseDataBase database = (ReleaseDataBase) object;
				ReleaseDataSource parent = (ReleaseDataSource) database.getParentModel();
				Connection connection = DatabaseUtil.getConnection(parent, database.getDatabaseName());
				if (connection != null) {
					try {
						DatabaseMetaData metaData = connection.getMetaData();
						String schemaTerm = metaData.getSchemaTerm();
						Statement stm1 = null;
						if (!schemaTerm.equals("")) { //$NON-NLS-1$
							stm1 = connection.createStatement();
							stm1.execute("create extension syslogical"); //$NON-NLS-1$
						}
						if (stm1 != null) {
							stm1.close();
						}
//						if (connection != null) {
//							connection.close();
//						}
						database.setExistTable(true);
						database.setHasInit(false);
						database.refresh();
						dbReplicationTree.refresh();
						MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.ReplicationView_Prompt, Messages.ReplicationView_syncEnable_success); //$NON-NLS-1$
					} catch (SQLException e) {
						MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
					}
				}
			}else if (object != null && object instanceof SubscribeDataBase) {
				SubscribeDataBase database = (SubscribeDataBase) object;
				SubscribeDataSource parent = (SubscribeDataSource) database.getParentModel();
				Connection connection = DatabaseUtil.getConnection(parent, database.getDatabaseName());
				if (connection != null) {
					try {
						DatabaseMetaData metaData = connection.getMetaData();
						String schemaTerm = metaData.getSchemaTerm();
						Statement stm1 = null;
						if (!schemaTerm.equals("")) { //$NON-NLS-1$
							stm1 = connection.createStatement();
							stm1.execute("create extension syslogical"); //$NON-NLS-1$
						}
						if (stm1 != null) {
							stm1.close();
						}
//						if (connection != null) {
//							connection.close();
//						}
						database.setExistTable(true);
						database.setHasInit(false);
						database.refresh();
						dbReplicationTree.refresh();
						MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.ReplicationView_Prompt, Messages.ReplicationView_syncEnable_success); //$NON-NLS-1$
					} catch (SQLException e) {
						MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}
	};
	private Action syncDDL = new Action(Messages.ReplicationView_Synchronous_DDL,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_sync)) {
		public void run() {
			if (object != null && object instanceof ReleaseDataBase) {
				ReleaseDataBase database = (ReleaseDataBase) object;
				DDLdialog dialog = new DDLdialog(UIUtils.getActiveShell(), database);
				dialog.open();
			}
		}
	};
	private Action createRelease = new Action(Messages.ReplicationView_create_release,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_add)) {
		public void run() {
			if (object != null && object instanceof ReleaseDataBase) {
				ReleaseDataBase sourceMeta = (ReleaseDataBase) object;
				PlatformUtil.openEditor(
						new DataBaseInput(sourceMeta, Messages.ReplicationView_create_release, dbReplicationTree),
						CreateReleaseEditor.ID);
			}
		}
	};
	private Action updateRelease = new Action(Messages.ReplicationView_update_release,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_update)) {
		public void run() {
			if (object != null && object instanceof ReleaseDataInfo) {
				ReleaseDataInfo releaseDataInfo = (ReleaseDataInfo) object;
				PlatformUtil.openEditor(
						new DataBaseInput(releaseDataInfo, Messages.ReplicationView_update_release+releaseDataInfo.getDbName()+"."+releaseDataInfo.getReleaseName(), dbReplicationTree),
						CreateReleaseEditor.ID);
			}
		}
	};
	private Action deleteRelease = new Action(Messages.ReplicationView_delete_release,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_delete)) {
		public void run() {
			if (object != null && object instanceof ReleaseDataInfo) {
				ReleaseDataInfo sourceMeta = (ReleaseDataInfo) object;
				ReleaseDataBase parentMeta = (ReleaseDataBase) sourceMeta.getParentModel();

				boolean open = MessageDialog.openConfirm(UIUtils.getActiveShell(),
						Messages.ReplicationView_delete_release, Messages.DeleteReleaseDialog_if_delete_current_release);
				if (open) {
					Connection connection = DatabaseUtil.getConnection((ReleaseDataSource) parentMeta.getParentModel(),parentMeta.getDatabaseName());
					if (connection != null) {
						try {
							DatabaseMetaData metaData = connection
									.getMetaData();
							String schemaTerm = metaData.getSchemaTerm();
							Statement stm1 = null;
							String sql = " SELECT syslogical.drop_replication_set('"+ sourceMeta.getReleaseName() + "')";
							if (!schemaTerm.equals("")) { //$NON-NLS-1$
								stm1 = connection.createStatement();
								stm1.execute(sql); //$NON-NLS-1$
							}
							if (stm1 != null) {
								stm1.close();
							}
							parentMeta.removeChild(sourceMeta);
							PlatformUtil.closeEditor(new DataBaseInput(sourceMeta, Messages.ReplicationView_update_release+sourceMeta.getDbName()+"."+sourceMeta.getReleaseName(), dbReplicationTree),true);
							dbReplicationTree.refresh();
						} catch (SQLException e) {
							MessageDialog.openError(UIUtils.getActiveShell(),"错误", e.getMessage()); //$NON-NLS-1$
						}
					}
				}
			}
		}
	};

	private Action actionRefresh = new Action(Messages.ReplicationView_refresh, //$NON-NLS-1$
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_refresh)) {
		public void run() {
//			StructuredSelection structuredSelection = new StructuredSelection(object);//eclipse本身的Link with Editor功能
			if (object != null && object instanceof ReplicationRoot) {
				ReplicationRoot tRoot = (ReplicationRoot) object;
				CollapseallTree();
				tRoot.refresh();
			} else if (object != null && object instanceof ReplicationFile) {
				ReplicationFile tfFolder = (ReplicationFile) object;
				tfFolder.removeAll();
				tfFolder.refresh();
				dbReplicationTree.refresh();
			} else if (object != null && object instanceof ReleaseDataSource) {
				ReleaseDataSource sourceMeta = (ReleaseDataSource) object;
				sourceMeta.removeAll();
				sourceMeta.refresh();
				sourceMeta.treeCollapsed();
				dbReplicationTree.refresh();
			} else if (object != null && object instanceof ReleaseDataBase) {
				ReleaseDataBase sourceMeta = (ReleaseDataBase) object;
				sourceMeta.removeAll();
				sourceMeta.refresh();
				dbReplicationTree.refresh();
			}else if(object != null && object instanceof ReleaseDataInfo){
				ReleaseDataInfo sourceMeta = (ReleaseDataInfo) object;
				sourceMeta.removeAll();
				sourceMeta.refresh();
				dbReplicationTree.refresh();
			} else if (object != null && object instanceof SubscribeDataSource) {
				SubscribeDataSource sourceMeta = (SubscribeDataSource) object;
				sourceMeta.removeAll();
				sourceMeta.refresh();
				sourceMeta.treeCollapsed();
				dbReplicationTree.refresh();
			} else if (object != null && object instanceof SubscribeDataBase) {
				SubscribeDataBase sourceMeta = (SubscribeDataBase) object;
				sourceMeta.removeAll();
				sourceMeta.refresh();
				dbReplicationTree.refresh();
			}else if(object != null && object instanceof SubscribeDataInfo){
				SubscribeDataInfo sourceMeta = (SubscribeDataInfo) object;
				sourceMeta.removeAll();
				sourceMeta.refresh();
				dbReplicationTree.refresh();
			}
		}
	};

	private Action createReleaseSource = new Action(Messages.ReplicationView_Registered_release_server,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_add)) {
		public void run() {
			if (object != null && object instanceof ReplicationFile) {
				ReplicationFile tfFolder = (ReplicationFile) object;

				PlatformUtil.openEditor(new DataBaseInput(tfFolder, Messages.ReplicationView_Registered_release_server,
						dbReplicationTree), CreateSourceConnectionEditor.ID);
			}
		}
	};
	private Action updateReleaseServers = new Action(Messages.ReplicationView_update_release_server,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_update)) {
		public void run() {
			if (object != null && object instanceof ReleaseDataSource) {
				ReleaseDataSource source = (ReleaseDataSource) object;

				PlatformUtil.openEditor(
						new DataBaseInput(source, Messages.ReplicationView_update_release_server+" "+source.getDbName(), dbReplicationTree),
						CreateSourceConnectionEditor.ID);
			}
		}
	};

	protected boolean confirm(String type,SubscribeDataInfo sourceMeta,SubscribeTable tableMeta) {
		
		Connection sourceCon = DatabaseUtil.getConnection((SubscribeDataSource) sourceMeta.getParentModel().getParentModel(),
				((SubscribeDataBase) sourceMeta.getParentModel()).getDatabaseName());
		
		String sql = ""; //$NON-NLS-1$
		if (type.equals("delete")) { //$NON-NLS-1$

			sql = " SELECT syslogical.drop_subscription('" + sourceMeta.getSubscribeName() + "',false)"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (type.equals("enable")) { //$NON-NLS-1$

			sql = " SELECT syslogical.alter_subscription_enable('" + sourceMeta.getSubscribeName() + "',false)"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (type.equals("disable")) { //$NON-NLS-1$

			sql = " SELECT syslogical.alter_subscription_disable('" + sourceMeta.getSubscribeName() + "',false)"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (type.equals("syncTable") && tableMeta != null) { //$NON-NLS-1$
			
			sql = " SELECT syslogical.alter_subscription_resynchronize_table('" + sourceMeta.getSubscribeName() + "','"+tableMeta.getTableName()+"'::regclass)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		if (sourceCon == null){
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", "此数据库未连接成功！请检查"); //$NON-NLS-1$
			return false;
		}
		try {
			DatabaseMetaData metaData = sourceCon.getMetaData();
			String schemaTerm = metaData.getSchemaTerm();
			if (!schemaTerm.equals("")) { //$NON-NLS-1$
				Statement stm = sourceCon.createStatement();
				stm.executeQuery(sql);
				stm.close();
			}
//			if (sourceCon != null) {
//				sourceCon.close();
//			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", e1.getMessage()); //$NON-NLS-1$
			return false;
		}
		return true;
	}
	
	/**
	 * 更新发布节点xml
	 * 
	 */
	public static void updateReleaseXml(ReleaseDataSource oldSource, ReleaseDataSource newSource) {

		ReplicationFile ifile = (ReplicationFile) oldSource.getParentModel();
		File file = ifile.getFile();
		File fileLocal = file;
		SAXReader reader = new SAXReader();
		List<Element> listEle = null;
		Document document;
		try {
			document = reader.read(file);
			Element root = document.getRootElement();
			listEle = root.elements();
			for (int i = 0, n = listEle.size(); i < n; i++) {
				Element element = listEle.get(i);
				if (element.element("name").getStringValue().equals(oldSource.getDbName())) { //$NON-NLS-1$
					element.element("name").setText(newSource.getDbName()); //$NON-NLS-1$
					element.element("server").setText(newSource.getDbServer()); //$NON-NLS-1$
					element.element("port").setText(newSource.getDbPort()); //$NON-NLS-1$
					element.element("driverName").setText(newSource.getDriverName()); //$NON-NLS-1$
					element.element("driverPath").setText(newSource.getDriverPath()); //$NON-NLS-1$
					element.element("username").setText(newSource.getDbUser()); //$NON-NLS-1$
					element.element("password").setText(newSource.getDbPasswrod()); //$NON-NLS-1$
					element.element("isSaveP").setText(newSource.isSaveP() ? "true" : "false"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
				}
			}
			OutputFormat xmlFormat = UIUtils.xmlFormat();
			XMLWriter output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
			output.write(document);
			output.close();

		} catch (DocumentException e) {
			e.printStackTrace();
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
		}
	}

	private Action createSubscribeSource = new Action(Messages.ReplicationView_Registered_subscriber_server,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_add)) {
		public void run() {
			if (object != null && object instanceof ReplicationFile) {
				ReplicationFile tfFolder = (ReplicationFile) object;
				PlatformUtil.openEditor(new DataBaseInput(tfFolder,
						Messages.ReplicationView_Registered_subscriber_server, dbReplicationTree),
						CreateSourceConnectionEditor.ID);
			}
		}
	};
	private Action updateSubscribeSource = new Action(Messages.ReplicationView_Update_subscriber_server,
			ImageURL.createImageDescriptor(KBReplicationCore.PLUGIN_ID, ImageURL.right_update)) {
		public void run() {
			if (object != null && object instanceof SubscribeDataSource) {
				SubscribeDataSource source = (SubscribeDataSource) object;
				PlatformUtil.openEditor(
						new DataBaseInput(source, Messages.ReplicationView_Update_subscriber_server+" "+source.getDbName(), dbReplicationTree),
						CreateSourceConnectionEditor.ID);
			}
		}
	};

	/**
	 *  更新订阅xml
	 * 
	 */
	public static void updateSubscribeXml(SubscribeDataSource oldSource, SubscribeDataSource newSource) {

		ReplicationFile ifile = (ReplicationFile) oldSource.getParentModel();
		File file = ifile.getFile();
		File fileLocal = file;
		SAXReader reader = new SAXReader();
		List<Element> listEle = null;
		Document document;
		try {
			document = reader.read(file);
			Element root = document.getRootElement();
			listEle = root.elements();
			List<ReleaseDataBase> list = new ArrayList<ReleaseDataBase>();
			for (int i = 0, n = listEle.size(); i < n; i++) {
				Element element = listEle.get(i);
				if (element.element("name").getStringValue().equals(oldSource.getDbName())) { //$NON-NLS-1$
					element.element("name").setText(newSource.getDbName()); //$NON-NLS-1$
					element.element("server").setText(newSource.getDbServer()); //$NON-NLS-1$
					element.element("port").setText(newSource.getDbPort()); //$NON-NLS-1$
					element.element("driverName").setText(newSource.getDriverName()); //$NON-NLS-1$
					element.element("driverPath").setText(newSource.getDriverPath()); //$NON-NLS-1$
					element.element("username").setText(newSource.getDbUser()); //$NON-NLS-1$
					element.element("password").setText(newSource.getDbPasswrod()); //$NON-NLS-1$
					element.element("isSaveP").setText(newSource.isSaveP() ? "true" : "false"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
				}
			}
			OutputFormat xmlFormat = UIUtils.xmlFormat();
			XMLWriter output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
			output.write(document);
			output.close();

		} catch (DocumentException e) {
			e.printStackTrace();
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * 得到订阅对应的发布信息
	 * 
	 * @param dataInfo
	 */
	public List<ReleaseDataInfo> getSubscribeToRelease(SubscribeDataInfo dataInfo) {

		List<ReleaseDataInfo> dataInfoList = new ArrayList<ReleaseDataInfo>();

		SubscribeDataBase datebase = (SubscribeDataBase) dataInfo.getParentModel();
		SubscribeDataSource parent = (SubscribeDataSource) datebase.getParentModel();
		
		ReplicationRoot root = (ReplicationRoot) parent.getParentModel().getParentModel();
		ReplicationFile ifile = (ReplicationFile) root.getChildren()[0];

		List<Element> listEle = ReplicationView.getlistEle(ifile);
		if (listEle == null || listEle.size() == 0) {
			return null;
		}
		List<ReleaseDataBase> list = new ArrayList<ReleaseDataBase>();
		for (int i = 0, n = listEle.size(); i < n; i++) {
			Element element = listEle.get(i);
			
			Element nodesElm = element.element("nodes"); //$NON-NLS-1$
			List<Element> nodeElm = nodesElm.elements("node"); //$NON-NLS-1$
			for (Element node : nodeElm) {
				ReleaseDataBase databaseMeta = new ReleaseDataBase();
				databaseMeta.setDbName(element.element("name").getStringValue()); //$NON-NLS-1$
				databaseMeta.setDbServer(element.element("server").getStringValue()); //$NON-NLS-1$
				databaseMeta.setDbPort(element.element("port").getStringValue()); //$NON-NLS-1$
				databaseMeta.setDbUser(element.element("username").getStringValue()); //$NON-NLS-1$
				databaseMeta.setDbPasswrod(element.element("password").getStringValue()); //$NON-NLS-1$
				databaseMeta.setDriverName(element.element("driverName").getStringValue()); //$NON-NLS-1$
				databaseMeta.setDriverPath(element.element("driverPath").getStringValue()); //$NON-NLS-1$
				databaseMeta.setNodeId(node.element("nodeId").getStringValue()); //$NON-NLS-1$
				databaseMeta.setDatabaseName(node.element("dbname").getStringValue()); //$NON-NLS-1$
				list.add(databaseMeta);
			}
		}

		Connection sourceCon = DatabaseUtil.getConnection(parent, datebase.getDatabaseName());
		if (sourceCon == null){
			return dataInfoList;
		}
		try {
			ResultSet subscribeSet = sourceCon.createStatement().executeQuery(
					"SELECT SUB_ORIGIN, ARRAY_TO_STRING(SUB_REPLICATION_SETS, ',') AS SETS FROM SYSLOGICAL.SUBSCRIPTION WHERE SUB_NAME = '" //$NON-NLS-1$
							+ dataInfo.getSubscribeName() + "'"); //$NON-NLS-1$
			while (subscribeSet.next()) {
				String id = subscribeSet.getString("SUB_ORIGIN"); //$NON-NLS-1$
				String sets = subscribeSet.getString("SETS"); //$NON-NLS-1$

				for (ReleaseDataBase releaseDataBase : list) {
					if (releaseDataBase.getNodeId().equals(id)) {
						String[] split = sets.split(","); //$NON-NLS-1$
						for (String str : split) {

							ReleaseDataInfo sourceMeta = new ReleaseDataInfo();
							sourceMeta.setDbName(releaseDataBase.getDbName());
							sourceMeta.setDbServer(releaseDataBase.getDbServer());
							sourceMeta.setDbPort(releaseDataBase.getDbPort());
							sourceMeta.setDbUser(releaseDataBase.getDbUser());
							sourceMeta.setDbPasswrod(releaseDataBase.getDbPasswrod());
							sourceMeta.setDriverName(releaseDataBase.getDriverName());
							sourceMeta.setDriverPath(releaseDataBase.getDriverPath());
							sourceMeta.setDatabaseName(releaseDataBase.getDatabaseName());
							sourceMeta.setDatabaseOid(releaseDataBase.getDatabaseOid());
							sourceMeta.setReleaseName(str);

							dataInfoList.add(sourceMeta);
						}
						break;
					}
				}
			}
			sourceCon.createStatement().close();
			subscribeSet.close();
//			if (!sourceCon.isClosed()) {
//				sourceCon.close();
//			}
		} catch (Exception e) {
//			MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
		}
		return dataInfoList;
	}

	public void CollapseallTree() {
		this.dbReplicationTree.collapseAll();
	}

	class LabelProvider extends CTableTreeLabelProvider {
		public Image getImage(Object element) {
			return super.getImage(element);
		}

		public String getText(Object element) {
			return super.getText(element);
		}
	}

	class ContentProvider extends CTreeStruredContentProvider {

		public Object[] getChildren(Object parentElement) {
			return super.getChildren(parentElement);
		}
	}

	@Override
	public void setFocus() {

	}
	public static List<Element> getlistEle(ReplicationFile ifile) {
		File file = ifile.getFile();
		SAXReader reader = new SAXReader();
		List<Element> listEle = null;
		Document document;
		try {
			document = reader.read(file);
			Element root = document.getRootElement();
			listEle = root.elements();
		} catch (DocumentException e) {
			e.printStackTrace();
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
		}
		return listEle;
	}

}
