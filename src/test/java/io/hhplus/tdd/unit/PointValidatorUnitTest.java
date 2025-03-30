package io.hhplus.tdd.unit;

import io.hhplus.tdd.config.PointProperties;
import io.hhplus.tdd.exceptions.NotEnoughPointException;
import io.hhplus.tdd.validator.PointValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class PointValidatorUnitTest {
    private PointValidator validator;

    @Mock
    private PointProperties mockProperties;

    @BeforeEach
    void setUp() {
        lenient().when(mockProperties.getMaxAmount()).thenReturn(1_000_000_000L);
        validator = new PointValidator(mockProperties);
    }

    @DisplayName("PointValidator: 유효한 Amount 성공 검증")
    @ParameterizedTest
    @ValueSource(longs = {0, 1, 100, 1_000_000_000})
    void validateAmount_WhenAmountIsValid(long amount) {
        assertDoesNotThrow(() -> validator.validateAmount(amount));
    }

    @DisplayName("PointValidator: 사용가능한 금액 성공 검증")
    @ParameterizedTest
    @CsvSource({
            "100, 10",
            "100, 100",
            "100, 30",
            "1_000_000_000, 1_000_000_000"
    })
    void validateUsableBalance_WhenUserPointMinusAmountGraterThanZero(long userAmount, long amount) {
        assertDoesNotThrow(() -> validator.validateUsableBalance(userAmount, amount));
    }

    @DisplayName("PointValidator: 비유효한 Amount long 변수 실패 검증")
    @ParameterizedTest
    @ValueSource(longs = {-1, -100, -1_000_000_000})
    void validateAmount_ThrowsException_WhenAmountIsInvalid(long amount) {
        assertThrows(IllegalArgumentException.class, () -> validator.validateAmount(amount));
    }

    @DisplayName("PointValidator: 유저포인트보다 사용 포인트가 큰 경우, 실패 검증")
    @ParameterizedTest
    @CsvSource({
            "100, 101",
            "100, 200",
            "1_000_000, 1_000_000_000"
    })
    void validateAmount_ThrowsException_WhenUserPointMinusAmountLessThenZero(long userAmount, long amount) {
        assertThrows(NotEnoughPointException.class, () -> validator.validateUsableBalance(userAmount, amount));
    }
}
