package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.exceptions.NotEnoughPointException;
import io.hhplus.tdd.exceptions.PointOverflowException;
import io.hhplus.tdd.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public UserPoint getPoint(long id) {
        UserPoint userPoint = userPointTable.selectById(id);
        if (userPoint.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        return userPoint;
    }

    public List<PointHistory> getUserPointHistory(long id) {
        UserPoint userPoint = userPointTable.selectById(id);
        if (userPoint.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        return pointHistoryTable.selectAllByUserId(id);
    }

    public UserPoint chargeUserPoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        // userPoint id 가 존재하면, 기존 amount 와 합, 없으면 요청받은 Amount 만
        try {
            amount = userPoint.isEmpty() ? amount : Math.addExact(userPoint.point(), amount);
        } catch (ArithmeticException e) {
            throw new PointOverflowException(id, userPoint.point(), amount);
        }
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        userPoint = userPointTable.insertOrUpdate(id, amount);

        return userPoint;
    }

    public UserPoint useUserPoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        if (userPoint.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        // charge 할 때 양수 오버플로우를 고려한 것 처럼 음수 오버플로우 고려는 하지 않음.
        // 기본적인 설계가 point 는 음수가 될 수 없기 때문
        if (amount > userPoint.point()) {
            throw new NotEnoughPointException(id);
        }

        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
        userPoint = userPointTable.insertOrUpdate(id, userPoint.point() - amount);
        return userPoint;
    }
}
