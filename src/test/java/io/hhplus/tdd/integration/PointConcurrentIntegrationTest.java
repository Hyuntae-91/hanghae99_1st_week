package io.hhplus.tdd.integration;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PointConcurrentIntegrationTest {
    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointTable userPointTable;

    @Test
    @DisplayName("동시성 테스트: 여러 스레드가 동일한 ID에 포인트를 동시에 충전")
    void concurrentChargeUserPoint_WithCountDownLatch() throws InterruptedException {
        long userId = 1L;
        int threadCount = 10;
        long chargeAmount = 100;

        CountDownLatch latch = new CountDownLatch(threadCount);

        userPointTable.insertOrUpdate(userId, 0);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    pointService.chargeUserPoint(userId, chargeAmount);
                } finally {
                    latch.countDown(); // latch 감소
                }
            }).start();
        }
        latch.await();

        // then: 최종 포인트가 (threadCount * chargeAmount) 인지 검증
        UserPoint result = pointService.getPoint(userId);
        assertEquals(threadCount * chargeAmount, result.point());
    }

    @Test
    @DisplayName("동시성 테스트: 여러 스레드가 동일한 ID에 포인트를 동시에 사용")
    void concurrentUseUserPoint_WithCountDownLatch() throws InterruptedException {
        long userId = 1L;
        int threadCount = 10;
        long useAmount = 100;
        long initialAmount = 10000L;

        CountDownLatch latch = new CountDownLatch(threadCount);

        userPointTable.insertOrUpdate(userId, initialAmount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    pointService.useUserPoint(userId, useAmount);
                } finally {
                    latch.countDown(); // latch 감소
                }
            }).start();
        }
        latch.await();

        // then: 최종 포인트가 initialAmount - (threadCount * useAmount) 인지 검증
        UserPoint result = pointService.getPoint(userId);
        assertEquals(initialAmount - (threadCount * useAmount), result.point());
    }

    @Test
    @DisplayName("동시성 테스트: 여러 스레드가 동일한 ID에 포인트를 동시에 충전 및 사용")
    void concurrentChargeAndUseUserPoint_WithCountDownLatch() throws InterruptedException {
        long userId = 1L;
        int threadCount = 20;
        long useAmount = 100;
        long chargeAmount = 200;
        long initialAmount = 10000L;

        CountDownLatch latch = new CountDownLatch(threadCount);

        userPointTable.insertOrUpdate(userId, initialAmount);

        for (int i = 0; i < threadCount; i++) {
            final int cur_idx = i;
            new Thread(() -> {
                try {
                    if (cur_idx % 2 == 0) {
                        pointService.chargeUserPoint(userId, chargeAmount);
                    } else {
                        pointService.useUserPoint(userId, useAmount);
                    }
                } finally {
                    latch.countDown(); // latch 감소
                }
            }).start();
        }
        latch.await();

        UserPoint result = pointService.getPoint(userId);
        Long expected_amount = initialAmount - ((threadCount / 2) * useAmount) + ((threadCount / 2) * chargeAmount);
        assertEquals(expected_amount, result.point());
    }
}
