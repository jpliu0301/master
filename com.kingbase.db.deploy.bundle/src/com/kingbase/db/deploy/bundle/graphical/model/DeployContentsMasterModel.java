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
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.graphical.model.AbstractActivityNode;
import org.pentaho.di.graphical.model.AbstractFlowContainerNode;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.deploy.bundle.KBDeployCore;
import com.kingbase.db.deploy.bundle.model.tree.CNodeEntity;
import com.kingbase.db.deploy.bundle.model.tree.CReadWriteEntity;
import com.kingbase.db.deploy.bundle.model.tree.MasterStandbyEntity;
import com.kingbase.db.deploy.bundle.model.tree.TableNodeEntity;

public class DeployContentsMasterModel extends AbstractFlowContainerNode {

	private DataBaseInput input;
	private CNodeEntity mainNodeDB;
	private List<CNodeEntity> prepareNodeDBList = new ArrayList<CNodeEntity>();
	private CNodeEntity mainNodePool;
	private List<CNodeEntity> prepareNodePoolList = new ArrayList<CNodeEntity>();
	private List<CNodeEntity> nodeList = new ArrayList<CNodeEntity>();
	private Map<String, Session> sessionMap = new HashMap<String, Session>();
	private ScheduledExecutorService service;
	private CReadWriteEntity node;
	private long time = System.currentTimeMillis();
	private CNodeEntity mainDownDB;// 当某次查询DB节点的时候，若是查询节点失败，则拿到上一次的节点，只绘制，不连线
	private List<CNodeEntity> prepareDownDBList = new ArrayList<CNodeEntity>();
	private CNodeEntity mainDownPool;// 当某次查询DB节点的时候，若是查询节点失败，则拿到上一次的节点，只绘制，不连线
	private List<CNodeEntity> prepareDownPoolList = new ArrayList<CNodeEntity>();
	private List<DeployConnection> lineList = new ArrayList<DeployConnection>();
	
	public final static String up = "up";
	public final static String down = "down";
	private final static String zero = "0";
	private final static String one = "1";
	private final static String main_DB = "main DB";
	private final static String slave_DB = "slave DB";
	private final static String underline = "_";
	private static String DBvip = null;
	private static String Clustervip = null;
	public final static Image subscriber_enable = ImageURL.createImage(KBDeployCore.PLUGIN_ID, ImageURL.tree_subscriber_enable);
	public final static Image subscriber_disable = ImageURL.createImage(KBDeployCore.PLUGIN_ID, ImageURL.tree_subscriber_disable);
	public final static Image database_enable = ImageURL.createImage(KBDeployCore.PLUGIN_ID, ImageURL.tree_database_enable);
	public final static Image database_disable = ImageURL.createImage(KBDeployCore.PLUGIN_ID, ImageURL.tree_database_disable);
	public final static Color red = ColorConstants.red;
	public final static Color lightGreen = ColorConstants.lightGreen;
	public final static Color black = ColorConstants.black;

	

	public DeployContentsMasterModel(DataBaseInput input) {
		super(UUID.randomUUID().toString(), "model");
		this.input = input;
		this.node = (CReadWriteEntity) input.getNode();
		getXmlNode(node);
		for (int i = 0; i < nodeList.size(); i++) {
			DeployConnection connectionLine = new DeployConnection(input);
			lineList.add(connectionLine);
		}
		DBvip = "ip addr | grep -w '" + node.getPosEntity().getDelegate_IP() + "'|wc -l";
		Clustervip = "ip addr | grep -w '" + node.getPoolEntity().getDelegate_IP() + "'|wc -l";
	}

	@Override
	protected void init() {
		super.init();
		timeTask(7);
	}

	@Override
	public void fromXML(Object arg0) {
		for (DeployConnection line : lineList) {
			line.setFlag(false);
		}

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
		} else {// 显示在pool down的前一刻的节点信息,不连线
			if (mainDownPool != null) {
				mainPool = new DeploySourceNode(input, mainDownPool, true, true);
				mainPool.fromXML(mainDownPool.getLocation() == null ? new Point(600, 250) : mainDownPool.getLocation());
				this.addChild(mainPool);
			}
		}

