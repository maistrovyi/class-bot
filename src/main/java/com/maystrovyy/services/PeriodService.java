package com.maystrovyy.services;

import com.maystrovyy.models.Period;
import com.maystrovyy.repositories.PeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeriodService {

    @Autowired
    private PeriodRepository periodRepository;

    public List<Period> findAll() {
        return periodRepository.findAll();
    }

    public Optional<Period> findById(Long id) {
        return periodRepository.findById(id);
    }

    public List<Period> findByGroupNameAndLessonWeek(String groupName, Integer lessonWeek) {
        return periodRepository.findByGroupNameAndLessonWeek(groupName, lessonWeek);
    }

    public List<Period> findByGroupNameAndDayNumberAndLessonWeek(String groupName, int dayNumber, Integer lessonWeek) {
        return periodRepository.findByGroupNameAndDayNumberAndLessonWeek(groupName, dayNumber, lessonWeek);
    }

    public List<Period> findByGroupName(String groupName) {
        return periodRepository.findByGroupName(groupName);
    }

    public Period save(Period period) {
        return periodRepository.save(period);
    }

    public List<Period> save(List<Period> periods) {
        return periodRepository.saveAll(periods);
    }

    public void update(Period period) {
        periodRepository.save(period);
    }

    public void delete(Period period) {
        periodRepository.delete(period);
    }

    public void delete(Long id) {
        periodRepository.deleteById(id);
    }

}