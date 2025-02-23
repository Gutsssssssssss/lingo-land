package com.ssafy.a603.lingoland.room.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.a603.lingoland.fairyTale.entity.FairyTale;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTaleMember;
import com.ssafy.a603.lingoland.fairyTale.repository.FairyTaleMemberRepository;
import com.ssafy.a603.lingoland.fairyTale.repository.FairyTaleRepository;
import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;
import com.ssafy.a603.lingoland.global.error.exception.NotFoundException;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.repository.MemberRepository;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.room.entity.Room;
import com.ssafy.a603.lingoland.room.entity.RoomId;
import com.ssafy.a603.lingoland.room.repository.RoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
	private final RoomRepository roomRepository;
	private final FairyTaleRepository fairyTaleRepository;
	private final FairyTaleMemberRepository fairyTaleMemberRepository;
	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public void create(String sessionId, CustomUserDetails customUserDetails, String title) {
		log.info("Creating new room with sessionId: {} and title: {}", sessionId, title);

		FairyTale fairyTale = FairyTale.builder()
			.title(title)
			.content(new ArrayList<>())
			.build();
		FairyTale saved = fairyTaleRepository.save(fairyTale);
		log.info("Saved new FairyTale with ID: {}", saved.getId());

		Member member = findMemberByCustomUserDetails(customUserDetails);
		FairyTaleMember fm = fairyTaleMemberRepository.save(
			FairyTaleMember.builder()
				.fairyTale(saved)
				.member(member)
				.build()
		);
		log.info("Saved FairyTaleMember for member ID: {}, fairytale id : {}", fm.getMember().getId(),
			fm.getMember().getId());
		RoomId roomId = new RoomId(sessionId, customUserDetails.getMemberId());
		Room room = Room.builder()
			.id(roomId)
			.fairyTale(saved)
			.build();
		Room savedRoom = roomRepository.save(room);
		log.info("Saved new Room with ID: {}", savedRoom.getId().toString());
		log.info("saved new Room with Fairy Tale : {}", savedRoom.getFairyTale().getId());
	}

	@Override
	@Transactional
	public void fairytaleStoryAdd(String sessionId, String starterLoginId, CustomUserDetails contributorDetails,
		FairyTale.Story story) {
		log.info("Adding story to FairyTale in room with sessionId: {} and starterLoginId: {}", sessionId,
			starterLoginId);

		Room room = findRoomByRoomId(sessionId, starterLoginId);
		Member contributor = findMemberByCustomUserDetails(contributorDetails);

		FairyTale fairyTale = fairyTaleRepository.findById(room.getFairyTale().getId()).orElseThrow(() -> {
			log.error("No such FairyTale : {}", room.getFairyTale().getId());
			return new NotFoundException(ErrorCode.FAIRY_TALE_NOT_FOUND);
		});
		fairyTale.addContent(story);
		fairyTaleMemberRepository.save(
			FairyTaleMember.builder()
				.fairyTale(fairyTale)
				.member(contributor)
				.build()
		);
		fairyTaleRepository.save(fairyTale);
		log.info("Added story to FairyTale in Room with ID: {}", room.getId());
	}

	@Override
	@Transactional
	public void fairytaleComplete(String sessionId, String starterLoginId, String cover, String summary) {
		log.info("Completing FairyTale in room with sessionId: {} and starterLoginId: {}", sessionId, starterLoginId);

		Room room = findRoomByRoomId(sessionId, starterLoginId);
		room.getFairyTale().completeBefore(cover, summary);
		room.getFairyTale().complete();
		log.info("Completed FairyTale in Room with ID: {}", room.getId());
	}

	@Override
	@Transactional
	public void fairytaleInComplete(String sessionId, String starterLoginId) {
		log.info("Marking FairyTale as incomplete in room with sessionId: {} and starterLoginId: {}", sessionId,
			starterLoginId);

		Room room = findRoomByRoomId(sessionId, starterLoginId);
		room.getFairyTale().inComplete();
		log.info("Marked FairyTale as incomplete in Room with ID: {}", room.getId());
	}

	@Override
	@Transactional
	public void endRooms(String sessionId) {
		log.info("Ending all rooms with sessionId: {}", sessionId);

		List<Room> rooms = roomRepository.findByIdSessionId(sessionId);
		rooms.forEach(room -> {
			room.delete();
			roomRepository.save(room);
			log.info("Ended Room with ID: {}", room.getId());
		});
	}

	@Override
	@Transactional
	public void endRoom(String sessionId, String starterLoginId) {
		log.info("Ending room with sessionId: {} and starterLoginId: {}", sessionId, starterLoginId);

		Room room = findRoomByRoomId(sessionId, starterLoginId);
		room.delete();
		roomRepository.save(room);
		log.info("Ended Room with ID: {}", room.getId());
	}

	@Override
	public FairyTale findFairyTale(String sessionId, String loginId) {
		log.info("Finding FairyTale in room with sessionId: {} and loginId: {}", sessionId, loginId);

		return findRoomByRoomId(sessionId, loginId).getFairyTale();
	}

	@Override
	public List<FairyTale> findFairyTalesInSession(String sessionId) {
		log.info("Finding all FairyTales in session with sessionId: {}", sessionId);

		List<Room> rooms = roomRepository.findByIdSessionId(sessionId);
		return rooms.stream()
			.map(Room::getFairyTale)
			.collect(Collectors.toList());
	}

	private Room findRoomByRoomId(String sessionId, String starterLoginId) {
		log.info("Finding Room with sessionId: {} and starterLoginId: {}", sessionId, starterLoginId);

		Member member = memberRepository.findByLoginId(starterLoginId).orElseThrow(() -> {
			log.error("No such member id : {}", starterLoginId);
			return new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
		});
		return roomRepository.findById(new RoomId(sessionId, member.getId())).orElseThrow(() -> {
			log.error("No such room");
			return new NotFoundException(ErrorCode.NOT_FOUND);
		});
	}

	private Member findMemberByCustomUserDetails(CustomUserDetails customUserDetails) {
		log.info("Finding Member with customUserDetails: {}", customUserDetails.getMemberId());

		return memberRepository.findById(customUserDetails.getMemberId()).orElseThrow(() -> {
			log.error("No such member id : {}", customUserDetails.getMemberId());
			return new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
		});
	}

	@Scheduled(cron = "0 0 * * * ?")
	public void roomDelteSchedule() {
		LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

		List<Room> roomsToDelete = roomRepository.findByCreatedAtBeforeAndIsDeletedFalse(oneHourAgo);
		for (Room room : roomsToDelete) {
			room.delete();
			roomRepository.save(room);
		}
	}
}