package com.ssafy.a603.lingoland.room.controller;

import com.ssafy.a603.lingoland.member.security.CurrentUser;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.room.dto.RoomResponseDTO;
import com.ssafy.a603.lingoland.room.service.RoomService;
import com.ssafy.a603.lingoland.room.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<?> createRoom(@CurrentUser CustomUserDetails customUserDetails){
        RoomResponseDTO roomResponseDTO = roomService.createRoom(customUserDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomResponseDTO);
    }

    @GetMapping("/{roomCode}")
    public Room getRoomByRoomCode(@PathVariable String roomCode){
        return null;
    }

    // join, quit


}
