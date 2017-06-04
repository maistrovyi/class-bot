package com.maystrovyy.services;

import com.maystrovyy.models.Schedule;
import com.maystrovyy.repositories.sql.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    public Schedule findById(Long id) {
        return scheduleRepository.findOne(id);
    }

    public Schedule findByGroupName(String groupName) {
        return scheduleRepository.findByGroupName(groupName);
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public void update(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    public void delete(Schedule schedule) {
        scheduleRepository.delete(schedule);
    }

    public void delete(Long id) {
        scheduleRepository.delete(id);
    }

}