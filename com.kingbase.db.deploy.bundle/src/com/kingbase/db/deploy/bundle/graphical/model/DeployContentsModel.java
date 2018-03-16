package com.kingbase.db.deploy.bundle.graphical.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.graphical.model.AbstractActivityNode;
import org.pentaho.di.graphical.model.AbstractFlowContainerNode;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.JschUtil;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.deploy.bundle.model.tree.CNodeEntity;
import com.kingbase.db.deploy.bundle.model.tree.CReadWriteEntity;
import com.kingbase.db.deploy.bundle.model.tree.ReadWriteEntity;

public class DeployContentsModel extends AbstractFlowContainerNode {

	private DataBaseInput input;
	private CNodeEntity mainNodeDB;
	private CNodeEntity mainNodePool;
	private List<CNodeEntity> prepareNodeDBList = new ArrayList<CNodeEntity>();
	private List<CNodeEntity> prepareNodePoolList = new ArrayList<CNodeEntity>();

	private List<CNodeEntity> nodeList = new ArrayList<CNodeEntity>();
	private Map<CNodeEntity, Session> sessionMap = new HashMap<CNodeEntity, Session>();
	private Timer timer;
	private CReadWriteEntity node;
	private boolean flag = true;
	private long time =  System.currentTimeMillis();

	public DeployContentsModel(DataBaseInput input) {
		super(UUID.randomUUID().toString(), "model");
		this.input = input;
		this.node = (CReadWriteEntity) input.getNode();
		getXmlNode(node);
	}

	@Override
	protected void init() {
		super.init();
		timeTask(1000);
	}

	class AutoRefreshThread extends TimerTask {

