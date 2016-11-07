package br.com.mariel.compras.domain;

public class Phone {

	private String phone;

	private Phone(String phone) {
		super();
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public static Phone reload(String phone) {
		return new Phone(phone);
	}
}
