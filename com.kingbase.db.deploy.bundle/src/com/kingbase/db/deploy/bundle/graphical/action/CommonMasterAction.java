package com.kingbase.db.deploy.bundle.graphical.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchPart;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.JschUtil;
import com.kingbase.db.core.util.KBProgressMonitorDialog;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.deploy.bundle.KBDeployCore;
import com.kingbase.db.deploy.bundle.graphical.editor.CreateMasterStatusEditor;
import com.kingbase.db.deploy.bundle.graphical.model.DeployContentsMasterModel;
import com.kingbase.db.deploy.bundle.model.tree.CNodeEntity;
import com.kingbase.db.deploy.bundle.model.tree.PoolEntity;

public class CommonMasterAction extends SelectionAction {

	private CNodeEntity entity;
	private String type;
	private PoolEntity poolEntity;
	private Map<String, Session> sessionMap;
	private String suss = "";
	private String name = "";
	private DeployContentsMasterModel viewer;

	public CommonMasterAction(IWorkbenchPart part, CNodeEntity entity, String type, CreateMasterStatusEditor editor) {
		super(part);
		setId(UUID.randomUUID().toString());
		this.type = type;
		this.entity = entity;
		this.viewer = editor.getContainerModel();
		this.poolEntity = editor.getContainerModel().getNode().getPoolEntity();
		this.sessionMap = editor.getContainerModel().getSessionMap();
		if (type.contains("_start")) {
			setText("启 动");
			setImageDescriptor(ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_refresh));
		} else if (type.contains("_disable")) {
			setText("停 止");
			setImageDescriptor(ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_delete));
		} else if (type.contains("_restart")) {
			setText("重 启");
			setImageDescriptor(ImageURL.createImageDescriptor(KBDeployCore.PLUGIN_ID, ImageURL.right_sync));
		}
		getName();
	}

	@Override
	public void run() {
		super.run();

		KBProgressMonitorDialog dialog = new KBProgressMonitorDialog(UIUtils.getActiveShell());
		try {

			dialog.run(true, false, new RunnAble());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		if (suss.equals("true") && type.equals("all_disable")) {
//			viewer.setMainNodeDB(null);
//			viewer.getMainDownDB().setType("down");
//			viewer.setPrepareNodeDBList(new ArrayList<CNodeEntity>());
//			
//			viewer.setMainNodePool(null);
//			viewer.getMainDownPool().setType("down");
//			viewer.setPrepareNodePoolList(new ArrayList<CNodeEntity>());
//			//down的DB
//			List<CNodeEntity> prepareDownDBList = viewer.getPrepareDownDBList();
//			viewer.setPrepareDownDBList(new ArrayList<CNodeEntity>());
//			for (CNodeEntity cNodeEntity : prepareDownDBList) {
//				cNodeEntity.setType("down");
//				viewer.getPrepareDownDBList().add(cNodeEntity);
//			}
//			//down 的pool
//			List<CNodeEntity> prepareDownPoolList = viewer.getPrepareDownPoolList();
//			viewer.setPrepareDownPoolList(new ArrayList<CNodeEntity>());
//			for (CNodeEntity cNodeEntity : prepareDownPoolList) {
//				cNodeEntity.setType("down");
//				viewer.getPrepareDownPoolList().add(cNodeEntity);
//			}
//			viewer.refresh();
//			viewer.fromXML(false);
//		}
	}

	private void function() {
		Session session = null;
		Set<String> keySet = sessionMap.keySet();
		String str = "";
		for (String key : keySet) {
			str = entity.getIp() + "_" + entity.getPort() + "_" + entity.getRootPass();
			if (str.equals(key)) {
				session = sessionMap.get(key);
				break;
			}
		}
		try {
			if (session == null) {
				session = JschUtil.connect(entity.getIp(), new Integer(entity.getPort()), "root", entity.getRootPass());
				sessionMap.remove(str);
				sessionMap.put(str, session);
			}
//			ScheduledExecutorService service = viewer.getService();
//			if (type.equals("all_restart") || type.equals("all_disable")) {
//
//				if (service != null && !service.isShutdown()) {
//					try {
//						service.shutdown();
//						if (!service.awaitTermination(500, TimeUnit.MILLISECONDS)) {
//							service.shutdownNow();
//						}
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
			if (type.contains("DB")) {
				if (type.contains("_start")) {
					JschUtil.execCommand1(session, startDB(entity));
				} else if (type.contains("_disable")) {
					JschUtil.execCommand1(session, stopDB(entity));
				} else if (type.contains("_restart")) {
					JschUtil.execCommand1(session, restartDB(entity));
				}
			} else if (type.contains("cluster")) {
				if (type.contains("_start")) {
					JschUtil.execCommand1(session, startPool(entity));
				} else if (type.contains("_disable")) {
					JschUtil.execCommand1(session, stopPool(entity));
				} else if (type.contains("_restart")) {
					JschUtil.execCommand1(session, restartPool(entity));
				}
			} else {
				if (type.contains("_start")) {
//					JschUtil.execCommand1(session, stopAll(entity));// 先清理环境
					JschUtil.execCommand1(session, restartAll(entity));
				} else if (type.contains("_disable")) {
					JschUtil.execCommand1(session, stopAll(entity));
				} else {
					JschUtil.execCommand1(session, restartAll(entity));
				}
			}
//			if (type.equals("all_start") || type.equals("all_restart")) {
//
//				if (service != null && service.isShutdown()) {
//					viewer.timeTask(6);
//				}
//			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			suss = "false";
		} catch (JSchException e) {
			e.printStackTrace();
			suss = "false";
		} finally {
		}
		suss = "true";
	}

	@Override
	protected boolean calculateEnabled() {

		if (entity.getType().equals("down")) {
			if (type.contains("_start")) {
				return true;
			} else if (type.contains("_disable")) {
				return false;
			} else if (type.contains("_restart")) {
				return true;
			}
		} else if (entity.getType().equals("up")) {
			if (type.contains("_start")) {
				return false;
			} else if (type.contains("_disable")) {
				return true;
			} else if (type.contains("_restart")) {
				return true;
			}
		} else {
			if (type.contains("_start")) {
				return true;
			} else if (type.contains("_disable")) {
				return false;
			} else if (type.contains("_restart")) {
				return false;
			}
		}
		return true;
	}

//	private String startAll(CNodeEntity entity) {
//		return "cd " + entity.getdPath() + "db/bin/;export LD_LIBRARY_PATH=../lib;./kingbase_monitor.sh start";
//	}

	private String stopAll(CNodeEntity entity) {
		return "cd " + entity.getdPath() + "db/bin/;export LD_LIBRARY_PATH=../lib;./kingbase_monitor.sh stop";
	}

	private String restartAll(CNodeEntity entity) {
		return "cd " + entity.getdPath() + "db/bin/;export LD_LIBRARY_PATH=../lib;./kingbase_monitor.sh restart";
	}

	private String startPool(CNodeEntity entity) {
		return "rm -f " + entity.getdPath() + "run/kingbasecluster/kingbasecluster_status; cd " + entity.getdPath()
				+ "kingbasecluster/bin/;export LD_LIBRARY_PATH=../lib;./kingbasecluster -a ../etc/cluster_hba.conf -f ../etc/kingbasecluster.conf -F ../etc/pcp.conf";
	}

	private String stopPool(CNodeEntity entity) {
		return "cd " + entity.getdPath() + "kingbasecluster/bin/;export LD_LIBRARY_PATH=../lib;./pcp_stop_kingbasecluster -U" + poolEntity.getPcpUser() + " -w -p " + poolEntity.getPcp_port();
	}

	private String restartPool(CNodeEntity entity) {
		return "cd " + entity.getdPath() + "kingbasecluster/bin/;./restartcluster.sh";
	}

	private String startDB(CNodeEntity entity) {
		return "su - " + entity.getUser() + " -c \"cd " + entity.getdPath() + "db/bin/;LD_LIBRARY_PATH=../lib ./sys_ctl -D ../data -l ../kingbase.log start\"";
	}

	private String stopDB(CNodeEntity entity) {
		return "su - " + entity.getUser() + " -c \"cd " + entity.getdPath() + "db/bin/;LD_LIBRARY_PATH=../lib ./sys_ctl -D ../data -l ../kingbase.log stop -m immediate\"";
	}

	private String restartDB(CNodeEntity entity) {
		return "su - " + entity.getUser() + " -c \"cd " + entity.getdPath() + "db/bin/;LD_LIBRARY_PATH=../lib ./sys_ctl -D ../data -l ../kingbase.log restart\"";
	}

	class RunnAble implements IRunnableWithProgress {

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask(name, IProgressMonitor.UNKNOWN);
			new Thread() {
				public void run() {
					function();
				}
			}.start();

			while (true) {
				if (monitor.isCanceled()) {// 取消
					break;
				}
				if (suss.equals("true")) {
					break;
				}
				Thread.sleep(10);
			}
			monitor.done();
		}
	}

	private void getName() {
		if (type.equals("DB_start")) {
			name = "DB 启动...";
		} else if (type.equals("DB_disable")) {
			name = "DB 停止...";
		} else if (type.equals("DB_restart")) {
			name = "DB 重启...";
		} else if (type.equals("Cluster_start")) {
			name = "Cluster 启动...";
		} else if (type.equals("Cluster_disable")) {
			name = "Cluster 停止...";
		} else if (type.equals("Cluster_restart")) {
			name = "Cluster 重启...";
		} else if (type.equals("all_start")) {
			name = "集群 一键启动...";
		} else if (type.equals("all_disable")) {
			name = "集群 一键停止...";
		} else if (type.equals("all_restart")) {
			name = "集群 一键重启...";
		}
	}
}
