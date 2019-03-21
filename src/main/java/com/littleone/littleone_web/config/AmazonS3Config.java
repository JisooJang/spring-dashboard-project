package com.littleone.littleone_web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;

@PropertySource("classpath:awsS3.properties")
@Configuration
public class AmazonS3Config {
	
	@Value("${accesskey}")
	private String ACCESS_KEY;
	
	@Value("${secretKey}")
	private String SECRET_KEY;
	
	@Value("${endPoint}")
	private String END_POINT;
	
	@Bean
	public AWSCredentials AWSCredentials() {
		AWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
		return awsCredentials;
	}
	
	@Bean
	public AmazonS3 AmazonS3() {
		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(AWSCredentials()))
				.withRegion("ap-northeast-2")
				.build();
		return s3;
	}
	
	@Bean
	public AmazonSimpleEmailService AmazonSimpleEmailService() {
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(AWSCredentials()))
				.withRegion("us-west-2")
				.build();
		return client;
	}
	
	@Bean
	public AmazonDynamoDB AmazonDynamoDB() {
		AmazonDynamoDB dynamodb = AmazonDynamoDBClientBuilder.standard()
				.withRegion("ap-northeast-2")
				.withCredentials(new AWSStaticCredentialsProvider(AWSCredentials()))
				.build();
		return dynamodb;
	}
}
