package com.logparse.core.model;

public class ParseObjectArianna extends ParseObject{
	
	private String plateNumberNat;
	private String obuid;
	private String plate;
	private String time;
	private String tbaid;
	private String guid;
	private String response;
	public String getPlateNumberNat() {
		return plateNumberNat;
	}
	public void setPlateNumberNat(String plateNumberNat) {
		this.plateNumberNat = plateNumberNat;
	}
	public String getObuid() {
		return obuid;
	}
	public void setObuid(String obuid) {
		this.obuid = obuid;
	}
	@Override
	public String toString() {
		return "ParseObject [plateNumberNat=" + plateNumberNat + ", obuid=" + obuid + ", plate=" + plate + ", time="
				+ time + ", tbaid=" + tbaid + ", guid=" + guid + ", response=" + response + "]";
	}
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTbaid() {
		return tbaid;
	}
	public void setTbaid(String tbaid) {
		this.tbaid = tbaid;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
}
