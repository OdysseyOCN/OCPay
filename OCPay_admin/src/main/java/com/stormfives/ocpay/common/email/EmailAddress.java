package com.stormfives.ocpay.common.email;

import javax.mail.internet.InternetAddress;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * email收件人和发件人信息
 * @author niuchangqing
 */
public class EmailAddress implements Serializable{

	private static final long serialVersionUID = -2896557752432404684L;

	/**
	 * 邮箱地址
	 */
	private String address;
	
	/**
	 * 收发件名称
	 */
	private String personal;
	
	/**
	 * 字符编码
	 */
	private String charset = "UTF-8";

	public EmailAddress() {
	}

	/**
	 * 
	 * @param address	邮箱地址
	 * @param personal	收发人名称 如果为空，默认为邮箱地址
	 */
	public EmailAddress(String address,String personal){
		super();
		this.address = address;
		this.personal = personal == null?address:personal.length() <= 0?address:personal;
		this.charset = "UTF-8";
	}
	
	/**
	 * 
	 * @param address	邮箱地址
	 */
	public EmailAddress(String address){
		super();
		this.address = address;
		this.personal = address;
		this.charset = "UTF-8";
	}
	
	/**
	 * 得到收发件人信息
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public InternetAddress getInternetAddress() throws UnsupportedEncodingException {
		return new InternetAddress(this.address,this.personal,this.charset);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
