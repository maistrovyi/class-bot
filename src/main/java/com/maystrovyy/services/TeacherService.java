package com.maystrovyy.services;

import com.google.common.collect.Sets;
import com.maystrovyy.models.Teacher;
import com.maystrovyy.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher save(Teacher schedule) {
        return teacherRepository.save(schedule);
    }

    public Set<Teacher> save(Set<Teacher> teachers) {
//        TODO implement Java 9 factory method Set.of
        return Sets.newHashSet(teacherRepository.saveAll(teachers));
    }

    public void update(Teacher schedule) {
        teacherRepository.save(schedule);
    }

    public void delete(Teacher schedule) {
        teacherRepository.delete(schedule);
    }

    public void delete(Long id) {
        teacherRepository.deleteById(id);
    }

}