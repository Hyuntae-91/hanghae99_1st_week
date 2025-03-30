package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.validator.ValidationWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final ValidationWrapper validationWrapper;

    public PointService(
            UserPointTable userPointTable,
            PointHistoryTable pointHistoryTable,
            ValidationWrapper validationWrapper
    ) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
        this.validationWrapper = validationWrapper;
    }

    public UserPoint getPoint(long id) {
        validationWrapper.validateId(id);
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getUserPointHistory(long id) {
        validationWrapper.validateId(id);
        return pointHistoryTable.selectAllByUserId(id);
    }

    public UserPoint chargeUserPoint(long id, long amount) {
        validationWrapper.validateId(id);
        validationWrapper.validateAmount(amount);

        UserPoint userPoint = userPointTable.selectById(id);
        amount = Math.addExact(userPoint.point(), amount);

        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        userPoint = userPointTable.insertOrUpdate(id, amount);

        return userPoint;
    }

    public UserPoint useUserPoint(long id, long amount) {
        validationWrapper.validateId(id);
        validationWrapper.validateAmount(amount);

        UserPoint userPoint = userPointTable.selectById(id);
        validationWrapper.validateUsableBalance(userPoint.point(), amount);

        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
        userPoint = userPointTable.insertOrUpdate(id, userPoint.point() - amount);
        return userPoint;
    }
}
