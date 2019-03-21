package com.littleone.littleone_web.repository;

import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Family_group;

public interface GroupRepository extends Repository<Family_group, Integer> {
	
	public Family_group save(Family_group group);
	
	public void delete(int idx);
}
