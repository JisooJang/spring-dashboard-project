package com.littleone.littleone_web.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.CreateTemplateRequest;
import com.amazonaws.services.simpleemail.model.CreateTemplateResult;
import com.amazonaws.services.simpleemail.model.DeleteTemplateRequest;
import com.amazonaws.services.simpleemail.model.DeleteTemplateResult;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailResult;
import com.amazonaws.services.simpleemail.model.Template;
import com.amazonaws.services.simpleemail.model.UpdateTemplateRequest;
import com.amazonaws.services.simpleemail.model.UpdateTemplateResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityResult;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.littleone.littleone_web.domain.Notify_email;
import com.littleone.littleone_web.repository.NotifyEmailRepository;

@Service
public class MailSenderService {

	@Autowired
	MemberService memberService;
	
	@Autowired
	AmazonSimpleEmailService amazonSESServiceClient;
	
	@Autowired
	NotifyEmailRepository repository;

	public void addEmailIdentitiy(String email) {
		VerifyEmailIdentityRequest request = new VerifyEmailIdentityRequest().withEmailAddress(email);
		VerifyEmailIdentityResult response = amazonSESServiceClient.verifyEmailIdentity(request);
	}

	public void sendWelcomeMailBySES(String destination, String sender, String template_name, String member_name) {
		SendTemplatedEmailRequest request = new SendTemplatedEmailRequest();
		Destination des = new Destination();
		Collection<String> d = new ArrayList<String>();
		d.add(destination);
		des.setToAddresses(d);

		request.setTemplate(template_name);
		request.setSource(sender);
		request.setDestination(des);
		request.setTemplateData("{ " + "\"member_name\":" + "\"" + member_name + "\"" + "}");

		SendTemplatedEmailResult result = amazonSESServiceClient.sendTemplatedEmail(request);
	}
	
	public void sendFindPWMailBySES(Collection<String> destination, String sender, String template_name, Map<String, String> template_data) {
		SendTemplatedEmailRequest request = new SendTemplatedEmailRequest();
		Destination des = new Destination();
		des.setToAddresses(destination);

		request.setTemplate(template_name);
		request.setSource(sender);
		request.setDestination(des);
		
		
		request.setTemplateData("{ " + "\"password_modify_url\":" + "\"" + template_data.get("password_modify_url") + "\"," +
				"\"member_name\":" + "\"" +  template_data.get("member_name") + "\"" + "}");

		SendTemplatedEmailResult result = amazonSESServiceClient.sendTemplatedEmail(request);
	}

	public void member_leave_auth_send(Collection<String> destination, String sender, String template_name, Map<String, String> template_data, String auth_url) {
		SendTemplatedEmailRequest request = new SendTemplatedEmailRequest();
		Destination des = new Destination();
		des.setToAddresses(destination);

		request.setTemplate(template_name);
		request.setSource(sender);
		request.setDestination(des);
		request.setTemplateData("{ " + "\"auth_url\":" + "\"" + auth_url + "\"" + "}");

		SendTemplatedEmailResult result = amazonSESServiceClient.sendTemplatedEmail(request);
	}
	
	// 공용 템플릿으로 사용 가능한 메소드
	public void sendMail(Collection<String> destination, String sender, String template_name, Map<String, String> template_data) {
		SendTemplatedEmailRequest request = new SendTemplatedEmailRequest();
		Destination des = new Destination();
		des.setToAddresses(destination);

		request.setTemplate(template_name);
		request.setSource(sender);
		request.setDestination(des);
		
		String jsonString = new Gson().toJson(template_data);
		
		request.setTemplateData(jsonString);

		SendTemplatedEmailResult result = amazonSESServiceClient.sendTemplatedEmail(request);
	}

	public void createTemplate(String subjectPart, String templateName, String textPart, String htmlPart) {
		Template template = new Template();
		template.setSubjectPart(subjectPart);
		template.setTemplateName(templateName);
		template.setTextPart(textPart);
		template.setHtmlPart(htmlPart);
		CreateTemplateRequest request = new CreateTemplateRequest().withTemplate(template);
		CreateTemplateResult result = amazonSESServiceClient.createTemplate(request);
	}

	public void updateTemplate(String template_name, String html_content, String subject) {
		UpdateTemplateRequest request = new UpdateTemplateRequest();
		Template template = new Template();
		template.setHtmlPart(html_content);
		template.setTemplateName(template_name);
		template.setSubjectPart(subject);
		request.setTemplate(template);

		UpdateTemplateResult result = amazonSESServiceClient.updateTemplate(request);
	}
	
	public void deleteTemplate(String templateName) {
		DeleteTemplateRequest request = new DeleteTemplateRequest();
		request.setTemplateName(templateName);
		DeleteTemplateResult result = amazonSESServiceClient.deleteTemplate(request);
	}

	public String get_html_content(String file_path_and_name) throws IOException {
		String content = Files.toString(new File(file_path_and_name), Charsets.UTF_8);
		return content;
	}

	public boolean checkBeforeFile(File file){
		if(file.exists()){
			if(file.isFile() && file.canRead()){
				return true;
			}
		}
		return false;
	}
	
	public Notify_email save_notify_email(String email) {
		Notify_email m = new Notify_email(email, memberService.set_now_time());
		return repository.save(m);
	}
	
	public Notify_email find_notify_email(String email) {
		return repository.findOne(email);
		//return null;
	}
	
	public void delete_notify_email(String email) {
		//repository.delete(email);
	}

}
