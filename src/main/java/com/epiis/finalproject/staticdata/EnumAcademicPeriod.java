package com.epiis.finalproject.staticdata;


public enum EnumAcademicPeriod {
	PLANNED("Planificado"),
	ACTIVE("Activo"),
	COMPLETED("Finalizado"),
	CLOSED("Cerrado"),
	CANCELLED("Cancelado");
	
	private String status;
	
	EnumAcademicPeriod(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return status;
	}
}
