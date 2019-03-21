package com.littleone.littleone_web.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBNativeBoolean;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;

@DynamoDBTable(tableName="Peepee")
public class Peepee {
	private int infant_idx;
	private Long date_time;
	private float temp;
	private float humid;
	private boolean change_chk;
	private boolean defec_chk;
	
	public Peepee() {
		super();
	}
	
	@DynamoDBHashKey(attributeName="infant_idx")
	public int getInfant_idx() {
		return infant_idx;
	}
	public void setInfant_idx(int infant_idx) {
		this.infant_idx = infant_idx;
	}
	@DynamoDBRangeKey(attributeName="date_time")
	public Long getDate_time() {
		return date_time;
	}
	public void setDate_time(Long date_time) {
		this.date_time = date_time;
	}
	@DynamoDBAttribute(attributeName="temp")
	public float getTemp() {
		return temp;
	}
	public void setTemp(float temp) {
		this.temp = temp;
	}
	@DynamoDBAttribute(attributeName="humid")
	public float getHumid() {
		return humid;
	}
	public void setHumid(float humid) {
		this.humid = humid;
	}
	
	@DynamoDBTyped(DynamoDBAttributeType.BOOL)
	@DynamoDBAttribute(attributeName="change_chk")
	public boolean isChange_chk() {
		return change_chk;
	}
	public void setChange_chk(boolean change_chk) {
		this.change_chk = change_chk;
	}
	
	@DynamoDBTyped(DynamoDBAttributeType.BOOL)
	@DynamoDBAttribute(attributeName="defec_chk")
	public boolean isDefec_chk() {
		return defec_chk;
	}
	public void setDefec_chk(boolean defec_chk) {
		this.defec_chk = defec_chk;
	}
}
