package com.maystrovyy.services;

import com.maystrovyy.models.Group;
import com.maystrovyy.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public Optional<Group> findById(Long id) {
        return groupRepository.findById(id);
    }

    public Group findByName(String groupName) {
        return groupRepository.findByName(groupName);
    }

    public Group save(Group period) {
        return groupRepository.save(period);
    }

}