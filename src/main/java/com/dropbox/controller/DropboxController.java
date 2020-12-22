package com.dropbox.controller;

import com.dropbox.core.DbxException;
import com.dropbox.dto.DropboxFileDTO;
import com.dropbox.dto.DropboxFolderDTO;
import com.dropbox.service.IDropboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController("controllerDropbox")
public class DropboxController {

	@Autowired
	IDropboxService dropboxService;

	@GetMapping("/")
	public ModelAndView getAllFile() throws DbxException {
		ModelAndView mav = new ModelAndView("index");
		List<DropboxFileDTO> ls = dropboxService.getAllFile();
		mav.addObject("DTO", ls);
		return mav;
	}

	// Upload file -> Ok
	@PostMapping("/upload/file")
	public ModelAndView uploadFile(@RequestParam("file") MultipartFile file,
											 @RequestParam("path") String path) throws Exception {
		StringBuilder sb = null;

		if (path.equals("")){
			sb = new StringBuilder();
			sb.append("/Root/");
			sb.append(file.getOriginalFilename());
			dropboxService.uploadFile(file, sb.toString());
		}else{
			sb = new StringBuilder();
			sb.append(path);
			sb.append("/");
			sb.append(file.getOriginalFilename());
			dropboxService.uploadFile(file, sb.toString());
		}

		return new ModelAndView("redirect:"+"/");
	}

	// Delete file -> OK
	@PostMapping("/delete/file")
	public ModelAndView deleteFile(@RequestParam("pathFileDel") String pathFileDel) throws Exception {
		dropboxService.deleteFile(pathFileDel);
		return new ModelAndView("redirect:" + "/");
	}

	// Download file -> Ok
	@GetMapping("/download/file")
	public void downloadFile(HttpServletResponse response,
							 @RequestParam("pathFileDown") String pathDown) throws Exception {
		dropboxService.downloadFile(response, pathDown);
	}

	// Download zip -> Ok
	@GetMapping("/download/zip")
	public void downloadZipFile(HttpServletResponse response,
							 @RequestParam("pathFolderDown") String pathFolderDown) throws Exception {
		dropboxService.downloadZipFile(response, pathFolderDown);
	}

	// Create folder -> Ok
	@PostMapping("/create/folder")
	public ModelAndView createFolder(@RequestParam("folderName") String folderName) throws DbxException {
		dropboxService.createFolder(folderName);
		return new ModelAndView("redirect:"+"/list/folder");
	}

	// Delete folder -> Ok
	@PostMapping("/delete/folder")
	public ModelAndView deleteFolder(@RequestParam(value = "pathFolderDel") String pathFolderDel) throws DbxException {
		dropboxService.deleteFolder(pathFolderDel);
		return new ModelAndView("redirect:"+"/list/folder");
	}

	// Get list folder -> Ok
	@GetMapping("/list/folder")
	public ModelAndView listFolder() throws DbxException, IOException {
		ModelAndView mav = new ModelAndView("list_folder");
		List<DropboxFolderDTO> ls = dropboxService.getFolderList();
		mav.addObject("FOLDERS", ls);
		return mav;
	}


	// Get file in path folder
	@GetMapping("/list/file-by-folder")
	public ModelAndView getFileList(@RequestParam(value = "target") String target) throws Exception {
		ModelAndView mav = new ModelAndView("list_by_folder");
		List<DropboxFileDTO> ls = dropboxService.getFileList(target);
		String [] folder = target.split("/");
		mav.addObject("FOLDER", folder[1]);
		mav.addObject("DTO", ls);
		return mav;
	}

	@GetMapping("/upload")
	public ModelAndView pageUpload() throws DbxException {
		ModelAndView mav = new ModelAndView("upload");
		List<DropboxFolderDTO> ls = dropboxService.getFolderList();
		mav.addObject("DTO_FOLDER", ls);
		return mav;
	}
}
