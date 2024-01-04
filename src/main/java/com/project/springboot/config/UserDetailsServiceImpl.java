package com.project.springboot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.project.springboot.dao.Userrepoasitery;
import com.project.springboot.entities.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private Userrepoasitery userrepoasitery;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userrepoasitery.getUserByUsername(username);
		if(user== null)
		{
			throw new UsernameNotFoundException("User not found");
		}
		CustomUserDetails customuserdetails = new CustomUserDetails(user);
		return customuserdetails;
	}

}
