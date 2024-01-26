package com.smart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class HomeController {	
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@GetMapping("/home")
	public String home()
	{
		return "home";
	}
	
	@GetMapping("/about")
	public String about()
	{
		return "about";
	}
	
	@GetMapping("/login")
	public String Login()
	{
		return "login";
	}
	

	@GetMapping("/register")
	public String signup(Model model)
	{
		model.addAttribute("user",new User());
		return "register";
	
	}
	
	@PostMapping("/do-register")
	public String registeruser(@Valid @ModelAttribute("user") User user,BindingResult bindingresult,@RequestParam(value="agreement" ,defaultValue="false") boolean agreement,Model model,HttpSession session)
	{
		try {
			if(!agreement)
			{
				System.out.print("You are not agree terms and condition");
				throw new Exception("You are not agree terms and condition");
			}
			
			if(bindingresult.hasErrors())
			{
				model.addAttribute("user",user);
				return "register";
				
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println("Aggrement="+agreement);
			System.out.println("user="+user);
			
			User result=this.userRepository.save(user);
			
			model.addAttribute("user",new User());
			session.setAttribute("message",new Message("Succesfully Registered!!","alert-success"));
			return "register";

		}
		catch(Exception e)
		{
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("something went wrong!!"+e.getMessage(),"alert-danger"));
			return "register";
		}
			}
}
