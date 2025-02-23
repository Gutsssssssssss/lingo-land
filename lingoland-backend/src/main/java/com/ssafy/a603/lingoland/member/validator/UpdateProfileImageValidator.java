package com.ssafy.a603.lingoland.member.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UpdateProfileImageValidator implements Validator {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    @Override
    public boolean supports(Class<?> clazz) {
        return MultipartFile.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MultipartFile file = (MultipartFile) target;

        if (file.isEmpty()) {
            errors.rejectValue("file", "FileEmpty", "File cannot be empty");
        } else if (file.getSize() > MAX_IMAGE_SIZE) { // 5MB 제한
            errors.rejectValue("file", "FileTooLarge", "File size cannot exceed 5MB");
        }

        String contentType = file.getContentType();
        if (!isSupportedContentType(contentType)) {
            errors.rejectValue("file", "UnsupportedFileType", "Only image files are allowed");
        }
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif");
    }
}
