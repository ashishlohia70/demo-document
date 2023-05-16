package com.example.demo.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Document {
	@Id
	@GeneratedValue
	private Long id;
	private String fileName;
	private String filePath;
	private String createdBy = "test@test.com";
	@CreationTimestamp
	private Instant createdAt;
	private String updatedBy = "test@test.com";
	@UpdateTimestamp
	private Instant updatedAt;
	private boolean fileDeleted;
}
