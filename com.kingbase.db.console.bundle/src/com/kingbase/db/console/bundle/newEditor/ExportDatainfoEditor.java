package com.kingbase.db.console.bundle.newEditor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.pentaho.di.viewer.CBasicTreeViewer;
import org.pentaho.di.viewer.CTableTreeLabelProvider;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.CTreeStruredContentProvider;

import com.kingbase.db.console.bundle.KBConsoleCore;
import com.kingbase.db.console.bundle.model.tree.DatabaseEntity;
import com.kingbase.db.console.bundle.model.tree.ExportOrImportEntity;
import com.kingbase.db.console.bundle.model.tree.SchemaEntity;
import com.kingbase.db.console.bundle.model.tree.TableEntity;
import com.kingbase.db.console.bundle.views.ConsoleView;
import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.UIUtils;

public class ExportDatainfoEditor extends EditorPart {
	public static final String ID = "com.kingbase.db.console.bundle.newEditor.ExportDatainfoEditor";

	private DataBaseInput input;
	private ExportOrImportEntity node;
	private FormToolkit toolkit;
	private Button databaseB;
	private Button tableB;
	private Button schemaB;
	private Button textB;
	private Button excelB;
	private Button xmlB;
	private Text txtExportPath;
	private CBasicTreeViewer cTreeView;
	private Connection connection;
	private final List<String> commands = new ArrayList<String>();
	private List<CTableTreeNode> treeList = new ArrayList<CTableTreeNode>();
	private String success = null;
	
	private String database = "";
	private String schemaName = "";
	private String tableName = "";
	private List<TableEntity> childTableList = null;
	private List<SchemaEntity> childSchemaList = null;

	private String txtExportPathT ;
	private boolean tableFlag = false;
	private boolean schemaFlag = false;
	private boolean databaseFlag = false;

	public ExportDatainfoEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		this.input = (DataBaseInput) input;
		this.node = (ExportOrImportEntity) this.input.getNode();
		this.connection = ConsoleView.getConnection(node.getAddress(), node.getPort(), node.getUser(),
				node.getPassword(), UIUtils.getDatabase());
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		GridData parent_gd = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(parent_gd);
		parent.setLayout(new GridLayout());
		parent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		IManagedForm managedForm = new ManagedForm(parent);
		toolkit = managedForm.getToolkit();
		final ScrolledForm form = managedForm.getForm();
		GridData data1 = new GridData(GridData.FILL_BOTH);
		form.setLayoutData(data1);
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		toolkit.decorateFormHeading(form.getForm());

		Group group1 = new Group(form.getBody(), SWT.NONE);
		group1.setText("导出信息");
		group1.setLayout(new GridLayout());
		group1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group1.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Composite compositeInfo = new Composite(group1, SWT.NONE);
		GridLayout layout1 = new GridLayout(2, false);
		compositeInfo.setLayout(layout1);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		compositeInfo.setLayoutData(data);
		compositeInfo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Label createLabel = toolkit.createLabel(compositeInfo, "导出格式    ");

		Composite comRedio = new Composite(compositeInfo, SWT.NONE);
		GridLayout layout11 = new GridLayout(3, true);
		comRedio.setLayout(layout11);
		GridData data11 = new GridData(SWT.FILL, SWT.FILL, true, false);
		comRedio.setLayoutData(data11);
		comRedio.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		textB = toolkit.createButton(comRedio, "文本", SWT.RADIO);
		GridData textB_gd = new GridData(GridData.FILL_BOTH);
		textB.setLayoutData(textB_gd);
		excelB = toolkit.createButton(comRedio, "excel", SWT.RADIO);
		GridData excelB_gd = new GridData(GridData.FILL_BOTH);
		excelB.setLayoutData(excelB_gd);
		xmlB = toolkit.createButton(comRedio, "xml", SWT.RADIO);
		GridData xmlB_gd = new GridData(GridData.FILL_BOTH);
		xmlB.setLayoutData(xmlB_gd);

