package com.ssafy.a603.lingoland.group.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;
import com.ssafy.a603.lingoland.global.error.exception.ForbiddenException;
import com.ssafy.a603.lingoland.global.error.exception.InvalidInputException;
import com.ssafy.a603.lingoland.global.error.exception.NotFoundException;
import com.ssafy.a603.lingoland.group.dto.CreateGroupDTO;
import com.ssafy.a603.lingoland.group.dto.GroupInfoResponseDTO;
import com.ssafy.a603.lingoland.group.dto.JoinGroupRequestDTO;
import com.ssafy.a603.lingoland.group.dto.MemberInGroupResponseDTO;
import com.ssafy.a603.lingoland.group.dto.UpdateGroupDTO;
import com.ssafy.a603.lingoland.group.entity.Group;
import com.ssafy.a603.lingoland.group.entity.GroupMember;
import com.ssafy.a603.lingoland.group.entity.GroupMemberId;
import com.ssafy.a603.lingoland.group.repository.GroupMemberRepository;
import com.ssafy.a603.lingoland.group.repository.GroupRepository;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.repository.MemberRepository;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.util.ImgUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
	private static final String GROUP_IMAGE_PATH = "group";
	private final GroupRepository groupRepository;
	private final MemberRepository memberRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final ImgUtils imgUtils;

	@Override
	@Transactional
	public Group create(CreateGroupDTO request, CustomUserDetails customUserDetails) {
		log.info("Creating group with name: {}", request.name());
		Member member = getMemberFromUserDetails(customUserDetails);
		Group group = Group.builder()
			.name(request.name())
			.password(request.password())
			.description(request.description())
			.leader(member)
			.build();

		group.setGroupImagePath(imgUtils.getImagePathWithDefaultImage(GROUP_IMAGE_PATH));
		Group createdGroup = groupRepository.save(group);

		addMemberToGroup(group, member, "그룹장 입니다.");

		log.info("Group created with ID: {}", createdGroup.getId());
		return createdGroup;
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean checkNameDuplication(String groupName) {
		log.info("Checking name duplication for group: {}", groupName);
		return groupRepository.existsByName(groupName);
	}

	@Override
	public Boolean isGroupLeader(int groupId, CustomUserDetails customUserDetails) {
		Group group = getGroupById(groupId);
		return isGroupLeader(group, customUserDetails.getMemberId());
	}

	@Override
	@Transactional(readOnly = true)
	public List<GroupInfoResponseDTO> findMyGroups(String keyword, CustomUserDetails customUserDetails) {
		log.info("Finding my groups with keyword: {}", keyword);
		return groupRepository.findGroupsByMemberId(customUserDetails.getMemberId(), keyword, true);
	}

	@Override
	@Transactional(readOnly = true)
	public List<GroupInfoResponseDTO> findNotMyGroups(String keyword, CustomUserDetails customUserDetails) {
		log.info("Finding groups not belonging to user with keyword: {}", keyword);
		return groupRepository.findGroupsByMemberId(customUserDetails.getMemberId(), keyword, false);
	}

	@Override
	@Transactional(readOnly = true)
	public GroupInfoResponseDTO findById(int id) {
		log.info("Finding group by ID: {}", id);
		return groupRepository.findGroupInfoById(id);
	}

	@Override
	@Transactional
	public void update(Integer groupId, UpdateGroupDTO request, MultipartFile groupImage,
		CustomUserDetails customUserDetails) {
		log.info("Updating group with ID: {}", groupId);
		Group group = getGroupById(request.id());
		group.updateGroup(request);
		if (groupImage != null) {
			imgUtils.deleteImage(group.getGroupImage());
			group.setGroupImagePath(imgUtils.saveImage(groupImage, GROUP_IMAGE_PATH));
		}

		log.info("Group with ID: {} updated successfully.", groupId);
	}

	@Override
	@Transactional
	public void deleteById(int groupId, CustomUserDetails customUserDetails) {
		log.info("Deleting group with ID: {}", groupId);
		Group group = groupRepository.findById(groupId).orElseThrow(
			() -> new InvalidInputException(ErrorCode.GROUP_INVALID_INPUT)
		);
		if (!isGroupLeader(group, customUserDetails.getMemberId())) {
			log.error("Member with ID: {} is not the leader of group ID: {}", customUserDetails.getMemberId(), groupId);
			throw new ForbiddenException(ErrorCode.GROUP_NOT_LEADER);
		}
		group.delete();
		log.info("Group with ID: {} deleted successfully.", groupId);
	}

	@Override
	@Transactional
	public void addMemberToGroupWithPasswordCheck(int groupId, JoinGroupRequestDTO joinGroupRequestDTO,
		CustomUserDetails customUserDetails) {
		log.info("Adding member to group with ID: {}", groupId);
		Group group = groupRepository.findById(groupId).orElseThrow(
			() -> new InvalidInputException(ErrorCode.GROUP_INVALID_INPUT)
		);

		if (group.getPassword().intValue() != joinGroupRequestDTO.password().intValue()) {
			log.error("Password mismatch for group ID: {}", groupId);
			throw new InvalidInputException(ErrorCode.GROUP_PASSWORD_MISMATCH);
		}

		Member member = getMemberFromUserDetails(customUserDetails);
		addMemberToGroup(group, member, joinGroupRequestDTO.description());
		log.info("Member added to group ID: {}", groupId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MemberInGroupResponseDTO> findAllMembersByGroupId(int groupId, String keyword,
		CustomUserDetails customUserDetails) {
		log.info("Finding all members in group with ID: {}", groupId);
		Member member = getMemberFromUserDetails(customUserDetails);

		if (!isGroupMember(member, groupId)) {
			log.error("Member with ID: {} is not part of group ID: {}", member.getId(), groupId);
			throw new ForbiddenException(ErrorCode.MEMBER_FORBIDDEN);
		}

		return groupRepository.findAllMembersInGroup(groupId, keyword);
	}

	@Override
	@Transactional
	public void removeMemberFromGroup(int groupId, CustomUserDetails customUserDetails) {
		log.info("Removing member from group with ID: {}", groupId);

		GroupMemberId groupMemberId = GroupMemberId.builder()
			.groupId(groupId)
			.memberId(customUserDetails.getMemberId())
			.build();
		GroupMember groupMember = groupMemberRepository.findById(groupMemberId)
			.orElseThrow(() -> {
				log.error("Not found Member-Group relation");
				return new NotFoundException(ErrorCode.NOT_FOUND);
			});
		groupMember.quit();
		Group group = getGroupById(groupId);
		group.quit();

		log.info("Member removed from group ID: {}", groupId);
	}

	@Override
	@Transactional
	public Group save(Group group) {
		return groupRepository.save(group);
	}

	private void addMemberToGroup(Group group, Member member, String description) {
		log.info("add member {} to group {}", member.getId(), group.getId());
		GroupMemberId groupMemberId = GroupMemberId.builder()
			.groupId(group.getId())
			.memberId(member.getId())
			.build();

		GroupMember groupMember = GroupMember.builder()
			.id(groupMemberId)
			.description(description)
			.build();
		groupMember.addGroup(group);
		groupMember.addMember(member);

		group.join();

		groupMemberRepository.save(groupMember);
	}

	private boolean isGroupMember(Member member, int groupId) {
		for (GroupMember groupMember : member.getGroupMembers()) {
			if (groupMember.getGroup().getId() == groupId)
				return true;
		}
		return false;
	}

	private Member getMemberFromUserDetails(CustomUserDetails customUserDetails) {
		return memberRepository.findById(customUserDetails.getMemberId())
			.orElseThrow(() -> {
				log.error("Not found member with ID {}", customUserDetails.getMemberId());
				return new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
			});
	}

	private Group getGroupById(int groupId) {
		return groupRepository.findById(groupId).orElseThrow(
			() -> new InvalidInputException(ErrorCode.GROUP_INVALID_INPUT)
		);
	}

	private boolean isGroupLeader(Group group, Integer memberId) {
		return group.getLeader().getId() == memberId;
	}
}
