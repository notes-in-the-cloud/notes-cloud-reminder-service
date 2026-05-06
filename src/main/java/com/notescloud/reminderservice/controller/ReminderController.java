package com.notescloud.reminderservice.controller;

import com.notescloud.reminderservice.enums.ReminderFilter;
import com.notescloud.reminderservice.model.ReminderModel;
import com.notescloud.reminderservice.service.ReminderService;
import com.notescloud.reminderservice.view.ReminderView;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    public ReminderView create(
            @PathVariable UUID userId,
            @RequestBody ReminderModel model) {

        model.setUserId(userId);
        model.setId(null);
        return reminderService.create(model);
    }

    @PutMapping
    public ReminderView update(
            @PathVariable UUID userId,
            @RequestBody ReminderModel model) {

        model.setUserId(userId);
        return reminderService.update(model);
    }

    @GetMapping
    public List<ReminderView> getReminders(
            @PathVariable UUID userId,
            @RequestParam(required = false) ReminderFilter status) {

        if (status == null) {
            return reminderService.getAllForUser(userId);
        }
        return switch (status) {
            case PENDING   -> reminderService.getPendingForUser(userId);
            case COMPLETED -> reminderService.getCompletedForUser(userId);
        };
    }

    @GetMapping("/{id}")
    public ReminderView getById(
            @PathVariable UUID userId,
            @PathVariable UUID id) {
        return reminderService.getById(id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID userId,
            @PathVariable UUID id) {
        reminderService.delete(id, userId);
    }
}
