package com.kingbase.db.deploy.bundle.editor.page;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.kingbase.db.core.util.PickerShellTableSort;
import com.kingbase.db.core.util.SafeProperties;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.deploy.bundle.model.tree.CNodeEntity;
import com.kingbase.db.deploy.bundle.model.tree.CReadWriteEntity;
import com.kingbase.db.deploy.bundle.model.tree.KeyValueEntity;
import com.kingbase.db.deploy.bundle.model.tree.KeyValueModifier;
import com.kingbase.db.deploy.bundle.model.tree.PoolEntity;
import com.kingbase.db.deploy.bundle.model.tree.TableNodeEntity;
import com.kingbase.db.deploy.bundle.model.tree.TableNodeModifier1;

public class ReadWriteSecondPage {

	private TableViewer tv2; // 第步表格
	private TableViewer tv4; // kingbasecluster.conf属性表格
	private PoolEntity entity = new PoolEntity();
	private Table table;
	private Text txtdb;
	private String txtdbName;
	private Button btnBase;
	private Button btnHig;
	private Text txt1;
	private Text txt2;
	private Text txt3;
	private Text txt4;
	private Text txt5;
//	private Text txt6;
	private Text txt7;
	private Text txt8;
	private Text txtpost;
	private Table tableVar;
	private CNodeEntity[] models;
	private String fileSeparator = System.getProperty("file.separator");

	public PoolEntity getEntity() {
		List<TableNodeEntity> list = new ArrayList<TableNodeEntity>();

		for (int i = 0; i < table.getItems().length; i++) {
			TableItem item = table.getItems()[i];
			list.add((TableNodeEntity) item.getData());
		}
		if (models.length > 0) {
			for (TableNodeEntity entity : list) {
				for (CNodeEntity node : models) {
					if (entity.getPhysicalMachine().equals(node.getName())) {
						entity.setNodeEntity(node);
					}
				}
			}
		}
		entity.setListPool(list);
		entity.setPoolzipPath(txtdb.getText());
		entity.setPoolzipName(txtdbName);
		if (btnBase.getSelection()) {
			entity.setIsPoolBase(true);
		} else {
			entity.setIsPoolBase(false);
		}
		entity.setDelegate_IP(txt1.getText());
		entity.setPort(txt2.getText());
		entity.setPcp_port(txt3.getText());
		entity.setWd_port(txt5.getText());
//		entity.setNetcard(txt6.getText());
		entity.setPcpUser(txt7.getText());
		entity.setPcpPass(txt8.getText());

		entity.setPoolPath(txtpost.getText());

		List<KeyValueEntity> listKey = new ArrayList<KeyValueEntity>();
		for (int i = 0; i < tableVar.getItems().length; i++) {
			TableItem item = tableVar.getItems()[i];
			listKey.add((KeyValueEntity) item.getData());
		}
		entity.setListKey2(listKey);
		return entity;
	}

