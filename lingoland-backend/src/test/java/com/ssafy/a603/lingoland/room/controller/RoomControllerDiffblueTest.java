package com.ssafy.a603.lingoland.room.controller;

import static org.mockito.Mockito.when;

import com.ssafy.a603.lingoland.member.entity.Member;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.room.dto.RoomResponseDTO;
import com.ssafy.a603.lingoland.room.service.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {RoomController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class RoomControllerDiffblueTest {
    @Autowired
    private RoomController roomController;

    @MockBean
    private RoomService roomService;

    /**
     * Method under test: {@link RoomController#createRoom(CustomUserDetails)}
     */
    @Test
    void testCreateRoom() throws Exception {
        // Arrange
        when(roomService.createRoom(Mockito.<CustomUserDetails>any())).thenReturn(new RoomResponseDTO("Code", 3));
        MockHttpServletRequestBuilder postResult = MockMvcRequestBuilders.post("/api/v1/rooms");
        MockHttpServletRequestBuilder requestBuilder = postResult.param("customUserDetails",
                String.valueOf(new CustomUserDetails(new Member())));

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(roomController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"code\":\"Code\",\"memberCount\":3}"));
    }

    /**
     * Method under test: {@link RoomController#createRoom(CustomUserDetails)}
     */
    @Test
    void testCreateRoom2() throws Exception {
        // Arrange
        when(roomService.createRoom(Mockito.<CustomUserDetails>any())).thenReturn(new RoomResponseDTO("Code", 3));
        MockHttpServletRequestBuilder postResult = MockMvcRequestBuilders.post("/api/v1/rooms");
        MockHttpServletRequestBuilder requestBuilder = postResult.param("customUserDetails", String.valueOf(""));

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(roomController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"code\":\"Code\",\"memberCount\":3}"));
    }
}
