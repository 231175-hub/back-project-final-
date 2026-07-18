package com.epiis.finalproject.dto.response.school;

import com.epiis.finalproject.generic.ResponseGeneric;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseSchoollGetAll extends ResponseGeneric{
	private String urlmageSchool;
	
	@JsonProperty("urlImageSchool")
	public String getUrlFormatWeb() {
		if (this.urlmageSchool != null && !this.urlmageSchool.isEmpty()) {
			String correctPath = this.urlmageSchool.replace("\\", "/");
			
			return correctPath;
		}
		return null;
	}
}
