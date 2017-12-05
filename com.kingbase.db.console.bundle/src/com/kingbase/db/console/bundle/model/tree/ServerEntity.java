package com.kingbase.db.console.bundle.model.tree;

/**
 * 服务器状态实体类
 * @author jpliu
 *
 */
public class ServerEntity {

	public ServerEntity(){}
	
	public ServerEntity(String id,String type,String result){
		this.id=id;
		this.type=type;
		this.result=result;
	}
	private String id;
	private String type;
	private String result;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
