/**
 * 
 */
package com.kingbase.db.replication.bundle.graphical.figure;

import java.util.Collection;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.pentaho.di.graphical.figure.FigureWithAnchor;
import org.pentaho.di.viewer.CBasicTreeViewer;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataBase;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataInfo;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataSource;
import com.kingbase.db.replication.bundle.model.tree.ReplicationFile;
import com.kingbase.db.replication.bundle.model.tree.ReplicationRoot;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataBase;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataInfo;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataSource;

/**
 * @author jpliu
 *
 */
public class ReplicationFigure extends FigureWithAnchor implements MouseMotionListener {

	private static final int[] LINE_DASH = new int[] { 3 };
	private DataBaseInput input;
	private SubscribeDataInfo subInfo;
	private ReleaseDataInfo relInfo;

	public ReplicationFigure(String text, Image image, DataBaseInput input, SubscribeDataInfo dataInfo) {
		super();
		this.input = input;
		this.subInfo = dataInfo;
		init(image);
		addMouseMotionListener(this);
		setLayoutManager(new ToolbarLayout());
		setToolTip(new Label(text));
		setBorder(new MarginBorder(2));
	}

	public ReplicationFigure(String text, Image image, DataBaseInput input, ReleaseDataInfo dataInfo) {
		super();
		this.input = input;
		this.relInfo = dataInfo;
		init(image);
		addMouseMotionListener(this);
		setLayoutManager(new ToolbarLayout());
		setToolTip(new Label(text));
		setBorder(new MarginBorder(2));
	}

	private void init(Image image) {
		ImageFigure iconFigure = new ImageFigure(image);
		add(iconFigure);
		setOpaque(false);
	}

	private boolean isOver = false;

	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		if (isOver) {
			int w = r.getSize().width - 1;
			int h = r.getSize().height - 1;
			g.setLineDash(LINE_DASH);
			g.drawRectangle(r.x, r.y, w, h);
		}
		super.paintFigure(g);
	}

	public void mouseDragged(MouseEvent me) {
	}

	public void mouseEntered(MouseEvent me) {
		isOver = true;
		repaint();
	}

	public void mouseExited(MouseEvent me) {
		isOver = false;
		repaint();
	}

	public void mouseHover(MouseEvent me) {
	}

	@Override
	public void handleMousePressed(MouseEvent event) {
		super.handleMousePressed(event);
		System.out.println("测试点击事件");
		
		CBasicTreeViewer treeView = input.getTreeView();
		ReplicationRoot root = (ReplicationRoot) (treeView.getTree().getItems())[0].getData();// 发布与订阅folder
		root.treeExpanded();
		ReplicationFile relFolder = (ReplicationFile) ((root.getChildren())[0]);
		ReplicationFile subFolder = (ReplicationFile) ((root.getChildren())[1]);

		if (relInfo != null) {
			relFolder.treeExpanded();
			Collection sourceList = relFolder.getChildrenList();
			boolean falgRel = false;
			for (Object objSource : sourceList) {// 循环判断服务器
				ReleaseDataSource data = (ReleaseDataSource) objSource;
				if (data.getDbName().equals(relInfo.getDbName()) && data.getDbServer().equals(relInfo.getDbServer())
						&& data.getDbPort().equals(relInfo.getDbPort()) && data.getDbUser().equals(relInfo.getDbUser())
						&& data.getDbPasswrod().equals(relInfo.getDbPasswrod())) {
					data.treeExpanded();
					Collection databaseList = data.getChildrenList();
					for (Object objDB : databaseList) {// 循环判断服务器下面的数据库
						ReleaseDataBase database = (ReleaseDataBase) objDB;
						if (database.getDatabaseName().equals(relInfo.getDatabaseName())) {
							database.treeExpanded();
							Collection relInfoList = database.getChildrenList();
							if (relInfoList == null) {
								continue;
							}
							for (Object objRel : relInfoList) {// 循环判断服务器下对应的数据库下面具体的发布，合适给予选中
								ReleaseDataInfo release = (ReleaseDataInfo) objRel;
								if (release.getReleaseName().equals(relInfo.getReleaseName())) {

									StructuredSelection structuredSelection = new StructuredSelection(release);//eclipse本身的Link with Editor功能
									treeView.collapseAll();
									treeView.setSelection(structuredSelection, true);
									falgRel = true;
									break;

								}
							}
						}
						if (falgRel) {
							break;
						}
					}
				}
				if (falgRel) {
					break;
				}
			}
		}
		if (subInfo != null) {
			subFolder.treeExpanded();
			Collection sourceList = subFolder.getChildrenList();
			boolean falgSub = false;
			for (Object objSource : sourceList) {// 循环判断服务器
				SubscribeDataSource data = (SubscribeDataSource) objSource;
				if (data.getDbName().equals(subInfo.getDbName()) && data.getDbServer().equals(subInfo.getDbServer())
						&& data.getDbPort().equals(subInfo.getDbPort()) && data.getDbUser().equals(subInfo.getDbUser())
						&& data.getDbPasswrod().equals(subInfo.getDbPasswrod())) {
					data.treeExpanded();
					Collection databaseList = data.getChildrenList();
					for (Object objDB : databaseList) {// 循环判断服务器下面的数据库
						SubscribeDataBase database = (SubscribeDataBase) objDB;
						if (database.getDatabaseName().equals(subInfo.getDatabaseName())) {
							database.treeExpanded();
							Collection subInfoList = database.getChildrenList();
							if (subInfoList == null) {
								continue;
							}
							for (Object objRel : subInfoList) {// 循环判断服务器下对应的数据库下面具体的发布，合适给予选中
								SubscribeDataInfo subscribe = (SubscribeDataInfo) objRel;
								if (subscribe.getSubscribeName().equals(subInfo.getSubscribeName())) {
									
									StructuredSelection structuredSelection = new StructuredSelection(subscribe);
									treeView.collapseAll();
									treeView.setSelection(structuredSelection, true);
									falgSub = true;
									break;
									
								}
							}
						}
						if (falgSub) {
							break;
						}
					}
				}
				if (falgSub) {
					break;
				}
			}
		}
	}

	public void mouseMoved(MouseEvent me) {
	}

	public boolean hasBreakpoint() {
		return false;
	}

	@Override
	public String getText() {
		return null;
	}

}
