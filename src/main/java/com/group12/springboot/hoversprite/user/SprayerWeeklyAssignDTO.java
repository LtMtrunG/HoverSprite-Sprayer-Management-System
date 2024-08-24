package com.group12.springboot.hoversprite.user;

import com.group12.springboot.hoversprite.user.enums.Expertise;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SprayerWeeklyAssignDTO {

    private Long id;
    private Expertise expertise;
    private int booking_assign;

    @Override
    public String toString() {
        return "SprayerWeeklyAssignDTO{" +
                "id=" + id +
                ", expertise=" + expertise +
                ", booking_assign=" + booking_assign +
                '}';
    }
}
