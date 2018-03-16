package com.kingbase.db.replication.bundle.util;

import java.util.ArrayList;
import java.util.List;

public class DataTypeEntity {

	private String name;         //名称
	private boolean isLength;    //长度
	private boolean isAccuracy;  //精度
	
	public DataTypeEntity(String name,boolean isLength,boolean isAccuracy){
		this.name=name;
		this.isLength=isLength;
		this.isAccuracy=isAccuracy;
	}
	public String getName() {
		return name;
	}
	public boolean isLength() {
		return isLength;
	}
	public boolean isAccuracy() {
		return isAccuracy;
	}
	
	public static List<DataTypeEntity> getInstence(){
		List<DataTypeEntity> list=new ArrayList<DataTypeEntity>();
		list.add(new DataTypeEntity("BOOLEAN",false,false));
		list.add(new DataTypeEntity("BYTEA",false,false));
		list.add(new DataTypeEntity("BIGINT",false,false));
		list.add(new DataTypeEntity("BIT",false,false));
		list.add(new DataTypeEntity("BIT VARYING",true,false));
		list.add(new DataTypeEntity("BLOB",false,false));
		list.add(new DataTypeEntity("BINARY",false,false));
		list.add(new DataTypeEntity("CHAR",true,false));
		list.add(new DataTypeEntity("CLOB",false,false));
		list.add(new DataTypeEntity("DOUBLE",false,false));
		list.add(new DataTypeEntity("DATE",false,false));
		list.add(new DataTypeEntity("DATETIME",false,false));
		list.add(new DataTypeEntity("DECIMAL",true,false));
		list.add(new DataTypeEntity("FLOAT",false,false));
		list.add(new DataTypeEntity("INT4",false,false));
		list.add(new DataTypeEntity("INT8",false,false));
		list.add(new DataTypeEntity("INTEGER",false,false));
		list.add(new DataTypeEntity("INTERVAL YEAR",false,false));
		list.add(new DataTypeEntity("INTERVAL MONTH",false,false));
		list.add(new DataTypeEntity("INTERVAL DAY",false,false));
		list.add(new DataTypeEntity("INTERVAL HOUR",false,false));
		list.add(new DataTypeEntity("INTERVAL MINUTE",false,false));
		list.add(new DataTypeEntity("INTERVAL SECOND",false,false));
		list.add(new DataTypeEntity("INTERVAL YEAR TO MONTH",false,false));
		list.add(new DataTypeEntity("INTERVAL DAY TO SECOND",false,false));
		list.add(new DataTypeEntity("NUMERIC",true,true));
		list.add(new DataTypeEntity("REAL",false,false));
		list.add(new DataTypeEntity("SMALLINT",false,false));
		list.add(new DataTypeEntity("TEXT",false,false));
		list.add(new DataTypeEntity("TIME",false,false));
		list.add(new DataTypeEntity("TIMESTAMP",false,false));
		list.add(new DataTypeEntity("TIMESTAMP WITH TIME ZONE",false,false));
		list.add(new DataTypeEntity("TIME WITH TIME ZONE",false,false));
		list.add(new DataTypeEntity("TINYINT",false,false));
		list.add(new DataTypeEntity("VARCHAR",true,false));
		list.add(new DataTypeEntity("XML",false,false));
		  
		return list;
	}
}
