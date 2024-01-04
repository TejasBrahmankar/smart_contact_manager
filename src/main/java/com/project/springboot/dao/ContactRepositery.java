package com.project.springboot.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.springboot.entities.Contact;

public interface ContactRepositery extends JpaRepository<Contact, Integer> {

	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactsByUser(@RequestParam("userId")int userId,Pageable pageable);
}
