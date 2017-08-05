package gov.osti.pojo;

public class AuthoritiesObject {

	
	private String name;
	private String code;
	private String status;
	
	public AuthoritiesObject() {
		
	}
	
	public AuthoritiesObject(String name, String code, String status) {
		this.name = name;
		this.code = code;
		this.status = status;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
