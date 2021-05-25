package com.ai.apac.smartenv.arrange.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CommuterAndMileageDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int code;

    private boolean success;

    private String msg;
	
    private List<CommuterAndMileage> data;

	@Data
	public static class CommuterAndMileage {
		private String deviceCode;
		private String workBeginTime;
		private String workOffTime;
		private String mileage;
		private List<Section> sections;
		
    }
	
	@Data
	public static class Section {
		private String firstStart;
		private String firstOff;
		private String secondStart;
		private String secondOff;
    }
}
