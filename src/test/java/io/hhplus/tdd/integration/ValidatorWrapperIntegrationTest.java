package io.hhplus.tdd.integration;

import io.hhplus.tdd.config.PointProperties;
import io.hhplus.tdd.exceptions.NotEnoughPointException;
import io.hhplus.tdd.validator.IdValidator;
import io.hhplus.tdd.validator.PointValidator;
import io.hhplus.tdd.validator.ValidationWrapper;
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
public class ValidatorWrapperIntegrationTest {
    private ValidationWrapper validationWrapper;

    @Mock
    private PointProperties mockProperties;

    @BeforeEach
    void setUp() {
        lenient().when(mockProperties.getMaxId()).thenReturn(1_000_000_000L);  // PointProperties stub 처리
        lenient().when(mockProperties.getMaxAmount()).thenReturn(1_000_000_000L);
        IdValidator idValidator = new IdValidator(mockProperties);
        PointValidator pointValidator = new PointValidator(mockProperties);
        validationWrapper = new ValidationWrapper(idValidator, pointValidator);
    }

    @DisplayName("ValidationWrapper: 유효한 ID 성공 검증")
    @ParameterizedTest
    @ValueSource(longs = {1, 100, 1_000_000_000})
    void validationWrapper_WhenIdIsValid(long id) {
        assertDoesNotThrow(() -> validationWrapper.validateId(id));
    }

    @DisplayName("ValidationWrapper: 유효한 Amount 성공 검증")
    @ParameterizedTest
    @ValueSource(longs = {0, 1, 100, 1_000_000_000})
    void validationWrapper_WhenAmountIsValid(long amount) {
        assertDoesNotThrow(() -> validationWrapper.validateAmount(amount));
    }

    @DisplayName("ValidationWrapper: 사용가능한 금액 성공 검증")
    @ParameterizedTest
    @CsvSource({
            "100, 10",
            "100, 100",
            "100, 30",
            "1_000_000_000, 1_000_000_000"
    })
    void validateUsableBalance_WhenUserPointMinusAmountGraterThanZero(long userAmount, long amount) {
        assertDoesNotThrow(() -> validationWrapper.validateUsableBalance(userAmount, amount));
    }

    @DisplayName("ValidationWrapper: 비유효한 ID 실패 검증")
    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    void validationWrapper_ThrowsException_WhenIdIsInvalid(long id) {
        assertThrows(IllegalArgumentException.class, () -> validationWrapper.validateId(id));
    }

    @DisplayName("ValidationWrapper: 비유효한 Amount long 변수 실패 검증")
    @ParameterizedTest
    @ValueSource(longs = {-1, -100, -1_000_000_000})
    void validationWrapper_ThrowsException_WhenAmountIsInvalid(long amount) {
        assertThrows(IllegalArgumentException.class, () -> validationWrapper.validateAmount(amount));
    }

    @DisplayName("ValidationWrapper: 유저포인트보다 사용 포인트가 큰 경우, 실패 검증")
    @ParameterizedTest
    @CsvSource({
            "100, 101",
            "100, 200",
            "1_000_000, 1_000_000_000"
    })
    void validateUsableBalance_ThrowsException_WhenUserPointMinusAmountLessThenZero(long userAmount, long amount) {
        assertThrows(NotEnoughPointException.class, () -> validationWrapper.validateUsableBalance(userAmount, amount));
    }
}
