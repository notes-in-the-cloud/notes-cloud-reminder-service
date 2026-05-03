package com.notescloud.reminderservice.converter;

import com.notescloud.reminderservice.entity.Reminder;
import com.notescloud.reminderservice.model.ReminderModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Component
public class ReminderModelToEntityConverter implements Converter<ReminderModel, Reminder> {

    @Override
    public Reminder convert(@NonNull ReminderModel source) {
        Reminder reminder = new Reminder();
        reminder.setId(source.getId());
        reminder.setUserId(source.getUserId());
        reminder.setHeading(source.getHeading());
        reminder.setDescription(source.getDescription());
        reminder.setReminderDate(source.getReminderDate());
        reminder.setReminderTime(source.getReminderTime());

        if (source.getReminderDate() != null && source.getReminderTime() != null) {
            LocalTime timeWithoutSeconds = source.getReminderTime().truncatedTo(ChronoUnit.MINUTES);
            Instant remindAt = source.getReminderDate()
                    .atTime(source.getReminderTime())
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
            reminder.setRemindAt(remindAt);
            reminder.setReminderTime(timeWithoutSeconds);
        }

        reminder.setPriority(source.getPriority());
        reminder.setStatus(source.getStatus() != null ? source.getStatus() : com.notescloud.reminderservice.enums.Status.PENDING);
        reminder.setNotifyInApp(source.isNotifyInApp());
        reminder.setNotifyPush(source.isNotifyPush());
        return reminder;
    }
}