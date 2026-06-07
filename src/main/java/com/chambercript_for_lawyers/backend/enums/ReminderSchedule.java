package com.chambercript_for_lawyers.backend.enums;
public enum ReminderSchedule {
    ONE_DAY_BEFORE(1),
    TWO_DAYS_BEFORE(2),
    THREE_DAYS_BEFORE(3),
    ONE_WEEK_BEFORE(7),
    ONE_MONTH_BEFORE(30);

    private final int days;

    ReminderSchedule(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
