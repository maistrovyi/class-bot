package com.maystrovyy.repositories;

import com.maystrovyy.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Schedule findByGroupName(String groupName);

}