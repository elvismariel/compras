package br.com.mariel.compras.enumeration;

import java.util.Arrays;

public enum TypeStatus {
	
	Buy(0,"Compra"),
	Product(1,"Produto"),
	User(2,"Usuario"),
	Share(3, "Compartilhar"),
	Delete(4, "Excluir");
	
	private int code;
	private String header;
	
	private TypeStatus(int code, String header) {
		this.code = code;
		this.header = header;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getHeader() {
		return header;
	}
	
	@Override
	public String toString() {
		return header;
	}
	
	public static TypeStatus from(int code) {
        for(TypeStatus currentValue : TypeStatus.values()){
            if (currentValue.code == code)
                return currentValue;
        }
        throw new RuntimeException("Invalid Gender [" + code +"], valids are " + Arrays.toString(TypeStatus.values()));
    }
	
	public static TypeStatus from(String header) {
        for(TypeStatus currentValue : TypeStatus.values()){
            if ((currentValue.header).equals(header))
                return currentValue;
        }
        throw new RuntimeException("Invalid Gender [" + header +"], valids are " + Arrays.toString(TypeStatus.values()));
    }

	public static boolean isValid(String value) {
		for(TypeStatus currentValue : TypeStatus.values()){
            if ((currentValue.header).equals(value))
                return true;
        }
		return false;
	}
}
