/**
 * 
 */
package com.kingbase.db.replication.bundle.graphical.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.graphical.model.AbstractActivityNode;
import org.pentaho.di.graphical.model.AbstractFlowContainerNode;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataInfo;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataBase;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataInfo;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataSource;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;

/**
 * @author jpliu
 *
 */
public class ReplicationProcess extends AbstractFlowContainerNode {

	private static final long serialVersionUID = 1L;
	private ReplicationSourceNode outNode = null;
	private ReplicationTargetNode inNote = null;
	private SubscribeDataInfo dataInfo;
	private DataBaseInput input;
	private List<ReplicationConnection> connectionList = new ArrayList<ReplicationConnection>();
	private Connection sourceCon = null;
	private long time = System.currentTimeMillis();
	private ScheduledExecutorService service;

	public ReplicationProcess(DataBaseInput input) {
		super(UUID.randomUUID().toString(), "订阅");
		this.input = input;
		dataInfo = (SubscribeDataInfo) input.getNode();

		SubscribeDataBase datebase = (SubscribeDataBase) dataInfo.getParentModel();
		SubscribeDataSource source = (SubscribeDataSource) datebase.getParentModel();
		sourceCon = DatabaseUtil.getConnection(source, datebase.getDatabaseName());
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
						if (dataInfo == null) {
							return;
						}
						System.out.println("==="+Thread.currentThread().getName()+"-----------");
						singleRefreshGraphical();
					}
				});
			}
		}, initialDelay2, 6, TimeUnit.SECONDS);
	}

	/**
	 * 单个节点的即先将所有的node删除，之后再重新绘制
	 * 
	 */
	public void singleRefreshGraphical() {

		SubscribeDataBase parentModel = (SubscribeDataBase) dataInfo.getParentModel();
		if (parentModel.getChildrenList().isEmpty()) {

			return;
		} else {
			dataInfo = getSubscribeInfo(dataInfo);
			if (dataInfo == null) {
				return;
			}
			List<ReleaseDataInfo> list = getSubscribeToRelease(dataInfo);

			Collection childrenList = this.getChildrenList();
			for (Object object : childrenList) {
				if (object instanceof ReplicationTargetNode) {
					ReplicationTargetNode targetNode = (ReplicationTargetNode) object;
					SubscribeDataInfo targetData = targetNode.getDataInfo();

					if (dataInfo.getDatabaseName().equals(targetData.getDatabaseName())
							&& (dataInfo.getSubscribeName().equals(targetData.getSubscribeName()))) {
						dataInfo.setLocation(targetNode.getLocation());
					}

				} else if (object instanceof ReplicationSourceNode) {
					ReplicationSourceNode sourceNode = (ReplicationSourceNode) object;
					ReleaseDataInfo sourceData = sourceNode.getDataInfo();
					for (ReleaseDataInfo info : list) {

						if (info.getDatabaseName().equals(sourceData.getDatabaseName())
								&& (info.getReleaseName().equals(sourceData.getReleaseName()))) {
							info.setLocation(sourceNode.getLocation());
						}
					}
				}
			}
			dataInfo.setReleaseList(list);
			fromXML(null);
		}
	}

	public void fromXML(Object owner) {
		this.removeAll();
		ReplicationTargetNode inNote = new ReplicationTargetNode(UUID.randomUUID().toString(),
				dataInfo.getSubscribeName(), dataInfo, input);
		inNote.fromXML(dataInfo.getLocation() != null ? dataInfo.getLocation() : new Point(200, 100));
		this.addChild(inNote);

		List<ReleaseDataInfo> releaseList = dataInfo.getReleaseList();
		if (releaseList==null||releaseList.size() == 0) {
			return;
		}
		for (int i = 0; i < releaseList.size(); i++) {

			ReleaseDataInfo releaseDataInfo = releaseList.get(i);
			ReplicationSourceNode outNode = new ReplicationSourceNode(UUID.randomUUID().toString(),
					releaseDataInfo.getReleaseName(), releaseDataInfo, input);
			outNode.fromXML(releaseDataInfo.getLocation() != null ? releaseDataInfo.getLocation()
					: new Point(400, 100 * (i + 1)));
			this.addChild(outNode);

			ReplicationConnection connectionLine = new ReplicationConnection(dataInfo, input);
			connectionLine.setTarget(inNote);
			connectionLine.setSource(outNode);

			connectionLine.setTargetTerminal(null);
			connectionLine.attachTarget();
			connectionLine.setSourceTerminal(null);
			connectionLine.attachSource();
			connectionLine.setParent(this);// 最好是子类设置父类，若是process.addChild(connectionLine);可能会出错

			connectionList.add(connectionLine);
		}
		long curtime = System.currentTimeMillis() - time;
		if (curtime / 600000 == 1) {// 每半时触发一次gc
			System.gc();
			time  = System.currentTimeMillis();
		}
	}

	public IFigure getFigure() {
		return null;
	}

	public ReplicationSourceNode getOutNode() {
		return outNode;
	}

	public void setOutNode(ReplicationSourceNode outNode) {
		this.outNode = outNode;
	}

	public ReplicationTargetNode getInNote() {
		return inNote;
	}

	public void setInNote(ReplicationTargetNode inNote) {
		this.inNote = inNote;
	}

	@Override
	public List<AbstractActivityNode> getFlowNodeList() {
		List<AbstractActivityNode> list = new ArrayList<AbstractActivityNode>();
		list.add(outNode);
		list.add(inNote);
		return list;
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
	 * @param dataInfo
	 */
	public List<ReleaseDataInfo> getSubscribeToRelease(SubscribeDataInfo dataInfo) {

		List<ReleaseDataInfo> dataInfoList = new ArrayList<ReleaseDataInfo>();

		List<ReleaseDataInfo> releaseList = dataInfo.getReleaseList();
		ReleaseDataInfo releaseDataInfo = null;
		if (releaseList != null && releaseList.size() > 0) {
			releaseDataInfo = releaseList.get(0);
		} else {
			return null;
		}
		if (sourceCon == null) {

			SubscribeDataSource parent = (SubscribeDataSource) dataInfo.getParentModel().getParentModel();
			sourceCon = DatabaseUtil.getConnection(parent, dataInfo.getDatabaseName());
		}
		try {
			ResultSet subscribeSet = sourceCon.createStatement().executeQuery(
					"SELECT SUB_ORIGIN, ARRAY_TO_STRING(SUB_REPLICATION_SETS, ',') AS SETS FROM SYSLOGICAL.SUBSCRIPTION WHERE SUB_NAME = '"
							+ dataInfo.getSubscribeName() + "'");
			while (subscribeSet.next()) {
				String sets = subscribeSet.getString("SETS");

				String[] split = sets.split(",");
				for (String str : split) {

					ReleaseDataInfo sourceMeta = new ReleaseDataInfo();
					sourceMeta.setDbName(releaseDataInfo.getDbName());
					sourceMeta.setDbServer(releaseDataInfo.getDbServer());
					sourceMeta.setDbPort(releaseDataInfo.getDbPort());
					sourceMeta.setDbUser(releaseDataInfo.getDbUser());
					sourceMeta.setDbPasswrod(releaseDataInfo.getDbPasswrod());
					sourceMeta.setDriverName(releaseDataInfo.getDriverName());
					sourceMeta.setDriverPath(releaseDataInfo.getDriverPath());
					sourceMeta.setDatabaseName(releaseDataInfo.getDatabaseName());
					sourceMeta.setDatabaseOid(releaseDataInfo.getDatabaseOid());
					sourceMeta.setReleaseName(str);

					dataInfoList.add(sourceMeta);
				}
			}
			sourceCon.createStatement().close();
			subscribeSet.close();
		} catch (Exception e) {
		}
		return dataInfoList;
	}

	/**
	 * 得到具体订阅信息
	 * 
	 */
	public SubscribeDataInfo getSubscribeInfo(SubscribeDataInfo dataInfo) {

		if (sourceCon == null) {

			SubscribeDataSource parent = (SubscribeDataSource) dataInfo.getParentModel().getParentModel();
			sourceCon = DatabaseUtil.getConnection(parent, dataInfo.getDatabaseName());
		}
		try {
			ResultSet subscribeSet = sourceCon.createStatement()
					.executeQuery("SELECT SUB_ENABLED FROM SYSLOGICAL.SUBSCRIPTION SUB WHERE SUB.SUB_NAME = '"
							+ dataInfo.getSubscribeName() + "'");
			while (subscribeSet.next()) {
				String subscribeEnable = subscribeSet.getString("SUB_ENABLED"); //$NON-NLS-1$
				dataInfo.setSubscribeEnable(subscribeEnable);
			}
			sourceCon.createStatement().close();
			subscribeSet.close();
		} catch (Exception e) {
			MessageDialog.openError(UIUtils.getActiveShell(), "Error", e.getMessage());
			return null;
		}
		return dataInfo;
	}

	public ScheduledExecutorService getService() {
		return service;
	}

	public void setService(ScheduledExecutorService service) {
		this.service = service;
	}
}