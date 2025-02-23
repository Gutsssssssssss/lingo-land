package com.ssafy.a603.lingoland.global.listener;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ssafy.a603.lingoland.member.repository.MemberRepository;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;

public class AuditorAwareImpl implements AuditorAware<String> {

	@Autowired
	private MemberRepository memberRepository;

	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
			|| !(authentication.getPrincipal() instanceof CustomUserDetails)) {
			return Optional.empty();
		}
		return Optional.of(((CustomUserDetails)authentication.getPrincipal()).getUsername());
	}
}
