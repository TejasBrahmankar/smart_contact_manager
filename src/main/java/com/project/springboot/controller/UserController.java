package com.project.springboot.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.project.springboot.dao.ContactRepositery;
import com.project.springboot.dao.Userrepoasitery;
import com.project.springboot.entities.Contact;
import com.project.springboot.entities.User;
import com.project.springboot.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
@Autowired
	private Userrepoasitery userrepo;
@Autowired
private ContactRepositery contactrepo;
@ModelAttribute
  public void addCommon(Model m,Principal p)
  {
	  String username= p.getName();
		System.out.println("User is " + username);
		
		User user = userrepo.getUserByUsername(username);
		System.out.println(user);
		m.addAttribute("user",user);
  }

	@GetMapping("/index")
	public String dashboard(Model m,Principal p)
	{
		return "normal/user_dash";
	}
	
	@GetMapping("/add_contact")
	public String addContact(Model m)
	{
		m.addAttribute("contact",new Contact());
		return "normal/add_contact";
	}
	
	@PostMapping("/process_contact")
	public String processform(@ModelAttribute Contact contact,
			@RequestParam("pimage")MultipartFile file, Principal p,HttpSession session)
	{
		try {
		String name= p.getName();
		User user = userrepo.getUserByUsername(name);
		//file upload
		if(file.isEmpty())
		{
			System.out.println("filee is empty");
			contact.setImage("contact.png");
		}
		else
		{
			contact.setImage(file.getOriginalFilename());
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("uploaded");
		}
		contact.setUser(user);
		user.getContacts().add(contact);
		this.userrepo.save(user);
		System.out.println(contact);
		System.out.println("added");
		//success contact
		session.setAttribute("message",new Message("Successfully added !!","alert-success"));
		}
		catch(Exception ex)
		{
			ex.getMessage();
			session.setAttribute("message",new Message("Something went wrong !!","alert-danger"));
		}
		return "normal/add_contact";
	}
	// Show contacts
	@GetMapping("/show_contacts/{page}")
	public String showcontacts(@PathVariable("page")Integer page,Model m,Principal p)
	{
		String name= p.getName();
		User user = userrepo.getUserByUsername(name);
		Pageable pageable= PageRequest.of(page,2);
		Page<Contact> contacts=this.contactrepo.findContactsByUser(user.getId(),pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentpage",page);
		m.addAttribute("totalpages", contacts.getTotalPages());
		return "normal/view_contacts";
	}
	
	@GetMapping("/{cid}/contact")
	public String viewcontactdetail(@PathVariable("cid")Integer cid,Model m,Principal p)
	{
        Optional<Contact> contactdet=this.contactrepo.findById(cid);
       Contact contactdatail= contactdet.get();
       String name= p.getName();
		User user = userrepo.getUserByUsername(name);
		if(user.getId()==contactdatail.getUser().getId())
		{
			 m.addAttribute("contact", contactdatail);
		}
       
		return "normal/contact_detail";
	}
	
	@GetMapping("delete/{cid}")
	public String deletecontact(@PathVariable("cid")Integer cid,Model m,Principal p,HttpSession session)
	{
        Contact contactdet=this.contactrepo.findById(cid).get();
      // Contact contactdatail= contactdet.get();
//        contactdet.setUser(null);
       String name= p.getName();
		User user = userrepo.getUserByUsername(name);
		user.getContacts().remove(contactdet);
		this.userrepo.save(user);
			//contactdet.setUser(null);
			//this.contactrepo.delete(contactdet);
			
			
			session.setAttribute("message", new Message("Contact deleted !!", "success"));
		
       
		return "redirect:/user/show_contacts/0";
	}
	//update form preview
	@PostMapping("/update/{cid}")
	public String updateForm(@PathVariable("cid")Integer cid, Model m)
	{
		Contact cont = this.contactrepo.findById(cid).get();
		m.addAttribute("contact", cont);
		return "normal/updateForm";
	}
	
	//update form process
	@PostMapping("/process_updateContact")
	public String processUpdateform(@ModelAttribute Contact contact,@RequestParam("pimage")MultipartFile file, Principal p,HttpSession session)
	{
		System.out.println(contact.getCid());
		System.out.println(contact.getName());
		Contact oldcontdetails = this.contactrepo.findById(contact.getCid()).get();
		try
		{
			if(!file.isEmpty())
			{
				//delete image
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile,oldcontdetails.getImage());
				file1.delete();
				//update new image
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}
			else
			{
				contact.setImage(oldcontdetails.getImage());
			}
			User user = this.userrepo.getUserByUsername(p.getName());
			contact.setUser(user);
			this.contactrepo.save(contact);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return "redirect:/user/"+contact.getCid()+"/contact";
		
	}
}
