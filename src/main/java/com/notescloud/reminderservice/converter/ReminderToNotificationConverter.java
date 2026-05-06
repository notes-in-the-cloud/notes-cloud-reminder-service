package com.notescloud.reminderservice.converter;

import com.notescloud.reminderservice.entity.Notification;
import com.notescloud.reminderservice.entity.Reminder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ReminderToNotificationConverter implements Converter<Reminder, Notification> {

    @Override
    public Notification convert(@NonNull Reminder source) {
        Notification notification = new Notification();
        notification.setUserId(source.getUserId());
        notification.setReminderId(source.getId());
        notification.setHeading(source.getHeading());
        notification.setMessage(source.getDescription());
        notification.setPriority(source.getPriority());
        notification.setRead(false);
        return notification;
    }
}
