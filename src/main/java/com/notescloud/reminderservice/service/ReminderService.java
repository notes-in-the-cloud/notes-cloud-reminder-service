package com.notescloud.reminderservice.service;

import com.notescloud.reminderservice.entity.Reminder;
import com.notescloud.reminderservice.enums.Status;
import com.notescloud.reminderservice.exception.ReminderNotFoundException;
import com.notescloud.reminderservice.model.ReminderModel;
import com.notescloud.reminderservice.repository.ReminderRepository;
import com.notescloud.reminderservice.view.ReminderView;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final ConversionService conversionService;

    @Transactional
    public ReminderView create(ReminderModel model) {
        var entity = conversionService.convert(model, Reminder.class);
        var saved = reminderRepository.save(entity);
        return conversionService.convert(saved, ReminderView.class);
    }

    @Transactional
    public ReminderView update(ReminderModel model) {
        Reminder existing = reminderRepository.findById(model.getId())
                .orElseThrow(() -> new ReminderNotFoundException(model.getId()));

        if (!existing.getUserId().equals(model.getUserId())) {
            throw new ReminderNotFoundException(model.getId());
        }

        if (model.getHeading() != null) {
            existing.setHeading(model.getHeading());
        }

        if (model.getDescription() != null) {
            existing.setDescription(model.getDescription());
        }
        if (model.getReminderDate() != null) {
            existing.setReminderDate(model.getReminderDate());
        }
        if (model.getReminderTime() != null) {
            existing.setReminderTime(model.getReminderTime());
        }
        if (model.getReminderDate() != null || model.getReminderTime() != null) {
            LocalTime timeWithoutSeconds = existing.getReminderTime().truncatedTo(ChronoUnit.MINUTES);
            Instant remindAt = existing.getReminderDate()
                    .atTime(timeWithoutSeconds)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
            existing.setRemindAt(remindAt);
            existing.setReminderTime(timeWithoutSeconds);
        }
        if (model.getPriority() != null) {
            existing.setPriority(model.getPriority());
        }
        if (model.getStatus() != null) {
            existing.setStatus(model.getStatus());
        }
        existing.setNotifyInApp(model.isNotifyInApp());
        existing.setNotifyEmail(model.isNotifyEmail());
        existing.setNotifyPush(model.isNotifyPush());

        var saved = reminderRepository.save(existing);
        return conversionService.convert(saved, ReminderView.class);

    }

    @Transactional(readOnly = true)
    public List<ReminderView> getAllForUser(UUID userId) {
        return reminderRepository
                .findByUserIdOrderByReminderDateAscReminderTimeAsc(userId)
                .stream()
                .map(r -> conversionService.convert(r, ReminderView.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReminderView> getCompletedForUser(UUID userId) {
        return reminderRepository
                .findByUserIdAndStatusOrderByReminderDateAscReminderTimeAsc(userId, Status.COMPLETED)
                .stream()
                .map(r -> conversionService.convert(r, ReminderView.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReminderView> getPendingForUser(UUID userId) {
        return reminderRepository
                .findByUserIdAndStatusIn(userId, List.of(Status.PENDING, Status.FIRED))
                .stream()
                .map(r -> conversionService.convert(r, ReminderView.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public ReminderView getById(UUID id, UUID userId) {
        var reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ReminderNotFoundException(id));
        if (!reminder.getUserId().equals(userId)) {
            throw new ReminderNotFoundException(id);
        }
        return conversionService.convert(reminder, ReminderView.class);
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        var reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ReminderNotFoundException(id));
        if (!reminder.getUserId().equals(userId)) {
            throw new ReminderNotFoundException(id);
        }
        reminderRepository.delete(reminder);
    }

}
