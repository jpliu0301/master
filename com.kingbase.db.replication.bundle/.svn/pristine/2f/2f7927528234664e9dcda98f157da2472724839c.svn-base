package com.kingbase.db.replication.bundle.graphical.model;

import org.eclipse.draw2d.geometry.Point;
import org.pentaho.di.graphical.model.ActivityTargetNode;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataInfo;
/**
 * @author jpliu
 *
 */
public class ReplicationTargetNode extends ActivityTargetNode {

	private static final long serialVersionUID = 1L;
	private SubscribeDataInfo dataInfo;
    private DataBaseInput input;
	public ReplicationTargetNode(String id, String name, SubscribeDataInfo dataInfo, DataBaseInput input) {
		super(id, name);
		setName(name);
		this.input = input;
		this.dataInfo = dataInfo;
	}

	public void fromXML(Point point) {
		setLocation(point);
	}

	public SubscribeDataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(SubscribeDataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}

	public DataBaseInput getInput() {
		return input;
	}

	public void setInput(DataBaseInput input) {
		this.input = input;
	}
}
