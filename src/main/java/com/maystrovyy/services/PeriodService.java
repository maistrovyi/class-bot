package com.maystrovyy.services;

import com.maystrovyy.models.Period;
import com.maystrovyy.repositories.sql.PeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeriodService {

    @Autowired
    private PeriodRepository periodRepository;

    public List<Period> findAll() {
        return periodRepository.findAll();
    }

    public Period findById(Long id) {
        return periodRepository.findOne(id);
    }

    public Period save(Period period) {
        return periodRepository.save(period);
    }

    public void update(Period period) {
        periodRepository.save(period);
    }

    public void delete(Period period) {
        periodRepository.delete(period);
    }

    public void delete(Long id) {
        periodRepository.delete(id);
    }

}