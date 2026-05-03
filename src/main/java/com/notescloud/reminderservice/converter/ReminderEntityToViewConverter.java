package com.notescloud.reminderservice.converter;

import com.notescloud.reminderservice.entity.Reminder;
import com.notescloud.reminderservice.view.ReminderView;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ReminderEntityToViewConverter implements Converter<Reminder, ReminderView> {

    @Override
    public ReminderView convert(@NonNull Reminder source) {
        ReminderView view = new ReminderView();
        view.setId(source.getId());
        view.setUserId(source.getUserId());
        view.setHeading(source.getHeading());
        view.setDescription(source.getDescription());
        view.setReminderDate(source.getReminderDate());
        view.setReminderTime(source.getReminderTime());
        view.setPriority(source.getPriority());
        view.setStatus(source.getStatus());
        view.setNotifyInApp(source.isNotifyInApp());
        view.setNotifyPush(source.isNotifyPush());
        view.setCreatedAt(source.getCreatedAt());
        view.setUpdatedAt(source.getUpdatedAt());
        return view;
    }
}