	public Composite getCom1(Composite composite1, String[] nodeName,
			CNodeEntity[] models, CReadWriteEntity oldReadEntity, String type) {
		this.models = models;
		Composite comm1 = new Composite(composite1, SWT.NONE);
		comm1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		comm1.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		comm1.setLayoutData(gd);
		comm1.setVisible(false);
		gd.exclude = true;
		Group groupNode = new Group(comm1, SWT.NONE);
		groupNode.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		groupNode.setText("kingbaseCluster配置");
		groupNode.setLayout(new GridLayout());
		GridData dataGroup = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		dataGroup.horizontalSpan = 2;
		dataGroup.heightHint = 210;
		groupNode.setLayoutData(dataGroup);
		Composite comAdd = new Composite(groupNode, SWT.NONE);
		comAdd.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		comAdd.setLayout(new GridLayout(2, false));
		comAdd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		table = new Table(comAdd, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayout(new GridLayout());
		dataGroup = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		dataGroup.heightHint = 170;
		table.setLayoutData(dataGroup);
		String[] string = new String[] { "pool", "物理机器", "节点类型", "监听地址", "监听端口","网卡名" };
		for (int i = 0; i < string.length; i++) {
			TableColumn column = new TableColumn(table, SWT.CENTER);
			column.setText(string[i]);
			if (i == 3 || i == 4) {
				column.setWidth(0);
			} else {
				column.setWidth(250);
			}
		}

		tv2 = new TableViewer(table);
		tv2.setContentProvider(new ContentProvider());
		tv2.setLabelProvider(new TableLabelProvider());

		tv2.setColumnProperties(TableNodeModifier1.PROP_NAME);

		final CellEditor[] editors2 = new CellEditor[5];
		editors2[0] = new ComboBoxCellEditor(tv2.getTable(),
				TableNodeModifier1.TWOLIBRARY, SWT.READ_ONLY);
		editors2[1] = new ComboBoxCellEditor(tv2.getTable(), nodeName,
				SWT.READ_ONLY);
		editors2[2] = new ComboBoxCellEditor(tv2.getTable(),
				TableNodeModifier1.TWONODETYPE, SWT.READ_ONLY);
		editors2[3] = new TextCellEditor(tv2.getTable());
		editors2[4] = new TextCellEditor(tv2.getTable());
		tv2.setCellModifier(new TableNodeModifier1(tv2, nodeName));
		tv2.setCellEditors(editors2);

		Composite composite = new Composite(comAdd, SWT.NONE);
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		dataGroup = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(dataGroup);
		Button btn1 = new Button(composite, SWT.PUSH);
		btn1.setText("新增");
		btn1.setLayoutData(new GridData());
		((GridData) btn1.getLayoutData()).widthHint = 61;
		btn1.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				TableItem[] items = table.getItems();
				if (items.length > 0) {
					List<TableNodeEntity> list = new ArrayList<TableNodeEntity>();
					for (int i = 0; i < table.getItems().length; i++) {
						TableItem item = table.getItems()[i];
						list.add((TableNodeEntity) item.getData());
					}

					int k = 0;

					for (int i = 0; i < list.size(); i++) {
						TableNodeEntity entity = list.get(i);
						if (entity.getLibrary().equals("主分发")) {
							k++;
						}
					}
					if (k == 0) {
						TableNodeEntity entity = new TableNodeEntity();
						entity.setLibrary("主分发");
						if (nodeName != null && nodeName.length > 0) {
							entity.setPhysicalMachine(nodeName[0]);
						}
						entity.setNodeType("主pool");
						entity.setListenerAddress("");
						entity.setListenerPost("");
						entity.setNetcard("eth0");
						tv2.add(entity);
					} else {
						TableNodeEntity entity = new TableNodeEntity();
						entity.setLibrary("备分发");
						if (nodeName != null && nodeName.length > 0) {
							entity.setPhysicalMachine(nodeName[0]);
						}
						entity.setNodeType("备pool");
						entity.setListenerAddress("");
						entity.setListenerPost("");
						entity.setNetcard("eth0");
						tv2.add(entity);
					}

				} else {

					TableNodeEntity entity = new TableNodeEntity();
					entity.setLibrary("主分发");
					if (nodeName != null && nodeName.length > 0) {
						entity.setPhysicalMachine(nodeName[0]);
					}
					entity.setNodeType("主pool");
					entity.setListenerAddress("");
					entity.setListenerPost("");
					entity.setNetcard("eth0");
					tv2.add(entity);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		Button btn2 = new Button(composite, SWT.PUSH);
		btn2.setText("删除");
		btn2.setLayoutData(new GridData());
		((GridData) btn2.getLayoutData()).widthHint = 61;
		btn2.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				ISelection selection = tv2.getSelection();
				if (selection instanceof IStructuredSelection) {
					tv2.remove(((IStructuredSelection) selection)
							.getFirstElement());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Group groupVar = new Group(comm1, SWT.NONE);
		groupVar.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		groupVar.setText("参数设置");
		GridLayout gl1 = new GridLayout(3, false);
		gl1.marginTop = 8;
		gl1.marginLeft = 22;
		gl1.marginRight = 7;
		gl1.marginBottom = 11;
		gl1.verticalSpacing = 15;
		groupVar.setLayout(gl1);
		GridData dataGroup1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataGroup1.horizontalSpan = 2;
		groupVar.setLayoutData(dataGroup1);

		Label ladb = new Label(groupVar, SWT.NONE);
		ladb.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		ladb.setText("选择kingbaseCluster的zip包: ");
		txtdb = new Text(groupVar, SWT.BORDER);
		GridData griddb = new GridData();
		griddb.widthHint = 350;
		txtdb.setLayoutData(griddb);
		Button btndb = new Button(groupVar, SWT.NONE);
		btndb.setText("选择");
		btndb.setLayoutData(new GridData());
		((GridData) btndb.getLayoutData()).widthHint = 61;
		btndb.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(groupVar.getShell());
				dialog.setFilterExtensions(new String[] { "*.zip" });
				String open = dialog.open();
				if (open == null || open.equals("")) {
					txtdb.setText("");
					return;
				}
				txtdbName = dialog.getFileName();
				txtdb.setText(dialog.getFilterPath() + fileSeparator
						+ dialog.getFileName());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		btnBase = new Button(groupVar, SWT.RADIO);
		btnBase.setText("基础属性");
		btnBase.setSelection(true);

		btnHig = new Button(groupVar, SWT.RADIO);
		btnHig.setText("高级属性");
		btnBase.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		btnHig.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		Composite comAll = new Composite(groupVar, SWT.NONE);
		comAll.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		GridData gridDataC = new GridData(GridData.FILL_HORIZONTAL);
		gridDataC.horizontalSpan = 3;
		comAll.setLayout(new GridLayout());
		comAll.setLayoutData(gridDataC);

		Composite comp = new Composite(comAll, SWT.NONE);
		comp.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		GridData da = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(da);
		GridLayout layC = new GridLayout(4, false);
		layC.verticalSpacing = 11;
		layC.horizontalSpacing = 11;
		comp.setLayout(layC);

		Label lb1 = new Label(comp, SWT.NONE);
		lb1.setText("delegate_IP");
		GridData gridt = new GridData(GridData.FILL_HORIZONTAL);
		txt1 = new Text(comp, SWT.BORDER);
		txt1.setLayoutData(gridt);

		Label lb2 = new Label(comp, SWT.NONE);
		lb2.setText("port");
		txt2 = new Text(comp, SWT.BORDER);
		txt2.setLayoutData(gridt);
		txt2.setText("9999");

		Label lb3 = new Label(comp, SWT.NONE);
		lb3.setText("pcp_port");
		txt3 = new Text(comp, SWT.BORDER);
		txt3.setLayoutData(gridt);
		txt3.setText("9898");

		Label lb4 = new Label(comp, SWT.NONE);
		lb4.setText("check_db");
		txt4 = new Text(comp, SWT.BORDER);
		txt4.setLayoutData(gridt);
		txt4.setText("template1");

		Label lb5 = new Label(comp, SWT.NONE);
		lb5.setText("wd_port");
		txt5 = new Text(comp, SWT.BORDER);
		txt5.setLayoutData(gridt);
		txt5.setText("9000");

//		Label lb6 = new Label(comp, SWT.NONE);
//		lb6.setText("netcard");
//		txt6 = new Text(comp, SWT.BORDER);
//		txt6.setLayoutData(gridt);
//		txt6.setText("eth0");

		Label lb7 = new Label(comp, SWT.NONE);
		lb7.setText("pcp账号");
		txt7 = new Text(comp, SWT.BORDER);
		txt7.setLayoutData(gridt);
		txt7.setText("kingbase");

		Label lb8 = new Label(comp, SWT.NONE);
		lb8.setText("pcp密码(默认123456)");
		if(type.equals("update")){
			lb8.setText("pcp密码");
		}
		txt8 = new Text(comp, SWT.BORDER);
		txt8.setLayoutData(gridt);
		txt8.setText("123456");
        txt8.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				lb8.setText("pcp密码");
			}
		});
		
		lb1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb4.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb5.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
//		lb6.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb7.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb8.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		UIUtils.verifyTextNumber(txt2);
		UIUtils.verifyTextNumber(txt3);
		UIUtils.verifyTextNumber(txt5);

		Composite comp1 = new Composite(comAll, SWT.NONE);
		comp1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		comp1.setLayoutData(gridDataC);
		comp1.setLayout(new GridLayout(3, false));
		comp1.setVisible(false);
		Label lapost = new Label(comp1, SWT.NONE);
		lapost.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lapost.setText("选择kingbasecluster.conf文件: ");
		txtpost = new Text(comp1, SWT.BORDER);
		txtpost.setLayoutData(griddb);
		Button btnpost = new Button(comp1, SWT.NONE);
		btnpost.setText("选择");
		btnpost.setLayoutData(new GridData());
		((GridData) btnpost.getLayoutData()).widthHint = 61;
		tableVar = new Table(comp1, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);

		btnpost.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(groupVar.getShell());
				dialog.setFilterExtensions(new String[] { "*.conf" });
				String open = dialog.open();
				if (open == null || open.equals("")) {
					txtpost.setText("");
					return;
				}
				txtpost.setText(dialog.getFilterPath() + fileSeparator
						+ dialog.getFileName());
				SafeProperties prop = new SafeProperties();
				try {
					InputStream fis = new FileInputStream(txtpost.getText());
					prop.load(fis);
					Object[] objs = prop.keySet().toArray();
					List<KeyValueEntity> listkv = new ArrayList<KeyValueEntity>();
					for (int i = 0; i < objs.length; i++) {
						KeyValueEntity entity = new KeyValueEntity();
						entity.setKey(objs[i].toString());
						String value = prop.getProperty(objs[i].toString());
						if (value != null && value != "" && value.contains("#")) {
							value = value.substring(0, value.indexOf("#"));
						}
						entity.setValue(value);
						entity.setOldValue(value);
						listkv.add(entity);
					}
					tv4.setInput(listkv);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		tableVar.setLinesVisible(true);
		tableVar.setHeaderVisible(true);
		tableVar.setLayout(new GridLayout());
		dataGroup = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		dataGroup.heightHint = 350;
		dataGroup.horizontalSpan = 3;
		tableVar.setLayoutData(dataGroup);
		String[] string1 = new String[] { "属性", "值" };
		for (int i = 0; i < string1.length; i++) {
			TableColumn column = new TableColumn(tableVar, SWT.NONE);
			column.setText(string1[i]);
			if (i == 0) {
				column.setWidth(150);
			} else {
				column.setWidth(300);
			}
		}

		tv4 = new TableViewer(tableVar);
		tv4.setContentProvider(new ContentProvider());
		tv4.setLabelProvider(new KeyValueProvider());
		tv4.setColumnProperties(KeyValueModifier.KEY_VALUE);

		final CellEditor[] editors3 = new CellEditor[2];
		editors3[0] = new TextCellEditor(tv4.getTable());
		editors3[1] = new TextCellEditor(tv4.getTable());
		tv4.setCellModifier(new KeyValueModifier(tv4));
		tv4.setCellEditors(editors3);
		new PickerShellTableSort(tableVar);
		
		btnBase.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (comp1.getVisible()) {
					comp.setVisible(true);
					comp1.setVisible(false);
					GridData gd3 = (GridData) comp1.getLayoutData();
					gd3.exclude = !gd3.exclude;
					comp1.layout();
					comp1.getParent().layout();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		btnHig.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (comp.getVisible()) {
					comp1.setVisible(true);
					comp.setVisible(false);
					GridData gd3 = (GridData) comp.getLayoutData();
					gd3.exclude = !gd3.exclude;
					comp.layout();
					comp.getParent().layout();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		if (oldReadEntity != null) {
			tv2.setInput(oldReadEntity.getPoolEntity().getListPool());
			txtdb.setText(oldReadEntity.getPoolEntity().getPoolzipPath());
			if (oldReadEntity.getPoolEntity().getIsPoolBase()) {
				btnBase.setSelection(true);
				txt1.setText(oldReadEntity.getPoolEntity().getDelegate_IP());
				txt2.setText(oldReadEntity.getPoolEntity().getPort());
				txt3.setText(oldReadEntity.getPoolEntity().getPcp_port());
				txt5.setText(oldReadEntity.getPoolEntity().getWd_port());
//				txt6.setText(oldReadEntity.getPoolEntity().getNetcard());
				txt7.setText(oldReadEntity.getPoolEntity().getPcpUser());
				txt8.setText(oldReadEntity.getPoolEntity().getPcpPass());

			} else {
				btnHig.setSelection(true);
				txtpost.setText(oldReadEntity.getPoolEntity().getPoolPath());
				tv4.setInput(oldReadEntity.getPoolEntity().getListKey2());
			}

		}
		if (type.equals("update")) {
			txt1.setEnabled(false);
			txt2.setEnabled(false);
			txt3.setEnabled(false);
			txt4.setEnabled(false);
			txt5.setEnabled(false);
//			txt6.setEnabled(false);
			txt7.setEnabled(false);
			txt8.setEnabled(false);
			txtdb.setEditable(false);
			txtpost.setEditable(false);
			btnpost.setEnabled(false);
			btndb.setEnabled(false);
			btn1.setEnabled(false);
			btn2.setEnabled(false);
			comp1.setEnabled(false);
			comAdd.setEnabled(false);
		}
		return comm1;
	}

}
