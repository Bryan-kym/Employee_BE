package com.bryan.staff.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
@Entity
public class User implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Long id;
	private String userid;
	private String firstName;
	private String lastName;
	private String username;
	private Long nid;
	private Long mobile;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	private String email;
	private String department;
	private String position;
	private String profileImageUrl;
	private Date lastLoginDate;
	private Date lastLoginDateDisplay;
	private Date joinDate;
	private String roles;
	private String[] authorities;
	private boolean isActive;
	private boolean isNotLocked;
	
	public User() {}
	
	public User(Long id, String userid, String firstName, String lastName, String username, Long nid, Long mobile,
			String password, String email, String department, String position, String profileImageUrl,
			Date lastLoginDate, Date lastLoginDateDisplay, Date joinDate, String roles, String[] authorities,
			boolean isActive, boolean isNotLocked) {
		this.id = id;
		this.userid = userid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.nid = nid;
		this.mobile = mobile;
		this.password = password;
		this.email = email;
		this.department = department;
		this.position = position;
		this.profileImageUrl = profileImageUrl;
		this.lastLoginDate = lastLoginDate;
		this.lastLoginDateDisplay = lastLoginDateDisplay;
		this.joinDate = joinDate;
		this.roles = roles;
		this.authorities = authorities;
		this.isActive = isActive;
		this.isNotLocked = isNotLocked;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getNid() {
		return nid;
	}
	public void setNid(Long nid) {
		this.nid = nid;
	}
	public Long getMobile() {
		return mobile;
	}
	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public Date getLastLoginDateDisplay() {
		return lastLoginDateDisplay;
	}
	public void setLastLoginDateDisplay(Date lastLoginDateDisplay) {
		this.lastLoginDateDisplay = lastLoginDateDisplay;
	}
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String[] getAuthorities() {
		return authorities;
	}
	public void setAuthorities(String[] authorities) {
		this.authorities = authorities;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isNotLocked() {
		return isNotLocked;
	}
	public void setNotLocked(boolean isNotLocked) {
		this.isNotLocked = isNotLocked;
	}
	
	

}
