package com.kingbase.db.console.bundle.newEditor;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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

import com.kingbase.db.console.bundle.model.tree.ServerEntity;
import com.kingbase.db.console.bundle.views.ContentProvider;

public class ServiceStatusEditor extends EditorPart {
	public static final String ID = "com.kingbase.db.console.bundle.newEditor.ServiceStatusEditor";
	private FormToolkit toolkit;
	private Text txtDatabasePath;
	private TableViewer tableView;
	private final List<String> commands = new ArrayList<String>();
	private List<ServerEntity> listLog = new ArrayList<ServerEntity>();
	private List<ServerEntity> listBase = new ArrayList<ServerEntity>();
	private Timer timer;

	public ServiceStatusEditor() {
		// TODO Auto-generated constructor stub
	}

	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO Auto-generated method stub
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		initListLog();
	}

	private void refresh() {
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		long period = Integer.valueOf(5) * 1000;
		timer.schedule(new AutoRefreshThread(), period, period);
	}

	class AutoRefreshThread extends TimerTask {

		@Override
		public void run() {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					getType();
				}
			});
		}

	}

	protected void getType() {

		System.out.println("=-===");
		listBase.clear();
		listBase.addAll(listLog);
		commands.clear();
		commands.add("pidof");
		commands.add("kingbase");
		boolean execCommand = execCommand(commands);
		if (execCommand) {
			listBase.get(0).setResult("开");
		} else {
			listBase.get(0).setResult("关");
		}

		commands.clear();
		commands.add("pidof");
		commands.add("sys_audit");
		boolean execCommand2 = execCommand(commands);
		if (execCommand2) {
			listBase.get(1).setResult("开");
		} else {
			listBase.get(1).setResult("关");
		}

		commands.clear();
		commands.add("pidof");
		commands.add("job_manager");
		boolean execCommand3 = execCommand(commands);
		if (execCommand3) {
			listBase.get(2).setResult("开");
		} else {
			listBase.get(2).setResult("关");
		}

		tableView.setInput(listBase);
		tableView.refresh();
	}

	public void initListLog() {
		listLog.add(new ServerEntity("1", "数据库", ""));
		listLog.add(new ServerEntity("2", "审计服务", ""));
		listLog.add(new ServerEntity("3", "任务服务", ""));
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
		parent.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));

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
		group1.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));

		Composite compositeInfo = new Composite(group1, SWT.NONE);
		GridLayout layout1 = new GridLayout(2, false);
		compositeInfo.setLayout(layout1);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		compositeInfo.setLayoutData(data);
		compositeInfo.setBackground(Display.getDefault().getSystemColor( SWT.COLOR_WHITE));

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
		compositePath.setBackground(Display.getDefault().getSystemColor( SWT.COLOR_WHITE));

		txtDatabasePath = new Text(compositePath, SWT.BORDER | SWT.READ_ONLY);
		dataw = new GridData(GridData.FILL_HORIZONTAL);
		txtDatabasePath.setLayoutData(dataw);
		txtDatabasePath.setText("");
		Button btn1 = new Button(compositePath, SWT.NONE);
		btn1.setText("选择");
		btn1.setLayoutData(new GridData());

		btn1.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(compositePath
						.getShell());
				String path = dialog.open();
				txtDatabasePath.setText(path == null ? "" : path); //$NON-NLS-1$
				if (path != null && !path.equals("")) {
					getType();
					refresh();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		tableView = new TableViewer(group1, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.BORDER);
		data = new GridData(GridData.FILL_BOTH);
		final Table table = tableView.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(data);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width;
		// 设置表列
		String[] string = new String[] { "服务类型", "状态" };
		for (int i = 0; i < string.length; i++) {
			TableColumn column = new TableColumn(table, SWT.CENTER);
			column.setText(string[i]);
			if (i == 0) {
				column.setWidth(350);
			} else if (i == 1) {
				column.setWidth(100);
			}
		}

		tableView.setContentProvider(new ContentProvider());
		tableView.setLabelProvider(new TableSessionProvider());
		tableView.setInput(listLog);

		Composite compOpera = new Composite(parent, SWT.NONE);
		compOpera.setLayout(new GridLayout(2, false));
		GridData data111 = new GridData(GridData.FILL_HORIZONTAL);
		compOpera.setLayoutData(data111);
		Label label111 = new Label(compOpera, SWT.None);
		label111.setText("");
		data111 = new GridData(GridData.FILL_HORIZONTAL);
		data111.horizontalSpan = 1;
		label111.setLayoutData(data111);
		compOpera.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		label111.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));

		Button btnCancel = new Button(compOpera, SWT.PUSH);
		btnCancel.setText("关闭");
		data111 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnCancel.setLayoutData(data111);
		((GridData) btnCancel.getLayoutData()).widthHint = 61;

		btnCancel.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				closeEditor();
			}
		});
	}

	private void closeEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		page.closeEditor(page.getActiveEditor(), true);
		if(timer!=null){
			timer.cancel();
		}
	}
	@Override
	public void dispose() {
		super.dispose();
		if (timer != null) {
			timer.cancel();
		}
	}

	private boolean execCommand(List<String> buffer) {
		ProcessBuilder builder = new ProcessBuilder(buffer);
		builder.redirectErrorStream(true);
		Process process;
		try {
			process = builder.start();
			// Read out dir output
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			int exitValue = process.waitFor();
			if (exitValue != 0) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void setFocus() {
	}

	class TableSessionProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			ServerEntity entity = (ServerEntity) element;
			switch (columnIndex) {
			case 0:
				return entity.getType();
			case 1:
				return entity.getResult();

			default:
				return "";
			}
		}

	}

}
