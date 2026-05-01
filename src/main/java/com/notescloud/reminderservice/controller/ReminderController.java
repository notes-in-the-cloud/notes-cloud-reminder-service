package com.notescloud.reminderservice.controller;

import com.notescloud.reminderservice.model.ReminderModel;
import com.notescloud.reminderservice.service.ReminderService;
import com.notescloud.reminderservice.view.ReminderView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderView create(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody ReminderModel model){

        model.setUserId(userId);
        model.setId(null);
        return reminderService.create(model);
    }

    @PutMapping
    public ReminderView update(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody ReminderModel model){

        model.setUserId(userId);
        return reminderService.update(model);
    }

    @GetMapping()
    public List<ReminderView> getAllRemindersForUser(@RequestHeader("X-User-Id") UUID userId){
        return reminderService.getAllForUser(userId);
    }

    @GetMapping("/completed")
    public List<ReminderView> getAllCompletedRemindersForUser(@RequestHeader("X-User-Id") UUID userId){
        return reminderService.getCompletedForUser(userId);
    }

    @GetMapping("/pending")
    public List<ReminderView> getAllPendingRemindersForUser(@RequestHeader("X-User-Id") UUID userId){
        return reminderService.getPendingForUser(userId);
    }

    @GetMapping("/{id}")
    public ReminderView getById(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id){
        return reminderService.getById(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id){
        reminderService.delete(id, userId);
    }

}
