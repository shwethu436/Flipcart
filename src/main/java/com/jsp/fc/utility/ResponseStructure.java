package com.jsp.fc.utility;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
public class ResponseStructure <T>{
	private int statusCode;
	private String message;
	private T data;
	
	public int getStatusCode() {
		return statusCode;
	}
	public ResponseStructure<T> setStatusCode(int statusCode){
		this.statusCode= statusCode;
		return this;
	}
	
	public String getMessage() {
		return message;
	}
	public ResponseStructure<T> setMessage(String message){
		this.message=message;
		return this;
	}
	
	public T getdata() {
		return data;
	}
	public ResponseStructure<T> setData(T data){
		this.data=data;
		return this;
	}

}
