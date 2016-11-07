package br.com.mariel.compras.enumeration;

import java.util.Arrays;

public enum CrudStatus {
	
	Create(0,"Inserir"),
	Update(1,"Alterar"),
	Delete(2, "Deletar");
	
	private int code;
	private String header;
	
	private CrudStatus(int code, String header) {
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
	
	public static CrudStatus from(int code) {
        for(CrudStatus currentValue : CrudStatus.values()){
            if (currentValue.code == code)
                return currentValue;
        }
        throw new RuntimeException("Invalid Gender [" + code +"], valids are " + Arrays.toString(CrudStatus.values()));
    }
	
	public static CrudStatus from(String header) {
        for(CrudStatus currentValue : CrudStatus.values()){
            if ((currentValue.header).equals(header))
                return currentValue;
        }
        throw new RuntimeException("Invalid Gender [" + header +"], valids are " + Arrays.toString(CrudStatus.values()));
    }

	public static boolean isValid(String value) {
		for(CrudStatus currentValue : CrudStatus.values()){
            if ((currentValue.header).equals(value))
                return true;
        }
		return false;
	}
}
