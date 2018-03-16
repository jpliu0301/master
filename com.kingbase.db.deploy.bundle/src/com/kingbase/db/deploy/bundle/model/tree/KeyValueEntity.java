package com.kingbase.db.deploy.bundle.model.tree;

public class KeyValueEntity {
	public KeyValueEntity(){}
	
	public KeyValueEntity(String key,String value){
		this.key=key;
		this.value=value;
	}
	
	private String key;
	private String value;
	private String oldValue;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	
}
