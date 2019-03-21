package com.littleone.littleone_web.domain;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType;

@DynamoDBTable(tableName="Bottle")
public class Bottle {
	private int infant_idx;
	private long date_time;
	private float temp;
	private float angle;
	private boolean feed_chk;
	
	public Bottle() {
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

	@DynamoDBAttribute(attributeName="angle")
	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	@DynamoDBTyped(DynamoDBAttributeType.BOOL)
	@DynamoDBAttribute(attributeName="feed_chk")
	public boolean isFeed_chk() {
		return feed_chk;
	}

	public void setFeed_chk(boolean feed_chk) {
		this.feed_chk = feed_chk;
	}
	
}
