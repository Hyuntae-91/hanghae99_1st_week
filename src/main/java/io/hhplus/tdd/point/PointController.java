package io.hhplus.tdd.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/point")
public class PointController {
    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;

    @Autowired
    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @GetMapping("/{id}")
    public UserPoint point(
            @PathVariable @Min(1) long id
    ) {
        return pointService.getPoint(id);
    }

    @GetMapping("/{id}/histories")
    public List<PointHistory> history(
            @PathVariable @Min(1) long id
    ) {
        return pointService.getUserPointHistory(id);
    }

    @PatchMapping("/{id}/charge")
    public UserPoint charge(
            @PathVariable @Min(1) long id,
            @RequestBody @Min(0) long amount
    ) {
        return pointService.chargeUserPoint(id, amount);
    }

    @PatchMapping("/{id}/use")
    public UserPoint use(
            @PathVariable @Min(1) long id,
            @RequestBody @Max(0) long amount
    ) {
        return pointService.useUserPoint(id, amount);
    }
}
