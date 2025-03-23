package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis,
        Status status
) {
    public enum Status {
        ACTIVE,
        INACTIVE,
        EMPTY
    }

    public UserPoint(long id, long point, long updateMillis) {
        this(id, point, updateMillis, Status.ACTIVE); // 기본값: EMPTY.. 근데 이렇게 짜도 될까..;
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis(), Status.EMPTY);
    }

    public boolean isEmpty() {
        return status == Status.EMPTY;
    }
}
