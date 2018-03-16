package com.kingbase.db.replication.bundle.graphical.model;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.eclipse.core.resources.IProject;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.graphical.model.AbstractActivityNode;
import org.pentaho.di.graphical.model.AbstractFlowContainerNode;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.PlatformUtil;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataBase;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataInfo;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataSource;
import com.kingbase.db.replication.bundle.model.tree.ReplicationFile;
import com.kingbase.db.replication.bundle.model.tree.ReplicationRoot;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataBase;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataInfo;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataSource;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;
import com.kingbase.db.replication.bundle.views.ReplicationView;

public class ReplicationAllProcess extends AbstractFlowContainerNode {

	private static final long serialVersionUID = 1L;
	private List<SubscribeDataInfo> dataInfoList = new ArrayList<SubscribeDataInfo>();
	private List<SubscribeDataBase> subDBList = null;// 订阅数据库
	private List<ReleaseDataBase> relDBList = null;// 发布数据库
	private List<ReleaseDataInfo> relInfoList = null;// 未被订阅的发布
	private DataBaseInput input;
	private long time = System.currentTimeMillis();
	private ScheduledExecutorService service;

	public ReplicationAllProcess(DataBaseInput input) {
		super(UUID.randomUUID().toString(), "订阅");
		this.input = input;
		
		timerReadXML();
		allRefreshGraphical();
	}

