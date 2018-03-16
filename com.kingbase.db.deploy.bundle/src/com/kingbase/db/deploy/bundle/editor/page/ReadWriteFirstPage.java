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
import com.kingbase.db.deploy.bundle.model.tree.PosEntity;
import com.kingbase.db.deploy.bundle.model.tree.TableNodeEntity;
import com.kingbase.db.deploy.bundle.model.tree.TableNodeModifier;

public class ReadWriteFirstPage {

	private TableViewer tv1; // 第一步表格
	private TableViewer tv3; // kingbase.conf属性表格
	private PosEntity entity = new PosEntity();
	private Text txtn;
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
	private Text txt6;
	private Text txt7;
	private Text txtpost;
	private Table tableVar;
	private CNodeEntity[] models;
	private String fileSeparator = System.getProperty("file.separator");
	private Text txt9;
	private Text txt10;
	private Button caseBtn;
	private Text txt12;

	public Composite getCom(Composite composite1, String[] nodeName,
			CNodeEntity[] models, CReadWriteEntity oldReadEntity, String type) {
		this.models = models;
		Composite comm1 = new Composite(composite1, SWT.NONE);
		comm1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		comm1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		comm1.setLayout(new GridLayout(2, false));

		Label lbn = new Label(comm1, SWT.NONE);
		lbn.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lbn.setText("集群名称:");
		lbn.setLayoutData(new GridData());
		txtn = new Text(comm1, SWT.BORDER);
		GridData d = new GridData();
		d.widthHint = 300;
		txtn.setLayoutData(d);
		UIUtils.verifyText(txtn);

		Group groupNode = new Group(comm1, SWT.NONE);
		groupNode.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		groupNode.setText("数据库配置");
		groupNode.setLayout(new GridLayout());
		GridData dataGroup = new GridData(SWT.FILL, SWT.FILL, true, false);
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
		dataGroup = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataGroup.heightHint = 170;
		table.setLayoutData(dataGroup);
		String[] string = new String[] { "库", "物理机器", "节点类型", "监听地址", "监听端口" };
		for (int i = 0; i < string.length; i++) {
			TableColumn column = new TableColumn(table, SWT.CENTER);
			column.setText(string[i]);
			column.setWidth(150);
		}

		tv1 = new TableViewer(table);
		tv1.setContentProvider(new ContentProvider());
		tv1.setLabelProvider(new TableLabelProvider());

		tv1.setColumnProperties(TableNodeModifier.PROP_NAME);

		final CellEditor[] editors2 = new CellEditor[5];
		editors2[0] = new ComboBoxCellEditor(tv1.getTable(),
				TableNodeModifier.ONELIBRARY, SWT.READ_ONLY);
		editors2[1] = new ComboBoxCellEditor(tv1.getTable(), nodeName,
				SWT.READ_ONLY);
		editors2[2] = new ComboBoxCellEditor(tv1.getTable(),
				TableNodeModifier.ONENODETYPE, SWT.READ_ONLY);
		editors2[3] = new TextCellEditor(tv1.getTable());
		editors2[4] = new TextCellEditor(tv1.getTable());
		tv1.setCellModifier(new TableNodeModifier(tv1, nodeName));
		tv1.setCellEditors(editors2);

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
			int m = 54321;

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
						if (entity.getLibrary().equals("主库")) {
							k++;
						}
					}
					if (k == 0) {
						TableNodeEntity entity = new TableNodeEntity();
						entity.setLibrary("主库");
						if (nodeName != null && nodeName.length > 0) {
							entity.setPhysicalMachine(nodeName[0]);
						}
						entity.setNodeType("主backend");
						entity.setListenerAddress("*");
						entity.setListenerPost(m + "");
						m++;
						tv1.add(entity);
					} else {
						TableNodeEntity entity = new TableNodeEntity();
						entity.setLibrary("备库");
						if (nodeName != null && nodeName.length > 0) {
							entity.setPhysicalMachine(nodeName[0]);
						}
						entity.setNodeType("备backend");
						entity.setListenerAddress("*");
						entity.setListenerPost(m + "");
						m++;
						tv1.add(entity);
					}

				} else {

					TableNodeEntity entity = new TableNodeEntity();
					entity.setLibrary("主库");
					if (nodeName != null && nodeName.length > 0) {
						entity.setPhysicalMachine(nodeName[0]);
					}
					entity.setNodeType("主backend");
					entity.setListenerAddress("*");
					entity.setListenerPost(m + "");
					m++;
					tv1.add(entity);
				}
				TableItem[] items2 = table.getItems();
				txt1.setText(2 * items2.length + "");
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
				ISelection selection = tv1.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection sselection = (IStructuredSelection) selection;
					tv1.remove(sselection.getFirstElement());
					TableItem[] items2 = table.getItems();
					txt1.setText(2 * items2.length + "");
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
		ladb.setText("选择数据库zip包: ");
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
					return ;
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
		btnHig.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		btnBase.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

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
		lb1.setText("max_wal_senders");
		GridData gridt = new GridData(GridData.FILL_HORIZONTAL);
		txt1 = new Text(comp, SWT.BORDER);
		txt1.setLayoutData(gridt);

		Label lb2 = new Label(comp, SWT.NONE);
		lb2.setText("max_standby_archive_delay");
		txt2 = new Text(comp, SWT.BORDER);
		txt2.setLayoutData(gridt);

		Label lb3 = new Label(comp, SWT.NONE);
		lb3.setText("wal_keep_segments");
		txt3 = new Text(comp, SWT.BORDER);
		txt3.setLayoutData(gridt);

		Label lb4 = new Label(comp, SWT.NONE);
		lb4.setText("max_standby_streaming_delay");
		txt4 = new Text(comp, SWT.BORDER);
		txt4.setLayoutData(gridt);

		Label lb5 = new Label(comp, SWT.NONE);
		lb5.setText("replication_timeout");
		txt5 = new Text(comp, SWT.BORDER);
		txt5.setLayoutData(gridt);

		Label lb6 = new Label(comp, SWT.NONE);
		lb6.setText("wal_receiver_status_interval");
		txt6 = new Text(comp, SWT.BORDER);
		txt6.setLayoutData(gridt);
		
		Label lb9 = new Label(comp, SWT.NONE);
		lb9.setText("dbUser");
		txt9 = new Text(comp, SWT.BORDER);
		txt9.setLayoutData(gridt);
		
		Label lb10 = new Label(comp, SWT.NONE);
		lb10.setText("dbPassword(默认123456)");
		if(type.equals("update")){
			lb10.setText("dbPassword");
		}
		txt10 = new Text(comp, SWT.BORDER|SWT.PASSWORD);
		txt10.setLayoutData(gridt);
		txt10.setText("123456");
        txt10.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				lb10.setText("dbPassword");
			}
		});

		Label lb7 = new Label(comp, SWT.NONE);
		lb7.setText("hot_standby_feedback");
		txt7 = new Text(comp, SWT.BORDER);
		txt7.setLayoutData(gridt);
		
		Label lb11 = new Label(comp, SWT.NONE);
		lb11.setText("case insensitive");
		caseBtn = new Button(comp, SWT.CHECK);
		caseBtn.setLayoutData(gridt);
		lb11.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		caseBtn.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		caseBtn.setSelection(true);
		
		Label lb12 = new Label(comp, SWT.NONE);
		lb12.setText("delegate_IP");
		txt12 = new Text(comp, SWT.BORDER);
		txt12.setLayoutData(gridt);
		txt12.setText("");
		
		lb1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb4.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb5.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb6.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb7.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb9.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb10.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lb12.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		txt1.setText(2 * models.length + "");
		txt2.setText("0");
		txt3.setText("0");
		txt4.setText("0");
		txt5.setText("0");
		txt6.setText("0");
		txt7.setText("0");
		txt9.setText("SYSTEM");

		Composite comp1 = new Composite(comAll, SWT.NONE);
		comp1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		comp1.setLayoutData(gridDataC);
		comp1.setLayout(new GridLayout(3, false));
		comp1.setVisible(false);
		Label lapost = new Label(comp1, SWT.NONE);
		lapost.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lapost.setText("选择kingbase.conf文件: ");
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
					tv3.setInput(listkv);
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

		tv3 = new TableViewer(tableVar);
		tv3.setContentProvider(new ContentProvider());
		tv3.setLabelProvider(new KeyValueProvider());
		tv3.setColumnProperties(KeyValueModifier.KEY_VALUE);

		final CellEditor[] editors3 = new CellEditor[2];
		editors3[0] = new TextCellEditor(tv3.getTable());
		editors3[1] = new TextCellEditor(tv3.getTable());
		tv3.setCellModifier(new KeyValueModifier(tv3));
		tv3.setCellEditors(editors3);
		new PickerShellTableSort(tableVar);
		
		btnBase.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (comp1.getVisible()) {
					comp1.setVisible(false);
					GridData gd31 = (GridData) comp1.getLayoutData();
					gd31.exclude = true;
					comp1.layout();
					comp1.getParent().layout();
					
					comp.setVisible(true);
					GridData gd3 = (GridData) comp.getLayoutData();
					gd3.exclude = false;
					comp.layout();
					comp.getParent().layout();
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
					comp.setVisible(false);
					GridData gd3 = (GridData) comp.getLayoutData();
					gd3.exclude = true;
					comp.layout();
					comp.getParent().layout();
					
					comp1.setVisible(true);
					GridData gd31 = (GridData) comp1.getLayoutData();
					gd31.exclude = false;
					comp1.layout();
					comp1.getParent().layout();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		if (oldReadEntity != null) {
			txtn.setText(oldReadEntity.getPosEntity().getName());
			tv1.setInput(oldReadEntity.getPosEntity().getListDb());
			txtdb.setText(oldReadEntity.getPosEntity().getDbzipPath());
			if (oldReadEntity.getPosEntity().getIsDbBase()) {
				btnBase.setSelection(true);
				txt1.setText(oldReadEntity.getPosEntity().getMax_wal());
				txt2.setText(oldReadEntity.getPosEntity().getMax_standby_arc());
				txt3.setText(oldReadEntity.getPosEntity().getWal_keep());
				txt4.setText(oldReadEntity.getPosEntity().getMax_standby_str());
//				txt5.setText(oldReadEntity.getPosEntity().getReplication());
				txt6.setText(oldReadEntity.getPosEntity().getWal_receiver());
				txt7.setText(oldReadEntity.getPosEntity().getHot_standby());
				txt9.setText(oldReadEntity.getPosEntity().getDbUser());
				txt10.setText(oldReadEntity.getPosEntity().getDbPassword());
				caseBtn.setSelection(oldReadEntity.getPosEntity().isInsensitive());
				txt12.setText(oldReadEntity.getPosEntity().getDelegate_IP());

			} else {
				btnHig.setSelection(true);
				txtpost.setText(oldReadEntity.getPosEntity().getPosPath());
				tv3.setInput(oldReadEntity.getPosEntity().getListKey1());
			}
		}
		
		if (type.equals("update")) {
			txt1.setEnabled(false);
			txt2.setEnabled(false);
			txt3.setEnabled(false);
			txt4.setEnabled(false);
			txt5.setEnabled(false);
			txt6.setEnabled(false);
			txt7.setEnabled(false);
			txt9.setEnabled(false);
			txt10.setEnabled(false);
			txtdb.setEditable(false);
			txtpost.setEditable(false);
			btnpost.setEnabled(false);
			btndb.setEnabled(false);
			btn1.setEnabled(false);
			btn2.setEnabled(false);
			comp1.setEnabled(false);
			comAdd.setEnabled(false);
			caseBtn.setEnabled(false);
			txt12.setEnabled(false);
		}
		
		UIUtils.verifyTextNumber(txt1);
		UIUtils.verifyTextNumber(txt2);
		UIUtils.verifyTextNumber(txt3);
		UIUtils.verifyTextNumber(txt4);
		UIUtils.verifyTextNumber(txt5);
		UIUtils.verifyTextNumber(txt6);
		UIUtils.verifyTextNumber(txt7);
		UIUtils.verifyTextNotSpace(txt9);
		return comm1;
	}

	public PosEntity getEntity() {
		entity.setName(txtn.getText());
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
		entity.setListDb(list);
		entity.setDbzipPath(txtdb.getText());
		entity.setDbzipName(txtdbName);
		if (btnBase.getSelection()) {
			entity.setIsDbBase(true);
		} else {
			entity.setIsDbBase(false);
		}
		entity.setMax_wal(txt1.getText());
		entity.setMax_standby_arc(txt2.getText());
		entity.setWal_keep(txt3.getText());
		entity.setMax_standby_str(txt4.getText());
//		entity.setReplication(txt5.getText());
		entity.setWal_receiver(txt6.getText());
		entity.setHot_standby(txt7.getText());
		entity.setDbUser(txt9.getText());
		entity.setDbPassword(txt10.getText());
		entity.setPosPath(txtpost.getText());
		entity.setInsensitive(caseBtn.getSelection());
		entity.setDelegate_IP(txt12.getText());

		List<KeyValueEntity> listKey = new ArrayList<KeyValueEntity>();
		for (int i = 0; i < tableVar.getItems().length; i++) {
			TableItem item = tableVar.getItems()[i];
			listKey.add((KeyValueEntity) item.getData());
		}
		entity.setListKey1(listKey);
		return entity;
	}
}
