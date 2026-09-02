package com.yosri.moneymap.service;

import com.yosri.moneymap.dto.ExpenseDTO;
import com.yosri.moneymap.entity.Profile;
import com.yosri.moneymap.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final EmailService emailService;
    private final ProfileRepository profileRepository;
    private final ExpenseService expenseService;

    @Value("${money.map.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 20 * * *")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Sending Daily Income/Expense Reminder");
        log.info("Job started: sendDailyIncomeExpenseReminder()");
        List<Profile> profiles = profileRepository.findAll();

        for (Profile profile : profiles) {
            String body =
                    "<div style='font-family:Arial;max-width:600px;margin:auto;padding:30px;'>"
                            + "<h2>Hello " + profile.getFullName() + " 👋</h2>"
                            + "<p>Friendly reminder to add your income and expenses for today in <b>Money Map</b>.</p>"
                            + "<div style='text-align:center;margin:25px;'>"
                            + "<a href='" + frontendUrl + "' style='background:#3498db;color:white;"
                            + "padding:12px 25px;text-decoration:none;border-radius:6px;'>"
                            + "Open Money Map</a></div>"
                            + "<p>Best regards,<br><b>Money Map Team</b></p>"
                            + "</div>";
            emailService.sendEmail(profile.getEmail(),"Daily Reminder",body);
        }
        log.info("Reminders sent successfully");
    }

    @Scheduled(cron = "0 0 21 * * *")
    public void sendDailyExpenseSummary(){
        log.info("Sending Daily Expense Summary");
        List<Profile> profiles = profileRepository.findAll();
        for (Profile profile : profiles) {
            List<ExpenseDTO> todayExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());

            if(!todayExpenses.isEmpty()){
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse:collapse;width:100%;'>");
                table.append("<tr style='background-color:#f2f2f2;'>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>S.N</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
                        .append("</tr>");
                int i = 1;
                for (ExpenseDTO expense : todayExpenses) {
                    table.append("<tr>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount()).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getCategoryName()).append("</td>");
                    table.append("</tr>");
                }
                table.append("</table>");
                String body = "Hi " + profile.getFullName()
                        + ",<br><br>Here is a summary of your expenses for today:<br><br>"
                        + table
                        + "<br><br>Best regards,<br>Money Manager Team";

                emailService.sendEmail(profile.getEmail(), "Daily Expense Summary", body);
            }
        }
        log.info("Expense summaries sent successfully");
    }

}
