package io.hhplus.tdd.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.hhplus.tdd.common.json.StrictLongDeserializer;
import jakarta.validation.constraints.Min;

public class ChargePointAmountRequest {
    @Min(0)
    @JsonDeserialize(using = StrictLongDeserializer.class)
    private long amount;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
