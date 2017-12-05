package com.kingbase.db.console.bundle.newEditor;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
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

import com.kingbase.db.console.bundle.KBConsoleCore;
import com.kingbase.db.console.bundle.model.tree.ExportOrImportEntity;
import com.kingbase.db.console.bundle.views.ConsoleView;
import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.PlatformUtil;
import com.kingbase.db.core.util.UIUtils;

public class ServerInfoEditor extends EditorPart {
	public static final String ID = "com.kingbase.db.console.bundle.newEditor.ServerInfoEditor";
	private DataBaseInput input;
	private Text txtName;
	private Text txtPassword;
	private Text txtAddress;
	private Text txtPort;
	private ExportOrImportEntity node;
	private FormToolkit toolkit;
	private String driverName = "com.kingbase.Driver";
	private String driverPath;
	private Text txtDatabasePath;

	public ServerInfoEditor() {
		// TODO Auto-generated constructor stub
	}

	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		this.input = (DataBaseInput) input;
		this.node = (ExportOrImportEntity) this.input.getNode();

		driverPath = PlatformUtil.getConfigurationFile(KBConsoleCore.PLUGIN_ID, "driver/kingbasejdbc4.jar")
				.toString(); // $NON-NLS-1$
	}

	public File getFileInBundle(String path) {
		return PlatformUtil.getConfigurationFile(KBConsoleCore.PLUGIN_ID, path);
	}

	
	public void createPartControl(final Composite parent) {

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
		group1.setText("服务信息");
		group1.setLayout(new GridLayout());
		group1.setLayoutData(new GridData(GridData.FILL_BOTH));
		group1.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Composite compositeInfo = new Composite(group1, SWT.NONE);
		GridLayout layout1 = new GridLayout(2, false);
		compositeInfo.setLayout(layout1);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		compositeInfo.setLayoutData(data);
		compositeInfo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Label lbAddress = toolkit.createLabel(compositeInfo, "IP地址", SWT.NONE);
		lbAddress.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtAddress = new Text(compositeInfo, SWT.BORDER);
		txtAddress.setLayoutData(data);
		txtAddress.setTextLimit(63);
		txtAddress.setText("locahost");
		UIUtils.verifyTextNotSpace(txtAddress);

		Label lbPort = toolkit.createLabel(compositeInfo, "端口号", SWT.NONE);
		lbPort.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtPort = new Text(compositeInfo, SWT.BORDER);
		txtPort.setLayoutData(data);
		txtPort.setText("54321");
		txtPort.setTextLimit(63);
		UIUtils.verifyTextNumber(txtPort);

		Label lbUserName = toolkit.createLabel(compositeInfo, "用户名", SWT.NONE);
		lbUserName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtName = new Text(compositeInfo, SWT.BORDER);
		txtName.setLayoutData(data);
		txtName.setText("");
		txtName.setTextLimit(63);
		UIUtils.verifyTextNotSpace(txtName);

		Label lbPassword = toolkit.createLabel(compositeInfo, "密码", SWT.NONE);
		lbPassword.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtPassword = new Text(compositeInfo, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(data);
		txtPassword.setText("");
		txtPassword.setTextLimit(63);
		
		Label ldatabasePath = toolkit.createLabel(compositeInfo, "数据库目录", SWT.NONE);
		ldatabasePath.setLayoutData(new GridData());

		final Composite compositePath = new Composite(compositeInfo, SWT.None);
		GridLayout layout21 = new GridLayout(2, false);
		layout21.horizontalSpacing = 2;
		layout21.verticalSpacing = 0;
		layout21.marginTop = 0;
		compositePath.setLayout(layout21);
		GridData dataw = new GridData(GridData.FILL_HORIZONTAL);
		compositePath.setLayoutData(dataw);
		compositePath.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		txtDatabasePath = new Text(compositePath, SWT.BORDER | SWT.READ_ONLY);
		dataw = new GridData(GridData.FILL_HORIZONTAL);
		txtDatabasePath.setLayoutData(dataw);
		txtDatabasePath.setText("");
		Button btn1 = new Button(compositePath, SWT.NONE);
		btn1.setText("选择");
		btn1.setLayoutData(new GridData());

		btn1.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(compositePath.getShell());
				String path = dialog.open();
				txtDatabasePath.setText(path == null ? "" : path); //$NON-NLS-1$
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		txtName.setText(node.getUser() == null ? "" : node.getUser());
		txtPassword.setText(node.getPassword() == null ? "" : node.getPassword());
		txtPort.setText(node.getPort() == null ? "54321" : node.getPort());
		txtAddress.setText(node.getAddress() == null ? "localhost" : node.getAddress());
		txtDatabasePath.setText(node.getDatabasePath() == null ? "" : node.getDatabasePath());

		Composite compOpera = new Composite(parent, SWT.NONE);
		compOpera.setLayout(new GridLayout(3, false));
		GridData data111 = new GridData(GridData.FILL_HORIZONTAL);
		compOpera.setLayoutData(data111);
		Label label111 = new Label(compOpera, SWT.None);
		label111.setText("");
		data111 = new GridData(GridData.FILL_HORIZONTAL);
		data111.horizontalSpan = 1;
		label111.setLayoutData(data111);
		compOpera.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		label111.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		final Button btnConfirm = new Button(compOpera, SWT.PUSH);
		btnConfirm.setText("确定");
		data111 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnConfirm.setLayoutData(data111);
		((GridData) btnConfirm.getLayoutData()).widthHint = 61;
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {

				if (!check()) {
					return;
				}

				Connection connection = ConsoleView.getConnection(txtAddress.getText().trim(), txtPort.getText().trim(),
						txtName.getText().trim(), txtPassword.getText().trim(),UIUtils.getDatabase()); // $NON-NLS-1$
				if (connection == null) {
					MessageDialog.openInformation(parent.getShell(), "提示", "服务器连接失败，请检查");
					return;
				} else {
					node.setConnection(connection);
				}
				if (node.getAddress() != null) {
					try {
						if (node.getConnection() != null) {
							node.getConnection().close();
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					MessageDialog.openInformation(parent.getShell(), "提示", "服务器信息更新成功!");
				} else {
					MessageDialog.openInformation(parent.getShell(), "提示", "服务器信息新建成功!");
				}
				
				node.setAddress(txtAddress.getText().trim());
				node.setUser(txtName.getText().trim());
				node.setPassword(txtPassword.getText().trim());
				node.setPort(txtPort.getText().trim());
				node.setDatabase(UIUtils.getDatabase());
				node.setDriverName(driverName);
				node.setDriverPath(driverPath);
				node.setDatabasePath(txtDatabasePath.getText().trim());
				input.getTreeView().refresh();
				closeEditor();
			}
		});

		Button btnCancel = new Button(compOpera, SWT.PUSH);
		btnCancel.setText("取消");
		data111 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnCancel.setLayoutData(data111);
		((GridData) btnCancel.getLayoutData()).widthHint = 61;

		btnCancel.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				closeEditor();
			}
		});
	}

	protected boolean check() {
		if (txtName.getText().trim().equals("")) {
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", "用户名不能为空");
			txtName.setFocus();
			return false;
		}
		if (txtPassword.getText().trim().equals("")) {
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", "用户密码不能为空");
			txtPassword.setFocus();
			return false;
		}
		if (txtAddress.getText().trim().equals("")) {
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", "IP地址不能为空");
			txtAddress.setFocus();
			return false;
		}
		if (txtPort.getText().trim().equals("")) {
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", "端口号不能为空");
			txtPort.setFocus();
			return false;
		}
		return true;
	}

	private void closeEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeEditor(page.getActiveEditor(), true);
	}

	
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