		DeploySourceNode mainDB = null;
		if (mainNodeDB != null) {

			mainDB = new DeploySourceNode(input, mainNodeDB, false, true);
			mainDB.fromXML(mainNodeDB.getLocation() == null ? new Point(600, 450) : mainNodeDB.getLocation());
			this.addChild(mainDB);
		} else {// 显示在pool down的前一刻的节点信息,不连线
			if (mainDownDB != null) {
				mainDB = new DeploySourceNode(input, mainDownDB, false, true);
				mainDB.fromXML(mainDownDB.getLocation() == null ? new Point(600, 450) : mainDownDB.getLocation());
				this.addChild(mainDB);
			}
		}
		if ((mainNodeDB != null || mainDownDB != null) && (mainNodePool != null || mainDownPool != null)) {

			createLine(mainPool, mainDB, mainDB.getNode().getType());
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
				prepareDB.fromXML(cNodeEntity.getLocation() == null ? new Point(600 + k * 100, 600) : cNodeEntity.getLocation());
				this.addChild(prepareDB);
				createLine(mainDB, prepareDB, prepareDB.getNode().getType());// main
																				// DB---slave
																				// DB
				
				prepareDB = null;
				i++;
			}
		} else {// 显示在pool down的前一刻的节点信息,不连线
			if (mainNodeDB == null && prepareDownDBList != null && prepareDownDBList.size() > 0) {
				for (CNodeEntity cNodeEntity : prepareDownDBList) {
					if (i % 2 == 0) {
						k = i / 2;
					} else {
						k = -(k + 1);
					}
					DeployTargetNode prepareDB = new DeployTargetNode(input, cNodeEntity, false, false);
					prepareDB.fromXML(cNodeEntity.getLocation() == null ? new Point(600 + k * 100, 600) : cNodeEntity.getLocation());
					this.addChild(prepareDB);
					createLine(mainDB, prepareDB, prepareDB.getNode().getType());// main
																					// DB---slave
					prepareDB = null;														// DB
					i++;
				}
			}
		}

		if (prepareNodePoolList != null && prepareNodePoolList.size() > 0) {
			for (CNodeEntity cNodeEntity : prepareNodePoolList) {
				DeployTargetNode preparePool = new DeployTargetNode(input, cNodeEntity, true, false);
				preparePool.fromXML(cNodeEntity.getLocation() == null ? new Point(600 + k * 100, 150) : cNodeEntity.getLocation());
				this.addChild(preparePool);
				
				createLine(mainPool, preparePool, preparePool.getNode().getType());
				createLine(preparePool, mainPool, mainPool.getNode().getType());
				
				preparePool = null;
			}
		} else {// 显示在pool down的前一刻的节点信息,不连线
			if (mainNodePool == null && prepareDownPoolList != null && prepareDownPoolList.size() > 0) {
				for (CNodeEntity cNodeEntity : prepareDownPoolList) {
					if (i % 2 == 0) {
						k = i / 2;
					} else {
						k = -(k + 1);
					}
					DeployTargetNode preparePool = new DeployTargetNode(input, cNodeEntity, true, false);
					preparePool.fromXML(cNodeEntity.getLocation() == null ? new Point(600 + k * 100, 150) : cNodeEntity.getLocation());
					this.addChild(preparePool);
					createLine(mainPool, preparePool, preparePool.getNode().getType());
					createLine(preparePool, mainPool, mainPool.getNode().getType());
					
					preparePool = null;
					i++;
				}
			}
		}
		
		this.refresh();
		mainDB = null;
		mainPool = null;
		long curtime = System.currentTimeMillis() - time;
		// System.out.println(curtime / 1000);
		if (curtime / 1200000 == 1) {// 每20分钟触发一次gc
			System.gc();
			time = System.currentTimeMillis();
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
					if (obj.getIp().equals(node.getIp()) && obj.getPort().equals(node.getPort()) && obj.getRootPass().equals(node.getRootPass()) && obj.getUser().equals(node.getUser())
							&& obj.getNodeType().equals(node.getNodeType()) && obj.getName().equals(node.getName()) && obj.getdPath().equals(node.getdPath())) {
						obj.setLocation(targetNode.getLocation());
					}
				}
			} else if (object instanceof DeploySourceNode) {
				DeploySourceNode sourceNode = (DeploySourceNode) object;
				CNodeEntity node = sourceNode.getNode();
				for (CNodeEntity obj : nodeList) {
					if (obj.getIp().equals(node.getIp()) && obj.getPort().equals(node.getPort()) && obj.getRootPass().equals(node.getRootPass()) && obj.getUser().equals(node.getUser())
							&& obj.getNodeType().equals(node.getNodeType()) && obj.getName().equals(node.getName()) && obj.getdPath().equals(node.getdPath())) {
						obj.setLocation(sourceNode.getLocation());
					}
				}
			}
		}
	}

	private void getXmlNode(CReadWriteEntity entity) {
		MasterStandbyEntity parent = (MasterStandbyEntity) entity.getParentModel();
		IFolder folder = parent.getFolder();
		IFile file = (IFile) folder.findMember("master.xml");

		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(new BufferedReader(new InputStreamReader(new FileInputStream(file.getLocation().toFile()), "UTF-8")));
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
					String nodeType = ele.attribute("nodeType").getStringValue();
					String library = ele.attribute("library").getStringValue();
					String listenerPost = ele.attribute("listenerPost").getStringValue();
					node.setName(list.get(0));
					node.setIp(list.get(1));
					node.setPort(list.get(2));
					node.setRootPass(list.get(3));
					node.setUser(list.get(4));
					node.setdPath(list.get(5));
					node.setLibrary(library);
					node.setNodeType(nodeType);
					node.setListenerPost(listenerPost);
					node.setType("unused");

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
					String nodeType = ele.attribute("nodeType").getStringValue();
					String library = ele.attribute("library").getStringValue();
					String listenerPost = ele.attribute("listenerPost").getStringValue();
					node.setName(list.get(0));
					node.setIp(list.get(1));
					node.setPort(list.get(2));
					node.setRootPass(list.get(3));
					node.setUser(list.get(4));
					node.setdPath(list.get(5));
					node.setLibrary(library);
					node.setNodeType(nodeType);
					node.setListenerPost(listenerPost);
					node.setType("unused");

					nodeList.add(node);
				}
			}
		}
		for (CNodeEntity nodeDB : nodeList) {
			nodeDB.setType("down");
			nodeDB.setLocation(null);
			if (nodeDB.getLibrary().equals(main_DB)) {
				mainDownDB = nodeDB;
			} else if (nodeDB.getLibrary().equals(slave_DB)) {
				prepareDownDBList.add(nodeDB);
			} else if (nodeDB.getLibrary().equals("main cluster")) {
				mainDownPool = nodeDB;
			} else if (nodeDB.getLibrary().equals("slave cluster")) {
				prepareDownPoolList.add(nodeDB);
			}
		}
	}

	/**
	 * AbstractActivityNode source 源点 ; AbstractActivityNode target 目标点
	 * 
	 * @param status
	 */
	private void createLine(AbstractActivityNode source, AbstractActivityNode target, String status) {

		// DeployConnection connectionLine = new DeployConnection(input);
		for (DeployConnection line : lineList) {
			if (!line.isFlag()) {
				line.setFlag(true);
				line.setSource(source);
				line.setTarget(target);
				line.setStatus(status);

				line.setTargetTerminal(null);
				line.attachTarget();
				line.setSourceTerminal(null);
				line.attachSource();
				line.setParent(this);
				return ;
			}
		}
	}

	public void allNodeStatus(CReadWriteEntity entity) {

		mainNodeDB = null;
		prepareNodeDBList.clear();
		mainNodePool = null;
		prepareNodePoolList.clear();
		List<TableNodeEntity> listPool = node.getPoolEntity().getListPool();
		List<TableNodeEntity> listDB = node.getPosEntity().getListDb();
		for (TableNodeEntity node1 : listDB) {
			CNodeEntity node = node1.getNodeEntity();
			node.setNodeType(node1.getNodeType());
			for (CNodeEntity match : nodeList) {
				if (match.getIp().equals(node1.getNodeEntity().getIp()) && match.getPort().equals(node1.getNodeEntity().getPort()) && match.getRootPass().equals(node1.getNodeEntity().getRootPass())
						&& match.getUser().equals(node1.getNodeEntity().getUser()) && match.getNodeType().equals(node1.getNodeType())// 注意
						&& match.getName().equals(node1.getNodeEntity().getName()) && match.getdPath().startsWith(node1.getNodeEntity().getdPath())) {
					node.setLocation(match.getLocation());
					node.setdPath(match.getdPath());
				}
			}
			checkMainDB1(node);
		}
		if (mainNodeDB == null && prepareNodeDBList.size() == listDB.size()) {
			for (CNodeEntity slave : prepareNodeDBList) {
				if (slave.getIp().equals(mainDownDB.getIp()) && slave.getPort().equals(mainDownDB.getPort()) && slave.getRootPass().equals(mainDownDB.getRootPass())
						&& slave.getUser().equals(mainDownDB.getUser()) && slave.getNodeType().equals(mainDownDB.getNodeType())// 注意
						&& slave.getName().equals(mainDownDB.getName()) && slave.getdPath().startsWith(mainDownDB.getdPath())) {
					mainNodeDB = slave;
					break;
				}
			}
			prepareNodeDBList.remove(mainNodeDB);
		}
		for (TableNodeEntity node1 : listPool) {
			CNodeEntity node = node1.getNodeEntity();
			node.setNodeType(node1.getNodeType());
			for (CNodeEntity match : nodeList) {
				if (match.getIp().equals(node.getIp()) && match.getPort().equals(node.getPort()) && match.getRootPass().equals(node.getRootPass()) && match.getUser().equals(node.getUser())
						&& match.getNodeType().equals(node1.getNodeType())// 注意
						&& match.getName().equals(node.getName()) && match.getdPath().startsWith(node.getdPath())) {
					node.setLocation(match.getLocation());
				}
			}
			Session session = null;
			String key = node.getIp() + underline + node.getPort() + underline + node.getRootPass();
			session = sessionMap.get(key);
			try {
				if (session != null && session.isConnected()) {
//					String command = "ls " + node.getdPath() + "log/kingbasecluster/kingbasecluster.pid |wc -l";
					String command = "ps -ef | grep `cat " + node.getdPath() + "log/kingbasecluster/kingbasecluster.pid 2>/dev/null` 2>/dev/null | grep -v grep | wc -l";
					
					ChannelExec openChannel = execCommand(session, command);
					if (openChannel == null) {// 只要是连不上了，就去掉
						session.disconnect();
						sessionMap.remove(key);
					}
					String value = returnInputStream(openChannel);
					if (mainNodePool == null) {
						ChannelExec channel = execCommand(session, Clustervip);
						if (channel == null) {
							session.disconnect();
							sessionMap.remove(key);
						}
						String returnValue = returnInputStream(channel);
						if (channel != null) {
							channel.disconnect();
						}
						if (returnValue.trim().equals(one)) {
							mainNodePool = node;
							mainNodePool.setLibrary(node1.getLibrary());
							mainNodePool.setListenerPost(entity.getPoolEntity().getPort());
							mainNodePool.setType(value.trim().equals(zero) ? down : up);
						} else {
							node.setType(value.trim().equals(zero) ? down : up);
							node.setLibrary(node1.getLibrary());
							node.setListenerPost(entity.getPoolEntity().getPort());
							prepareNodePoolList.add(node);
						}
					} else {
						node.setType(value.trim().equals(zero) ? down : up);
						node.setLibrary(node1.getLibrary());
						node.setListenerPost(entity.getPoolEntity().getPort());
						prepareNodePoolList.add(node);
					}

					if (openChannel != null) {
						openChannel.disconnect();
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		if (mainNodePool == null && prepareNodePoolList.size() == listPool.size()) {
			for (CNodeEntity slave : prepareNodePoolList) {
				if (slave.getIp().equals(mainDownPool.getIp()) && slave.getPort().equals(mainDownPool.getPort()) && slave.getRootPass().equals(mainDownPool.getRootPass())
						&& slave.getUser().equals(mainDownPool.getUser()) && slave.getNodeType().equals(mainDownPool.getNodeType())// 注意
						&& slave.getName().equals(mainDownPool.getName()) && slave.getdPath().startsWith(mainDownPool.getdPath())) {
					mainNodePool = slave;
					break;
				}
			}
			prepareNodePoolList.remove(mainNodePool);
		}
	}

	/**
	 * 检查哪个是主DB，哪个是备DB
	 */
	private void checkMainDB1(CNodeEntity node1) {
		String key = node1.getIp() + underline + node1.getPort() + underline + node1.getRootPass();
		Session session = sessionMap.get(key);
		if (session == null || !session.isConnected()) {
			sessionMap.remove(key);
			return;
		}
		String count = "";
		String command = " cd " + node1.getdPath() + "db/bin/;export LD_LIBRARY_PATH=../lib;./ksql -U" + node.getPosEntity().getDbUser() + " -p " + node.getPosEntity().getListenerPort()
				+ " TEST -c \"select 33333;\" | grep 33333 | wc -l";
		ChannelExec openChannel = execCommand(session, command);
		if (openChannel == null) {// 只要是连不上了，就去掉
			sessionMap.remove(key);
		}
		count = returnInputStream(openChannel);
		if (mainNodeDB == null) {// 主DB为空时，需要区分

			ChannelExec openCommand = execCommand(session, DBvip);
			String returnValue = returnInputStream(openCommand);
			if (openCommand != null) {
				openCommand.disconnect();
			}
			if (returnValue.trim().equals(one)) {

				node1.setListenerPost(node.getPosEntity().getListenerPort());
				node1.setLibrary(main_DB);
				node1.setType(count.trim().equals(zero) ? down : up);
				mainNodeDB = node1;
			} else {

				node1.setListenerPost(node.getPosEntity().getListenerPort());
				node1.setLibrary(slave_DB);
				node1.setType(count.trim().equals(zero) ? down : up);
				prepareNodeDBList.add(node1);
			}
		} else {// 主DB不为空时，其余DB均为备DB
			node1.setListenerPost(node.getPosEntity().getListenerPort());
			node1.setLibrary(slave_DB);
			node1.setType(count.trim().equals(zero) ? down : up);
			prepareNodeDBList.add(node1);
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

	public ScheduledExecutorService getService() {
		return service;
	}

	public void setService(ScheduledExecutorService service) {
		this.service = service;
	}

	@Override
	public IFigure getFigure() {
		return null;
	}

	public CNodeEntity getMainNodeDB() {
		return mainNodeDB == null ? mainDownDB : mainNodeDB;
	}

	public void setMainNodeDB(CNodeEntity mainNodeDB) {
		this.mainNodeDB = mainNodeDB;
	}

	public List<CNodeEntity> getPrepareNodeDBList() {
		return prepareNodeDBList;
	}

	public void setPrepareNodeDBList(List<CNodeEntity> prepareNodeDBList) {
		this.prepareNodeDBList = prepareNodeDBList;
	}

	public CNodeEntity getMainNodePool() {
		return mainNodePool == null ? mainDownPool : mainNodePool;
	}

	public void setMainNodePool(CNodeEntity mainNodePool) {
		this.mainNodePool = mainNodePool;
	}

	public List<CNodeEntity> getPrepareNodePoolList() {
		return prepareNodePoolList;
	}

	public void setPrepareNodePoolList(List<CNodeEntity> prepareNodePoolList) {
		this.prepareNodePoolList = prepareNodePoolList;
	}

	public Map<String, Session> getSessionMap() {
		return sessionMap;
	}

	public void setSessionMap(Map<String, Session> sessionMap) {
		this.sessionMap = sessionMap;
	}

	public CReadWriteEntity getNode() {
		return node;
	}

	public void setNode(CReadWriteEntity node) {
		this.node = node;
	}

	public CNodeEntity getMainDownDB() {
		return mainDownDB;
	}

	public void setMainDownDB(CNodeEntity mainDownDB) {
		this.mainDownDB = mainDownDB;
	}

	public List<CNodeEntity> getPrepareDownDBList() {
		return prepareDownDBList;
	}

	public void setPrepareDownDBList(List<CNodeEntity> prepareDBList) {
		this.prepareDownDBList = prepareDBList;
	}

	public CNodeEntity getMainDownPool() {
		return mainDownPool;
	}

	public void setMainDownPool(CNodeEntity mainDownPool) {
		this.mainDownPool = mainDownPool;
	}

	public List<CNodeEntity> getPrepareDownPoolList() {
		return prepareDownPoolList;
	}

	public void setPrepareDownPoolList(List<CNodeEntity> prepareDownPoolList) {
		this.prepareDownPoolList = prepareDownPoolList;
	}

	public void timeTask(long delay2) {
		service = Executors.newScheduledThreadPool(3);
		long initialDelay2 = 2;
		// 从现在开始2秒钟之后，每隔2秒钟执行一次job2
		service.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						fromXML(true);
					}
				});
			}
		}, initialDelay2, delay2, TimeUnit.SECONDS);
		service.scheduleWithFixedDelay(new Runnable() {// 异步刷新与机器的连接,线程池内的线程本身就时后台线程

					@Override
					public void run() {
//						System.out.println("    " + Thread.currentThread().getName() + " ============");
						for (CNodeEntity nodeDB : nodeList) {
							if (nodeDB.getLibrary().contains("DB")) {

								try {
									String key = nodeDB.getIp() + underline + nodeDB.getPort() + underline + nodeDB.getRootPass();
									Session session = sessionMap.get(key);
									if (session == null) {
										session = connect(nodeDB.getIp(), new Integer(nodeDB.getPort()), "root", nodeDB.getRootPass());
										if (session != null) {
											sessionMap.put(key, session);
										}
									}
								} catch (NumberFormatException e) {
									e.printStackTrace();
								} catch (JSchException e) {
									e.printStackTrace();
								}
							} else if (nodeDB.getLibrary().contains("cluster")) {
								try {
									String key = nodeDB.getIp() + underline + nodeDB.getPort() + underline + nodeDB.getRootPass();
									Session session = sessionMap.get(key);
									if (session == null) {
										session = connect(nodeDB.getIp(), new Integer(nodeDB.getPort()), "root", nodeDB.getRootPass());
										if (session != null) {
											sessionMap.put(key, session);
										}
									}
								} catch (NumberFormatException e) {
									e.printStackTrace();
								} catch (JSchException e) {
									e.printStackTrace();
								}
							}
						}
						// thread.setDaemon(true);
						// thread.start();
					}
				}, initialDelay2, 10, TimeUnit.SECONDS);
	}

	public static Session connect(String host, Integer port, String user, String password) throws JSchException {
		Session session = null;
		try {
			JSch jsch = new JSch();
			if (port != null) {
				session = jsch.getSession(user, host, port.intValue());
			} else {
				session = jsch.getSession(user, host);
			}
			session.setPassword(password);
			// 设置第一次登陆的时候提示，可选值:(ask | yes | no)
			session.setConfig("StrictHostKeyChecking", "no");
			// 8秒连接超时
			session.connect(8000);
		} catch (JSchException e) {
			// e.printStackTrace();
			System.out.println("SFTPUitl 获取" + host + "连接发生错误\n" + e.getMessage());
			return null;
		}
		return session;
	}

	public static ChannelExec execCommand(Session session, String command) {
		ChannelExec openChannel = null;
		try {
			openChannel = (ChannelExec) session.openChannel("exec");
			openChannel.setCommand(command);
			openChannel.connect();
		} catch (JSchException e) {
			// e.printStackTrace();
			return null;
		}
		return openChannel;
	}

	/**
	 * 获取正确返回值
	 * 
	 * */
	public static String returnInputStream(ChannelExec channel) {
		if (channel == null) {
			return "";
		}
		StringBuffer strBuffer = new StringBuffer(); // 执行SSH返回的结果
		byte[] tmp = new byte[256]; // 读数据缓存
		try {
			InputStream in = channel.getInputStream();
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 256);
					if (i < 0) {
						break;
					}
					strBuffer.append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					if(in.available()>0) continue;
					channel.disconnect();
					break;
				}
				try {
					Thread.sleep(10);
				} catch (Exception ee) {
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		tmp = null;
		System.out.println("-----------"+(strBuffer.toString().trim().equals("")?"空":strBuffer.toString()));
		return strBuffer.toString();
	}
}
