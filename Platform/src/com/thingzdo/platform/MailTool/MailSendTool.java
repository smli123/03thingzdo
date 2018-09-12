package com.thingzdo.platform.MailTool;

public class MailSendTool {
	 private MailSenderInfo 		m_mailInfo 			= new MailSenderInfo();    
	 private SimpleMailSender 	m_mailSender 	= new SimpleMailSender();   
	 
	 public MailSendTool(String strSendToAddress)
	 {
		 m_mailInfo.setMailServerHost("smtp.126.com");    
	     m_mailInfo.setMailServerPort("25");    
	     m_mailInfo.setValidate(true);    
		 m_mailInfo.setUserName("Thingzdo@126.com");    
		 m_mailInfo.setPassword("Thingzdo11");//您的邮箱密码    
		 m_mailInfo.setFromAddress("Thingzdo@126.com");    
		 m_mailInfo.setToAddress(strSendToAddress);
		 
		 m_mailInfo.setContent("尊敬的用户：");    
	 }
	 public void SetSubject(String strSubject)
	 {
		 m_mailInfo.setSubject(strSubject);    
	 }
	 public void SetContent(String strContent)
	 {
		 m_mailInfo.setContent(strContent);
	 }
	 public boolean Send()
	 {
		 return m_mailSender.sendTextMail(m_mailInfo);
	 }
}
