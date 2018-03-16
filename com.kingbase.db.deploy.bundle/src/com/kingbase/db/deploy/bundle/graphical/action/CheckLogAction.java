package com.kingbase.db.deploy.bundle.graphical.action;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.JschUtil;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.deploy.bundle.KBDeployCore;
import com.kingbase.db.deploy.bundle.graphical.editor.CreateMasterStatusEditor;
import com.kingbase.db.deploy.bundle.model.tree.CNodeEntity;

public class CheckLogAction extends SelectionAction {
	private CNodeEntity entity;
	private String type;
	private Map<String, Session> sessionMap;

	public CheckLogAction(IWorkbenchPart part, CNodeEntity entity, String type, CreateMasterStatusEditor editor) {
		super(part);
		setId(UUID.randomUUID().toString());
		this.entity = entity;
		this.type = type;
		this.sessionMap = editor.getContainerModel().getSessionMap();

		setText("查看 " + type);
		setImageDescriptor(ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.replication));
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public void run() {
		super.run();
		Session session = null;
		Set<String> keySet = sessionMap.keySet();
		for (String key : keySet) {
			String str = entity.getIp() + "_" + entity.getPort() + "_" + entity.getRootPass();
			if (str.equals(key)) {
				session = sessionMap.get(key);
				break;
			}
		}
		if (session != null && !session.isConnected()) {
			try {
				session.connect(15000);
			} catch (JSchException e) {
				e.printStackTrace();
			}
		}

		ChannelExec exec = JschUtil.execCommand(session, "cat /tmp/" + type);
		String values = JschUtil.returnInputStream(exec);
		CheckLogDialog dialog = new CheckLogDialog(UIUtils.getActiveShell(), values);
		dialog.open();
		
		if (exec != null) {
			exec.disconnect();
		}

	}

	class CheckLogDialog extends Dialog {

		private Shell shell;
		private Text txt;
		private String values;

		public CheckLogDialog(Shell shell, String values) {
			super(shell);
			this.shell = shell;
			this.values = values;

			createDialog();
		}

		private void createDialog() {

			shell = new Shell(getParent(), SWT.SHELL_TRIM|SWT.MODELESS | SWT.RESIZE);
			shell.setText(type);
			shell.setImage(ImageURL.createImage(KBDeployCore.PLUGIN_ID, ImageURL.replication));
			final GridLayout gridLayout_31 = new GridLayout();

			gridLayout_31.verticalSpacing = 0;
			gridLayout_31.marginWidth = 0;
			gridLayout_31.marginHeight = 0;
			gridLayout_31.horizontalSpacing = 0;
			shell.setLayout(gridLayout_31);
			shell.setSize(500, 630);
			if (shell != null) {// 居中
				Monitor[] monitorArray = Display.getCurrent().getMonitors();
				Rectangle rectangle = monitorArray[0].getBounds();
				Point size = shell.getSize();
				shell.setLocation((rectangle.width - size.x) / 2, (rectangle.height - size.y) / 2);
			}

			Group groupNode = new Group(shell, SWT.NONE);
			groupNode.setText("日志信息");
			groupNode.setLayout(new GridLayout());
			GridData dataGroup = new GridData(SWT.FILL, SWT.FILL, true, true);
			dataGroup.horizontalSpan = 2;
			groupNode.setLayoutData(dataGroup);

			txt = new Text(groupNode, SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			txt.setLayoutData(data);
			txt.setEditable(false);
			txt.setText(values);

			Composite compOpera = new Composite(shell, SWT.None);
			compOpera.setLayout(new GridLayout(2, false));
			GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
			compOpera.setLayoutData(data1);
			Label label = new Label(compOpera, SWT.None);
			data1 = new GridData(GridData.FILL_HORIZONTAL);
			label.setLayoutData(data1);

			Button btnClose = new Button(compOpera, SWT.PUSH);
			btnClose.setText("关闭");
			GridData button_gd111 = new GridData(GridData.HORIZONTAL_ALIGN_END);
			button_gd111.widthHint = 65;
			btnClose.setLayoutData(button_gd111);
			btnClose.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					shell.dispose();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}

		public void open() {
			shell.open();
			shell.layout();
			Display display = getParent().getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		}
	}

}
