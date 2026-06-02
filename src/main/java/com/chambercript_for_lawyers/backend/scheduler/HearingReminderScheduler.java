package com.chambercript_for_lawyers.backend.scheduler;

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
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendUpcomingHearingReminders() {
        System.out.println("Running automated SMS reminder job...");

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Hearing> upcomingHearings = hearingRepository.findByHearingDateAndSmsReminderEnabledTrue(tomorrow);

        for (Hearing hearing : upcomingHearings) {

            String lawFirmCode = hearing.getLawFirmCode();

            if (lawFirmCode != null) {
                User user = userRepository.findByLawFirmCode(lawFirmCode).orElse(null);

                if (user != null && user.getPhone() != null && !user.getPhone().trim().isEmpty()) {

                    String clientPhone = user.getPhone();

                    String caseTitle = hearing.getLegalCase().getCaseTitle();
                    String caseNumber = hearing.getLegalCase().getCaseNumber();
                    String courtName = hearing.getLegalCase().getCourt().getCourtName();

                    String message = String.format(
                            "Reminder: Your case '%s' (%s) is scheduled for a hearing tomorrow at %s. Please be present. - Law Firm Name",
                            caseTitle, caseNumber, courtName
                    );

                    smsService.sendSms(clientPhone, message);
                    System.out.println("SMS sent to: " + clientPhone + " for case: " + caseNumber);

                } else {
                    System.out.println("Skipped SMS: No valid phone number found for Client ID " + lawFirmCode);
                }
            } else {
                System.out.println("Skipped SMS: No Client ID assigned to Case " + hearing.getLegalCase().getCaseNumber());
            }
        }
    }
}