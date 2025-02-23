package com.ssafy.a603.lingoland.group.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.a603.lingoland.group.dto.CreateGroupDTO;
import com.ssafy.a603.lingoland.group.dto.GroupInfoResponseDTO;
import com.ssafy.a603.lingoland.group.dto.JoinGroupRequestDTO;
import com.ssafy.a603.lingoland.group.dto.MemberInGroupResponseDTO;
import com.ssafy.a603.lingoland.group.dto.UpdateGroupDTO;
import com.ssafy.a603.lingoland.group.entity.Group;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;

public interface GroupService {
	Group create(CreateGroupDTO request, CustomUserDetails customUserDetails);

	Boolean checkNameDuplication(String groupName);

	Boolean isGroupLeader(int GroupId, CustomUserDetails customUserDetails);

	List<GroupInfoResponseDTO> findMyGroups(String keyword, CustomUserDetails customUserDetails);

	List<GroupInfoResponseDTO> findNotMyGroups(String keyword, CustomUserDetails customUserDetails);

	GroupInfoResponseDTO findById(int id);

	void update(Integer groupId, UpdateGroupDTO request, MultipartFile groupImage,
		CustomUserDetails customUserDetails);

	void deleteById(int id, CustomUserDetails customUserDetails);

	void addMemberToGroupWithPasswordCheck(int groupId, JoinGroupRequestDTO joinGroupRequestDTO,
		CustomUserDetails customUserDetails);

	List<MemberInGroupResponseDTO> findAllMembersByGroupId(int groupId, String keyword,
		CustomUserDetails customUserDetails);

	void removeMemberFromGroup(int groupId, CustomUserDetails customUserDetails);

	Group save(Group group);
}
