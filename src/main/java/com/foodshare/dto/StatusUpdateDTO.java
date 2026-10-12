package com.foodshare.dto;

import com.foodshare.model.FoodStatus;
import javax.validation.constraints.NotNull;

public class StatusUpdateDTO {

    @NotNull(message = "Status is required")
    private FoodStatus status;

    private Long volunteerId;

    public StatusUpdateDTO() {}

    public FoodStatus getStatus() { return status; }
    public void setStatus(FoodStatus status) { this.status = status; }

    public Long getVolunteerId() { return volunteerId; }
    public void setVolunteerId(Long volunteerId) { this.volunteerId = volunteerId; }
}
