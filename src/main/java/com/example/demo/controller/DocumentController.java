package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ErrorDto;
import com.example.demo.dto.ResponseDto;
import com.example.demo.service.DocumentService;

@RestController()
@RequestMapping("/document")
public class DocumentController {

	@Autowired
	private DocumentService documentService;

	@PostMapping
	public ResponseEntity<ResponseDto> uploadFile(@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(documentService.upload(file));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ResponseDto> replaceFile(@RequestParam("file") MultipartFile file,
			@PathVariable("id") Long id) {
		return ResponseEntity.ok(documentService.replace(id, file));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseDto> deleteFile(@PathVariable("id") Long id) {
		return ResponseEntity.ok(documentService.delete(id));
	}

	@GetMapping("/{id}")
	public ResponseEntity<InputStreamResource> download(@PathVariable("id") Long id) {
		return documentService.download(id);
	}
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<ResponseDto> getAllByUserId(@PathVariable("userId") String userId) {
		return ResponseEntity.ok(documentService.getAllByUserId(userId));
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ResponseDto> errorHandler(RuntimeException exception){
		ErrorDto errorDto = new ErrorDto("0", exception.getLocalizedMessage());
		ResponseDto responseDto = ResponseDto.builder().success(false).errors(List.of(errorDto)).build();
		return ResponseEntity.internalServerError().body(responseDto);
	}
}
