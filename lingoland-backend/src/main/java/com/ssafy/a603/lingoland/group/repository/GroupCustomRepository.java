package com.ssafy.a603.lingoland.group.repository;

import java.util.List;

import com.ssafy.a603.lingoland.group.dto.GroupInfoResponseDTO;
import com.ssafy.a603.lingoland.group.dto.MemberInGroupResponseDTO;

public interface GroupCustomRepository {
	List<MemberInGroupResponseDTO> findAllMembersInGroup(Integer groupId, String keyword);

	List<GroupInfoResponseDTO> findGroupsByMemberId(Integer memberId, String keyword, boolean includeMember);

	GroupInfoResponseDTO findGroupInfoById(Integer groupId);
}
