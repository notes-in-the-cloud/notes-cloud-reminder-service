package com.notescloud.reminderservice.converter;

import com.notescloud.reminderservice.config.ReminderProperties;
import com.notescloud.reminderservice.entity.Reminder;
import com.notescloud.reminderservice.enums.Status;
import com.notescloud.reminderservice.model.ReminderModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.DateTimeException;
import java.time.temporal.ChronoUnit;

@Component
public class ReminderModelToEntityConverter implements Converter<ReminderModel, Reminder> {

    private final ZoneId fallbackZone;

    public ReminderModelToEntityConverter(ReminderProperties reminderProperties) {
        this.fallbackZone = ZoneId.of(reminderProperties.timezone());
    }

    @Override
    public Reminder convert(@NonNull ReminderModel source) {
        Reminder reminder = new Reminder();

        reminder.setId(source.getId());
        reminder.setUserId(source.getUserId());
        reminder.setHeading(source.getHeading());
        reminder.setDescription(source.getDescription());
        reminder.setReminderDate(source.getReminderDate());

        if (source.getReminderDate() != null && source.getReminderTime() != null) {
            LocalTime timeWithoutSeconds = source.getReminderTime().truncatedTo(ChronoUnit.MINUTES);

            Instant remindAt = source.getReminderDate()
                .atTime(timeWithoutSeconds)
                .atZone(resolveZone(source.getTimezone()))
                .toInstant();

            reminder.setReminderTime(timeWithoutSeconds);
            reminder.setRemindAt(remindAt);
        }

        reminder.setPriority(source.getPriority());
        reminder.setStatus(source.getStatus() != null ? source.getStatus() : Status.PENDING);
        reminder.setNotifyInApp(source.getNotifyInApp() == null || source.getNotifyInApp());

        return reminder;
    }

    private ZoneId resolveZone(String timezone) {
        if (timezone == null || timezone.isBlank()) {
            return fallbackZone;
        }

        try {
            return ZoneId.of(timezone);
        } catch (DateTimeException ex) {
            return fallbackZone;
        }
    }
}