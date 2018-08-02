package com.stormfives.ocpay.common.email;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Properties;


/**
 * 发送邮件服务
 * @author niuchangqing
 */
public class SendEmailService {
	private static final Log log = LogFactory.getLog(SendEmailService.class); 
	/**
	 * 发送纯文本邮件
	 * @param info	发送邮件的基本信息
	 * @param config	发送邮件的服务配置
	 * @return
	 */
	public static boolean sendTextEmail(EmailInfo info,EmailConfig config){
		try {
			if(!validation(info,config)){
				//信息验证失败
				return false;
			}
			//1.创建一个邮件
			Properties props = config.getProperties();
			Session session = Session.getDefaultInstance(props); // 根据参数配置，创建会话对象（为了发送邮件准备的）
			MimeMessage message = getMimeMessage(session, info);
			//2.发送邮件
			Transport transport = session.getTransport();
			String userName = "";
			if(StringUtils.isEmpty(info.getUserName())){
				//等于空获取发件人地址
				Field senderAddress = info.getSender().getClass().getDeclaredField("address");
				senderAddress.setAccessible(true);
				userName = senderAddress.get(info.getSender()).toString();
			}else{
				userName = info.getUserName();
			}
	        transport.connect(userName, info.getUserPwd());
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 发送带附件的邮件
	 * @param info
	 * @param config
	 * @return
	 */
	public static boolean sendAttachmentEmail(EmailInfo info,EmailConfig config){
		try {
			if(!validation(info,config)){
				//信息验证失败
				return false;
			}
			//1.创建一个邮件
			Properties props = config.getProperties();
			Session session = Session.getDefaultInstance(props); // 根据参数配置，创建会话对象（为了发送邮件准备的）
			MimeMessage message = getMimeMessage(session, info);
			// 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
	        Multipart multipart = new MimeMultipart();
	        // 添加邮件正文
	        BodyPart contentPart = new MimeBodyPart();
	        contentPart.setContent("附件测试", "text/html;charset=UTF-8");
	        multipart.addBodyPart(contentPart);
	        
	        //添加附件
	        String[] attachment = info.getAttachmentFilePath();
	        for(int i = 0;i< attachment.length;i++){
	        	if(!StringUtils.isEmpty(attachment[i])){
	        		File file = new File(attachment[i]);
	                BodyPart attachmentBodyPart = new MimeBodyPart();
	                DataSource source = new FileDataSource(file);
	                attachmentBodyPart.setDataHandler(new DataHandler(source));
	                
	                //MimeUtility.encodeWord可以避免文件名乱码
	                attachmentBodyPart.setFileName(MimeUtility.encodeWord(file.getName()));
	                multipart.addBodyPart(attachmentBodyPart);
	        	}
	        }
	        message.setContent(multipart);
	        //保存前面的设置
	        message.saveChanges();
	        
	        //2.发送邮件
			Transport transport = session.getTransport();
			String userName = "";
			if(StringUtils.isEmpty(info.getUserName())){
				//等于空获取发件人地址
				Field senderAddress = info.getSender().getClass().getDeclaredField("address");
				senderAddress.setAccessible(true);
				userName = senderAddress.get(info.getSender()).toString();
			}else{
				userName = info.getUserName();
			}
	        transport.connect(userName, info.getUserPwd());
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
	        return true;
		} catch (Exception e) {
			log.info("发送带附件的邮件出现异常");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 获取发送邮件基础信息
	 * @param session
	 * @param info
	 * @return
	 * @throws Exception
	 */
	private static MimeMessage getMimeMessage(Session session,EmailInfo info) throws Exception{
		MimeMessage message = new MimeMessage(session);     // 创建邮件对象
		//1.设置发件人
		message.setFrom(info.getSender().getInternetAddress());
		//2.设置收件人
		List<EmailAddress> toPeople = info.getToPeople();
		for(int i =0;i< toPeople.size();i++){
			if(i == 0){
				message.setRecipient(MimeMessage.RecipientType.TO, toPeople.get(i).getInternetAddress());
				continue;
			}
			//多个收件人
			message.addRecipient(MimeMessage.RecipientType.TO, toPeople.get(i).getInternetAddress());
		}
		//3.如果有抄送人，添加抄送人信息
		List<EmailAddress> ccPeople = info.getCcPeople();
		if(ccPeople != null && ccPeople.size() > 0){
			for(int i =0 ;i< ccPeople.size() ;i++){
				if(i == 0){
					message.setRecipient(MimeMessage.RecipientType.CC,ccPeople.get(i).getInternetAddress());
					continue;
				}
				message.addRecipient(MimeMessage.RecipientType.CC, ccPeople.get(i).getInternetAddress());
			}
		}
		//4.如果有密送人，添加密送人信息
		List<EmailAddress> bCcPeople = info.getbCcPeople();
		if(bCcPeople != null && bCcPeople.size() > 0){
			for(int i = 0; i< bCcPeople.size();i++){
				if(i == 0){
					message.setRecipient(MimeMessage.RecipientType.BCC,bCcPeople.get(i).getInternetAddress());
					continue;
				}
				message.addRecipient(MimeMessage.RecipientType.BCC, bCcPeople.get(i).getInternetAddress());
			}
		}
		//5.设置邮件主题
		message.setSubject(info.getSubject(),"UTF-8");
		//6.邮件正文
		message.setContent(info.getContent(), "text/html;charset=UTF-8");
		//7. 设置显示的发件时间
        message.setSentDate(new Date());
        //8. 保存前面的设置
        message.saveChanges();
		return message;
	}
	
	/**
	 * 基本信息验证
	 * @param info
	 * @param config
	 * @return
	 */
	private static boolean validation(EmailInfo info,EmailConfig config){
		if(config == null){
			log.info("email config is empty");
			log.info("邮件配置信息为空,EmailConfig为空");
			return false;
		}
		
		if(info == null){
			log.info("email info is empty");
			log.info("邮件基本信息为空,EmailInfo为空");
			return false;
		}
		//验证发件人是否为空
		if(info.getSender() == null){
			log.info("email sender is empty");
			log.info("发件人信息为空");
			return false;
		}
		try {
			EmailAddress sender = info.getSender();
			if(!validationEmailAddress(sender)){
				return false;
			}
		} catch (Exception e) {
			log.info("验证发件人信息出现异常");
			e.printStackTrace();
			return false;
		}
		
		//验证收件人信息
		if(info.getToPeople() == null || info.getToPeople().size() <= 0){
			log.info("收件人信息为空");
			return false;
		}
		List<EmailAddress> toPeople = info.getToPeople();
		for (EmailAddress emailAddress : toPeople) {
			try {
				if(!validationEmailAddress(emailAddress)){
					return false;
				}
			} catch (Exception e) {
				log.info("验证收件人信息出现异常");
				e.printStackTrace();
				return false;
			}
		}
		
		//如果抄送人不为空，验证抄送人信息
		if(info.getCcPeople() != null && info.getCcPeople().size() > 0){
			List<EmailAddress> ccPeople = info.getCcPeople();
			for (EmailAddress emailAddress : ccPeople) {
				try {
					if(!validationEmailAddress(emailAddress)){
						return false;
					}
				} catch (Exception e) {
					log.info("验证抄送人信息出现异常");
					e.printStackTrace();
					return false;
				}
			}
		}
		
		//如果密送人不为空，验证密送人信息
		if(info.getbCcPeople() != null && info.getbCcPeople().size() > 0){
			List<EmailAddress> bCcPeople = info.getbCcPeople();
			for (EmailAddress emailAddress : bCcPeople) {
				try {
					if(!validationEmailAddress(emailAddress)){
						return false;
					}
				} catch (Exception e) {
					log.info("验证密送人信息出现异常");
					e.printStackTrace();
					return false;
				}
			}
		}
		//验证邮件登录服务器密码是否正确
		if(StringUtils.isEmpty(info.getUserPwd())){
			log.info("邮箱服务登录密码为空");
			return false;
		}
		//验证邮箱内容
		if(StringUtils.isEmpty(info.getContent())){
			log.info("邮件内容为空");
			return false;
		}
		//验证邮件主题是否为空
		if(StringUtils.isEmpty(info.getSubject())){
			log.info("邮件主题为空");
			return false;
		}
		return true;
	}
	
	/**
	 * 验证收发邮件人信息
	 * @param emailAddress
	 * @return
	 * @throws Exception
	 */
	private static boolean validationEmailAddress(EmailAddress emailAddress) throws Exception{
		Field address = emailAddress.getClass().getDeclaredField("address");
		address.setAccessible(true);
		if(address.get(emailAddress) == null){
			log.info("邮件发件人地址为空");
			return false;
		}
		Field personal = emailAddress.getClass().getDeclaredField("personal");
		personal.setAccessible(true);
		if(personal.get(emailAddress) == null){
			log.info("邮件发件人名称为空");
			return false;
		}
		return true;
	}
/*
	public static void main(String[] args){
		EmailInfo info = new EmailInfo();

		EmailAddress sender = new EmailAddress("derekwang2012@sina.com","Derek Wang");
		EmailAddress receiver = new EmailAddress("878310409@qq.com");
		List<EmailAddress> receivelist = new ArrayList<>();
		receivelist.add(receiver);
		info.setSender(sender);
		info.setToPeople(receivelist);
		info.setUserPwd("dongjie520");
		info.setContent("亲爱的用户 878310409@qq.com, 您好！<br/><br/>" +
				"您收到这封这封电子邮件是因为您 (也可能是某人冒充您的名义) 注册了stormfives.com 账户，请点击下面的链接激活该账户。" +
				"<br/>" +
				"http://localhost/api/exchange/verifyemail/wdtesttoken" +
				"<br/>" +
				"(如果您无法点击这个链接，请将此链接复制到浏览器地址栏后访问)" +
				"<br/>" +
				"为了保证您帐号的安全性，该链接有效期为三天，并且点击一次后将失效!" +
				"<br/>" +
				"<br/>" +
				"如果这不是您本人的操作，可以放心地忽略此电子邮件。" +
				"<br/>" +
				"<br/>" +
				"感谢您使用StormFives 国际区块链资产交易平台" +
				"访问地址 http://stormfives.com/");
		info.setSubject("绑定邮箱验证");

		EmailConfig config = new EmailConfig("smtp.sina.com","465");

		sendTextEmail(info,config);

	}*/
}
