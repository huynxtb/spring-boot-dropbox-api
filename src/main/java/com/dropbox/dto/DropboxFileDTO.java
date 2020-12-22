package com.dropbox.dto;

import lombok.Data;

@Data
public class DropboxFileDTO {
    private String name;
    private String size;
    private String pathLower;
    private String pathDisplay;
    private String pathFolder;
    private String folder;
}
