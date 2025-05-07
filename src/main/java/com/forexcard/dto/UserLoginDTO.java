package com.forexcard.dto;


public class UserLoginDTO {
    private Integer id;
    private String role;
    private String email;
    private String password;

    
    public UserLoginDTO() {
		super();
	}

	public UserLoginDTO(Integer id, String role, String email, String password) {
        this.id = id;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    public Integer getId() { return id; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

	public void setId(Integer id) {
		this.id = id;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
    
}
