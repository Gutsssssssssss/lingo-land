package com.ssafy.a603.lingoland.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.a603.lingoland.group.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer>, GroupCustomRepository {
	boolean existsByName(String name);
}
