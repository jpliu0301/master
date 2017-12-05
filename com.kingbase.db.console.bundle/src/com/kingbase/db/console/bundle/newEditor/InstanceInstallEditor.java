package com.kingbase.db.console.bundle.newEditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
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

import com.kingbase.db.console.bundle.views.ConsoleView;
import com.kingbase.db.core.util.UIUtils;

public class InstanceInstallEditor extends EditorPart {
	public static final String ID = "com.kingbase.db.console.bundle.newEditor.InstanceInstallEditor";
	private Text txtPort;
	private Text txtDatabase;
	private Text txtAddress;
	private Text txtPassword;
	private Text txtName;
	private FormToolkit toolkit;
	private StyledText txtContent;

	public InstanceInstallEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// TODO Auto-generated method stub
		setSite(site);
		setInput(input);
		setPartName(input.getName());
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

		Group group11 = new Group(form.getBody(), SWT.NONE);
		group11.setText("账号信息");
		group11.setLayout(new GridLayout());
		group11.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group11.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Composite comp = new Composite(group11, SWT.NONE);
		GridLayout comp_gl = new GridLayout(2, false);
		GridData comp_gd = new GridData(GridData.FILL_HORIZONTAL);
		comp_gl.verticalSpacing = 10;
		comp.setLayout(comp_gl);
		comp.setLayoutData(comp_gd);
		comp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Label lbName = new Label(comp, SWT.NONE);
		lbName.setText("用户名：");
		lbName.setLayoutData(new GridData());
		txtName = new Text(comp, SWT.BORDER);
		GridData data11 = new GridData(GridData.FILL_HORIZONTAL);
		txtName.setLayoutData(data11);
		txtName.setTextLimit(63);
		UIUtils.verifyTextNotSpace(txtName);
		lbName.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Label lbPassword = new Label(comp, SWT.NONE);
		lbPassword.setText("密码：");
		lbPassword.setLayoutData(new GridData());
		txtPassword = new Text(comp, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(data11);
		txtPassword.setTextLimit(63);
		lbPassword.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Label lbAddress = new Label(comp, SWT.NONE);
		lbAddress.setText("IP地址：");
		lbAddress.setLayoutData(new GridData());
		txtAddress = new Text(comp, SWT.BORDER);
		txtAddress.setText("localhost");
		txtAddress.setLayoutData(data11);
		txtAddress.setTextLimit(63);
		txtAddress.setEnabled(false);
		lbAddress.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Label lbPort = new Label(comp, SWT.NONE);
		lbPort.setText("Port：");
		lbPort.setLayoutData(new GridData());
		txtPort = new Text(comp, SWT.BORDER);
		txtPort.setText("54321");
		txtPort.setLayoutData(data11);
		txtPort.setTextLimit(63);
		UIUtils.verifyTextNumber(txtPort);
		lbPort.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		
		Label lbDatabase = new Label(comp, SWT.NONE);
		lbDatabase.setText("Database：");
		lbDatabase.setLayoutData(new GridData());
		txtDatabase = new Text(comp, SWT.BORDER);
		txtDatabase.setText("TEST");
		txtDatabase.setLayoutData(data11);
		txtDatabase.setTextLimit(63);
		lbDatabase.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Group group2 = new Group(form.getBody(), SWT.WRAP);
		group2.setText(".ctl文件内容");
		GridLayout layout1 = new GridLayout(1, false);
		group2.setLayout(layout1);
		group2.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		GridData data3 = new GridData(GridData.FILL_BOTH);
		group2.setLayoutData(data3);

		txtContent = new StyledText(group2, SWT.V_SCROLL | SWT.BORDER);
		txtContent.setText("");
		txtContent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite compOpera = new Composite(form.getBody(), SWT.NONE);
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
		btnConfirm.setText("加载");
		data111 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnConfirm.setLayoutData(data111);
		((GridData) btnConfirm.getLayoutData()).widthHint = 61;
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String content = txtContent.getText();
				if (content.trim().equals("")) {
					txtContent.setFocus();
					MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "请手动输入.ctl控制文件内容");
					return;
				}
				if (txtDatabase.getText().trim().equals("")) {
					txtDatabase.setFocus();
					MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "请填写数据库名称");
					return;
				}

				File blukload = blukload(content.getBytes());

				Connection connection = ConsoleView.getConnection(txtAddress.getText(), txtPort.getText(),
						txtName.getText(), txtPassword.getText(),txtDatabase.getText());

				if (connection != null) {

					try {
						Statement state = connection.createStatement();
						ResultSet result = state.executeQuery("SELECT BULKLOAD('" + blukload.toString() + "')");
						String str = null;
						while (result.next()) {
							str = result.getString(1);
						}
						if (str != null && !str.equals("")) {
							MessageDialog.openConfirm(UIUtils.getActiveShell(), "提示", "实例加载成功");
						}
					} catch (Exception e1) {
						blukload.delete();
						MessageDialog.openConfirm(UIUtils.getActiveShell(), "提示",e1.getMessage());
						return;
					}
				}
				blukload.delete();
//				closeEditor();
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

	protected File blukload(byte[] content) {

		IPath path = new Path(
				ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator + "bukload");
		File filedir = new File(path.toOSString());
		if (!filedir.exists()) {
			try {
				filedir.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fop;
		try {
			fop = new FileOutputStream(filedir);
			byte[] contentInBytes = content;

			fop.write(contentInBytes);
			fop.write('\n');
			fop.flush();
			fop.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filedir;
	}

	private void closeEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeEditor(page.getActiveEditor(), true);
	}

	@Override
	public void setFocus() {
	}

}
