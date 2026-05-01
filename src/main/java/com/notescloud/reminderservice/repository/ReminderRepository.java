package com.notescloud.reminderservice.repository;

import com.notescloud.reminderservice.entity.Reminder;
import com.notescloud.reminderservice.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    List<Reminder> findByUserIdOrderByReminderDateAscReminderTimeAsc(UUID userId);

    List<Reminder> findByUserIdAndStatusOrderByReminderDateAscReminderTimeAsc(UUID userId, Status status);

    @Query("""
            SELECT r FROM Reminder r
            WHERE r.status = :status AND r.remindAt <= :now
            ORDER BY r.remindAt ASC
            """)
    List<Reminder> findDueReminders(@Param("status") Status status, @Param("now") Instant now, Pageable pageable);


    @Query("""
        SELECT r FROM Reminder r
        WHERE r.userId = :userId AND r.status IN :statuses
        ORDER BY r.reminderDate ASC, r.reminderTime ASC
        """)
    List<Reminder> findByUserIdAndStatusIn(@Param("userId") UUID userId,
                                           @Param("statuses") List<Status> statuses);
}
