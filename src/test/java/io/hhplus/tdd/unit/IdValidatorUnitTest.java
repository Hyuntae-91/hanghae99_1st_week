package io.hhplus.tdd.unit;

import io.hhplus.tdd.config.PointProperties;
import io.hhplus.tdd.validator.IdValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class IdValidatorUnitTest {
    private IdValidator idValidator;

    @Mock
    private PointProperties mockProperties;

    @BeforeEach
    void setUp() {
        lenient().when(mockProperties.getMaxId()).thenReturn(1_000_000_000L);
        idValidator = new IdValidator(mockProperties);
    }

    @DisplayName("IdValidator: 유효한 ID 성공 검증")
    @ParameterizedTest
    @ValueSource(longs = {1, 100, 1_000_000_000})
    void validateUserId_WhenIdIsValid(long id) {
        assertDoesNotThrow(() -> idValidator.validateUserId(id));
    }

    @DisplayName("IdValidator: 비유효한 ID 실패 검증")
    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    void validateUserId_ThrowsException_WhenIdIsInvalid(long id) {
        assertThrows(IllegalArgumentException.class, () -> idValidator.validateUserId(id));
    }
}
