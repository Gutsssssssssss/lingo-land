package com.ssafy.a603.lingoland.fairyTale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.a603.lingoland.fairyTale.entity.FairyTale;

@Repository
public interface FairyTaleRepository extends JpaRepository<FairyTale, Integer>, FairyTaleCustomRepository {
}
