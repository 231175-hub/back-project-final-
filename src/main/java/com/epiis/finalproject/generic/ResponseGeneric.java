package com.epiis.finalproject.generic;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class ResponseGeneric {
	private String type;
	private List<String> listMessage;
	
	protected ResponseGeneric() {
		this.type = "error";
		this.listMessage = new ArrayList<>();
	}
	
	public String getType() {
		return this.type;
	}
	
	public void success() {
		this.type = "success";
	}
	
	public void warning() {
		this.type = "warning";
	}
	
	public void error() {
		this.type = "error";
	}
	
	public void exception() {
		this.type = "exception";
	}
}
