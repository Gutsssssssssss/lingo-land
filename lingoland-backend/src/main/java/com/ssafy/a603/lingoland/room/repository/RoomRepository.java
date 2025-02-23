package com.ssafy.a603.lingoland.room.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.a603.lingoland.room.entity.Room;
import com.ssafy.a603.lingoland.room.entity.RoomId;

@Repository
public interface RoomRepository extends JpaRepository<Room, RoomId> {
	List<Room> findByIdSessionId(String sessionId);

	List<Room> findByCreatedAtBeforeAndIsDeletedFalse(LocalDateTime time);
}
