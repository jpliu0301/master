package com.kingbase.db.console.bundle.newEditor;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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

import com.kingbase.db.console.bundle.views.ContentProvider;
import com.kingbase.db.core.util.UIUtils;

public class InstanceUninstallEditor extends EditorPart {
	public static final String ID = "com.kingbase.db.console.bundle.newEditor.InstanceUninstallEditor";
	private FormToolkit toolkit;
	private Text txtDatabasePath;
	private final List<String> commands = new ArrayList<String>();
	private List<String> listFile = new ArrayList<String>();
	private CheckboxTableViewer tableView;

	public InstanceUninstallEditor() {
		// TODO Auto-generated constructor stub
	}

	
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// TODO Auto-generated method stub
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}

	public String getToolTipText() {
        // TODO Auto-generated method stub
        return "111";
    }
	
	
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	
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
		group1.setText("实例信息");
		group1.setLayout(new GridLayout());
		group1.setLayoutData(new GridData(GridData.FILL_BOTH));
		group1.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Composite compositeInfo = new Composite(group1, SWT.NONE);
		GridLayout layout1 = new GridLayout(2, false);
		compositeInfo.setLayout(layout1);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		compositeInfo.setLayoutData(data);
		compositeInfo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

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
				if (path != null && !path.equals("")) {
					commands.clear();
					commands.add(path + "/kingbase");
					commands.add("--list");

					execCommand(commands);
					if(listFile.size()>0){
						tableView.setInput(listFile);
					}
				}
			}

			
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
//		checkboxTableViewer = new CheckboxTableViewer(table);
		data = new GridData(GridData.FILL_BOTH);
		final Table table = new Table(group1, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK | SWT.MULTI);
		table.setLayoutData(data);
		tableView = new CheckboxTableViewer(table);

//		Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();
//		int width = screenSize.width;
		// 设置表列
		TableColumn choice = new TableColumn(table, SWT.BORDER);
		  choice.setText(" ");
		  choice.setWidth(70);
		  choice.setAlignment(SWT.CENTER);
		
		 TableColumn ideal = new TableColumn(table, SWT.BORDER);
		  ideal.setAlignment(SWT.CENTER);
		  ideal.setText("实例信息");
		  ideal.setWidth(140);

		tableView.setContentProvider(new ContentProvider());
		tableView.setLabelProvider(new TableSessionProvider());
		table.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableView.getSelection();
				Object obj = selection.getFirstElement();
				TableItem[] items = tableView.getTable().getItems();
				for (TableItem item : items) {
					item.setChecked(false);
				}
				tableView.setChecked(obj, true);
			}
		});

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
		btnConfirm.setText("卸载");
		data111 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnConfirm.setLayoutData(data111);
		((GridData) btnConfirm.getLayoutData()).widthHint = 61;
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {

				if (txtDatabasePath.getText().trim().equals("")) {
					MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "请选择数据库安装目录");
					return ;
				}
				Object[] objects = tableView.getCheckedElements();
				for (Object item : objects) {
						String data = (String) item;
						commands.clear();
						commands.add(txtDatabasePath.getText() + "/kingbase");
						commands.add("--uninstall");
						commands.add((data.split(" "))[0]);
						execCommand1(commands);
				}

				commands.clear();
				commands.add(txtDatabasePath.getText() + "/kingbase"); 
				commands.add("--list");

				execCommand(commands);
				if (listFile.size() > 0) {
					tableView.setInput(null);
					tableView.setInput(listFile);
				}
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

	private void execCommand(List<String> buffer) {
		listFile.clear();
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
				listFile.add(line);
			}

			int exitValue = process.waitFor();
//			if (exitValue != 0) {
//				listFile = new ArrayList<String>();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void execCommand1(List<String> buffer) {
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
				MessageDialog.openError(UIUtils.getActiveShell(), "错误",
						"错误日志： \n" + errorBuffer.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void closeEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeEditor(page.getActiveEditor(), true);
	}
	
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	class TableSessionProvider extends LabelProvider implements ITableLabelProvider {

		
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		
		public String getColumnText(Object element, int columnIndex) {
			String str =(String) element;
			switch (columnIndex) {
			case 0:
				return "";
			case 1:
				return str;

			default:
				return "";
			}
		}

	}
}
