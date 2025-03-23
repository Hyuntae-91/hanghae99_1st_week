package io.hhplus.tdd.unit;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(PointController.class)
class PointControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;  // Service는 Mock 처리

    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, Long.MAX_VALUE})
    void getPointUser_ShouldReturnUserPoint_WhenIdIsValid(long validId) throws Exception {
        /*
            테스트 목적: /point/{id} endpoint 의 validation 정상 동작 체크
            테스트 변수: 0, 100, Long.MAX_VALUE (long 형 최대 크기)
            테스트 결과: validation 통과 후 Mock UserPoint 객체 응답
                - 빈 UserPoint 객체를 Mocking 하였으므로, 그에 대한 검증
         */

        // Mock 서비스 반환값 설정
        when(pointService.getPoint(validId))
                .thenReturn(UserPoint.empty(validId));

        mockMvc.perform(get("/point/" + validId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validId))
                .andExpect(jsonPath("$.point").value(0)) // UserPoint.empty는 0 포인트
                .andExpect(jsonPath("$.updateMillis").exists());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, Long.MAX_VALUE})
    void getPointUserHistory_ShouldReturnUserPoint_WhenIdIsValid(long validId) throws Exception {
        /*
            테스트 목적: /point/{id} endpoint 의 validation 정상 동작 체크
            테스트 변수: 0, 100, Long.MAX_VALUE (long 형 최대 크기)
         */
        mockMvc.perform(get("/point/" + validId))
                .andExpect(status().isOk());  // TODO: 응답 결과 처리 및 mocking 처리
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, Long.MAX_VALUE})
    void getPointUserCharge_ShouldReturnUserPoint_WhenIdIsValid(long validId) throws Exception {
        /*
            테스트 목적: /point/{id} endpoint 의 validation 정상 동작 체크
            테스트 변수: 0, 100, Long.MAX_VALUE (long 형 최대 크기)
         */
        mockMvc.perform(
                        patch("/point/" + validId + "/charge")
                                .contentType("application/json")
                                .content(String.valueOf(1))
                )
                .andExpect(status().isOk());  // TODO: 응답 결과 처리 및 mocking 처리
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L})
    void getPointUser_ShouldReturn400_WhenIdIsZeroOrNegative(long invalidId) throws Exception {
        /*
            테스트 목적: /point/{id} endpoint 의 validation 정상 동작 체크
            테스트 변수: 0, -1, -100 (0 과 음수)
         */
        mockMvc.perform(get("/point/" + invalidId))
                .andExpect(status().isBadRequest());  // TODO: 전체 Exception 이 아닌, 관련 exception 으로 변경
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L})
    void getPointUserHistory_ShouldReturn400_WhenIdIsZeroOrNegative(long invalidId) throws Exception {
        /*
            테스트 목적: /point/{id} endpoint 의 validation 정상 동작 체크
            테스트 변수: 0, -1, -100 (0 과 음수)
         */
        mockMvc.perform(get("/point/" + invalidId + "/histories"))
                .andExpect(status().isBadRequest());
    }
}
