package com.stamhe.springboot.model;


/*
create database user_201908;
use user_201908;

CREATE TABLE `users_201908_009` (
  `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(70) NOT NULL,
  `email` varchar(70) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

 */
public class UserModel {
	private Long user_id;
    private String name;
    private String email;
    
    
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return "UserModel [user_id=" + user_id + ", name=" + name + ", email=" + email + "]";
	}
}
