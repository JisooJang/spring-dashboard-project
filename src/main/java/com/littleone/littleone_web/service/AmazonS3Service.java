package com.littleone.littleone_web.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.amazonaws.util.IOUtils;

@PropertySource("classpath:awsS3.properties")
@Service
public class AmazonS3Service {
	
	@Value("${bucketName}")
	private String BUCKET_NAME;

	@Autowired
	private AmazonS3 amazonS3;

	public void putThumbnail(String bucket_name, String filename, InputStream inputStream, ObjectMetadata metadata) throws IOException {
		try {
			String filePath = (filename).replace(File.separatorChar, '/');
			PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, bucket_name + "/" + filePath, inputStream, metadata);
			request.setCannedAcl(CannedAccessControlList.PublicRead);	// 파일 권한 public으로 설정
			amazonS3.putObject(request);

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " +
					"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} finally {
			if(inputStream != null) {
				inputStream.close();
			}
		}
	}

	public void putThumbnailBI(String bucket_name, String filename, String extension, BufferedImage bufferedImage) throws IOException {
		ByteArrayOutputStream os = null;
		ByteArrayInputStream byteArrayInputStream = null;
		ObjectMetadata objectMetadata = null;
		try {
			String filePath = (filename).replace(File.separatorChar, '/');

			// outputstream에 image객체를 저장 
			os = new ByteArrayOutputStream();
			//ImageIO.write(bufferedImage, "png", os);
			ImageIO.write(bufferedImage, extension.toLowerCase(), os);

			//byte[]로 변환
			byte[] bytes = os.toByteArray();

			objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(bytes.length);
			//objectMetadata.setContentType("image/" + extension);
			objectMetadata.setContentType("image/" + extension.toLowerCase());
			//objectMetadata.setHeader(key, value);

			byteArrayInputStream = new ByteArrayInputStream(bytes);            

			PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, bucket_name + "/" + filePath, byteArrayInputStream, objectMetadata);
			request.setCannedAcl(CannedAccessControlList.PublicRead);	// 파일 권한 public으로 설정
			amazonS3.putObject(request);

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " +
					"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} finally {
			if(os != null) {
				os.flush();
				os.close();
			}
			if(byteArrayInputStream != null) {
				byteArrayInputStream.close();
			}
		}
	}

	public void deleteThumbnail(String bucket_name, String filename) {
		try {
			amazonS3.deleteObject(new DeleteObjectRequest(BUCKET_NAME, bucket_name + "/" + filename));	
		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
		} catch (AmazonClientException ace) {
			ace.printStackTrace();
		}
	}

	public void copyObject(String tmp_bucket_name, String sourceKey, String des_bucket_name, String destinationKey) {
		// 임시버킷 이름, 도착버킷이름, 임시버킷객체명, 도착버킷객체명
		try {
			//CopyObjectRequest copyObjRequest = new CopyObjectRequest(tmp_bucket_name, sourceKey, des_bucket_name, destinationKey);
			CopyObjectRequest copyObjRequest = new CopyObjectRequest(BUCKET_NAME, (tmp_bucket_name + "/" + sourceKey), BUCKET_NAME, (des_bucket_name + "/" + destinationKey));
			copyObjRequest.setCannedAccessControlList(CannedAccessControlList.PublicRead);	// 파일 권한 public으로 설정
			amazonS3.copyObject(copyObjRequest);
		} catch(AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process 
			// it, so it returned an error response.
			e.printStackTrace();
		} catch(SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client  
			// couldn't parse the response from Amazon S3.
			e.printStackTrace();
		}

	}

	public void createBucket(String bucket_name) {
		if (!amazonS3.doesBucketExistV2("dashboard/diary/" + bucket_name)) {
			System.out.println("service : 버킷 존재하지 않음. 버킷생성");
			amazonS3.createBucket(new CreateBucketRequest(bucket_name));
			String bucketLocation = amazonS3.getBucketLocation(new GetBucketLocationRequest(bucket_name));
			System.out.println("Bucket location: " + bucketLocation);
		} 
	}

	public boolean checkBucket(String bucket_name) {
		//String bucketLocation = amazonS3.getBucketLocation(new GetBucketLocationRequest(bucket_name));
		if (amazonS3.doesBucketExistV2(bucket_name)) {	// 존재하면
			return true;
		} else {
			// 존재하지 않으면
			return false;
		}
	}

	public ObjectMetadata getMetadata(MultipartFile thumbnail) throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		InputStream input = null;
		try {
			input = thumbnail.getInputStream();
			Long contentLength = Long.valueOf(IOUtils.toByteArray(input).length);
			metadata.setContentLength(contentLength);
			metadata.setContentType(thumbnail.getContentType());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if(input != null) {
				input.close();
			}	
		}
		return metadata;
	}

	public void setBucketLifeCycle(String bucket_name) {
		/*BucketLifecycleConfiguration.Rule rule = new BucketLifecycleConfiguration.Rule()
                .withId("Archive and then delete rule")
                .withFilter(new LifecycleFilter(new LifecycleTagPredicate(new Tag("archive", "true"))))
                //.addTransition(new Transition().withDays(30).withStorageClass(StorageClass.StandardInfrequentAccess))
                //.addTransition(new Transition().withDays(365).withStorageClass(StorageClass.Glacier))
                //.withExpirationInDays(3650)
                .withStatus(BucketLifecycleConfiguration.ENABLED);*/

		BucketLifecycleConfiguration configuration = amazonS3.getBucketLifecycleConfiguration(bucket_name);

		if(configuration != null && configuration.getRules() != null) {
			configuration.getRules().add(new BucketLifecycleConfiguration.Rule().withId("expiration_rule")
					.withExpirationInDays(1)
					.withStatus(BucketLifecycleConfiguration.ENABLED));
		} else {
			BucketLifecycleConfiguration.Rule rule = new BucketLifecycleConfiguration.Rule()
	                .withFilter(new LifecycleFilter(new LifecycleTagPredicate(new Tag("archive", "true"))))
	                .withExpirationInDays(1)
	                .withStatus(BucketLifecycleConfiguration.ENABLED);
			 BucketLifecycleConfiguration configuration_new = new BucketLifecycleConfiguration()
		                .withRules(rule);
			
			amazonS3.setBucketLifecycleConfiguration(bucket_name, configuration_new);
		}

		
	}

	public InputStream getInputStream(String bucket_name, String file_name) {
		GetObjectRequest request = new GetObjectRequest(BUCKET_NAME, bucket_name + "/" + file_name);
		S3Object object = amazonS3.getObject(request);
		InputStream inputStream = object.getObjectContent();
		return inputStream;

	}


}
