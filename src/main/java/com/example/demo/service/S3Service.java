package com.example.demo.service;

import java.io.InputStream;

import com.amazonaws.services.s3.model.S3Object;

public interface S3Service {
	void uploadFile(String bucketName, String fileName, long contentLength, InputStream inputStream);

	S3Object downloadFile(String bucketName, String path);
	
	void deleteFile(String bucketName, String path);
}
