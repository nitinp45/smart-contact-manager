package com.smart.controllers;


import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.Emailservice;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPassword {

	Random random=new Random(1000);
	
	@Autowired
	private Emailservice emailService;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/forgotpassword")
	public String forgotpassword()
	{
		return "forgotpassword";
	}
	
	@PostMapping("/send-otp")
	public String SendOTP(@RequestParam("email") String email,HttpSession session)
	{
		int otp=random.nextInt(99999);
		
		System.out.println("OTP="+otp);
		
		String subject="This is OTP";
		String message="<h1>otp="+otp+"</h1>";
				
		String to=email;
		
		boolean flag=this.emailService.sendEmail(to, subject, message);
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else {
			session.setAttribute("message", new Message("check your email id!","alert-success"));
			return "forgotpassword";
		}
	}
	
	
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp,HttpSession session)
	{
		int myotp=(int)session.getAttribute("myotp");   //otp stored in session
		String email=(String)session.getAttribute("email");  //email stored in session
		
		if(myotp==otp)    // If the OTP entered by the user matches the OTP stored in the session
		{
		User user =this.userRepository.getUserByUserName(email);
		
		if(user==null)    //check if user exist in database or not
		{
			session.setAttribute("message",new Message("User not exit!!","alert-danger"));
			return "forgotpassword";
		}
		else {
			return "change_password_form";
		}
		}
		else {
			session.setAttribute("message",new Message("You entered wrong OTP!!","alert-danger"));
			return "verify_otp";
		}
	}
	
	@PostMapping("/reset-password")
	public String changePass(@RequestParam("newPassword") String newPassword,HttpSession session)
	{
		
		String email=(String)session.getAttribute("email");
		
		User user=this.userRepository.getUserByUserName(email);
		
		user.setPassword(bCryptPasswordEncoder.encode(newPassword));
		
	    this.userRepository.save(user);
		return "redirect:/login";
		
	}
	
}
