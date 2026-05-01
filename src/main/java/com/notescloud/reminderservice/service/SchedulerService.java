package com.notescloud.reminderservice.service;

import com.notescloud.reminderservice.entity.Reminder;
import com.notescloud.reminderservice.enums.Status;
import com.notescloud.reminderservice.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private static final int BATCH_SIZE = 100;

    private final ReminderRepository reminderRepository;

    // Polling-based scheduler. Under heavy load (thousands of due reminders
    // at the same minute), firing may span multiple ticks - acceptable for
    // human notifications.
    //
    // Scaling options if needed:
    //   1. Parallel processing: split batch across threads via @Async or
    //      parallelStream(). Trade-off: higher DB pool usage.
    //   2. Horizontal partitioning: multiple instances, each handling a subset
    //      of users (e.g. HASH(user_id) % instanceCount). Requires coordination
    //      via Kubernetes StatefulSets or similar.
    @Scheduled(fixedDelay = 15_000)
    @Transactional
    public void findDueReminders() {
        Instant now = Instant.now();
        List<Reminder> dueReminders = reminderRepository.findDueReminders(
                Status.PENDING,
                now,
                PageRequest.of(0, BATCH_SIZE)
        );

        if (dueReminders.isEmpty()) {
            log.debug("No reminders found to fire in {}", Instant.now());
            return;
        }

        log.info("Found {} due reminder(s) to fire", dueReminders.size());

        for (var reminder : dueReminders) {
            try {
                fireReminder(reminder);
            } catch (Exception e) {
                log.error("Failed to fire reminder {}", reminder.getId(), e);
            }
        }

    }

    private void fireReminder(Reminder reminder) {
        log.info("Firing reminder {} for user {}: \"{}\" (priority: {})",
                reminder.getId(),
                reminder.getUserId(),
                reminder.getDescription(),
                reminder.getPriority());

        reminder.setStatus(Status.FIRED);
        reminderRepository.save(reminder);
    }

}
