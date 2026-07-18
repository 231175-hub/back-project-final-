package com.epiis.finalproject.business;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.epiis.finalproject.dto.request.schoolfile.RequestSchoolFileInsert;
import com.epiis.finalproject.dto.response.school.ResponseSchoolFileInsert;
import com.epiis.finalproject.entity.EntitySchool;
import com.epiis.finalproject.entity.EntitySchoolfile;
import com.epiis.finalproject.repository.RepositorySchool;
import com.epiis.finalproject.repository.RepositorySchoolFile;

@Service
public class BusinessSchoolFile {
	private final RepositorySchoolFile repositorySchoolFile;
	
	private final RepositorySchool repositorySchool;
	
	public BusinessSchoolFile(RepositorySchoolFile repositorySchoolFile, RepositorySchool repositorySchool) {
		this.repositorySchoolFile = repositorySchoolFile;
		this.repositorySchool = repositorySchool;
	}
	
	private String storageDir = "storage";
	
	public ResponseSchoolFileInsert insert(RequestSchoolFileInsert request) throws Exception {
		ResponseSchoolFileInsert response = new ResponseSchoolFileInsert();
		
		Optional<EntitySchool> optional = repositorySchool.findById(request.getIdSchool());
		
		EntitySchool entitySchool = optional.get();
		
		Path storagePath = Paths.get(storageDir, "schoolFile", entitySchool.getNameSchool());
		
		if (!Files.exists(storagePath)) {
			Files.createDirectories(storagePath);
		}
		
		MultipartFile[] files = request.getFiles();
		List<EntitySchoolfile> listEntitySchoolFile = new ArrayList<>();
		
		if (files != null && files.length > 0) {
			for (MultipartFile file : files) {
				if (file.isEmpty()) {
					continue;
				}
				
				String originalFileName = file.getOriginalFilename();
				
				String extension = "";
				
				if (originalFileName != null && originalFileName.contains(".")) {
					extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
				}
				
				String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
				
				Path filePath = storagePath.resolve(fileName);
				
				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				
				EntitySchoolfile entitySchoolfile = new EntitySchoolfile();
				
				entitySchoolfile.setIdSchoolFile(UUID.randomUUID().toString());
				entitySchoolfile.setParentSchool(entitySchool);
				entitySchoolfile.setName(fileName);
				entitySchoolfile.setExtension(extension);
				entitySchoolfile.setCreatedAt(new java.sql.Date(new Date().getTime()));
				entitySchoolfile.setUpdatedAt(entitySchoolfile.getCreatedAt());
				
				listEntitySchoolFile.add(entitySchoolfile);
			}
		}
		
		if (!listEntitySchoolFile.isEmpty()) {
			repositorySchoolFile.saveAll(listEntitySchoolFile);
		}
		
		response.success();
		response.getListMessage().add("Regitro de archivos realizados Correctamente");
		
		return response;
	}

	public List<EntitySchoolfile> getBySchool(String idSchool) {
		Optional<EntitySchool> optional = repositorySchool.findById(idSchool);
		if (optional.isPresent()) {
			return repositorySchoolFile.findAll().stream()
				.filter(file -> file.getParentSchool() != null && file.getParentSchool().getIdSchool().equals(idSchool))
				.toList();
		}
		return new ArrayList<>();
	}

	public void deleteById(String idSchoolFile) {
		repositorySchoolFile.deleteById(idSchoolFile);
	}
}
