package com.StudentSystem.model;

import java.io.Serializable;

public class StudentSystemModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private int empid;          // Employee ID (primary key)
    private String name;        // Employee full name
    private String password;    // Hashed password
    private int age;            // Employee age
    private String contact;     // Contact number
    private int posid;          // Position ID (foreign key)
    private int deptid;         // Department ID (foreign key)
    private int conid;          // Contract ID (foreign key)
    private String conperiod;   // Contract period description
    private String imagePath;   // Profile image path (relative)
    private String department;  // Department name (for display)
    private String position;    // Position title (for display)

    public StudentSystemModel() {}

    public StudentSystemModel(int empid, String name, String password, int age, String contact, int posid,
                               int deptid, int conid, String conperiod) {
        this.empid = empid;
        this.name = name;
        this.password = password;
        this.age = age;
        this.contact = contact;
        this.posid = posid;
        this.deptid = deptid;
        this.conid = conid;
        this.conperiod = conperiod;
    }

    // Getters and setters for all fields below
    
	public int getEmpid() {
		return empid;
	}

	public void setEmpid(int empid) {
		this.empid = empid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public int getPosid() {
		return posid;
	}

	public void setPosid(int posid) {
		this.posid = posid;
	}

	public int getDeptid() {
		return deptid;
	}

	public void setDeptid(int deptid) {
		this.deptid = deptid;
	}

	public int getConid() {
		return conid;
	}

	public void setConid(int conid) {
		this.conid = conid;
	}

	public String getConperiod() {
		return conperiod;
	}

	public void setConperiod(String conperiod) {
		this.conperiod = conperiod;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
