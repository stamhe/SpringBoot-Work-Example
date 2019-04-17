package com.stamhe.springboot.user.model;

import java.io.Serializable;

public class UserModel implements Serializable {
	private Long user_id;
	private String name;
	private String createTime;
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
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "UserModel [user_id=" + user_id + ", name=" + name + ", createTime=" + createTime + "]";
	}
}
