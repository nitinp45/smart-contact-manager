package com.smart.service;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class Emailservice {
	
	public boolean sendEmail(String to,String subject,String msg)
	{
		boolean flag=false;
		
		String from="nitin451807@gmail.com";
		
		String host="smtp.gmail.com";
		
	//SMTP CONFIG
		Properties properties=new Properties();
		properties.put("mail.smtp.auth",true);
		properties.put("mail.smtp.starttls.enable",true);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.host", "smtp.gmail.com");

		
		String userName="nitin184507";
		String password="pxpk xzsf idtv fdlv";
		Session session=Session.getInstance(properties,new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(userName,password);
			}
		});
		
		try {
			MimeMessage message=new MimeMessage(session);
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setFrom(from);
			message.setSubject(subject);
			message.setText(msg);
			Transport.send(message);
			flag=true;
		}
	catch(Exception e)
		{
		e.printStackTrace();
		}
		return flag;
	}

}
