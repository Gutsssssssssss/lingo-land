package com.ssafy.a603.lingoland.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;
import com.ssafy.a603.lingoland.global.error.exception.InvalidInputException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ImgUtils {
	private final String STORE_PATH;
	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("webp", "jpg", "jpeg", "png", "gif");
	private final String DEFAULT_IMAGE = "default.jpg";

	public ImgUtils(@Value("${image.store-url}") String STORE_PATH) {
		this.STORE_PATH = STORE_PATH;
		log.info("ImgUtils initialized with STORE_PATH: {}", STORE_PATH);
	}

	public String saveImage(MultipartFile img, String path) {
		String fileName = img.getOriginalFilename();
		if (fileName == null) {
			log.error("Image filename is null");
			throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
		}

		String extension = getFileExtension(fileName);
		validateFileExtension(extension);

		String savedFilename = generateUniqueFilename(extension);
		String savedPath = STORE_PATH + '/' + path + '/' + savedFilename;
		String findPath = path + '/' + savedFilename;
		ensureDirectoryExists(path);

		File file = new File(savedPath);
		try {
			img.transferTo(file);
			log.info("Image saved successfully at {}", savedPath);
			return findPath;
		} catch (IOException e) {
			log.error("Failed to save image: {}", e.getMessage());
			throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	public void deleteImage(String imgPath) {
		if (imgPath.endsWith(DEFAULT_IMAGE)) {
			return;
		}
		Path filePath = Paths.get(STORE_PATH + '/' + imgPath);
		try {
			Files.delete(filePath);
			log.info("Image deleted successfully at {}", imgPath);
		} catch (IOException e) {
			log.error("Failed to delete image: {}", e.getMessage());
			throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	public String getDefaultImage() {
		log.info("Returning default image: {}", this.DEFAULT_IMAGE);
		return this.DEFAULT_IMAGE;
	}

	public String getImagePathWithDefaultImage(String path) {
		return path + '/' + DEFAULT_IMAGE;
	}

	public String saveBase64Image(String base64String, String path) {
		String[] parts = base64String.split(",");
		String imageString = parts.length > 1 ? parts[1] : parts[0];

		byte[] imageBytes = Base64.getDecoder().decode(imageString);

		String savedFilename = generateUniqueFilename("webp");
		String savedPath = STORE_PATH + '/' + path + '/' + savedFilename;
		log.info("save Path : {}", savedPath);
		String findPath = path + '/' + savedFilename;
		log.info("nginx path : {}", findPath);
		ensureDirectoryExists(path);

		File file = new File(savedPath);
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(imageBytes);
			log.info("Base64 image saved successfully at {}", savedPath);
			return findPath;
		} catch (IOException e) {
			log.error("Failed to save Base64 image: {}", e.getMessage());
			throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	private String getFileExtension(String fileName) {
		int extensionIdx = fileName.lastIndexOf(".");
		return fileName.substring(extensionIdx + 1);
	}

	private void validateFileExtension(String extension) {
		if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
			log.error("Invalid file extension: {}", extension);
			throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
		}
		log.info("Valid file extension: {}", extension);
	}

	private String generateUniqueFilename(String extension) {
		String uniqueFilename = UUID.randomUUID().toString() + '.' + extension;
		log.info("Generated unique filename: {}", uniqueFilename);
		return uniqueFilename;
	}

	private void ensureDirectoryExists(String path) {
		File dir = new File(STORE_PATH + '/' + path);
		if (!dir.exists()) {
			dir.mkdirs();
			log.info("Created directory: {}", dir.getAbsolutePath());
		}
	}
}