package com.kingbase.db.replication.bundle.graphical.model;

import org.eclipse.draw2d.geometry.Point;
import org.pentaho.di.graphical.model.ActivitySourceNode;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataInfo;
/**
 * @author jpliu
 *
 */
public class ReplicationSourceNode extends ActivitySourceNode {

	private static final long serialVersionUID = 1L;
	private ReleaseDataInfo dataInfo;
	private DataBaseInput input;

	public ReplicationSourceNode(String id, String name, ReleaseDataInfo dataInfo, DataBaseInput input) {
		super(id, name);
		setName(name);
		this.input = input;
		this.dataInfo = dataInfo;
	}

	public void fromXML(Point point) {
		setLocation(point);
	}

	public ReleaseDataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(ReleaseDataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}

	public DataBaseInput getInput() {
		return input;
	}

	public void setInput(DataBaseInput input) {
		this.input = input;
	}
}
