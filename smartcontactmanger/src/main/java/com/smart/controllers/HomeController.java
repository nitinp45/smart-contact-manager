package com.smart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.smart.dao.UserRepository;
import com.smart.entities.User;

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
	public String registeruser(@Valid @ModelAttribute("user") User user,BindingResult bindingresult,Model model)
	{
		try {
			if(bindingresult.hasErrors())
			{
				model.addAttribute("user",user);
				return "register";

			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("user="+user);

			User result=this.userRepository.save(user);

			model.addAttribute("user",new User());
			return "redirect:/login";

		}
		catch(Exception e)
		{
			e.printStackTrace();
			model.addAttribute("user",user);
			return "register";
		}
	}
}
