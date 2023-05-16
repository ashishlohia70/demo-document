package com.example.demo.service.impl;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.example.demo.service.S3Service;

@Service
@Transactional
public class S3ServiceImpl implements S3Service {

	@Autowired
	private AmazonS3 s3client;

	@Override
	public void uploadFile(String bucketName, String fileName, long contentLength, InputStream inputStream) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(contentLength);
		try {
			s3client.putObject(bucketName, fileName, inputStream, objectMetadata);
		} catch (AmazonServiceException e) {
			throw new IllegalStateException("Failed to upload the file", e);
		}
	}

	@Override
	public S3Object downloadFile(String bucketName, String path) {
		return s3client.getObject(bucketName, path);
	}

	@Override
	public void deleteFile(String bucketName, String path) {
		s3client.deleteObject(bucketName, path);
	}

}
