package com.littleone.littleone_web.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.s3.AmazonS3;
import com.littleone.littleone_web.domain.Bottle;
import com.littleone.littleone_web.domain.Peepee;
import com.littleone.littleone_web.domain.Temp;
import com.amazonaws.services.dynamodbv2.model.Select;

@Service
public class DynamoDBService {

	@Autowired
	private AmazonDynamoDB amazonDynamoDB;

	private DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);

	private DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

	public void saveTemp(Temp temp) {
		mapper.save(temp);
	}
	
	public void saveBottle(Bottle bottle) {
		mapper.save(bottle);
	}
	
	public void savePeepee(Peepee peepee) {
		mapper.save(peepee);
	}

	// 테이블 정보 갖고오기
	public String tableInfo() {
		TableDescription table_info = 
				amazonDynamoDB.describeTable("Temp").getTable();
		System.out.println(table_info);
		return table_info.toString();
	}

	// idx + range_key 복합키고 temp 데이터 읽기
	public void readTemp(String idx, String range_key) {
		Table table = dynamoDB.getTable("Temp");
		/*Temp temp = mapper.load(Temp.class, idx, range_key);
		System.out.println("serialnum : " + temp.getSerial_num());
		System.out.println("time : " + temp.getTime());*/

		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("idx = :v_id")
				.withValueMap(new ValueMap()
						.withString(":v_id", idx));
		ItemCollection<QueryOutcome> items = table.query(spec);
		Iterator<Item> iterator = items.iterator();
		Item item = null;
		while (iterator.hasNext()) {
			item = iterator.next();
			System.out.println(item.toJSONPretty());
		}

	}

	// 기본키 + 날짜(yyyymmdd)로 temp 데이터 읽기
	public void readTempByDate(String infant_idx, String date) {
		//Table table = dynamoDB.getTable("Temp");
		String date_start = date + "T00:00:00Z";
		String date_end = date + "T23:59:59Z";
		System.out.println(date_start + "   ,   " + date_end);
		/*QuerySpec spec = new QuerySpec()
				//.withProjectionExpression(projectionExpression)
			    //.withKeyConditionExpression("infant_idx = :infant_idx")
			    .withHashKey("infant_idx", infant_idx)
			    .withFilterExpression("#time between :date_start and :date_end")
			    .withValueMap(new ValueMap()
			        //.withString(":infant_idx", infant_idx)
			        .withString(":date_start", date_start)
			        .withString(":date_end", date_end))
			    .withNameMap(new NameMap()
			    		.with("#time", "time"));	// time은 키워드기때문에 별도의 이름 속성 적용 필요
		 */			        		    
		/*ItemCollection<QueryOutcome> items = table.query(spec);
		Iterator<Item> iterator = items.iterator();
		Item item = null;
		while (iterator.hasNext()) {
		    item = iterator.next();
		    System.out.println(item.toJSONPretty());
		}*/

		Map<String, AttributeValue> expressionAttributeValues = 
				new HashMap<String, AttributeValue>();
		expressionAttributeValues.put(":infant_idx", new AttributeValue().withS(infant_idx));
		//expressionAttributeValues.put(":date_start", new AttributeValue().withN(date_start));
		//expressionAttributeValues.put(":date_end", new AttributeValue().withN(date_end));
		expressionAttributeValues.put(":date", new AttributeValue().withS(date));

		ScanRequest scanRequest = new ScanRequest()
				.withTableName("Temp")
				.withFilterExpression("infant_idx = :infant_idx and date_created = :date")
				.withExpressionAttributeValues(expressionAttributeValues);

		ScanResult result = amazonDynamoDB.scan(scanRequest);

		for (Map<String, AttributeValue> item : result.getItems()) {
			System.out.println(item);
		}

	}
	
	// 기본키 + 날짜(yyyymmdd)로 peepee 데이터 읽기 (배변 횟수 구함)
	public int readPeepeeCountByDate(String infant_idx, String date) {	//yyyyMMdd		
		Map<String, AttributeValue> expressionAttributeValues = 
				new HashMap<String, AttributeValue>();
		expressionAttributeValues.put(":infant_idx", new AttributeValue().withN(infant_idx));
		expressionAttributeValues.put(":date_start", new AttributeValue().withN(date + "000000"));
		expressionAttributeValues.put(":date_end", new AttributeValue().withN(date + "235959"));
		expressionAttributeValues.put(":defec_chk",new AttributeValue().withBOOL(true));	// 배변활동이 일어난 item만 count 결과에 포함시킨다.

		
		ScanRequest request = new ScanRequest()
		.withSelect(Select.COUNT)
		.withTableName("Peepee")
		.withFilterExpression("infant_idx = :infant_idx AND date_time BETWEEN :date_start AND :date_end AND defec_chk = :defec_chk")
		.withExpressionAttributeValues(expressionAttributeValues);
		
		int count = 0;
		ScanResult result = null;
        do {
            result = amazonDynamoDB.scan(request);
            count += result.getCount();
            request.setExclusiveStartKey(result.getLastEvaluatedKey());
        } while (result.getLastEvaluatedKey() != null);

		return count;

	}
	
	
	// 기본키 + 날짜(yyyymmdd)로 bottle 데이터 읽기 (수유 횟수 구함)
	public int readBottleCountByDate(String infant_idx, String date) {	//yyyyMMddHHmmss
		Map<String, AttributeValue> expressionAttributeValues = 
				new HashMap<String, AttributeValue>();
		expressionAttributeValues.put(":infant_idx", new AttributeValue().withN(infant_idx));
		expressionAttributeValues.put(":date_start", new AttributeValue().withN(date + "000000"));
		expressionAttributeValues.put(":date_end", new AttributeValue().withN(date + "235959"));
		expressionAttributeValues.put(":feed_chk",new AttributeValue().withBOOL(true));	// 배변활동이 일어난 item만 count 결과에 포함시킨다.
		
		ScanRequest request = new ScanRequest()
		.withSelect(Select.COUNT)
		.withTableName("Bottle")
		.withFilterExpression("infant_idx = :infant_idx AND date_time BETWEEN :date_start AND :date_end AND feed_chk = :feed_chk")
		.withExpressionAttributeValues(expressionAttributeValues);

		int count = 0;
        ScanResult result = null;
        do {
            result = amazonDynamoDB.scan(request);
            count += result.getCount();
            request.setExclusiveStartKey(result.getLastEvaluatedKey());
        } while (result.getLastEvaluatedKey() != null);

		return count;

	}


	// 기본키로 temp 데이터 삭제
	public void deleteTemp(String idx) {
		mapper.delete(mapper.load(Temp.class, idx, DynamoDBMapperConfig.ConsistentReads.EVENTUAL));
	}

	
	// 기본키로 temp 다중 행 삭제
	public void batchDeleteTemp(String[] idx) {
		List<Temp> delete_item = new ArrayList<Temp>();
		for(int i=0 ; i<idx.length ; i++) {
			delete_item.add(mapper.load(Temp.class, idx[i], DynamoDBMapperConfig.ConsistentReads.EVENTUAL));
		}
		mapper.batchDelete(delete_item);
	}
	
	// temp 다중 행 insert
	public void batchSaveTemp(List<Temp> temps) {
		mapper.batchSave(temps);
	}
	
	// peepee 다중 행 insert
	public void batchSavePeepee(List<Peepee> peepees) {
		//mapper.batchSave(Arrays.asList(peepees));
		mapper.batchSave(peepees);
	}
	
	// bottle 다중 행 insert
	public void batchSaveBottle(List<Bottle> bottles) {
		mapper.batchSave(bottles);
	}
	
	public String readupdated(String idx) {
		return "";
	}
	
	// 특정 날짜(yyyymmdd)의 가장 최근 insert 시간 읽어오기
	public String getRecentTemp(int idx, String date) {
		Table table = dynamoDB.getTable("Temp");
		RangeKeyCondition rangeKeyCondition = new RangeKeyCondition("date_time");
		rangeKeyCondition.between(Long.parseLong(date + "000000"), Long.parseLong(date + "235959"));
		QuerySpec spec = new QuerySpec()
			    .withHashKey("infant_idx", idx)
			    .withRangeKeyCondition(rangeKeyCondition)
			    .withMaxResultSize(1)
			    .withScanIndexForward(false);	// descending order
		
		ItemCollection<QueryOutcome> items = table.query(spec);
		Iterator<Item> iterator = items.iterator();
		Item item = null;
		String temp = null;
		String lastTime = null;
		while (iterator.hasNext()) {
		    item = iterator.next();
		    temp = (String)item.get("temp").toString();
		    lastTime = (String)item.get("date_time").toString();
		}
		
 		return temp + "-" + lastTime;
	}
	
	// 특정 날짜(yyyymmdd)의 가장 최근 insert 시간 읽어오기
	public String getRecentPeepee(int infant_idx, String date) {
		Table table = dynamoDB.getTable("Peepee");
		RangeKeyCondition rangeKeyCondition = new RangeKeyCondition("date_time");
		rangeKeyCondition.between(Long.parseLong(date + "000000"), Long.parseLong(date + "235959"));
		//rangeKeyCondition.beginsWith(date); 
		QuerySpec spec = new QuerySpec()
			    .withHashKey("infant_idx", infant_idx)
			    .withRangeKeyCondition(rangeKeyCondition)
			    .withMaxResultSize(1)
			    .withScanIndexForward(false);	// descending order
		
		ItemCollection<QueryOutcome> items = table.query(spec);
		Iterator<Item> iterator = items.iterator();
		Item item = null;
		while (iterator.hasNext()) {
		    item = iterator.next();
		    /*System.out.println("idx: " + item.get("infant_idx"));
		    System.out.println("date_time: " + item.get("date_time"));
		    System.out.println(item.toJSONPretty());*/
		}
		
		if(item != null && item.get("date_time") != null) {
			return (String)item.get("date_time").toString();
		} else {
			return null;
		}
		
	}
	
	// 특정 날짜(yyyymmdd)의 가장 최근 insert 시간 읽어오기
	public String getRecentBottle(int infant_idx, String date) {
		Table table = dynamoDB.getTable("Bottle");
		RangeKeyCondition rangeKeyCondition = new RangeKeyCondition("date_time");
		rangeKeyCondition.between(Long.parseLong(date + "000000"), Long.parseLong(date + "235959"));
		//rangeKeyCondition.beginsWith(date);
		QuerySpec spec = new QuerySpec()
				//.withProjectionExpression(projectionExpression)
			    //.withKeyConditionExpression("infant_idx = :infant_idx")
			    .withHashKey("infant_idx", infant_idx)
			    .withRangeKeyCondition(rangeKeyCondition)
			    .withMaxResultSize(1)
			    .withScanIndexForward(false);	// descending order
		ItemCollection<QueryOutcome> items = table.query(spec);
		Iterator<Item> iterator = items.iterator();
		Item item = null;
		
		while (iterator.hasNext()) {
		    item = iterator.next();
		    /*System.out.println("idx : " + item.get("infant_idx"));
		    System.out.println("date_time : " + item.get("date_time"));
		    System.out.println(item.toJSONPretty());*/   
		}
		
		if(item != null && item.get("date_time") != null) {
			return (String)item.get("date_time").toString();
		} else {
			return null;
		}
	}
	
	
	// 현재날짜 yyyyMMdd (int)형식으로 리턴
	public int get_now_date() {
		SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
		String date2 = date.format(new Date());
		return Integer.parseInt(date2);
	}
	
	// 현재날짜 yyyyMMddHHmmss (Long)형식으로 리턴
	public Long get_now_time() {
		SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
		String date2 = date.format(new Date());
		return Long.parseLong(date2);
	}
}
