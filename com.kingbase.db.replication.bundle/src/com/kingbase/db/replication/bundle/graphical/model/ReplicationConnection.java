/**
 * 
 */
package com.kingbase.db.replication.bundle.graphical.model;

import java.util.UUID;

import org.pentaho.di.graphical.model.BendpointConnection;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataInfo;

/**
 * @author jpliu
 *
 */
public class ReplicationConnection extends BendpointConnection {

	private static final long serialVersionUID = 1L;
	private SubscribeDataInfo dataInfo;
    private DataBaseInput input;
	public ReplicationConnection(SubscribeDataInfo dataInfo, DataBaseInput input) {
		this(UUID.randomUUID().toString(), "订阅");
		this.dataInfo = dataInfo;
		this.input = input;
	}

	public ReplicationConnection(String id, String name) {
		super(id, name);
	}

	public void attachSource() {
		if (!(getSource() instanceof ReplicationSourceNode)) {
			return;
		}
		if (getSource() == getTarget()) {
			return;
		}
		super.attachSource();
	}

	/**
	 * 绑定目标
	 */
	public void attachTarget() {
		if (!(getTarget() instanceof ReplicationTargetNode)) {
			return;
		}
		super.attachTarget();
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
