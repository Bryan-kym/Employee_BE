package com.bryan.staff.Service;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.sun.mail.smtp.SMTPTransport;

import static com.bryan.staff.Constant.EmailConstant.*;

@Service
public class EmailService {
	
	public void sendNewPasswordemail(String firstName, String password, String email) throws MessagingException {
		Message message = createEmail(firstName, password, email);
		SMTPTransport smtpTransport = (SMTPTransport) getEmailsession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
		smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
		smtpTransport.send(message, message.getAllRecipients());
		smtpTransport.close();
		
	}
	private Message createEmail(String firstName, String password, String email) throws AddressException, MessagingException {
		Message message = new MimeMessage(getEmailsession());
		message.setFrom(new InternetAddress(FROM_EMAIL));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email,false));
		message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAIL,false));
		message.setSubject(EMAIL_SUBJECT);
		message.setText("Hello "+ firstName + ", \n \n Your new account passsword is: " + password + "\n \n Human Resource Manager");
		message.setSentDate(new Date());
		message.saveChanges();
		return message;
		
	}
	
	
	private Session getEmailsession() {
		Properties properties = System.getProperties();
		properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
		properties.put(SMTP_AUTH, true);
		properties.put(SMTP_PORT, DEFAULT_PORT);
		properties.put(SMTP_STARTTLS_ENABLE, true);
		properties.put(SMTP_STARTTLS_REQUIRED, true);
		return Session.getInstance(properties,null);
	}

}
