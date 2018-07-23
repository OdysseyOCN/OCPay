package com.stormfives.ocpay.common.email;

import java.io.Serializable;
import java.util.Properties;

/**
 * 发送邮件基础配置信息
 * @author niuchangqing
 *
 */
public class EmailConfig implements Serializable{
	
	private static final long serialVersionUID = 5226649744930778971L;
	/**
	 * 使用的邮箱协议
	 * 该值为空时，默认为：smtp
	 */
	private String mailTransportProtocol;
	/**
	 * 发件人邮箱的SMTP服务器地址
	 * 详情可以去邮箱的设置中查看
	 */
	private String mailSmtpHost;
	
	/**
	 * SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
	 * 需要改为对应邮箱的 SMTP 服务器的端口, 具体可查看对应邮箱服务的帮助,
	 * QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看)
	 */
	private String mailSmtpPort;
	
	/**
	 * 需要的请求验证
	 * 为空时 默认为true
	 */
	private boolean mailSmtpAuth;
	
	/**
	 * @param mailSmtpHost	发件人SMTP服务器地址
	 * @param mailSmtpPort	SMTP服务器的端口
	 */
	public EmailConfig(String mailSmtpHost,String mailSmtpPort){
		super();
		this.mailSmtpHost = mailSmtpHost;
		this.mailSmtpPort = mailSmtpPort;
		this.mailSmtpAuth = true;
		this.mailTransportProtocol = "smtp";
	}
	
	public EmailConfig(String mailTransportProtocol,String mailSmtpHost,String mailSmtpPort,boolean mailSmtpAuth){
		super();
		this.mailSmtpHost = mailSmtpHost;
		this.mailSmtpPort = mailSmtpPort;
		this.mailSmtpAuth = mailSmtpAuth;
		this.mailTransportProtocol = mailTransportProtocol;
	}

	public Properties getProperties(){
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", this.mailTransportProtocol);
        props.setProperty("mail.smtp.host", this.mailSmtpHost);
        props.setProperty("mail.smtp.auth", this.mailSmtpAuth ? "true":"false"); 
        props.setProperty("mail.smtp.port", this.mailSmtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", this.mailSmtpPort);
		return props;
	}
	
	@Override
	public String toString() {
		return "EmailConfig [mailTransportProtocol=" + mailTransportProtocol + ", mailSmtpHost=" + mailSmtpHost
				+ ", mailSmtpPort=" + mailSmtpPort + ", mailSmtpAuth=" + mailSmtpAuth + "]";
	}
}
