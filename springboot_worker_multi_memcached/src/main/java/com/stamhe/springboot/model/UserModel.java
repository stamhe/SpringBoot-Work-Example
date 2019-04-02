package com.stamhe.springboot.model;

import java.io.Serializable;

public class UserModel implements Serializable {
	private static final long serialVersionUID = 8655851615465363473L;
    private Long id;
    private String username;
    private String password;
    
    
	public UserModel() {
		super();
	}

	public UserModel(Long id, String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "UserModel [id=" + id + ", username=" + username + ", password=" + password + "]";
	}
}
