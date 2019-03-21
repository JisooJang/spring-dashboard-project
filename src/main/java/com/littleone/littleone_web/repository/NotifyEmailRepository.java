package com.littleone.littleone_web.repository;

import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Notify_email;

public interface NotifyEmailRepository extends Repository<Notify_email, String>{
	public Notify_email save(Notify_email email);	// save or update
	public Notify_email findOne(String email);     
	public void delete(String email);
}
                                              