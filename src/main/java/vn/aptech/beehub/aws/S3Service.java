package vn.aptech.beehub.aws;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
	private String bucketName = "images-beehub";
	Regions regions = Regions.US_EAST_1;
	public String uploadToS3(InputStream inputStream, String filename) throws IOException, AmazonServiceException, SdkClientException {
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(regions).build();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType("image.jpeg");
		metadata.setContentLength(inputStream.available());
		boolean fileExists = s3Client.doesObjectExist(bucketName, filename);
	    if (fileExists) {
	        filename = UUID.randomUUID().toString() + "_" + filename;
	    }
		PutObjectRequest request = new PutObjectRequest(bucketName, filename, inputStream,metadata);
		s3Client.putObject(request);
		return s3Client.getUrl(bucketName, filename).toString();
	}
	public void deleteToS3(String filename)throws AmazonServiceException, SdkClientException {
		AmazonS3 s3Clent = AmazonS3ClientBuilder.standard().withRegion(regions).build();
		DeleteObjectRequest request = new DeleteObjectRequest(bucketName, filename);
		s3Clent.deleteObject(request); 
	}
	public String editToS3(InputStream inputStream, String filename) throws IOException, AmazonServiceException, SdkClientException {
		return uploadToS3(inputStream, filename);
	}
}
