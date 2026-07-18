package com.epiis.finalproject.staticdata;

public enum EnumRoles {
	ADMIN("Admin"),
	PROFESSOR("Profesor"),
	STUDENT("Estudiante");
	
	private String role;
	
	EnumRoles(String role) {
		this.role = role;
	}
	
	@Override
	public String toString() {
		return role;
	}
}
