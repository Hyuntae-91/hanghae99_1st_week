package io.hhplus.tdd.point;

import io.hhplus.tdd.dto.ChargePointAmountRequest;
import io.hhplus.tdd.dto.UsePointAmountRequest;
import io.hhplus.tdd.validator.ValidationWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@Validated
public class PointController {
    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;
    private final ValidationWrapper validationWrapper;

    @Autowired
    public PointController(PointService pointService, ValidationWrapper validationWrapper) {
        this.pointService = pointService;
        this.validationWrapper = validationWrapper;
    }

    @GetMapping("/{id}")
    public UserPoint point(
            @PathVariable @Min(1) long id
    ) {
        validationWrapper.validateId(id);
        return pointService.getPoint(id);
    }

    @GetMapping("/{id}/histories")
    public List<PointHistory> history(
            @PathVariable @Min(1) long id
    ) {
        validationWrapper.validateId(id);
        return pointService.getUserPointHistory(id);
    }

    @PatchMapping("/{id}/charge")
    public UserPoint charge(
            @PathVariable @Min(1) long id,
            @RequestBody @Valid ChargePointAmountRequest request
    ) {
        validationWrapper.validateId(id);
        return pointService.chargeUserPoint(id, request.getAmount());
    }

    @PatchMapping("/{id}/use")
    public UserPoint use(
            @PathVariable @Min(1) long id,
            @RequestBody @Valid UsePointAmountRequest request
    ) {
        validationWrapper.validateId(id);
        return pointService.useUserPoint(id, request.getAmount());
    }
}
