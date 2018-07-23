package com.stormfives.ocpay.common.email;

import java.io.Serializable;
import java.util.List;

/**
 * 发送邮件的基本信息
 * @author niuchangqing
 */
public class EmailInfo implements Serializable{
	private static final long serialVersionUID = -5335807099095782728L;
	/**
	 * 邮件发送者
	 */
	private EmailAddress sender;
	/**
	 * 收件人
	 * 可以是多个,但至少要有一个
	 */
	private List<EmailAddress> toPeople;
	
	/**
	 * 抄送人
	 * 可以是多个，可以为空
	 */
	private List<EmailAddress> ccPeople;
	
	/**
	 * 密送
	 * 可以是多个，可以为空
	 */
	private List<EmailAddress> bCcPeople;
	
	/**
	 * 登录服务的用户名
	 * 如果为空，默认为邮件发送者的地址
	 */
	private String userName;
	/**
	 * 登录服务的密码
	 * 该值不能为空
	 */
	private String userPwd;
	
	/**
	 * 邮件文本内容
	 */
	private String content;
	/**
	 * 邮件主题
	 */
	private String subject;
	
	/**
	 * 发送附件地址
	 */
	private String[] attachmentFilePath;

	public EmailAddress getSender() {
		return sender;
	}

	public void setSender(EmailAddress sender) {
		this.sender = sender;
	}

	public List<EmailAddress> getToPeople() {
		return toPeople;
	}

	public void setToPeople(List<EmailAddress> toPeople) {
		this.toPeople = toPeople;
	}

	public List<EmailAddress> getCcPeople() {
		return ccPeople;
	}

	public void setCcPeople(List<EmailAddress> ccPeople) {
		this.ccPeople = ccPeople;
	}

	public List<EmailAddress> getbCcPeople() {
		return bCcPeople;
	}

	public void setbCcPeople(List<EmailAddress> bCcPeople) {
		this.bCcPeople = bCcPeople;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String[] getAttachmentFilePath() {
		return attachmentFilePath;
	}

	public void setAttachmentFilePath(String[] attachmentFilePath) {
		this.attachmentFilePath = attachmentFilePath;
	}
}
