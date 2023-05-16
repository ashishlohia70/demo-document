package com.example.demo.service;

import java.util.Optional;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ResponseDto;

public interface DocumentService {
	ResponseDto upload(MultipartFile file);
	ResponseEntity<InputStreamResource> download(Long id);
	ResponseDto replace(Long id, MultipartFile file);
	ResponseDto delete(Long id);
	ResponseDto listFiles(Optional<Long> userId);
	ResponseDto getAllByUserId(String userId);
}
