package com.example.demo.service.impl;

import static org.apache.commons.lang3.StringUtils.join;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.demo.dto.AwsProperties;
import com.example.demo.dto.ResponseDto;
import com.example.demo.entity.Document;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.service.DocumentService;
import com.example.demo.service.S3Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class DocumentServiceImpl implements DocumentService {

	private static final String ATTACHMENT = "attachment";
	private static final String FULL_STOP = ".";
	@Autowired
	private S3Service s3Service;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private AwsProperties awsProperties;

	@Override
	public ResponseDto upload(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		String extension = StringUtils.getFilenameExtension(fileName);
		String uuid = UUID.randomUUID().toString();
		try (InputStream is = file.getInputStream()) {
			String filePath = join(awsProperties.getPath(), uuid, FULL_STOP, extension);
			s3Service.uploadFile(awsProperties.getBucketName(), filePath, file.getSize(), is);
			saveDocumentMetaData(fileName, filePath);
		} catch (IOException e) {
			log.error("Error while uploading file.", e);
			throw new RuntimeException();
		}
		return ResponseDto.builder().data("File was Uploaded SuccessFully").build();
	}

	private void saveDocumentMetaData(String fileName, String filePath) {
		Document document = new Document();
		document.setFileName(fileName);
		document.setFilePath(filePath);
		documentRepository.save(document);
	}
	
	@Override
	public ResponseDto replace(Long id, MultipartFile file) {
		Document document = documentRepository.findById(id).orElseThrow(RuntimeException::new);
		try (InputStream is = file.getInputStream()) {
			s3Service.uploadFile(awsProperties.getBucketName(), document.getFilePath(), file.getSize(), is);
			document.setFileName(file.getOriginalFilename());
			documentRepository.save(document);
		} catch (IOException e) {
			log.error("Error while uploading file.", e);
			throw new RuntimeException();
		}
		return ResponseDto.builder().data("File was Replaced SuccessFully").build();
	}

	@Override
	public ResponseEntity<InputStreamResource> download(Long id) {
		try {
			Document document = documentRepository.findById(id).orElseThrow(RuntimeException::new);
			S3Object s3Object = s3Service.downloadFile(awsProperties.getBucketName(), document.getFilePath());
			S3ObjectInputStream objectContent = s3Object.getObjectContent();
			InputStreamResource inputStreamResource = new InputStreamResource(objectContent);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentLength(s3Object.getObjectMetadata().getContentLength());
			headers.setContentType(MediaType.valueOf(s3Object.getObjectMetadata().getContentType()));
			headers.setContentDispositionFormData(ATTACHMENT, document.getFileName());
			return new ResponseEntity<InputStreamResource>(inputStreamResource, headers, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error while downloading file.", e);
			throw new RuntimeException();
		}
	}

	@Override
	public ResponseDto listFiles(Optional<Long> userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseDto delete(Long id) {
		Document document = documentRepository.findById(id).orElseThrow(RuntimeException::new);
		s3Service.deleteFile(awsProperties.getBucketName(), document.getFilePath());
		document.setFileDeleted(Boolean.TRUE);
		documentRepository.save(document);
		return ResponseDto.builder().data("File was Deleted SuccessFully").build();
	}

	@Override
	public ResponseDto getAllByUserId(String userId) {
		List<String> files = documentRepository.findByCreatedBy(userId).stream().map(Document::getFileName).collect(Collectors.toList());
		return ResponseDto.builder().data(files).build();
	}

}
