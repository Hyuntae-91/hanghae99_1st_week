package io.hhplus.tdd.integration;

import io.hhplus.tdd.point.*;
import io.hhplus.tdd.validator.ValidationWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(PointController.class)
class PointControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @MockBean
    private ValidationWrapper validationWrapper;

    @DisplayName("getPoint: 유효한 ID 성공 검증")
    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, 10_000_000_000L})
    void getPointUser_ShouldReturnUserPoint_WhenIdIsValid(long validId) throws Exception {
        // Stub 서비스 반환값 설정
        when(pointService.getPoint(validId))
                .thenReturn(UserPoint.empty(validId));

        mockMvc.perform(get("/point/" + validId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validId))
                .andExpect(jsonPath("$.point").value(0)) // UserPoint.empty는 0 포인트
                .andExpect(jsonPath("$.updateMillis").exists());
    }

    @DisplayName("getPointUserHistory: 유효한 ID 성공 검증")
    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, 10_000_000_000L})
    void getPointUserHistory_ShouldReturnListPointHistory_WhenIdIsValid(long validId) throws Exception {
        // Stub 서비스 반환값 설정
        List<PointHistory> mockHistory = List.of();
        // Stub 설정
        when(pointService.getUserPointHistory(validId)).thenReturn(mockHistory);

        // 요청 & 검증
        mockMvc.perform(get("/point/" + validId + "/histories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // 리스트 길이 확인
    }

    @DisplayName("patchPointUserCharge: 유효한 ID 성공 검증")
    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, 10_000_000_000L})
    void patchPointUserCharge_ShouldReturnUserPoint_WhenIdIsValid(long validId) throws Exception {
        // Stub 서비스 반환값 설정
        when(pointService.chargeUserPoint(validId, 1L))
                .thenReturn(UserPoint.empty(validId));

        mockMvc.perform(
                        patch("/point/" + validId + "/charge")
                                .contentType("application/json")
                                .content("{\"amount\": 1}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validId))
                .andExpect(jsonPath("$.point").value(0)) // UserPoint.empty는 0 포인트
                .andExpect(jsonPath("$.updateMillis").exists());
    }

    @DisplayName("patchPointUserCharge: 유효한 Amount 성공 검증")
    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 100L, 10_000_000_000L})
    void patchPointUserCharge_ShouldReturnUserPoint_WhenAmountIsValid(long validAmount) throws Exception {
        // Stub 서비스 반환값 설정
        when(pointService.chargeUserPoint(1, validAmount))
                .thenReturn(UserPoint.empty(1));

        String json = "{\"amount\": " + validAmount + "}";
        mockMvc.perform(
                        patch("/point/1/charge")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.point").value(0)) // UserPoint.empty는 0 포인트
                .andExpect(jsonPath("$.updateMillis").exists());
    }

    @DisplayName("patchPointUserUse: 유효한 ID 성공 검증")
    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, 10_000_000_000L})
    void patchPointUserUse_ShouldReturnUserPoint_WhenIdIsValid(long validId) throws Exception {
        // Mock 서비스 반환값 설정
        when(pointService.useUserPoint(validId, 500L))
                .thenReturn(UserPoint.empty(validId));

        mockMvc.perform(
                        patch("/point/" + validId + "/use")
                                .contentType("application/json")
                                .content("{\"amount\": 500}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validId))
                .andExpect(jsonPath("$.point").value(0)) // UserPoint.empty는 0 포인트
                .andExpect(jsonPath("$.updateMillis").exists());
    }

    @DisplayName("patchPointUserUse: 유효한 Amount 성공 검증")
    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 100L, 10_000_000_000L})
    void patchPointUserUse_ShouldReturnUserPoint_WhenAmountIsValid(long validAmount) throws Exception {
        // Mock 서비스 반환값 설정
        when(pointService.useUserPoint(1, validAmount))
                .thenReturn(UserPoint.empty(1));

        String json = "{\"amount\": " + validAmount + "}";
        mockMvc.perform(
                        patch("/point/1/use")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.point").value(0)) // UserPoint.empty는 0 포인트
                .andExpect(jsonPath("$.updateMillis").exists());
    }

    @DisplayName("getPointUser: 비유효한 ID 실패 검증 - String, Double")
    @ParameterizedTest
    @ValueSource(strings = {"abc", "d", "test", "3.14"})
    void getPointUser_ShouldReturn400_WhenIdIsStringAndDouble(String invalidId) throws Exception {
        mockMvc.perform(get("/point/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("getPointUserHistory: 비유효한 ID 실패 검증 - String, Double")
    @ParameterizedTest
    @ValueSource(strings = {"abc", "d", "test", "3.14"})
    void getPointUserHistory_ShouldReturn400_WhenIdIsStringAndDouble(String invalidId) throws Exception {
        mockMvc.perform(get("/point/" + invalidId + "/histories"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("patchPointUserCharge: 비유효한 ID 실패 검증 - String, Double")
    @ParameterizedTest
    @ValueSource(strings = {"abc", "d", "test", "3.14"})
    void patchPointUserCharge_ShouldReturn400_WhenIdIsStringAndDouble(String invalidId) throws Exception {
        mockMvc.perform(
                        patch("/point/" + invalidId + "/charge")
                                .contentType("application/json")
                                .content(String.valueOf(1))
                )
                .andExpect(status().isBadRequest());
    }

    @DisplayName("patchPointUserCharge: 비유효한 Amount 실패 검증 - String, Double")
    @ParameterizedTest
    @ValueSource(strings = {"abc", "d", "test", "3.14"})
    void patchPointUserCharge_ShouldReturn400_WhenAmountIsString(String invalidAmount) throws Exception {
        String requestJson = String.format("{\"amount\": %s}", invalidAmount);
        mockMvc.perform(
                        patch("/point/1/charge")
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());
    }

    @DisplayName("patchPointUserUse: 비유효한 ID 실패 검증 - String, Double")
    @ParameterizedTest
    @ValueSource(strings = {"abc", "d", "test", "3.14"})
    void patchPointUserUse_ShouldReturn400_WhenIdIsStringAndDouble(String invalidId) throws Exception {
        mockMvc.perform(
                        patch("/point/" + invalidId + "/charge")
                                .contentType("application/json")
                                .content(String.valueOf(1))
                )
                .andExpect(status().isBadRequest());
    }

    @DisplayName("patchPointUserUse: 비유효한 Amount 실패 검증 - String, Double")
    @ParameterizedTest
    @ValueSource(strings = {"abc", "d", "test", "3.14"})
    void patchPointUserUse_ShouldReturn400_WhenAmountIsString(String invalidAmount) throws Exception {
        mockMvc.perform(
                        patch("/point/1/charge")
                                .contentType("application/json")
                                .content(String.valueOf(invalidAmount))
                )
                .andExpect(status().isBadRequest());
    }
}

