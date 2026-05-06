package com.notescloud.reminderservice.converter;

import com.notescloud.reminderservice.entity.Notification;
import com.notescloud.reminderservice.view.NotificationView;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class NotificationEntityToViewConverter implements Converter<Notification, NotificationView> {

    @Override
    public NotificationView convert(@NonNull Notification source) {
        NotificationView view = new NotificationView();
        view.setId(source.getId());
        view.setUserId(source.getUserId());
        view.setReminderId(source.getReminderId());
        view.setHeading(source.getHeading());
        view.setMessage(source.getMessage());
        view.setPriority(source.getPriority());
        view.setRead(source.isRead());
        view.setReadAt(source.getReadAt());
        view.setFiredAt(source.getFiredAt());
        return view;
    }
}