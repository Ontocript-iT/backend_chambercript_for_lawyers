package com.chambercript_for_lawyers.backend.scheduler;

import com.chambercript_for_lawyers.backend.enums.ReminderSchedule;
import com.chambercript_for_lawyers.backend.model.Client;
import com.chambercript_for_lawyers.backend.model.Hearing;
import com.chambercript_for_lawyers.backend.model.User;
import com.chambercript_for_lawyers.backend.repository.HearingRepository;
import com.chambercript_for_lawyers.backend.repository.UserRepository;
import com.chambercript_for_lawyers.backend.services.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HearingReminderScheduler {

    private final HearingRepository hearingRepository;

    private final SmsService smsService;

    private final UserRepository userRepository;


    // Everyday 8 am
    @Transactional
//    @Scheduled(cron = "0 11 18 * * ?")
    public void sendUpcomingHearingReminders() {
        System.out.println("Running automated SMS reminder job...");

        LocalDate today = LocalDate.now();

        for (ReminderSchedule schedule : ReminderSchedule.values()) {
            LocalDate targetHearingDate = (schedule == ReminderSchedule.ONE_MONTH_BEFORE)
                    ? today.plusMonths(1)
                    : today.plusDays(schedule.getDays());
            List<Hearing> upcomingHearings = hearingRepository.findByHearingDateAndSchedule(targetHearingDate, schedule);
            for (Hearing hearing : upcomingHearings) {
                processAndSendSms(hearing, schedule);
            }
        }
        List<Hearing> customHearings = hearingRepository.findByCustomReminderDate(today);
        for (Hearing hearing : customHearings) {
            processAndSendSms(hearing, null);
        }
    }

    @Transactional(readOnly = true)
    public void processAndSendSms(Hearing hearing, ReminderSchedule schedule) {
        String lawFirmCode = hearing.getLawFirmCode();

        if (lawFirmCode == null) {
            return;
        }

        User user = userRepository.findByLawFirmCode(lawFirmCode).orElse(null);

        if (user != null && user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            String clientPhone = user.getPhone();
            String caseTitle = hearing.getLegalCase().getCaseTitle();
            String caseNumber = hearing.getLegalCase().getCaseNumber();
            String courtName = hearing.getLegalCase().getCourt().getCourtName();

            String timeFrameStr;

            if (schedule == null) {
                timeFrameStr = "on " + hearing.getHearingDate().toString();
            } else {
                switch (schedule) {
                    case ONE_DAY_BEFORE:
                        timeFrameStr = "tomorrow";
                        break;
                    case TWO_DAYS_BEFORE:
                        timeFrameStr = "in 2 days";
                        break;
                    case THREE_DAYS_BEFORE:
                        timeFrameStr = "in 3 days";
                        break;
                    case ONE_WEEK_BEFORE:
                        timeFrameStr = "in one week";
                        break;
                    case ONE_MONTH_BEFORE:
                        timeFrameStr = "in one month";
                        break;
                    default:
                        timeFrameStr = "soon";
                }
            }

            String message = String.format(
                    "Reminder: Your case '%s' (%s) is scheduled for a hearing %s at %s. Please be present. - Law Firm Name",
                    caseTitle, caseNumber, timeFrameStr, courtName
            );

            smsService.sendSms(clientPhone, message);
            smsService.canSendSmsAndIncrement(user.getId());
            System.out.println("SMS sent to: " + clientPhone + " for case: " + caseNumber);
        }
    }
}