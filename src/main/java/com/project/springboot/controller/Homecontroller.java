package com.project.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.project.springboot.dao.Userrepoasitery;
import com.project.springboot.entities.User;
import com.project.springboot.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller

public class Homecontroller {
	@Autowired
	private Userrepoasitery userrepoasitery;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@GetMapping("/")
	//@ResponseBody
   public String home()
   {
//		User user = new User();
//		user.setName("tej");
//		user.setEmail("tej@gmail.com");
//		userrepoasitery.save(user);
//		Contact contact = new Contact();
//		user.getContacts().add(contact);
	   return "home";
   }
	@GetMapping("/about")
	public String about()
	{
		return "about";
	}
	@GetMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("user", new User());
		return "signup";
	}
	@PostMapping("/logged")
	public String registered(@Valid @ModelAttribute("user")User user,BindingResult br,@RequestParam(value="agreement",defaultValue = "false")boolean agreement,Model m,
			HttpSession session)
	{
		try {
		if(!agreement)
		{
			System.out.println("you have to agree");
			throw new Exception("you have to agree");
		}
		
		if(br.hasErrors())
		{
			System.out.println(br);
			m.addAttribute("user", user);
			return "signup";
		}
		
		System.out.println("agree"+ agreement);
		System.out.println("user"+ user);
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User res=this.userrepoasitery.save(user);
		m.addAttribute("user",new User());
		session.setAttribute("message",new Message("Successfully registered","alert-success"));
		return "signup";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			m.addAttribute("user", user);		
			session.setAttribute("message",new Message("Something went wrong !!  "+ex.getMessage(),"alert-danger"));
			return "signup";
		}
		
	}
	@GetMapping("/signin")
	public String customlogin()
	{
		return "login";
	}
}