		@Override
		public void run() {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					fromXML(true);
				}
			});
		}
	}

	@Override
	public void fromXML(Object arg0) {

		restoreLocation();

		this.removeAll();
		if ((arg0 instanceof Boolean) && (Boolean) arg0) {
			allNodeStatus((CReadWriteEntity) input.getNode());
		}
		DeploySourceNode mainPool = null;
		if (mainNodePool != null) {

			mainPool = new DeploySourceNode(input, mainNodePool, true, true);
			mainPool.fromXML(mainNodePool.getLocation() == null ? new Point(600, 250) : mainNodePool.getLocation());
			this.addChild(mainPool);
		}
		DeployTargetNode mainDB = null;
		if (mainNodeDB != null) {

			mainDB = new DeployTargetNode(input, mainNodeDB, false, true);
			mainDB.fromXML(mainNodeDB.getLocation() == null ? new Point(600, 450) : mainNodeDB.getLocation());
			this.addChild(mainDB);
		}

		int i = 1;
		int k = 0;
		if (prepareNodeDBList != null && prepareNodeDBList.size() > 0) {
			for (CNodeEntity cNodeEntity : prepareNodeDBList) {
				if (i % 2 == 0) {
					k = i / 2;
				} else {
					k = -(k + 1);
				}

				DeployTargetNode prepareDB = new DeployTargetNode(input, cNodeEntity, false, false);
				prepareDB.fromXML(
						cNodeEntity.getLocation() == null ? new Point(600 + k * 100, 600) : cNodeEntity.getLocation());
				this.addChild(prepareDB);

				createLine(mainPool, prepareDB, prepareDB.getNode().getType());// 主pool---备库
				createLine(mainDB, prepareDB, prepareDB.getNode().getType());// 主库---备库
				i++;
			}
		}
		int j = 1;
		if (prepareNodePoolList != null && prepareNodePoolList.size() > 0) {
			for (CNodeEntity cNodeEntity : prepareNodePoolList) {
				DeployTargetNode preparePool = new DeployTargetNode(input, cNodeEntity, true, false);
				preparePool.fromXML(
						cNodeEntity.getLocation() == null ? new Point(600 + k * 100, 150) : cNodeEntity.getLocation());
				this.addChild(preparePool);

				createLine(mainPool, preparePool, preparePool.getNode().getType());// 主pool---备pool
				createLine(preparePool, mainPool, mainPool.getNode().getType());// 备pool---主pool
				j++;
			}
		}
		if (mainNodeDB != null && mainNodePool != null) {

			createLine(mainPool, mainDB, mainDB.getNode().getType());
		}
		this.refresh();
		long curtime = System.currentTimeMillis() - time;
		if (curtime / 3600000 > 1) {// 每小时触发一次gc
			System.gc();
			time  = System.currentTimeMillis();
		}
	}

	/**
	 * 记住上一步移动的位置
	 */
	private void restoreLocation() {
		Collection childrenList = this.getChildrenList();
		for (Object object : childrenList) {
			if (object instanceof DeployTargetNode) {
				DeployTargetNode targetNode = (DeployTargetNode) object;
				CNodeEntity node = targetNode.getNode();
				for (CNodeEntity obj : nodeList) {
					if (obj.getIp().equals(node.getIp()) && obj.getPort().equals(node.getPort())
							&& obj.getRootPass().equals(node.getRootPass()) && obj.getUser().equals(node.getUser())
							&& obj.getLibrary().equals(node.getLibrary()) && obj.getName().equals(node.getName())
							&& obj.getdPath().equals(node.getdPath())) {
						obj.setLocation(targetNode.getLocation());
					}
				}
			} else if (object instanceof DeploySourceNode) {
				DeploySourceNode sourceNode = (DeploySourceNode) object;
				CNodeEntity node = sourceNode.getNode();
				for (CNodeEntity obj : nodeList) {
					if (obj.getIp().equals(node.getIp()) && obj.getPort().equals(node.getPort())
							&& obj.getRootPass().equals(node.getRootPass()) && obj.getUser().equals(node.getUser())
							&& obj.getLibrary().equals(node.getLibrary()) && obj.getName().equals(node.getName())
							&& obj.getdPath().equals(node.getdPath())) {
						obj.setLocation(sourceNode.getLocation());
					}
				}
			}
		}
	}

	private void getXmlNode(CReadWriteEntity entity) {
		ReadWriteEntity parent = (ReadWriteEntity) entity.getParentModel();
		IFolder folder = parent.getFolder();
		IFile file = (IFile) folder.findMember("read.xml");

		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(new BufferedReader(
					new InputStreamReader(new FileInputStream(file.getLocation().toFile()), "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element root = document.getRootElement();
		List<Element> listEle = root.elements();
		for (int i = 0, n = listEle.size(); i < n; i++) {
			Element element = listEle.get(i);

			String nodeName = element.element("name").getStringValue();
			if (nodeName.equals(input.getName())) {

				Element nodesElm = element.element("dbNodes");
				List<Element> nodeElm = nodesElm.elements("dbNode");
				for (Element ele : nodeElm) {
					CNodeEntity node = new CNodeEntity();
					String[] stringValue = ele.getStringValue().split("\n");
					List<String> list = new ArrayList<String>();
					for (String str : stringValue) {
						if (!str.trim().equals("")) {
							list.add(str.trim());
						}
					}
					String library = ele.attribute("library").getStringValue();
					String listenerPost = ele.attribute("listenerPost").getStringValue();
					node.setName(list.get(0));
					node.setIp(list.get(1));
					node.setPort(list.get(2));
					node.setRootPass(list.get(3));
					node.setUser(list.get(4));
					node.setdPath(list.get(5));
					node.setLibrary(library);
					node.setListenerPost(listenerPost);
//					node.setType("unused");

					nodeList.add(node);
				}
				Element nodesElm1 = element.element("poolNodes");
				List<Element> nodeElm1 = nodesElm1.elements("poolNode");
				for (Element ele : nodeElm1) {
					CNodeEntity node = new CNodeEntity();
					String[] stringValue = ele.getStringValue().split("\n");
					List<String> list = new ArrayList<String>();
					for (String str : stringValue) {
						if (!str.trim().equals("")) {
							list.add(str.trim());
						}
					}
					String library = ele.attribute("library").getStringValue();
					String listenerPost = ele.attribute("listenerPost").getStringValue();
					node.setName(list.get(0));
					node.setIp(list.get(1));
					node.setPort(list.get(2));
					node.setRootPass(list.get(3));
					node.setUser(list.get(4));
					node.setdPath(list.get(5));
					node.setLibrary(library);
					node.setListenerPost(listenerPost);
//					node.setType("unused");
					
					nodeList.add(node);
				}
			}
		}
		for (CNodeEntity node : nodeList) {
			try {
				Session session = JschUtil.connect(node.getIp(), new Integer(node.getPort()), "root", node.getRootPass());
				if (session != null) {
					sessionMap.put(node, session);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSchException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * AbstractActivityNode source 源点 ; AbstractActivityNode target 目标点
	 * 
	 * @param status
	 */
	private void createLine(AbstractActivityNode source, AbstractActivityNode target, String status) {

		DeployConnection connectionLine = new DeployConnection(input);
		connectionLine.setSource(source);
		connectionLine.setTarget(target);
		connectionLine.setStatus(status);

		connectionLine.setTargetTerminal(null);
		connectionLine.attachTarget();
		connectionLine.setSourceTerminal(null);
		connectionLine.attachSource();
		connectionLine.setParent(this);
	}

	public void allNodeStatus(CReadWriteEntity entity) {

		mainNodeDB = null;
		prepareNodeDBList.clear();
		prepareNodePoolList.clear();
		for (CNodeEntity poolNode : nodeList) {
			if (poolNode.getLibrary().equals("主分发")) {
				poolNode.setListenerPost(entity.getPoolEntity().getPort());
				mainNodePool = poolNode;
				Session session = sessionMap.get(poolNode);
				if (session != null) {
					
					String command = "su - " + poolNode.getUser() + " -c \"export LD_LIBRARY_PATH="
							+ poolNode.getdPath() + "kingbasecluster/lib:" + poolNode.getdPath() + "db/lib;rm -f " +poolNode.getdPath()+"log/kingbasecluster_status; cd "
							+ poolNode.getdPath() + "kingbasecluster/bin/;./pcp_pool_status -U"+entity.getPoolEntity().getPcpUser()+" -w -p "+entity.getPoolEntity().getPcp_port()+"\"";
					ChannelExec openChannel = JschUtil.execCommand(session, command);

					Boolean status = writeInputStream(openChannel, command);
					if (!status) {
						mainNodePool.setType("down");
						if (flag) {
							boolean open = MessageDialog.openQuestion(UIUtils.getActiveShell(), "提示", "获取DB节点状态失败,是否重启kingbasecluster");
							if(open){
								JschUtil.execCommand1(session, "su - "+poolNode.getUser()+" -c \"rm -f " +poolNode.getdPath()+"run/kingbasecluster/kingbasecluster_status; export LD_LIBRARY_PATH="+poolNode.getdPath()+"kingbasecluster/lib; cd " + poolNode.getdPath() + "kingbasecluster/bin/;./kingbasecluster -a ../etc/cluster_hba.conf -f ../etc/kingbasecluster.conf -F ../etc/pcp.conf -n>./log.log 2>&1 &\"");
							}
						}
					} else {
						mainNodePool.setType("up");
					}
					flag = status;
					openChannel.disconnect();
				}
			} else if (poolNode.getLibrary().equals("备分发")){
				poolNode.setListenerPost(entity.getPoolEntity().getPort());
				Session session = sessionMap.get(poolNode);
				if (session != null) {

					String command = "su - " + poolNode.getUser() + " -c \"export LD_LIBRARY_PATH="
							+ poolNode.getdPath() + "kingbasecluster/lib:" + poolNode.getdPath() + "db/lib; cd "
							+ poolNode.getdPath() + "kingbasecluster/bin/;./pcp_pool_status -U"+entity.getPoolEntity().getPcpUser()+" -w -p "+entity.getPoolEntity().getPcp_port()+"\"";
					ChannelExec openChannel = JschUtil.execCommand(session, command);

					int status = JschUtil.returnSuccess(openChannel, 0);
					if (status!=0) {
						poolNode.setType("down");
					} else {
						poolNode.setType("up");
					}
					openChannel.disconnect();
				}
				prepareNodePoolList.add(poolNode);
			}
		}
	}

	public Boolean writeInputStream(ChannelExec channel, String command) {
		InputStream in;
		try {
			in = channel.getInputStream();
			int res = -1;
			StringBuffer buf = new StringBuffer();
			byte[] tmp = new byte[3072];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 3072);
					if (i < 0)
						break;
					String str = new String(tmp, 0, i);
					buf.append(str);
					if (str.contains("backend_hostname")) {
						String[] split = str.split("\n");
						analysisString(split);
					}
					str = null;
				}
				if (channel.isClosed()) {
					res = channel.getExitStatus();
					System.out.println(res + "-------" + (res == 0 ? "成功" : "出错:") + command);
					if (res != 0) {
						tmp = null;
						return false;
					}
					break;
				}
			}
			tmp = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 解析返回值
	 */
	private void analysisString(String[] split) {
		
		CNodeEntity entity = null;
		for (int i = 0; i < split.length; i++) {
			String value = split[i];
			if (value.contains("backend_hostname")) {
				entity = new CNodeEntity();
				entity.setIp((split[i + 1].split(":"))[1].trim());
			}
			if (value.contains("backend_port")) {
				entity.setPort((split[i + 1].split(":"))[1].trim());
			}
			if (value.contains("backend_data_directory")) {
				entity.setdPath((split[i + 1].split(":"))[1].trim());
			}
			if (value.contains("backend_status")) {
				String type = (split[i + 1].split(":"))[1].trim();
				entity.setType(type.equals("waiting") ? "up" : type);
			}
			entity = checkMainDB(entity);
		}
	}
	/**
	 * 检查哪个是主DB，哪个是备DB
	 */
	private CNodeEntity checkMainDB(CNodeEntity entity) {
		
		if ((entity != null && entity.getIp() != null && entity.getPort() != null && entity.getdPath() != null
				&& entity.getType() != null)) {

			boolean flag = true;
			for (CNodeEntity node : nodeList) {
				if (entity.getIp().equals(node.getIp()) && entity.getPort().equals(node.getListenerPost())
						&& entity.getdPath().startsWith(node.getdPath())) {
					Session session = sessionMap.get(node);

					if (mainNodeDB == null) {// 主DB为空时，需要区分

						ChannelExec openCommand = JschUtil.execCommand(session,
								"ls " + entity.getdPath() + "/recovery.done");
						String returnValue = JschUtil.returnInputStream(openCommand);
						openCommand.disconnect();
						if (!returnValue.equals("")) {
							entity.setLocation(node.getLocation());
							entity.setListenerPost(entity.getPort());
							entity.setPort(node.getPort());
							entity.setLibrary("主库");
							entity.setName(node.getName());
							entity.setUser(node.getUser());
							entity.setRootPass(node.getRootPass());
							entity.setdPath(node.getdPath());
							mainNodeDB = entity;
						} else {

							ChannelExec openCommand1 = JschUtil.execCommand(session,
									"ls " + entity.getdPath() + "/recovery.conf");
							String returnValue1 = JschUtil.returnInputStream(openCommand1);
							openCommand1.disconnect();
							if (!returnValue1.equals("")) {
								entity.setListenerPost(entity.getPort());
								entity.setPort(node.getPort());
								entity.setLocation(node.getLocation());
								entity.setName(node.getName());
								entity.setUser(node.getUser());
								entity.setRootPass(node.getRootPass());
								entity.setdPath(node.getdPath());
								entity.setLibrary("备库");
								prepareNodeDBList.add(entity);
							} else {
								entity.setListenerPost(entity.getPort());
								entity.setPort(node.getPort());
								entity.setLocation(node.getLocation());
								entity.setName(node.getName());
								entity.setUser(node.getUser());
								entity.setRootPass(node.getRootPass());
								entity.setdPath(node.getdPath());
								entity.setLibrary("主库");
								mainNodeDB = entity;
							}
						}
					} else {// 主DB不为空时，其余DB均为备DB
						entity.setListenerPost(entity.getPort());
						entity.setPort(node.getPort());
						entity.setLocation(node.getLocation());
						entity.setName(node.getName());
						entity.setUser(node.getUser());
						entity.setRootPass(node.getRootPass());
						entity.setdPath(node.getdPath());
						entity.setLibrary("备库");
						prepareNodeDBList.add(entity);
					}
				}else{
					if (!entity.getIp().equals(node.getIp())){
						flag = false;
					}
				}
			}
//			if (!flag) {
//				MessageDialog.openError(UIUtils.getActiveShell(), "错误",
//						entity.getIp() + "/" + entity.getPort() + "与设置的集群节点不一致，请检查");
//			}
			return null;
		} else {

			return entity;
		}
	}
	
	@Override
	public Object toXML(Object arg0) {
		return null;
	}

	@Override
	public List getFlowConnList() {
		return null;
	}

	@Override
	public List getFlowNodeList() {
		List<AbstractActivityNode> list = new ArrayList<AbstractActivityNode>();
		list.add(null);
		list.add(null);
		return list;
	}

	@Override
	public IFigure getFigure() {
		return null;
	}

	public CNodeEntity getMainNodeDB() {
		return mainNodeDB;
	}

	public void setMainNodeDB(CNodeEntity mainNodeDB) {
		this.mainNodeDB = mainNodeDB;
	}

	public CNodeEntity getMainNodePool() {
		return mainNodePool;
	}

	public void setMainNodePool(CNodeEntity mainNodePool) {
		this.mainNodePool = mainNodePool;
	}

	public List<CNodeEntity> getPrepareNodeDBList() {
		return prepareNodeDBList;
	}

	public void setPrepareNodeDBList(List<CNodeEntity> prepareNodeDBList) {
		this.prepareNodeDBList = prepareNodeDBList;
	}

	public List<CNodeEntity> getPrepareNodePoolList() {
		return prepareNodePoolList;
	}

	public void setPrepareNodePoolList(List<CNodeEntity> prepareNodePoolList) {
		this.prepareNodePoolList = prepareNodePoolList;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public Map<CNodeEntity, Session> getSessionMap() {
		return sessionMap;
	}

	public void setSessionMap(Map<CNodeEntity, Session> sessionMap) {
		this.sessionMap = sessionMap;
	}

	public CReadWriteEntity getNode() {
		return node;
	}

	public void setNode(CReadWriteEntity node) {
		this.node = node;
	}

	public List<CNodeEntity> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<CNodeEntity> nodeList) {
		this.nodeList = nodeList;
	}
	public void timeTask(long delay){
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		long period = 7000;
		timer.schedule(new AutoRefreshThread(), delay, period);
	}

}
