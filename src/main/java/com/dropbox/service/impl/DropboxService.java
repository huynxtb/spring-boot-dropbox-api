package com.dropbox.service.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.dto.DropboxFileDTO;
import com.dropbox.dto.DropboxFolderDTO;
import com.dropbox.service.IDropboxService;
import com.dropbox.utils.ConvertByteToMB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DropboxService implements IDropboxService {

    @Autowired
    DbxClientV2 dropboxClient;

    // upload file
    public void uploadFile(MultipartFile file, String path) throws IOException, DbxException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());
        Metadata uploadMetaData = dropboxClient.files().uploadBuilder(path).uploadAndFinish(inputStream);
        log.info("File has been uploaded: " + path + "\n" + uploadMetaData.toString());
        inputStream.close();
    }

    // Get file by root folder
    public List<DropboxFileDTO> getFileList(String path) throws DbxException {

        List<Metadata> entries = dropboxClient.files().listFolder(path).getEntries();
        List<DropboxFileDTO> result = new ArrayList<>();
        DropboxFileDTO dto = null;
        for (Metadata entry : entries) {
            if (entry instanceof FileMetadata) {
                dto = new DropboxFileDTO();
                dto.setName(entry.getName());
                dto.setSize(ConvertByteToMB.getSize(((FileMetadata) entry).getSize()));
                dto.setPathLower(entry.getPathLower());
                dto.setPathDisplay(entry.getPathDisplay());
                result.add(dto);
            }
        }

        return result;
    }

    // Get all folder
    public List<DropboxFolderDTO> getFolderList() throws DbxException {

        List<Metadata> entries = dropboxClient.files().listFolder("").getEntries();

        List<DropboxFolderDTO> result = new ArrayList<>();
        DropboxFolderDTO dto = null;

        for (Metadata entry : entries) {
            if (entry instanceof FolderMetadata) {
                dto = new DropboxFolderDTO();
                dto.setName(entry.getName());
                dto.setPathLower(entry.getPathLower());
                result.add(dto);
            }
        }

        return result;
    }


    // Download file
    public void downloadFile(HttpServletResponse response, String path) throws IOException, DbxException {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(path, "UTF-8") + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");

        ServletOutputStream outputStream = response.getOutputStream();
        dropboxClient.files().downloadBuilder(path).download(outputStream);

        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    // Download root folder
    public void downloadZipFile(HttpServletResponse response, String path) throws IOException, DbxException {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(path + ".zip", "UTF-8") + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");

        ServletOutputStream outputStream = response.getOutputStream();
        dropboxClient.files().downloadZipBuilder(path).download(outputStream);

        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    // Delete file
    public void deleteFile(String path) throws DbxException {
        dropboxClient.files().delete(path);
    }

    // Create folder
    public void createFolder(String folderName) throws DbxException {
        dropboxClient.files().createFolderV2("/"+folderName);
    }

    // Delete folder
    public void deleteFolder(String folderPath) throws DbxException {
        dropboxClient.files().deleteV2(folderPath);
    }

    // Get all file
    public List<DropboxFileDTO> getAllFile() throws DbxException {
        List<Metadata> entries = null;

        List<Metadata> merge = dropboxClient.files().listFolder("").getEntries();

        List<DropboxFolderDTO> list = getFolderList();

        List<DropboxFileDTO> ls = new ArrayList<>();

        DropboxFileDTO dto = null;

        for (DropboxFolderDTO folder : list) {
            entries = new ArrayList<>();
            entries = dropboxClient.files().listFolder(folder.getPathLower()).getEntries();
            merge.addAll(entries);
        }


        for (Metadata entry : merge) {
            if (entry instanceof FileMetadata) {
                dto = new DropboxFileDTO();
                dto.setName(entry.getName());
                dto.setSize(ConvertByteToMB.getSize(((FileMetadata) entry).getSize()));
                dto.setPathLower(entry.getPathLower());
                dto.setFolder(entry.getPathDisplay().split("/")[1]);
                dto.setPathFolder("/"+entry.getPathDisplay().split("/")[1]);

                ls.add(dto);
            }
        }
        return ls;
    }

}
