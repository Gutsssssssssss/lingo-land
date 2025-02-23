package com.ssafy.a603.lingoland.room.service;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.room.repository.RoomRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {RoomServiceImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class RoomServiceImplDiffblueTest {
    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private RedisTemplate redisTemplate;

    @MockBean
    private RoomRepository roomRepository;

    @Autowired
    private RoomServiceImpl roomServiceImpl;

    /**
     * Method under test: {@link RoomServiceImpl#createRoom(CustomUserDetails)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testCreateRoom() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "org.springframework.data.redis.core.ValueOperations.set(Object, Object)" because "valueOps" is null
        //       at com.ssafy.a603.lingoland.room.service.RoomServiceImpl.createRoom(RoomServiceImpl.java:30)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(null);

        // Act
        roomServiceImpl.createRoom(new CustomUserDetails(new Member()));
    }
}
