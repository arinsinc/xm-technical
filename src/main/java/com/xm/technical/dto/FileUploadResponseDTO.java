package com.xm.technical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileUploadResponseDTO {
    private String fileName;
    private int recordsProcessed;
    private String status;
    private String message;
}
