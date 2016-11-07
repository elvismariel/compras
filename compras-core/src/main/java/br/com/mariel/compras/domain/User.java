package br.com.mariel.compras.domain;

import java.util.List;

public class User {

	private String user_name;
	private String user_email;
	private String user_phone;
	private List<Phone> phones;

	private User(String user_name, String user_email, String user_phone) {
		super();
		this.user_name = user_name;
		this.user_email = user_email;
		this.user_phone = user_phone;
	}

	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getUser_phone() {
		return user_phone;
	}
	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}

	public List<Phone> getPhones() {
		return phones;
	}
	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	public static User reload(String user_phone) {
		return new User(null, null, user_phone);
	}
	
	public static User reload(String user_name, String user_email, String user_phone) {
		return new User(user_name, user_email, user_phone);
	}
}
