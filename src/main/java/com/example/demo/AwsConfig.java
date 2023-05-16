package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.demo.dto.AwsProperties;

@Configuration
public class AwsConfig {

	@Bean
	@ConfigurationProperties(prefix = "amazon-properties")
	public AwsProperties getAwsProperties() {
		return new AwsProperties();
	}

	@Bean
	public AmazonS3 s3client() {
		AwsProperties awsProperties = getAwsProperties();
		AWSCredentials credentials = new BasicAWSCredentials(awsProperties.getAccessKey(),
				awsProperties.getSecretKey());
		return AmazonS3ClientBuilder.standard().withRegion(awsProperties.getRegion())
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
	}
}
