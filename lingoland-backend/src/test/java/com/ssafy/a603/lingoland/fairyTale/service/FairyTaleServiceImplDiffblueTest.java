package com.ssafy.a603.lingoland.fairyTale.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssafy.a603.lingoland.fairyTale.entity.FairyTale;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTaleMember;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTaleMemberId;
import com.ssafy.a603.lingoland.fairyTale.repository.FairyTaleMemberRepository;
import com.ssafy.a603.lingoland.fairyTale.repository.FairyTaleRepository;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.repository.MemberRepository;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {FairyTaleServiceImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class FairyTaleServiceImplDiffblueTest {
	@MockBean
	private FairyTaleMemberRepository fairyTaleMemberRepository;

	@MockBean
	private FairyTaleRepository fairyTaleRepository;

	@Autowired
	private FairyTaleServiceImpl fairyTaleServiceImpl;

	@MockBean
	private MemberRepository memberRepository;

	/**
	 * Method under test:
	 * {@link FairyTaleServiceImpl#createFairyTale(String, String, String, List, List)}
	 */
	@Test
	void testCreateFairyTale() {
		// Arrange
		FairyTale.FairyTaleBuilder builderResult = FairyTale.builder();
		ArrayList<FairyTale.Story> content = new ArrayList<>();
		FairyTale buildResult = builderResult.content(content).cover("Cover").summary("Summary").title("Dr").build();
		when(fairyTaleRepository.save(Mockito.<FairyTale>any())).thenReturn(buildResult);
		ArrayList<FairyTale.Story> content2 = new ArrayList<>();

		// Act
		FairyTale actualCreateFairyTaleResult = fairyTaleServiceImpl.createFairyTale("Dr", "Cover", "Summary", content2,
			new ArrayList<>());

		// Assert
		verify(fairyTaleRepository).save(isA(FairyTale.class));
		assertEquals("Cover", actualCreateFairyTaleResult.getCover());
		assertEquals("Dr", actualCreateFairyTaleResult.getTitle());
		assertEquals("Summary", actualCreateFairyTaleResult.getSummary());
		assertNull(actualCreateFairyTaleResult.getId());
		List<FairyTale.Story> content3 = actualCreateFairyTaleResult.getContent();
		assertTrue(content3.isEmpty());
		assertTrue(actualCreateFairyTaleResult.getFairyTaleMembers().isEmpty());
		assertSame(content, content3);
	}

	/**
	 * Method under test:
	 * {@link FairyTaleServiceImpl#createFairyTale(String, String, String, List, List)}
	 */
	@Test
	void testCreateFairyTale2() {
		// Arrange
		FairyTale.FairyTaleBuilder builderResult = FairyTale.builder();
		ArrayList<FairyTale.Story> content = new ArrayList<>();
		FairyTale buildResult = builderResult.content(content).cover("Cover").summary("Summary").title("Dr").build();
		when(fairyTaleRepository.save(Mockito.<FairyTale>any())).thenReturn(buildResult);

		ArrayList<FairyTale.Story> content2 = new ArrayList<>();
		FairyTale.Story buildResult2 = FairyTale.Story.builder().illustration("Illustration").story("Story").build();
		content2.add(buildResult2);

		// Act
		FairyTale actualCreateFairyTaleResult = fairyTaleServiceImpl.createFairyTale("Dr", "Cover", "Summary", content2,
			new ArrayList<>());

		// Assert
		verify(fairyTaleRepository).save(isA(FairyTale.class));
		assertEquals("Cover", actualCreateFairyTaleResult.getCover());
		assertEquals("Dr", actualCreateFairyTaleResult.getTitle());
		assertEquals("Summary", actualCreateFairyTaleResult.getSummary());
		assertNull(actualCreateFairyTaleResult.getId());
		List<FairyTale.Story> content3 = actualCreateFairyTaleResult.getContent();
		assertTrue(content3.isEmpty());
		assertTrue(actualCreateFairyTaleResult.getFairyTaleMembers().isEmpty());
		assertSame(content, content3);
	}

	/**
	 * Method under test:
	 * {@link FairyTaleServiceImpl#createFairyTale(String, String, String, List, List)}
	 */
	@Test
	void testCreateFairyTale3() {
		// Arrange
		FairyTale.FairyTaleBuilder builderResult = FairyTale.builder();
		ArrayList<FairyTale.Story> content = new ArrayList<>();
		FairyTale buildResult = builderResult.content(content).cover("Cover").summary("Summary").title("Dr").build();
		when(fairyTaleRepository.save(Mockito.<FairyTale>any())).thenReturn(buildResult);

		ArrayList<FairyTale.Story> content2 = new ArrayList<>();
		FairyTale.Story buildResult2 = FairyTale.Story.builder().illustration("Illustration").story("Story").build();
		content2.add(buildResult2);
		FairyTale.Story buildResult3 = FairyTale.Story.builder().illustration("Illustration").story("Story").build();
		content2.add(buildResult3);

		// Act
		FairyTale actualCreateFairyTaleResult = fairyTaleServiceImpl.createFairyTale("Dr", "Cover", "Summary", content2,
			new ArrayList<>());

		// Assert
		verify(fairyTaleRepository).save(isA(FairyTale.class));
		assertEquals("Cover", actualCreateFairyTaleResult.getCover());
		assertEquals("Dr", actualCreateFairyTaleResult.getTitle());
		assertEquals("Summary", actualCreateFairyTaleResult.getSummary());
		assertNull(actualCreateFairyTaleResult.getId());
		List<FairyTale.Story> content3 = actualCreateFairyTaleResult.getContent();
		assertTrue(content3.isEmpty());
		assertTrue(actualCreateFairyTaleResult.getFairyTaleMembers().isEmpty());
		assertSame(content, content3);
	}

	/**
	 * Method under test:
	 * {@link FairyTaleServiceImpl#createFairyTale(String, String, String, List, List)}
	 */
	@Test
	void testCreateFairyTale4() {
		// Arrange
		FairyTale.FairyTaleBuilder builderResult = FairyTale.builder();
		ArrayList<FairyTale.Story> content = new ArrayList<>();
		FairyTale buildResult = builderResult.content(content).cover("Cover").summary("Summary").title("Dr").build();
		when(fairyTaleRepository.save(Mockito.<FairyTale>any())).thenReturn(buildResult);

		Member member = new Member();
		member.updateNickname("Nickname");
		member.updateRefreshToken("Refresh");
		Optional<Member> ofResult = Optional.of(member);
		when(memberRepository.findByLoginId(Mockito.<String>any())).thenReturn(ofResult);

		Member member2 = new Member();
		member2.updateNickname("Nickname");
		member2.updateRefreshToken("Refresh");
		FairyTaleMember.FairyTaleMemberBuilder builderResult2 = FairyTaleMember.builder();
		FairyTale.FairyTaleBuilder builderResult3 = FairyTale.builder();
		FairyTale fairyTale = builderResult3.content(new ArrayList<>())
			.cover("Cover")
			.summary("Summary")
			.title("Dr")
			.build();
		FairyTaleMember buildResult2 = builderResult2.fairyTale(fairyTale).member(member2).build();
		when(fairyTaleMemberRepository.save(Mockito.<FairyTaleMember>any())).thenReturn(buildResult2);
		ArrayList<FairyTale.Story> content2 = new ArrayList<>();

		ArrayList<String> writers = new ArrayList<>();
		writers.add("foo");

		// Act
		FairyTale actualCreateFairyTaleResult = fairyTaleServiceImpl.createFairyTale("Dr", "Cover", "Summary", content2,
			writers);

		// Assert
		verify(memberRepository).findByLoginId(eq("foo"));
		verify(fairyTaleRepository).save(isA(FairyTale.class));
		verify(fairyTaleMemberRepository).save(isA(FairyTaleMember.class));
		assertEquals("Cover", actualCreateFairyTaleResult.getCover());
		assertEquals("Dr", actualCreateFairyTaleResult.getTitle());
		assertEquals("Summary", actualCreateFairyTaleResult.getSummary());
		assertNull(actualCreateFairyTaleResult.getId());
		List<FairyTale.Story> content3 = actualCreateFairyTaleResult.getContent();
		assertTrue(content3.isEmpty());
		assertTrue(actualCreateFairyTaleResult.getFairyTaleMembers().isEmpty());
		assertSame(content, content3);
	}

	/**
	 * Method under test:
	 * {@link FairyTaleServiceImpl#createFairyTale(String, String, String, List, List)}
	 */
	@Test
	void testCreateFairyTale5() {
		// Arrange
		FairyTale.FairyTaleBuilder builderResult = FairyTale.builder();
		FairyTale buildResult = builderResult.content(new ArrayList<>())
			.cover("Cover")
			.summary("Summary")
			.title("Dr")
			.build();
		when(fairyTaleRepository.save(Mockito.<FairyTale>any())).thenReturn(buildResult);

		Member member = new Member();
		member.updateNickname("Nickname");
		member.updateRefreshToken("Refresh");
		Optional<Member> ofResult = Optional.of(member);
		when(memberRepository.findByLoginId(Mockito.<String>any())).thenReturn(ofResult);
		when(fairyTaleMemberRepository.save(Mockito.<FairyTaleMember>any())).thenThrow(
			new NoSuchElementException("foo"));
		ArrayList<FairyTale.Story> content = new ArrayList<>();

		ArrayList<String> writers = new ArrayList<>();
		writers.add("foo");

		// Act and Assert
		assertThrows(NoSuchElementException.class,
			() -> fairyTaleServiceImpl.createFairyTale("Dr", "Cover", "Summary", content, writers));
		verify(memberRepository).findByLoginId(eq("foo"));
		verify(fairyTaleRepository).save(isA(FairyTale.class));
		verify(fairyTaleMemberRepository).save(isA(FairyTaleMember.class));
	}

	/**
	 * Method under test:
	 * {@link FairyTaleServiceImpl#createFairyTale(String, String, String, List, List)}
	 */
	@Test
	void testCreateFairyTale6() {
		// Arrange
		Optional<Member> emptyResult = Optional.empty();
		when(memberRepository.findByLoginId(Mockito.<String>any())).thenReturn(emptyResult);
		ArrayList<FairyTale.Story> content = new ArrayList<>();

		ArrayList<String> writers = new ArrayList<>();
		writers.add("foo");

		// Act and Assert
		assertThrows(IllegalArgumentException.class,
			() -> fairyTaleServiceImpl.createFairyTale("Dr", "Cover", "Summary", content, writers));
		verify(memberRepository).findByLoginId(eq("foo"));
	}

	/**
	 * Method under test:
	 * {@link FairyTaleServiceImpl#createFairyTale(String, String, String, List, List)}
	 */
	@Test
	void testCreateFairyTale7() {
		// Arrange
		FairyTale.FairyTaleBuilder builderResult = FairyTale.builder();
		ArrayList<FairyTale.Story> content = new ArrayList<>();
		FairyTale buildResult = builderResult.content(content).cover("Cover").summary("Summary").title("Dr").build();
		when(fairyTaleRepository.save(Mockito.<FairyTale>any())).thenReturn(buildResult);

		Member member = new Member();
		member.updateNickname("Nickname");
		member.updateRefreshToken("Refresh");
		Optional<Member> ofResult = Optional.of(member);
		when(memberRepository.findByLoginId(Mockito.<String>any())).thenReturn(ofResult);

		Member member2 = new Member();
		member2.updateNickname("Nickname");
		member2.updateRefreshToken("Refresh");
		FairyTaleMember.FairyTaleMemberBuilder builderResult2 = FairyTaleMember.builder();
		FairyTale.FairyTaleBuilder builderResult3 = FairyTale.builder();
		FairyTale fairyTale = builderResult3.content(new ArrayList<>())
			.cover("Cover")
			.summary("Summary")
			.title("Dr")
			.build();
		FairyTaleMember buildResult2 = builderResult2.fairyTale(fairyTale).member(member2).build();
		when(fairyTaleMemberRepository.save(Mockito.<FairyTaleMember>any())).thenReturn(buildResult2);
		ArrayList<FairyTale.Story> content2 = new ArrayList<>();

		ArrayList<String> writers = new ArrayList<>();
		writers.add("42");
		writers.add("foo");

		// Act
		FairyTale actualCreateFairyTaleResult = fairyTaleServiceImpl.createFairyTale("Dr", "Cover", "Summary", content2,
			writers);

		// Assert
		verify(memberRepository, atLeast(1)).findByLoginId(Mockito.<String>any());
		verify(fairyTaleRepository).save(isA(FairyTale.class));
		verify(fairyTaleMemberRepository, atLeast(1)).save(Mockito.<FairyTaleMember>any());
		assertEquals("Cover", actualCreateFairyTaleResult.getCover());
		assertEquals("Dr", actualCreateFairyTaleResult.getTitle());
		assertEquals("Summary", actualCreateFairyTaleResult.getSummary());
		assertNull(actualCreateFairyTaleResult.getId());
		List<FairyTale.Story> content3 = actualCreateFairyTaleResult.getContent();
		assertTrue(content3.isEmpty());
		assertTrue(actualCreateFairyTaleResult.getFairyTaleMembers().isEmpty());
		assertSame(content, content3);
	}

	/**
	 * Method under test:
	 * {@link FairyTaleServiceImpl#findFairyTaleListByLoginId(CustomUserDetails)}
	 */
	@Test
	@Disabled("TODO: Complete this test")
	void testFindFairyTaleListByLoginId() {
		// TODO: Diffblue Cover was only able to create a partial test for this method:
		//   Reason: No inputs found that don't throw a trivial exception.
		//   Diffblue Cover tried to run the arrange/act section, but the method under
		//   test threw
		//   java.lang.NullPointerException: Cannot invoke "com.ssafy.a603.lingoland.member.entity.Member.getLoginId()" because "<local2>.member" is null
		//       at com.ssafy.a603.lingoland.member.security.CustomUserDetails.getUsername(CustomUserDetails.java:35)
		//       at com.ssafy.a603.lingoland.fairyTale.service.FairyTaleServiceImpl.getMemberFromUserDetails(FairyTaleServiceImpl.java:81)
		//       at com.ssafy.a603.lingoland.fairyTale.service.FairyTaleServiceImpl.findFairyTaleListByLoginId(FairyTaleServiceImpl.java:55)
		//   See https://diff.blue/R013 to resolve this issue.

		// Arrange
		when(fairyTaleRepository.findAllFairyTalesByMemberId(anyInt())).thenReturn(new ArrayList<>());

		Member member = new Member();
		member.updateNickname("Nickname");
		member.updateRefreshToken("Refresh");
		Optional<Member> ofResult = Optional.of(member);
		when(memberRepository.findByLoginId(Mockito.<String>any())).thenReturn(ofResult);

		// Act
		fairyTaleServiceImpl.findFairyTaleListByLoginId(new CustomUserDetails(null));
	}

	/**
	 * Method under test: {@link FairyTaleServiceImpl#findFairyTaleById(Integer)}
	 */
	@Test
	void testFindFairyTaleById() {
		// Arrange
		FairyTale.FairyTaleBuilder builderResult = FairyTale.builder();
		ArrayList<FairyTale.Story> content = new ArrayList<>();
		FairyTale buildResult = builderResult.content(content).cover("Cover").summary("Summary").title("Dr").build();
		Optional<FairyTale> ofResult = Optional.of(buildResult);
		when(fairyTaleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

		// Act
		FairyTale actualFindFairyTaleByIdResult = fairyTaleServiceImpl.findFairyTaleById(1);

		// Assert
		verify(fairyTaleRepository).findById(eq(1));
		assertEquals("Cover", actualFindFairyTaleByIdResult.getCover());
		assertEquals("Dr", actualFindFairyTaleByIdResult.getTitle());
		assertEquals("Summary", actualFindFairyTaleByIdResult.getSummary());
		assertNull(actualFindFairyTaleByIdResult.getId());
		List<FairyTale.Story> content2 = actualFindFairyTaleByIdResult.getContent();
		assertTrue(content2.isEmpty());
		assertTrue(actualFindFairyTaleByIdResult.getFairyTaleMembers().isEmpty());
		assertSame(content, content2);
	}

	/**
	 * Method under test: {@link FairyTaleServiceImpl#findFairyTaleById(Integer)}
	 */
	@Test
	void testFindFairyTaleById2() {
		// Arrange
		Optional<FairyTale> emptyResult = Optional.empty();
		when(fairyTaleRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);

		// Act and Assert
		assertThrows(NoSuchElementException.class, () -> fairyTaleServiceImpl.findFairyTaleById(1));
		verify(fairyTaleRepository).findById(eq(1));
	}

	/**
	 * Method under test: {@link FairyTaleServiceImpl#findFairyTaleById(Integer)}
	 */
	@Test
	void testFindFairyTaleById3() {
		// Arrange
		when(fairyTaleRepository.findById(Mockito.<Integer>any()))
			.thenThrow(new NoSuchElementException("no such fairyTale"));

		// Act and Assert
		assertThrows(NoSuchElementException.class, () -> fairyTaleServiceImpl.findFairyTaleById(1));
		verify(fairyTaleRepository).findById(eq(1));
	}

	/**
	 * Method under test:
	 * {@link FairyTaleServiceImpl#fairyTaleInvisible(Integer, CustomUserDetails)}
	 */
	@Test
	@Disabled("TODO: Complete this test")
	void testFairyTaleInvisible() {
		// TODO: Diffblue Cover was only able to create a partial test for this method:
		//   Reason: No inputs found that don't throw a trivial exception.
		//   Diffblue Cover tried to run the arrange/act section, but the method under
		//   test threw
		//   java.lang.NullPointerException: Cannot invoke "com.ssafy.a603.lingoland.member.entity.Member.getLoginId()" because "this.member" is null
		//       at com.ssafy.a603.lingoland.member.security.CustomUserDetails.getUsername(CustomUserDetails.java:35)
		//       at com.ssafy.a603.lingoland.fairyTale.service.FairyTaleServiceImpl.getMemberFromUserDetails(FairyTaleServiceImpl.java:81)
		//       at com.ssafy.a603.lingoland.fairyTale.service.FairyTaleServiceImpl.fairyTaleInvisible(FairyTaleServiceImpl.java:69)
		//   See https://diff.blue/R013 to resolve this issue.

		// Arrange
		Member member = new Member();
		member.updateNickname("Nickname");
		member.updateRefreshToken("Refresh");
		Optional<Member> ofResult = Optional.of(member);
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
		when(fairyTaleMemberRepository.findById(Mockito.<FairyTaleMemberId>any())).thenReturn(ofResult2);

		// Act
		fairyTaleServiceImpl.fairyTaleInvisible(1, new CustomUserDetails(null));
	}
}
