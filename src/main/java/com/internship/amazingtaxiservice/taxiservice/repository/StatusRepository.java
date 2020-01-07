package com.internship.amazingtaxiservice.taxiservice.repository;

import com.internship.amazingtaxiservice.taxiservice.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

}
