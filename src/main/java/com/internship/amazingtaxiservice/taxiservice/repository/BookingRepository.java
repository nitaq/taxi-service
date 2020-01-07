package com.internship.amazingtaxiservice.taxiservice.repository;

import com.internship.amazingtaxiservice.taxiservice.model.Booking;
import com.internship.amazingtaxiservice.taxiservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Page<Booking> findAllByUsersAndTimeBefore(User user, Date date, Pageable pageable);

    @Query("SELECT b from Booking  b where b.isReserved=true")
    Page<Booking> findReservedTaxi(Pageable pageable);
}
