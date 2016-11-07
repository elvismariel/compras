package br.com.mariel.compras.enumeration;

import java.util.Arrays;

public enum BuyStatus {
	
	Buy(0,"Enable"),
	Bought(1,"Ok");
	
	private int code;
	private String header;
	
	private BuyStatus(int code, String header) {
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
	
	public static BuyStatus from(int code) {
        for(BuyStatus currentValue : BuyStatus.values()){
            if (currentValue.code == code)
                return currentValue;
        }
        throw new RuntimeException("Invalid Gender [" + code +"], valids are " + Arrays.toString(BuyStatus.values()));
    }
	
	public static BuyStatus from(String header) {
        for(BuyStatus currentValue : BuyStatus.values()){
            if ((currentValue.name()).equals(header))
                return currentValue;
        }
        throw new RuntimeException("Invalid Gender [" + header +"], valids are " + Arrays.toString(BuyStatus.values()));
    }

	public static boolean isValid(String value) {
		for(BuyStatus currentValue : BuyStatus.values()){
            if ((currentValue.header).equals(value))
                return true;
        }
		return false;
	}
}
