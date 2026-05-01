package com.notescloud.reminderservice.repository;

import com.notescloud.reminderservice.entity.Reminder;
import com.notescloud.reminderservice.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    List<Reminder> findByUserIdOrderByReminderDateAscReminderTimeAsc(UUID userId);

    List<Reminder> findByUserIdAndStatusOrderByReminderDateAscReminderTimeAsc(UUID userId, Status status);
}
