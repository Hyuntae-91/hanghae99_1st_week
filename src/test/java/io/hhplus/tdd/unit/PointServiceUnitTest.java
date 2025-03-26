package io.hhplus.tdd.unit;

import io.hhplus.tdd.config.PointProperties;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exceptions.NotEnoughPointException;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.validator.IdValidator;
import io.hhplus.tdd.validator.PointValidator;
import io.hhplus.tdd.validator.ValidationWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceUnitTest {
    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;
    private ValidationWrapper validationWrapper;
    private PointService pointService;

    @Mock
    private PointProperties mockProperties;

    @BeforeEach
    void setUp() {
        userPointTable = mock(UserPointTable.class);
        pointHistoryTable = mock(PointHistoryTable.class);
        lenient().when(mockProperties.getMaxId()).thenReturn(1_000_000_000L);  // PointProperties stub 처리
        lenient().when(mockProperties.getMaxAmount()).thenReturn(1_000_000_000L);
        IdValidator idValidator = new IdValidator(mockProperties);
        PointValidator pointValidator = new PointValidator(mockProperties);
        validationWrapper = new ValidationWrapper(idValidator, pointValidator);
        pointService = new PointService(userPointTable, pointHistoryTable, validationWrapper);
    }

    private UserPoint userPoint(long id, long point) {
        return new UserPoint(id, point, System.currentTimeMillis());
    }

    @DisplayName("getPoint: 유효한 ID에 대해 UserPoint를 반환한다")
    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L})
    void getPoint_ReturnUserPoint_whenIdIsValid(long validId) {
        when(userPointTable.selectById(validId)).thenReturn(UserPoint.empty(validId));

        UserPoint result = pointService.getPoint(validId);
        assertEquals(validId, result.id());
    }

    @DisplayName("getPoint: ID가 1 미만일 경우 예외 발생")
    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -999L})
    void getPoint_ThrowException_whenIdIsInvalid(long invalidId) {
        assertThrows(IllegalArgumentException.class, () -> pointService.getPoint(invalidId));
    }

    @DisplayName("getUserPointHistory: 유효한 ID에 대해 히스토리 목록 반환")
    @ParameterizedTest
    @ValueSource(longs = {1L, 5L, 99L})
    void getUserPointHistory_validId_returnsHistoryList(long validId) {
        List<PointHistory> mockHistory = List.of(
                new PointHistory(validId, 1, System.currentTimeMillis(), TransactionType.CHARGE, System.currentTimeMillis())
        );
        when(pointHistoryTable.selectAllByUserId(validId)).thenReturn(mockHistory);

        List<PointHistory> result = pointService.getUserPointHistory(validId);
        assertEquals(mockHistory, result);
    }

    @DisplayName("getUserPointHistory: ID가 1 미만일 경우 예외 발생")
    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void getUserPointHistory_ThrowsException_InvalidId(long invalidId) {
        assertThrows(IllegalArgumentException.class, () -> pointService.getUserPointHistory(invalidId));
    }

    @DisplayName("chargeUserPoint: 유효한 데이터에 대해 포인트를 충전한다")
    @ParameterizedTest
    @CsvSource({
            "1, 100, 300, 400",
            "5, 0, 100, 100",
    })
    void chargeUserPoint_validInput_returnsUpdatedUserPoint(long id, long before, long amount, long expected) {
        // Stub 처리
        UserPoint beforePoint = userPoint(id, before);
        UserPoint afterPoint = userPoint(id, expected);

        when(userPointTable.selectById(id)).thenReturn(beforePoint);
        when(userPointTable.insertOrUpdate(id, expected)).thenReturn(afterPoint);

        UserPoint result = pointService.chargeUserPoint(id, amount);

        assertEquals(afterPoint, result);
        verify(pointHistoryTable).insert(eq(id), eq(expected), eq(TransactionType.CHARGE), anyLong());
        verify(userPointTable).insertOrUpdate(id, expected);
    }

    @DisplayName("chargeUserPoint: 잘못된 ID나 금액에 대해 예외 발생")
    @ParameterizedTest
    @CsvSource({
            "1, -100",
            "1, -50"
    })
    void chargeUserPoint_ThrowException_whenInvalidIdOrAmount(long id, long amount) {
        assertThrows(IllegalArgumentException.class, () -> pointService.chargeUserPoint(id, amount));
    }

    static Stream<Arguments> validUseCases() {
        return Stream.of(
                Arguments.of(1L, 1000L, 300L, 700L),
                Arguments.of(2L, 500L, 100L, 400L)
        );
    }

    @DisplayName("useUserPoint: 유효한 요청에 대해 포인트 사용 처리")
    @ParameterizedTest
    @MethodSource("validUseCases")
    void useUserPoint_validInput_returnsUpdatedUserPoint(long id, long before, long amount, long expected) {
        // Stub 처리
        UserPoint beforePoint = userPoint(id, before);
        UserPoint afterPoint = userPoint(id, expected);

        when(userPointTable.selectById(id)).thenReturn(beforePoint);
        when(userPointTable.insertOrUpdate(id, expected)).thenReturn(afterPoint);

        UserPoint result = pointService.useUserPoint(id, amount);

        assertEquals(afterPoint, result);
        verify(pointHistoryTable).insert(eq(id), eq(amount), eq(TransactionType.USE), anyLong());
        verify(userPointTable).insertOrUpdate(id, expected);
    }

    @DisplayName("useUserPoint: 잘못된 ID 또는 잘못된 금액 사용 시 예외 발생")
    @ParameterizedTest
    @CsvSource({
            "0, 100",
            "1, -100"
    })
    void useUserPoint_ThrowsException_InvalidInput(long id, long amount) {
        assertThrows(IllegalArgumentException.class, () -> pointService.useUserPoint(id, amount));
    }

    @DisplayName("useUserPoint: 잔액 부족 시 NotEnoughPointException 발생")
    @Test
    void useUserPoint_ThrowsException_NotEnoughPoint() {
        long id = 1L;
        UserPoint userPoint = userPoint(id, 200L);
        when(userPointTable.selectById(id)).thenReturn(userPoint);

        assertThrows(NotEnoughPointException.class, () -> pointService.useUserPoint(id, 300L));
    }
}
