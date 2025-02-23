package com.ssafy.a603.lingoland.fairyTale.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.a603.lingoland.fairyTale.dto.CreateFairyTaleRequestDTO;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTale;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTaleMember;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTaleMemberId;
import com.ssafy.a603.lingoland.fairyTale.repository.FairyTaleMemberRepository;
import com.ssafy.a603.lingoland.fairyTale.repository.FairyTaleRepository;
import com.ssafy.a603.lingoland.fairyTale.service.FairyTaleService;
import com.ssafy.a603.lingoland.fairyTale.service.FairyTaleServiceImpl;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.repository.MemberRepository;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;

@ContextConfiguration(classes = {FairyTaleController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class FairyTaleControllerDiffblueTest {
	@Autowired
	private FairyTaleController fairyTaleController;

	@MockBean
	private FairyTaleService fairyTaleService;

	/**
	 * Method under test:
	 * {@link FairyTaleController#createFairyTale(CreateFairyTaleRequestDTO)}
	 */
	@Test
	void testCreateFairyTale() throws Exception {
		// Arrange
		FairyTale.FairyTaleBuilder builderResult = FairyTale.builder();
		FairyTale buildResult = builderResult.content(new ArrayList<>())
			.cover("Cover")
			.summary("Summary")
			.title("Dr")
			.build();
		when(fairyTaleService.createFairyTale(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
			Mockito.<List<FairyTale.Story>>any(), Mockito.<List<String>>any())).thenReturn(buildResult);
		MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/api/v1/fairy-tales")
			.contentType(MediaType.APPLICATION_JSON);

		ObjectMapper objectMapper = new ObjectMapper();
		ArrayList<FairyTale.Story> content = new ArrayList<>();
		MockHttpServletRequestBuilder requestBuilder = contentTypeResult.content(objectMapper
			.writeValueAsString(new CreateFairyTaleRequestDTO("Dr", "Cover", "Summary", content, new ArrayList<>())));

		// Act
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(fairyTaleController)
			.build()
			.perform(requestBuilder);

		// Assert
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	/**
	 * Method under test:
	 * {@link FairyTaleController#findListByLoginId(CustomUserDetails)}
	 */
	@Test
	@Disabled("TODO: Complete this test")
	void testFindListByLoginId() {
		// TODO: Diffblue Cover was only able to create a partial test for this method:
		//   Reason: No inputs found that don't throw a trivial exception.
		//   Diffblue Cover tried to run the arrange/act section, but the method under
		//   test threw
		//   jakarta.servlet.ServletException: Request processing failed: java.lang.IllegalStateException: Cannot resolve parameter names for constructor public com.ssafy.a603.lingoland.member.security.CustomUserDetails(com.ssafy.a603.lingoland.member.entity.Member)
		//       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564)
		//       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
		//   java.lang.IllegalStateException: Cannot resolve parameter names for constructor public com.ssafy.a603.lingoland.member.security.CustomUserDetails(com.ssafy.a603.lingoland.member.entity.Member)
		//       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564)
		//       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
		//   See https://diff.blue/R013 to resolve this issue.

		// Arrange
		FairyTaleRepository fairyTaleRepository = mock(FairyTaleRepository.class);
		when(fairyTaleRepository.findAllFairyTalesByMemberId(anyInt())).thenReturn(new ArrayList<>());

		Member member = new Member();
		member.updateNickname("Nickname");
		member.updateRefreshToken("Refresh");
		Optional<Member> ofResult = Optional.of(member);
		MemberRepository memberRepository = mock(MemberRepository.class);
		when(memberRepository.findByLoginId(Mockito.<String>any())).thenReturn(ofResult);
		FairyTaleController fairyTaleController = new FairyTaleController(
			new FairyTaleServiceImpl(fairyTaleRepository, memberRepository, mock(FairyTaleMemberRepository.class)));

		// Act
		fairyTaleController.findListByLoginId(new CustomUserDetails(new Member()));
	}

	/**
	 * Method under test: {@link FairyTaleController#findFairyTaleById(Integer)}
	 */
	@Test
	void testFindFairyTaleById() {
		//   Diffblue Cover was unable to create a Spring-specific test for this Spring method.

		// Arrange
		FairyTaleRepository fairyTaleRepository = mock(FairyTaleRepository.class);
		FairyTale.FairyTaleBuilder builderResult = FairyTale.builder();
		ArrayList<FairyTale.Story> content = new ArrayList<>();
		FairyTale buildResult = builderResult.content(content).cover("Cover").summary("Summary").title("Dr").build();
		Optional<FairyTale> ofResult = Optional.of(buildResult);
		when(fairyTaleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

		// Act
		ResponseEntity<?> actualFindFairyTaleByIdResult = (new FairyTaleController(new FairyTaleServiceImpl(
			fairyTaleRepository, mock(MemberRepository.class), mock(FairyTaleMemberRepository.class))))
			.findFairyTaleById(1);

		// Assert
		verify(fairyTaleRepository).findById(eq(1));
		Object body = actualFindFairyTaleByIdResult.getBody();
		assertTrue(body instanceof FairyTale);
		HttpStatusCode statusCode = actualFindFairyTaleByIdResult.getStatusCode();
		assertTrue(statusCode instanceof HttpStatus);
		assertEquals("Cover", ((FairyTale)body).getCover());
		assertEquals("Dr", ((FairyTale)body).getTitle());
		assertEquals("Summary", ((FairyTale)body).getSummary());
		assertNull(((FairyTale)body).getId());
		assertEquals(200, actualFindFairyTaleByIdResult.getStatusCodeValue());
		assertEquals(HttpStatus.OK, statusCode);
		List<FairyTale.Story> content2 = ((FairyTale)body).getContent();
		assertTrue(content2.isEmpty());
		assertTrue(((FairyTale)body).getFairyTaleMembers().isEmpty());
		assertTrue(actualFindFairyTaleByIdResult.hasBody());
		assertTrue(actualFindFairyTaleByIdResult.getHeaders().isEmpty());
		assertSame(content, content2);
	}

	/**
	 * Method under test: {@link FairyTaleController#findFairyTaleById(Integer)}
	 */
	@Test
	@Disabled("TODO: Complete this test")
	void testFindFairyTaleById2() throws Exception {
		// TODO: Diffblue Cover was only able to create a partial test for this method:
		//   Diffblue AI was unable to find a test

		// Arrange
		FairyTale.FairyTaleBuilder builderResult = FairyTale.builder();
		FairyTale buildResult = builderResult.content(new ArrayList<>())
			.cover("Cover")
			.summary("Summary")
			.title("Dr")
			.build();
		when(fairyTaleService.findFairyTaleById(Mockito.<Integer>any())).thenReturn(buildResult);
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/fairy-tales/{fairyTaleId}",
			1);

		// Act and Assert
		MockMvcBuilders.standaloneSetup(fairyTaleController)
			.build()
			.perform(requestBuilder)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
			.andExpect(MockMvcResultMatchers.content()
				.string(
					"{\"id\":null,\"title\":\"Dr\",\"cover\":\"Cover\",\"summary\":\"Summary\",\"content\":[],\"createdAt\":[2024,7,26,9,5"
						+ ",16,402491000]}"));
	}

	/**
	 * Method under test:
	 * {@link FairyTaleController#invisible(Integer, CustomUserDetails)}
	 */
	@Test
	@Disabled("TODO: Complete this test")
	void testInvisible() {
		// TODO: Diffblue Cover was only able to create a partial test for this method:
		//   Reason: No inputs found that don't throw a trivial exception.
		//   Diffblue Cover tried to run the arrange/act section, but the method under
		//   test threw
		//   jakarta.servlet.ServletException: Request processing failed: java.lang.IllegalStateException: Cannot resolve parameter names for constructor public com.ssafy.a603.lingoland.member.security.CustomUserDetails(com.ssafy.a603.lingoland.member.entity.Member)
		//       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:593)
		//       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
		//   java.lang.IllegalStateException: Cannot resolve parameter names for constructor public com.ssafy.a603.lingoland.member.security.CustomUserDetails(com.ssafy.a603.lingoland.member.entity.Member)
		//       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:593)
		//       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
		//   See https://diff.blue/R013 to resolve this issue.

		// Arrange
		Member member = new Member();
		member.updateNickname("Nickname");
		member.updateRefreshToken("Refresh");
		Optional<Member> ofResult = Optional.of(member);
		MemberRepository memberRepository = mock(MemberRepository.class);
		when(memberRepository.findByLoginId(Mockito.<String>any())).thenReturn(ofResult);

		Member member2 = new Member();
		member2.updateNickname("Nickname");
		member2.updateRefreshToken("Refresh");
		FairyTaleMember.FairyTaleMemberBuilder builderResult = FairyTaleMember.builder();
		FairyTale.FairyTaleBuilder builderResult2 = FairyTale.builder();
		FairyTale fairyTale = builderResult2.content(new ArrayList<>())
			.cover("Cover")
			.summary("Summary")
			.title("Dr")
			.build();
		FairyTaleMember buildResult = builderResult.fairyTale(fairyTale).member(member2).build();
		Optional<FairyTaleMember> ofResult2 = Optional.of(buildResult);
		FairyTaleMemberRepository fairyTaleMemberRepository = mock(FairyTaleMemberRepository.class);
		when(fairyTaleMemberRepository.findById(Mockito.<FairyTaleMemberId>any())).thenReturn(ofResult2);
		FairyTaleController fairyTaleController = new FairyTaleController(
			new FairyTaleServiceImpl(mock(FairyTaleRepository.class), memberRepository, fairyTaleMemberRepository));

		// Act
		fairyTaleController.invisible(1, new CustomUserDetails(new Member()));
	}
}
