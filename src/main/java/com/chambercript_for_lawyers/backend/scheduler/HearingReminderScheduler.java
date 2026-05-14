package com.chambercript_for_lawyers.backend.scheduler;

import com.chambercript_for_lawyers.backend.model.Hearing;
import com.chambercript_for_lawyers.backend.repository.HearingRepository;
import com.chambercript_for_lawyers.backend.services.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HearingReminderScheduler {

    private final HearingRepository hearingRepository;

    private final SmsService smsService;

    // Everyday 8 am
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendUpcomingHearingReminders() {
        System.out.println("Running automated SMS reminder job...");

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Hearing> upcomingHearings = hearingRepository.findByHearingDateAndSmsReminderEnabledTrue(tomorrow);

        for (Hearing hearing : upcomingHearings) {
            String clientPhone = "0712345678";

            String caseTitle = hearing.getLegalCase().getCaseTitle();
            String caseNumber = hearing.getLegalCase().getCaseNumber();
            String courtName = hearing.getLegalCase().getCourt().getCourtName();

            String message = String.format(
                    "Reminder: Your case '%s' (%s) is scheduled for a hearing tomorrow at %s. Please be present. - Law Firm Name",
                    caseTitle, caseNumber, courtName
            );

            smsService.sendSms(clientPhone, message);
        }
    }
}