		Label createLabel1 = toolkit.createLabel(compositeInfo, "导出方式    ");

		Composite comRedio1 = new Composite(compositeInfo, SWT.NONE);
		GridLayout layout111 = new GridLayout(3, true);
		comRedio1.setLayout(layout111);
		GridData data111 = new GridData(SWT.FILL, SWT.FILL, true, false);
		comRedio1.setLayoutData(data111);
		comRedio1.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		databaseB = toolkit.createButton(comRedio1, "数据库", SWT.RADIO);
		GridData fillB_gd = new GridData(GridData.FILL_BOTH);
		databaseB.setLayoutData(fillB_gd);
		databaseB.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				databaseFlag = true;
				tableFlag = false;
				schemaFlag = false;
				treeList.clear();
				if (connection != null) {
					try {
						DatabaseMetaData metaData = connection.getMetaData();
						String schemaTerm = metaData.getSchemaTerm();
						if (!schemaTerm.equals("")) {
							Statement stmDatabase = connection.createStatement();// 查找数据库
							ResultSet rsDatabase = null;
							rsDatabase = stmDatabase.executeQuery(
									"SELECT DATNAME,OID FROM SYS_DATABASE WHERE NOT DATISTEMPLATE AND DATALLOWCONN");
							while (rsDatabase.next()) {
								DatabaseEntity database = new DatabaseEntity(node, "database");
								String datename = rsDatabase.getString("DATNAME");
								long oid = rsDatabase.getLong("OID");
								database.setOid(oid);
								database.setName(datename);
								treeList.add(database);
							}
							stmDatabase.close();
							if (rsDatabase != null) {
								rsDatabase.close();
							}
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
						MessageDialog.openError(UIUtils.getActiveShell(), "提示", e1.getMessage());
					}
				}
				 
				cTreeView.setInput(treeList);
				cTreeView.refresh();
				cTreeView.expandAll();
			}

		
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		schemaB = toolkit.createButton(comRedio1, "模式", SWT.RADIO);
		GridData schemaB_gd = new GridData(GridData.FILL_BOTH);
		schemaB.setLayoutData(schemaB_gd);
		schemaB.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				databaseFlag = false;
				tableFlag = false;
				schemaFlag = true;
				treeList.clear();
				if (connection != null) {
					try {
						DatabaseMetaData metaData = connection.getMetaData();
						String schemaTerm = metaData.getSchemaTerm();
						if (!schemaTerm.equals("")) {
							Statement stmDatabase = connection.createStatement();// 查找数据库
							ResultSet rsDatabase = null;
							rsDatabase = stmDatabase.executeQuery(
									"SELECT DATNAME,OID FROM SYS_DATABASE WHERE NOT DATISTEMPLATE AND DATALLOWCONN");
							while (rsDatabase.next()) {
								DatabaseEntity database = new DatabaseEntity(node, "schema");
								String datename = rsDatabase.getString("DATNAME");
								long oid = rsDatabase.getLong("OID");
								database.setOid(oid);
								database.setName(datename);
								treeList.add(database);
							}
							stmDatabase.close();
							if (rsDatabase != null) {
								rsDatabase.close();
							}
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
						MessageDialog.openError(UIUtils.getActiveShell(), "提示", e1.getMessage());
					}
				}
				cTreeView.setInput(treeList);
				cTreeView.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		tableB = toolkit.createButton(comRedio1, "表", SWT.RADIO);
		GridData tableB_gd = new GridData(GridData.FILL_BOTH);
		tableB.setLayoutData(tableB_gd);
		tableB.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				databaseFlag = false;
				tableFlag = true;
				schemaFlag = false;
				treeList.clear();
				if (connection != null) {
					try {
						DatabaseMetaData metaData = connection.getMetaData();
						String schemaTerm = metaData.getSchemaTerm();
						if (!schemaTerm.equals("")) {
							Statement stmDatabase = connection.createStatement();// 查找数据库
							ResultSet rsDatabase = null;
							rsDatabase = stmDatabase.executeQuery(
									"SELECT DATNAME,OID FROM SYS_DATABASE WHERE NOT DATISTEMPLATE AND DATALLOWCONN");
							while (rsDatabase.next()) {
								DatabaseEntity database = new DatabaseEntity(node, "table");
								String datename = rsDatabase.getString("DATNAME");
								long oid = rsDatabase.getLong("OID");
								database.setOid(oid);
								database.setName(datename);
								treeList.add(database);
							}
							stmDatabase.close();
							if (rsDatabase != null) {
								rsDatabase.close();
							}
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
						MessageDialog.openError(UIUtils.getActiveShell(), "提示", e1.getMessage());
					}
				}
				cTreeView.setInput(treeList);
				cTreeView.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label exportPath = toolkit.createLabel(compositeInfo, "导出路径", SWT.NONE);
		exportPath.setLayoutData(new GridData());

		final Composite compositePath = new Composite(compositeInfo, SWT.None);
		GridLayout layout21 = new GridLayout(2, false);
		layout21.horizontalSpacing = 2;
		layout21.verticalSpacing = 0;
		layout21.marginTop = 0;
		compositePath.setLayout(layout21);
		GridData dataw = new GridData(GridData.FILL_HORIZONTAL);
		compositePath.setLayoutData(dataw);
		compositePath.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		txtExportPath = new Text(compositePath, SWT.BORDER | SWT.READ_ONLY);
		dataw = new GridData(GridData.FILL_HORIZONTAL);
		txtExportPath.setLayoutData(dataw);
		txtExportPath.setText("");
		Button btn1 = new Button(compositePath, SWT.NONE);
		btn1.setText("选择");
		btn1.setLayoutData(new GridData());

		btn1.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(compositePath.getShell());
				String path = dialog.open();
				txtExportPath.setText(path == null ? "" : path);
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Composite compPath = new Composite(form.getBody(), SWT.BORDER);
		GridLayout layout211 = new GridLayout();
		compPath.setLayout(layout211);
		GridData dataw1 = new GridData(GridData.FILL_BOTH);
		compPath.setLayoutData(dataw1);
		compPath.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		
		cTreeView = new CBasicTreeViewer(compPath);
		GridData data1111 = new GridData(GridData.FILL_BOTH);
		final Tree tree = cTreeView.getTree();
		tree.setLayoutData(data1111);

		cTreeView.setLabelProvider(new LabelProvider());
		cTreeView.setContentProvider(new ContentProvider());
		
		Composite compOpera = new Composite(form.getBody(), SWT.NONE);
		compOpera.setLayout(new GridLayout(3, false));
		GridData data11111 = new GridData(GridData.FILL_HORIZONTAL);
		compOpera.setLayoutData(data11111);
		Label label111 = new Label(compOpera, SWT.None);
		label111.setText("");
		data11111 = new GridData(GridData.FILL_HORIZONTAL);
		data11111.horizontalSpan = 1;
		label111.setLayoutData(data11111);
		compOpera.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		label111.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		final Button btnConfirm = new Button(compOpera, SWT.PUSH);
		btnConfirm.setText("导出");
		data11111 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnConfirm.setLayoutData(data11111);
		((GridData) btnConfirm.getLayoutData()).widthHint = 61;
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtExportPathT = txtExportPath.getText();
				boolean check = check();
				if (!check) {
					return;
				}
				TreeItem[] selection = cTreeView.getTree().getSelection();
				Object object = selection[0].getData();
				
				if(object instanceof TableEntity){
					TableEntity object2 = (TableEntity) object;
					database = object2.getDatabase();
					tableName = object2.getName();
					schemaName = object2.getSchema();
				}
				if (object instanceof SchemaEntity) {
					SchemaEntity object2 = (SchemaEntity) object;
					database = object2.getDatabase();
					schemaName = object2.getName();
					childTableList = getTable(object2);
				}
				if(object instanceof DatabaseEntity){
					DatabaseEntity object2 = (DatabaseEntity) object;
					database = object2.getName();
					childSchemaList = getSchema(object2);
				}
				commands.clear();
				commands.add(node.getDatabasePath() + "/exp");
				if(textB.getSelection()){
					
					commands.add("FILETYPE=csv");
				}else if(excelB.getSelection()){
					
					commands.add("FILETYPE=xls");
				}else if(xmlB.getSelection()){
					
					commands.add("FILETYPE=xml");
				}
				commands.add("host="+node.getAddress());
				commands.add("port="+node.getPort());
				commands.add("dbname="+database);
				commands.add("user="+node.getUser());
				commands.add("password="+node.getPassword());
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(UIUtils.getActiveShell());
				try {
					dialog.run(true, true, new RunnAble());
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			
				if(success==null){
					MessageDialog.openConfirm(UIUtils.getActiveShell(), "提示", "导出数据成功,路径为：\n"+txtExportPathT);
				}else{
					MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "导出数据失败\n"+success);
				}
			 }
		});

		Button btnCancel = new Button(compOpera, SWT.PUSH);
		btnCancel.setText("取消");
		data11111 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnCancel.setLayoutData(data11111);
		((GridData) btnCancel.getLayoutData()).widthHint = 61;

		btnCancel.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				closeEditor();
			}
		});

	}
	class RunnAble implements IRunnableWithProgress {

		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask("start...", IProgressMonitor.UNKNOWN);

			List<String> command = new ArrayList<String>();
			if (tableFlag) {
				command.clear();
				command.addAll( commands);
				command.add("file="+txtExportPathT);
				command.add("resultfile=" + schemaName+"."+tableName);
				command.add("TABLES=(" + schemaName+"."+tableName + ")");
				execCommand(command);
			}
             else if (schemaFlag) {
				if (childTableList != null && childTableList.size() > 0) {
					for (TableEntity table : childTableList) {
						command.clear();
						command.addAll( commands);
						command.add("file="+txtExportPathT);
						command.add("resultfile=" +schemaName+"."+ table.getName() );
						command.add("TABLES=(" + schemaName+"."+table.getName()  + ")");
						execCommand(command);
					}
				}
			}
			else if (databaseFlag) {
				List<String> list = new ArrayList<String>();
				if (childSchemaList != null && childSchemaList.size() > 0) {
					for (SchemaEntity entity : childSchemaList) {
						list.add("mkdir");
						list.add("-p");
						list.add(txtExportPathT + "/" + entity.getName());
						execCommand(list);

						childTableList =getTable(entity);
						if (childTableList != null && childTableList.size() > 0) {

							for (TableEntity table : childTableList) {
								command.clear();
								command.addAll(commands);
								command.add("file="+txtExportPathT + "/" + entity.getName() + "/");
								command.add("resultfile=" +entity.getName()+"."+ table.getName() );
								command.add("TABLES=(" +entity.getName()+"."+ table.getName()  + ")");
								execCommand(command);
							}
						}
					}
				}
			}
			
			monitor.done();
		}
	}
	protected boolean check() {
		if (!textB.getSelection() && !excelB.getSelection()
				&& !xmlB.getSelection()) {
			MessageDialog
					.openWarning(UIUtils.getActiveShell(), "提示", "请选择导出格式");
			return false;
		}
		if (!databaseB.getSelection() && !schemaB.getSelection()
				&& !tableB.getSelection()) {
			MessageDialog
					.openWarning(UIUtils.getActiveShell(), "提示", "请选择导出方式");
			return false;
		}
		if (txtExportPathT.equals("")) {
			MessageDialog
					.openWarning(UIUtils.getActiveShell(), "提示", "请选择导出路径");
			txtExportPath.setFocus();
			return false;
		}
		TreeItem[] selection = cTreeView.getTree().getSelection();

		if (selection.length == 0) {
			MessageDialog
					.openWarning(UIUtils.getActiveShell(), "提示", "请选择导出数据");
			return false;
		}
		Object object = selection[0].getData();
		if (tableB.getSelection()) {
			if (!(object instanceof TableEntity)) {
				MessageDialog.openWarning(UIUtils.getActiveShell(), "提示",
						"请选择要导出的表");
				return false;
			}
		}
		else if (schemaB.getSelection()) {
			if (!(object instanceof SchemaEntity)) {
				MessageDialog.openWarning(UIUtils.getActiveShell(), "提示",
						"请选择要导出的模式");
				return false;
			}
		}
		else if (databaseB.getSelection()) {
			if (!(object instanceof DatabaseEntity)) {
				MessageDialog.openWarning(UIUtils.getActiveShell(), "提示",
						"请选择要导出的数据库");
				return false;
			}
		}
		return true;
	}

	private void execCommand(List<String> buffer) {
		ProcessBuilder builder = new ProcessBuilder(buffer);
		builder.redirectErrorStream(true);
		Process process;
		try {
			process = builder.start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			StringBuffer errorBuffer = new StringBuffer();
			while ((line = br.readLine()) != null) {
				errorBuffer.append(line+"\n");
			}

			int exitValue = process.waitFor();
			if (exitValue != 0) {
				success = errorBuffer.toString();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = e.getMessage();
		}
	}
	
	private void closeEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeEditor(page.getActiveEditor(), true);
	}
	@Override
	public void setFocus() {
	}
	class ContentProvider extends CTreeStruredContentProvider {

		public Object[] getChildren(Object parentElement) {
			return super.getChildren(parentElement);
		}

		@Override
		public boolean hasChildren(Object element) {
			return true;
		}
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

	private List<TableEntity> getTable(SchemaEntity object2) {
		List<TableEntity> list = new ArrayList<TableEntity>();
		Connection source = ConsoleView.getConnection(node.getAddress(),
				node.getPort(), node.getUser(), node.getPassword(), object2.getDatabase());
		if (source != null) {
			try {
				Statement stmSchema = source.createStatement();// 查找模式
				ResultSet rsSchema = null;

				rsSchema = stmSchema
						.executeQuery("select oid, relname from sys_class where relnamespace = "
								+ object2.getOid() + "and relkind = 'r'");
				while (rsSchema.next()) {
					TableEntity table = new TableEntity();
					String tableName = rsSchema.getString("relname");
					long tableOid = rsSchema.getLong("OID");
					table.setOid(tableOid);
					table.setName(tableName);
					table.setDatabase(object2.getDatabase());
					table.setSchema(object2.getName());
					list.add(table);// 将表添加进去
				}
				stmSchema.close();
				rsSchema.close();
				if(source!=null){
					source.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		return list;
	}
	public List<SchemaEntity> getSchema(DatabaseEntity object2) {
		List<SchemaEntity> list = new ArrayList<SchemaEntity>();
		Connection connection = ConsoleView.getConnection(node.getAddress(), node.getPort(), node.getUser(),
				node.getPassword(), object2.getName());
		if (connection != null) {
			try {
				DatabaseMetaData metaData = connection.getMetaData();
				String schemaTerm = metaData.getSchemaTerm();
				if (!schemaTerm.equals("")) {
					Statement stmDatabase = connection.createStatement();// 查找数据库
					ResultSet rsDatabase = null;
					rsDatabase = stmDatabase.executeQuery(
							"select oid ,nspname from  sys_namespace ss where ss.nspname NOT LIKE 'SYS_%'");
					while (rsDatabase.next()) {
						SchemaEntity schema = new SchemaEntity(node, "schema");
						String schemaName = rsDatabase.getString("nspname");
						long oid = rsDatabase.getLong("OID");
						schema.setOid(oid);
						schema.setName(schemaName);
						schema.setDatabase(object2.getName());

						if (!(schemaName.toLowerCase().startsWith("information")||schemaName.toLowerCase().startsWith("outln")||schemaName.toLowerCase().startsWith("utl_file"))) {
							list.add(schema);
						}
					}
					stmDatabase.close();
					if (rsDatabase != null) {
						rsDatabase.close();
					}
					if(connection!=null){
						connection.close();
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				return null;
			}
		}
		return list;
	}

}
