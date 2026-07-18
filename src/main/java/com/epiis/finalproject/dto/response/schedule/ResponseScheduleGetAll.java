package com.epiis.finalproject.dto.response.schedule;

public record ResponseScheduleGetAll(
	    String dayWeek,       
	    String startTime,    
	    String endTime,      
	    String classroom,    
	    String courseName,
	    String groupName
) {}
