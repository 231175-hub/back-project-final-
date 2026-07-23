package com.epiis.finalproject.business;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.epiis.finalproject.dto.request.school.RequestSchoolInsert;
import com.epiis.finalproject.dto.request.school.RequestSchoolUpdate;
import com.epiis.finalproject.dto.response.school.ResponseSchoolDeleteById;
import com.epiis.finalproject.dto.response.school.ResponseSchoolGetById;
import com.epiis.finalproject.dto.response.school.ResponseSchoolInsert;
import com.epiis.finalproject.dto.response.school.ResponseSchoolUpdate;
import com.epiis.finalproject.dto.response.school.ResponseSchoollGetAll;
import com.epiis.finalproject.entity.EntitySchool;
import com.epiis.finalproject.repository.RepositorySchool;

import jakarta.transaction.Transactional;

@Service
public class BusinessSchool{
	private final RepositorySchool repositorySchool;
	
	private String storageDir = "storage";
	
	public BusinessSchool(RepositorySchool repositorySchool) {
		this.repositorySchool = repositorySchool;
	}
	
	@Transactional
	public ResponseSchoolInsert insert(RequestSchoolInsert request) throws Exception {
		ResponseSchoolInsert response = new ResponseSchoolInsert();
		
		EntitySchool entitySchool = new EntitySchool();
		
		entitySchool.setIdSchool(UUID.randomUUID().toString());
		entitySchool.setNameSchool(request.getNameSchool());
		
		Path storagePath = Paths.get(storageDir, "imageSchool", request.getNameSchool());
		
		if (!Files.exists(storagePath)) {
			Files.createDirectories(storagePath);
		}
		
		MultipartFile file = request.getFile();
		Path filePath = null;
		
		if (file != null) {
			
			String originalFileName = file.getOriginalFilename();
			String fileName = UUID.randomUUID().toString()+ "_"+ originalFileName;
			filePath = storagePath.resolve(fileName);
			
			String relativeRoute = filePath.toString().replace('\\', '/');
			
			entitySchool.setUrlImageSchool(relativeRoute);
		}
		
		entitySchool.setCreatedAt(new java.sql.Date(new Date().getTime()));
		entitySchool.setUpdatedAt(entitySchool.getCreatedAt());
		
		repositorySchool.save(entitySchool);
		
		if (file != null && filePath != null) {
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		}
		
		response.success();
		response.getListMessage().add("Escuela registrada correctamente");
		
		return response;
	}
	
	public Map<String, Object> getAll() {
		ResponseSchoollGetAll response = new ResponseSchoollGetAll();
		
		List<EntitySchool> list = repositorySchool.findAll();
		
		Map<String, Object> res = new HashMap<>();
				
		response.success();
		response.getListMessage().add("Escuelas entregadas correctamente");
		
		res.put("message", response);
		res.put("data", list);
		
		return res;
	}
	
	public Map<String, Object> getById(String idSchool){
		ResponseSchoolGetById response = new ResponseSchoolGetById();
		
		Optional<EntitySchool> entitySchool = repositorySchool.findById(idSchool);
		
		Map<String, Object> res = new HashMap<>();
		
		response.success();
		response.getListMessage().add("Escuela encontrada exitosamente");
		
		res.put("message", response);
		res.put("data", entitySchool);
		
		return res;
	}
	
	public ResponseSchoolDeleteById deleteById(String idSchool) throws Exception {
		ResponseSchoolDeleteById response = new ResponseSchoolDeleteById();
		
		Optional<EntitySchool> optional = repositorySchool.findById(idSchool);
		
		if (optional.isPresent()) {
			EntitySchool entitySchool = optional.get();
			
			Path storagePath = Paths.get(storageDir, "imageSchool", entitySchool.getNameSchool());
			
			if (Files.exists(storagePath)) {
				try (var paths = Files.walk(storagePath)) {
	                paths.sorted(Comparator.reverseOrder())
	                      .map(Path::toFile)
	                      .forEach(java.io.File::delete);
	            }
			}
		}
		
		repositorySchool.deleteById(idSchool);
		
		response.success();
		response.getListMessage().add("Escuela eliminada correctamente");
		
		return response;
	}
	
	public ResponseSchoolUpdate update(String idSchool, RequestSchoolUpdate request) {
		ResponseSchoolUpdate response = new ResponseSchoolUpdate();
		
		Optional<EntitySchool> optinal = repositorySchool.findById(idSchool);
		
		if(optinal.isPresent()) {
			EntitySchool entitySchool = optinal.get();
			
			entitySchool.setNameSchool(request.getNameSchool());
			
			MultipartFile file = request.getFile();
			if (file != null && !file.isEmpty()) {
				try {
					if (entitySchool.getUrlImageSchool() != null) {
						Path oldFilePath = Paths.get(entitySchool.getUrlImageSchool());
						Files.deleteIfExists(oldFilePath);
					}
					
					Path storagePath = Paths.get(storageDir, "imageSchool", idSchool);
					if (!Files.exists(storagePath)) {
						Files.createDirectories(storagePath);
					}
					
					String originalFileName = file.getOriginalFilename();
					String fileName = UUID.randomUUID().toString() + "-" + originalFileName;
					Path filePath = storagePath.resolve(fileName);
					
					Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
					
					entitySchool.setUrlImageSchool(filePath.toString().replace('\\', '/'));
				} catch (IOException e) {
					response.error();
					response.getListMessage().add("Error al guardar la imagen" + e.getMessage());
					return response;
				}
			}
			
			entitySchool.setUpdatedAt(new java.sql.Date(new Date().getTime()));

			repositorySchool.save(entitySchool);
			
			response.success();
			response.getListMessage().add("Escuela actualizada correctamente");
		
			return response;
		}
		
		response.error();
		response.getListMessage().add("Error la escuela no se actualizo");
		
		return response;
	}
}
