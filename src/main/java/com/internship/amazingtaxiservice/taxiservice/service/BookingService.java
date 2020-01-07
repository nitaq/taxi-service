package com.internship.amazingtaxiservice.taxiservice.service;

import com.internship.amazingtaxiservice.taxiservice.model.Booking;
import com.internship.amazingtaxiservice.taxiservice.model.BookingDto;
import com.internship.amazingtaxiservice.taxiservice.model.EditBookingDTO;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;


public interface BookingService {

    Page<Booking> findAll(Pageable pageable);

    Booking findById(int theId);

    void save(Booking theBooking);

    void deleteById(int theId);

    void saveDto(BookingDto theBookingDto);

    void saveEditedDto(EditBookingDTO theBookingDto);

    Page<Booking> getPastBookings(int userId, Pageable pageable);

    Page<Booking> getReservedTaxis(Pageable pageable);

    void setTaxiToBooking(int bookingId, int taxiId);

    List<Booking> searchByQuery(String queryString);

    <T> CriteriaQuery<T> getCriteriaQuery(String queryString, RSQLVisitor<CriteriaQuery<T>, EntityManager> visitor);
}