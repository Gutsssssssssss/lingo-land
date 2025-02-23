package com.ssafy.a603.lingoland.group.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.a603.lingoland.group.dto.CreateGroupDTO;
import com.ssafy.a603.lingoland.group.dto.CreateGroupResponseDto;
import com.ssafy.a603.lingoland.group.dto.GroupInfoResponseDTO;
import com.ssafy.a603.lingoland.group.dto.JoinGroupRequestDTO;
import com.ssafy.a603.lingoland.group.dto.MemberInGroupResponseDTO;
import com.ssafy.a603.lingoland.group.dto.UpdateGroupDTO;
import com.ssafy.a603.lingoland.group.entity.Group;
import com.ssafy.a603.lingoland.group.service.GroupService;
import com.ssafy.a603.lingoland.group.validator.CreateGroupValidator;
import com.ssafy.a603.lingoland.group.validator.GroupPasswordValidator;
import com.ssafy.a603.lingoland.group.validator.UpdateGroupValidator;
import com.ssafy.a603.lingoland.member.security.CurrentUser;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupController {

	private final GroupService groupService;
	private final CreateGroupValidator createGroupValidator;
	private final GroupPasswordValidator groupPasswordValidator;
	private final UpdateGroupValidator updateGroupValidator;

	@PostMapping
	public ResponseEntity<?> createGroup(@RequestBody CreateGroupDTO createGroupDTO,
		@CurrentUser CustomUserDetails customUserDetails, BindingResult bindingResult) {
		createGroupValidator.validate(createGroupDTO, bindingResult);
		groupPasswordValidator.validate(createGroupDTO, bindingResult);
		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors()
				.stream()
				.map(ObjectError::getCode)
				.collect(Collectors.toList());
			return ResponseEntity.badRequest().body(errors);
		}
		Group group = groupService.create(createGroupDTO, customUserDetails);
		return ResponseEntity.status(HttpStatus.CREATED).body(CreateGroupResponseDto.builder()
			.groupId(group.getId())
			.build());
	}

	@GetMapping
	public ResponseEntity<?> groupListForJoin(@RequestParam(required = false, name = "keyword") String keyword,
		@CurrentUser CustomUserDetails customUserDetails) {
		List<GroupInfoResponseDTO> groupInfos = groupService.findNotMyGroups(keyword, customUserDetails);
		return ResponseEntity.status(HttpStatus.OK).body(groupInfos);
	}

	@GetMapping("/check/{groupName}")
	public ResponseEntity<?> checkIdDuplication(@PathVariable("groupName") String groupName) {
		if (groupService.checkNameDuplication(groupName)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} else {
			return ResponseEntity.status(HttpStatus.OK).build();
		}
	}

	@GetMapping("/{groupId}/check-leader")
	public ResponseEntity<?> checkGroupLeader(@PathVariable("groupId") Integer groupId,
		@CurrentUser CustomUserDetails customUserDetails) {
		return ResponseEntity.status(HttpStatus.OK).body(groupService.isGroupLeader(groupId, customUserDetails));
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<?> getGroupById(@PathVariable("groupId") Integer groupId) {
		GroupInfoResponseDTO groupInfo = groupService.findById(groupId);
		return ResponseEntity.status(HttpStatus.OK).body(groupInfo);
	}

	@GetMapping("/users")
	public ResponseEntity<?> getMyGroups(@RequestParam(required = false, name = "keyword") String keyword,
		@CurrentUser CustomUserDetails customUserDetails) {
		List<GroupInfoResponseDTO> groupInfos = groupService.findMyGroups(keyword, customUserDetails);
		return ResponseEntity.status(HttpStatus.OK).body(groupInfos);
	}

	@PutMapping(path = "/{groupId}", produces = "application/json", consumes = "multipart/form-data")
	public ResponseEntity<?> updateGroupInfo(@PathVariable("groupId") Integer groupId,
		@RequestPart(value = "updateGroup") UpdateGroupDTO updateGroupDTO,
		@RequestPart(value = "groupImage", required = false) MultipartFile groupImage,
		@CurrentUser CustomUserDetails customUserDetails, BindingResult bindingResult) {
		updateGroupValidator.validate(updateGroupDTO, bindingResult, customUserDetails);
		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getAllErrors()
				.stream()
				.map(ObjectError::getCode)
				.collect(Collectors.toList());
			return ResponseEntity.badRequest().body(errors);
		}

		groupService.update(groupId, updateGroupDTO, groupImage, customUserDetails);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping(path = "/{groupId}")
	public ResponseEntity<?> deleteGroup(@PathVariable("groupId") Integer groupId,
		@CurrentUser CustomUserDetails customUserDetails) {
		groupService.deleteById(groupId, customUserDetails);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PostMapping("/{groupId}/users")
	public ResponseEntity<?> joinGroup(@PathVariable("groupId") Integer groupId,
		@RequestBody JoinGroupRequestDTO joinGroupRequestDTO,
		@CurrentUser CustomUserDetails customUserDetails) {
		groupService.addMemberToGroupWithPasswordCheck(groupId, joinGroupRequestDTO, customUserDetails);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{groupId}/users")
	public ResponseEntity<?> findMembersInGroup(@PathVariable("groupId") Integer groupId,
		@RequestParam(required = false, name = "keyword") String keyword,
		@CurrentUser CustomUserDetails customUserDetails) {
		List<MemberInGroupResponseDTO> members = groupService.findAllMembersByGroupId(groupId, keyword,
			customUserDetails);
		return ResponseEntity.status(HttpStatus.OK).body(members);
	}

	@DeleteMapping("/{groupId}/users")
	public ResponseEntity<?> quitGroup(@PathVariable("groupId") Integer groupId,
		@CurrentUser CustomUserDetails customUserDetails) {
		groupService.removeMemberFromGroup(groupId, customUserDetails);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
