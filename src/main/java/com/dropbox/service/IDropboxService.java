package com.dropbox.service;

import com.dropbox.core.DbxException;
import com.dropbox.dto.DropboxFileDTO;
import com.dropbox.dto.DropboxFolderDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface IDropboxService {
    void uploadFile(MultipartFile file, String path) throws IOException, DbxException;
    List<DropboxFileDTO> getFileList(String path) throws DbxException;
    List<DropboxFolderDTO> getFolderList() throws DbxException;
    void downloadFile(HttpServletResponse response, String path) throws IOException, DbxException;
    void downloadZipFile(HttpServletResponse response, String path) throws IOException, DbxException;
    void deleteFile(String path) throws DbxException;
    void createFolder(String path) throws DbxException;
    void deleteFolder(String folderPath) throws DbxException;
    List<DropboxFileDTO> getAllFile() throws DbxException;
}
