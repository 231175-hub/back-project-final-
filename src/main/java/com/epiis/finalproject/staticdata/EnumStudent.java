package com.epiis.finalproject.staticdata;

public enum EnumStudent {
	ACTIVE("Activo"),
	INACTIVE("Inactivo"),
	WITHDRAWN("Retirado"),
	SUSPENDED("Suspendido"),
	ALUMNI("Egresado"),
	GRADUATE("Graduado"),
	EXPELLED("Expulsado");
	
	private String status;
	
	EnumStudent(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return this.status;
	}
}
