package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AwsProperties {
	private String region;
	private String bucketName;
	private String accessKey;
	private String secretKey;
	private String path;
}