	public void init() {
		super.init();

		service = Executors.newScheduledThreadPool(3);
		long initialDelay2 = 5;
		// 从现在开始5秒钟之后，每隔5秒钟执行一次job2
		service.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						allRefreshGraphical();
					}
				});
			}
		}, initialDelay2, 7, TimeUnit.SECONDS);
	}

	/**
	 * 单个节点的即先将所有的node删除，之后再重新绘制
	 * 
	 */
	public void allRefreshGraphical() {

		dataInfoList.clear();
		relInfoList = null;
		
		if (subDBList != null && subDBList.size() > 0) {
			List<SubscribeDataInfo> subList = new ArrayList<SubscribeDataInfo>();
			for (SubscribeDataBase database : subDBList) {
				subList.clear();
				List<SubscribeDataInfo> subInfoList = getSubscribeInfo(database,subList);// 所有的订阅
				if (subInfoList != null && subInfoList.size() > 0) {
					for (SubscribeDataInfo subInfo : subInfoList) {
						List<ReleaseDataInfo> subToRelList = getSubscribeToRelease(database, subInfo);// 此订阅所对应的所有发布
						subInfo.setReleaseList(subToRelList);
					}
					dataInfoList.addAll(subInfoList);
				}
			}
		}
		List<ReleaseDataInfo> allList = new ArrayList<ReleaseDataInfo>();
		if (relDBList != null && relDBList.size() > 0) {
			List<ReleaseDataInfo> list = new ArrayList<ReleaseDataInfo>();
			for (ReleaseDataBase database : relDBList) {
				list.clear();
				List<ReleaseDataInfo> relInfoList = getReleaseInfo(database,list);
				allList.addAll(relInfoList);
			}
		}
		if (allList == null || allList.size() == 0) {
			return;
		} else {
			relInfoList = allList;
			Collection childrenList = this.getChildrenList();
			for (Object object : childrenList) {
				if ((object instanceof ReplicationTargetNode) && dataInfoList.size() > 0) {
					for (SubscribeDataInfo dataInfo : dataInfoList) {

						ReplicationTargetNode targetNode = (ReplicationTargetNode) object;
						SubscribeDataInfo targetData = targetNode.getDataInfo();

						if (dataInfo.getDatabaseName().equals(targetData.getDatabaseName())
								&& (dataInfo.getSubscribeName().equals(targetData.getSubscribeName()))) {
							dataInfo.setLocation(targetNode.getLocation());
						}
					}

				} else if ((object instanceof ReplicationSourceNode)) {
					ReplicationSourceNode sourceNode = (ReplicationSourceNode) object;
					ReleaseDataInfo sourceData = sourceNode.getDataInfo();
					for (SubscribeDataInfo dataInfo : dataInfoList) {
						for (ReleaseDataInfo info : dataInfo.getReleaseList()) {
							if (info.getDbName().equals(sourceData.getDbName())
									&& info.getDbServer().equals(sourceData.getDbServer())
									&& info.getDbPort().equals(sourceData.getDbPort())
									&& info.getDbUser().equals(sourceData.getDbUser())
									&& info.getDatabaseName().equals(sourceData.getDatabaseName())
									&& info.getReleaseName().equals(sourceData.getReleaseName())) {
								info.setLocation(sourceNode.getLocation());
							}
						}
					}
					for (ReleaseDataInfo info : relInfoList) {
						if (info.getDbName().equals(sourceData.getDbName())
								&& info.getDbServer().equals(sourceData.getDbServer())
								&& info.getDbPort().equals(sourceData.getDbPort())
								&& info.getDbUser().equals(sourceData.getDbUser())
								&& info.getDatabaseName().equals(sourceData.getDatabaseName())
								&& info.getReleaseName().equals(sourceData.getReleaseName())) {
							info.setLocation(sourceNode.getLocation());
						}
					}
				}
			}
			fromXML(null);
		}
	}

	public void fromXML(Object owner) {
		
		Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();
		
		this.removeAll();
		List<ReplicationTargetNode> inNodeList = new ArrayList<ReplicationTargetNode>();
		List<ReplicationSourceNode> outNodeList = new ArrayList<ReplicationSourceNode>();
		List<ReplicationSourceNode> deleteList = new ArrayList<ReplicationSourceNode>();// 去除已经被订阅的发布
		int k = 0;
		int j = -1;
		int x = 100;
		if (dataInfoList != null && dataInfoList.size() > 0) {
			for (int i = 0; i < dataInfoList.size(); i++) {
				SubscribeDataInfo dataInfo = dataInfoList.get(i);
				ReplicationTargetNode inNote = new ReplicationTargetNode(UUID.randomUUID().toString(),
						dataInfo.getSubscribeName(), dataInfo, input);
				
				Point point = null;
				if (screenSize.height - 300 < 100 * (j + 1)) {// 换列显示
					j = 0;
					k++;
					point = new Point(100 + 100 * k, 100 * (j + 1));
					x = point.x;
				} else {
					j++;
					point = new Point(x, 100 * (j + 1));
				}
				inNote.fromXML(dataInfo.getLocation() != null ? dataInfo.getLocation() : point);// 订阅图标位置
				inNodeList.add(inNote);
			}
		}
		if (relInfoList != null && relInfoList.size() > 0) {
			for (int m = 0; m < relInfoList.size(); m++) {

				ReleaseDataInfo releaseDataInfo = relInfoList.get(m);
				ReplicationSourceNode outNode = new ReplicationSourceNode(UUID.randomUUID().toString(),
						releaseDataInfo.getReleaseName(), releaseDataInfo, input);
				outNodeList.add(outNode);
			}
		}
		k = 0;
		j = -1;
		x = 400;
		for (ReplicationTargetNode inNode : inNodeList) {
			SubscribeDataInfo subInfo = inNode.getDataInfo();
			for (ReleaseDataInfo relDataInfo : subInfo.getReleaseList()) {

				for (int n = 0; n < outNodeList.size(); n++) {
					ReplicationSourceNode outNode = outNodeList.get(n);
					ReleaseDataInfo relInfo = outNode.getDataInfo();
					if (relDataInfo.getDbName().equals(relInfo.getDbName())
							&& relDataInfo.getDbServer().equals(relInfo.getDbServer())
							&& relDataInfo.getDbPort().equals(relInfo.getDbPort())
							&& relDataInfo.getDbUser().equals(relInfo.getDbUser())
							&& relDataInfo.getDatabaseName().equals(relInfo.getDatabaseName())
							&& relDataInfo.getReleaseName().equals(relInfo.getReleaseName())) {

						Point point = null;
						if (screenSize.height - 300 < 100 * (j + 1)) {// 换列显示
							j = 0;
							k++;
							point = new Point(400 + 100 * k, 100 * (j + 1));
							x = point.x;
						} else {
							j++;
							point = new Point(x, 100 * (j + 1));
						}
						
						outNode.setLocation(relInfo.getLocation() != null ? relInfo.getLocation() : point);//被订阅的发布图标位置
						createLine(subInfo,inNode,outNode);
						deleteList.add(outNode);
					}
				}
			}
		}
		k = 0;
		j = -1;
		x = 700;
		outNodeList.removeAll(deleteList);// 只是剩下未被订阅的发布
		for (int i = 0; i < outNodeList.size(); i++) {
			ReplicationSourceNode outNode = outNodeList.get(i);
			ReleaseDataInfo relInfo = outNode.getDataInfo();
			Point point = null;
			if (screenSize.height - 300 < 100 * (j + 1)) {// 换列显示
				j = 0;
				k++;
				point = new Point(700 + 100 * k, 100 * (j + 1));
				x = point.x;
			} else {
				j++;
				point = new Point(x, 100 * (j + 1));
			}
			outNode.fromXML(relInfo.getLocation() != null ? relInfo.getLocation() : point);
			this.addChild(outNode);
		}
		
		 inNodeList = null;
		 outNodeList = null;
		 deleteList = null;// 去除已经被订阅的发布
		 
		 
		long curtime = System.currentTimeMillis() - time;
		if (curtime / 600000 == 1) {// 每小时触发一次gc
			System.gc();
			time = System.currentTimeMillis();
		}
	}
	private void createLine(SubscribeDataInfo subInfo, ReplicationTargetNode inNode, ReplicationSourceNode outNode){
		ReplicationConnection connectionLine = new ReplicationConnection(subInfo, input);
		connectionLine.setTarget(inNode);
		connectionLine.setSource(outNode);
		this.addChild(inNode);
		this.addChild(outNode);

		connectionLine.setTargetTerminal(null);
		connectionLine.attachTarget();
		connectionLine.setSourceTerminal(null);
		connectionLine.attachSource();
		connectionLine.setParent(this);// 最好是子类设置父类，若是process.addChild(connectionLine);可能会出错
	}

	public IFigure getFigure() {
		return null;
	}

	@Override
	public List<AbstractActivityNode> getFlowNodeList() {
		return null;
	}

	@Override
	public List<ReplicationConnection> getFlowConnList() {
		return null;
	}

	@Override
	public Object toXML(Object arg0) {
		return arg0;
	}

	/**
	 * 得到订阅对应的发布，以及发布的服务器，数据库等信息
	 * 
	 * @param subInfo
	 * 
	 * @param dataInfo
	 */
	public List<ReleaseDataInfo> getSubscribeToRelease(SubscribeDataBase datebase, SubscribeDataInfo subInfo) {

		SubscribeDataSource source = new SubscribeDataSource();
		source.setDbName(datebase.getDbName());
		source.setDbServer(datebase.getDbServer());
		source.setDbPort(datebase.getDbPort());
		source.setDbUser(datebase.getDbUser());
		source.setDbPasswrod(datebase.getDbPasswrod());
		source.setDriverName(datebase.getDriverName());
		source.setDriverPath(datebase.getDriverPath());

		Connection sourceCon = DatabaseUtil.getConnection(source, datebase.getDatabaseName());
		source = null;
		if (sourceCon == null){
			return null;
		}
		List<ReleaseDataInfo> relList = new ArrayList<ReleaseDataInfo>();
		try {
			ResultSet subscribeSet = sourceCon.createStatement().executeQuery(
					"SELECT SUB_ORIGIN, ARRAY_TO_STRING(SUB_REPLICATION_SETS, ',') AS SETS FROM SYSLOGICAL.SUBSCRIPTION WHERE SUB_NAME = '"
							+ subInfo.getSubscribeName() + "'");
			while (subscribeSet.next()) {
				String id = subscribeSet.getString("SUB_ORIGIN");
				String sets = subscribeSet.getString("SETS");
				for (ReleaseDataBase releaseDataBase : relDBList) {
					if (releaseDataBase.getNodeId().equals(id)) {
						String[] split = sets.split(",");
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

							relList.add(sourceMeta);
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
			MessageDialog.openError(UIUtils.getActiveShell(), "Error", e.getMessage());
		}
		return relList;
	}
	
	private void timerReadXML() {
		IProject proejct = PlatformUtil.getProject("General");// 只要动过服务器，就需要重新点开监控图
		ReplicationRoot rootFolder = new ReplicationRoot(proejct);
		rootFolder.treeExpanded();
		ReplicationFile file1 = (ReplicationFile) (rootFolder.getChildren())[0];
		ReplicationFile file2 = (ReplicationFile) (rootFolder.getChildren())[1];
		try {
			relDBList = getReleaseDatabase(file1);
			subDBList = getSubscribeDatabase(file2);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到本地xml的所有发布数据库的信息
	 * 
	 */
	private List<ReleaseDataBase> getReleaseDatabase(ReplicationFile ifile) throws DocumentException {
		List<ReleaseDataBase> list = new ArrayList<ReleaseDataBase>();
		List<Element> listEle = ReplicationView.getlistEle(ifile);

		for (int i = 0, n = listEle.size(); i < n; i++) {
			Element element = listEle.get(i);
			Element nodesElm = element.element("nodes");
			List<Element> nodeElm = nodesElm.elements("node");
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
		return list;
	}

	/**
	 * 得到本地xml的所有订阅数据库的信息
	 * 
	 */
	private List<SubscribeDataBase> getSubscribeDatabase(ReplicationFile ifile) throws DocumentException {
		List<SubscribeDataBase> list = new ArrayList<SubscribeDataBase>();
		List<Element> listEle = ReplicationView.getlistEle(ifile);

		for (int i = 0, n = listEle.size(); i < n; i++) {
			Element element = listEle.get(i);

			Element nodesElm = element.element("nodes");
			List<Element> nodeElm = nodesElm.elements("node");
			for (Element node : nodeElm) {
				SubscribeDataBase database = new SubscribeDataBase();
				database.setDbName(element.element("name").getStringValue()); //$NON-NLS-1$
				database.setDbServer(element.element("server").getStringValue()); //$NON-NLS-1$
				database.setDbPort(element.element("port").getStringValue()); //$NON-NLS-1$
				database.setDbUser(element.element("username").getStringValue()); //$NON-NLS-1$
				database.setDbPasswrod(element.element("password").getStringValue()); //$NON-NLS-1$
				database.setDriverName(element.element("driverName").getStringValue()); //$NON-NLS-1$
				database.setDriverPath(element.element("driverPath").getStringValue()); //$NON-NLS-1$
				database.setNodeId(node.element("nodeId").getStringValue()); //$NON-NLS-1$
				database.setDatabaseName(node.element("dbname").getStringValue()); //$NON-NLS-1$
				list.add(database);
			}
		}
		return list;
	}

	/**
	 * 得到具体的订阅信息
	 * @param list 
	 * 
	 */
	public List<SubscribeDataInfo> getSubscribeInfo(SubscribeDataBase database, List<SubscribeDataInfo> list) {
		SubscribeDataSource source = new SubscribeDataSource();

		source.setDbName(database.getDbName());
		source.setDbServer(database.getDbServer());
		source.setDbPort(database.getDbPort());
		source.setDbUser(database.getDbUser());
		source.setDbPasswrod(database.getDbPasswrod());
		source.setDriverName(database.getDriverName());
		source.setDriverPath(database.getDriverPath());

		Connection sourceCon = DatabaseUtil.getConnection(source, database.getDatabaseName());
		if (sourceCon == null){
			return list;
		}
		try {

			ResultSet subscribeSet = sourceCon.createStatement()
					.executeQuery("SELECT SUB_NAME, SUB_ENABLED FROM SYSLOGICAL.SUBSCRIPTION");
			while (subscribeSet.next()) {
				String subscribeName = subscribeSet.getString("SUB_NAME"); //$NON-NLS-1$
				String subscribeEnable = subscribeSet.getString("SUB_ENABLED"); //$NON-NLS-1$
				SubscribeDataInfo meta = new SubscribeDataInfo();// 具体的某个发布
				meta.setSubscribeName(subscribeName);
				meta.setSubscribeEnable(subscribeEnable);
				meta.setDatabaseName(database.getDatabaseName());
				meta.setDatabaseOid(database.getDatabaseOid());
				meta = setDataSourceMetaInfo(meta, database);
				list.add(meta);
			}
			sourceCon.createStatement().close();
			subscribeSet.close();
//			if (sourceCon != null) {
//				sourceCon.close();
//			}
		} catch (Exception e) {
			MessageDialog.openError(UIUtils.getActiveShell(), "Error", e.getMessage());
			return null;
		}
		return list;
	}

	/**
	 * 得到具体的发布信息
	 * 
	 */
	private List<ReleaseDataInfo> getReleaseInfo(ReleaseDataBase database,List<ReleaseDataInfo> list) {

		ReleaseDataSource source = new ReleaseDataSource();

		source.setDbName(database.getDbName());
		source.setDbServer(database.getDbServer());
		source.setDbPort(database.getDbPort());
		source.setDbUser(database.getDbUser());
		source.setDbPasswrod(database.getDbPasswrod());
		source.setDriverName(database.getDriverName());
		source.setDriverPath(database.getDriverPath());

		Connection sourceCon = DatabaseUtil.getConnection(source, database.getDatabaseName());
		source = null;
		if (sourceCon != null) {
			DatabaseMetaData metaData;
			try {

				metaData = sourceCon.getMetaData();
				String schemaTerm = metaData.getSchemaTerm();
				if (!schemaTerm.equals("")) {
					Statement stm1 = sourceCon.createStatement();
					ResultSet rs0 = null;
					rs0 = stm1.executeQuery("SELECT SET_NAME FROM SYSLOGICAL.REPLICATION_SET");
					while (rs0.next()) {
						ReleaseDataInfo metaChild = new ReleaseDataInfo();
						String release = rs0.getString("SET_NAME"); //$NON-NLS-1$
						metaChild.setReleaseName(release);
						metaChild.setDatabaseName(database.getDatabaseName());
						metaChild.setDatabaseOid(database.getDatabaseOid());
						setDataSourceMetaInfo(metaChild, database);

						list.add(metaChild);
					}
					stm1.close();
					if (rs0 != null) {
						rs0.close();
					}
				}
//				if (sourceCon != null) {
//					sourceCon.close();
//				}
			} catch (SQLException e) {
				e.printStackTrace();
				MessageDialog.openError(UIUtils.getActiveShell(), "Error", e.getMessage());
			}
		}
		return list;
	}

	public static SubscribeDataInfo setDataSourceMetaInfo(SubscribeDataInfo metaChild, SubscribeDataBase meta) {
		metaChild.setDbName(meta.getDbName());
		metaChild.setDbServer(meta.getDbServer());
		metaChild.setDbPort(meta.getDbPort());
		metaChild.setDbUser(meta.getDbUser());
		metaChild.setDbPasswrod(meta.getDbPasswrod());
		metaChild.setDriverName(meta.getDriverName());
		metaChild.setDriverPath(meta.getDriverPath());
		return metaChild;
	}

	public static ReleaseDataInfo setDataSourceMetaInfo(ReleaseDataInfo metaChild, ReleaseDataBase meta) {
		metaChild.setDbName(meta.getDbName());
		metaChild.setDbServer(meta.getDbServer());
		metaChild.setDbPort(meta.getDbPort());
		metaChild.setDbUser(meta.getDbUser());
		metaChild.setDbPasswrod(meta.getDbPasswrod());
		metaChild.setDriverName(meta.getDriverName());
		metaChild.setDriverPath(meta.getDriverPath());
		return metaChild;
	}

	public ScheduledExecutorService getService() {
		return service;
	}

	public void setService(ScheduledExecutorService service) {
		this.service = service;
	}
	
}
