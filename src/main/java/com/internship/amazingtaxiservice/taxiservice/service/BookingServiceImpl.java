package com.internship.amazingtaxiservice.taxiservice.service;

import com.internship.amazingtaxiservice.taxiservice.model.*;
import com.internship.amazingtaxiservice.taxiservice.repository.BookingRepository;
import com.internship.amazingtaxiservice.taxiservice.repository.TaxiRepository;
import com.internship.amazingtaxiservice.taxiservice.repository.UserRepository;
import com.internship.amazingtaxiservice.taxiservice.rsql.jpa.JpaCriteriaQueryVisitor;
import com.internship.amazingtaxiservice.taxiservice.utils.UserDoesNotExistException;
import com.internship.amazingtaxiservice.taxiservice.utils.EntryNotFoundException;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;

    private UserRepository userRepository;

    private TaxiRepository taxiRepository;

    private EntityManager entityManager;

    @Autowired
    public BookingServiceImpl(BookingRepository theBookingRepository, UserRepository theUserRepository, TaxiRepository theTaxiRepository, EntityManager entityManager) {
        bookingRepository = theBookingRepository;
        userRepository = theUserRepository;
        taxiRepository = theTaxiRepository;
        this.entityManager = entityManager;
    }


    @Override
    public Page<Booking> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }


    @Override
    public Booking findById(int theId) {
        Optional<Booking> result = bookingRepository.findById(theId);

        Booking theBooking = null;

        if (result.isPresent()) {
            theBooking = result.get();
        } else {
            throw new EntryNotFoundException("Booking");
        }

        return theBooking;
    }


    @Override
    public void save(Booking theBooking) {
        bookingRepository.save(theBooking);
    }


    @Override
    public void deleteById(int theId) {
        bookingRepository.deleteById(theId);
    }


    @Override
    public void saveDto(BookingDto theBookingDto) {
        Optional<User> user = userRepository.findById(theBookingDto.getUser_id());
        Optional<Taxi> taxi = taxiRepository.findById(theBookingDto.getTaxi_id());

        Booking booking = new Booking();

        if (user.isPresent()) {
            booking.setUsers(user.get());
        } else {
            throw new EntryNotFoundException("User");
        }

        if (taxi.isPresent()) {
            booking.setTaxi(taxi.get());
        } else {
            throw new EntryNotFoundException("Taxi");
        }

        booking.setPickupLocation(theBookingDto.getPickupLocation());
        booking.setDestination(theBookingDto.getDestination());
        booking.setTime(theBookingDto.getTime());

        bookingRepository.save(booking);
    }


    @Override
    public void saveEditedDto(EditBookingDTO theBookingDto) {
        Optional<Booking> booking = bookingRepository.findById(theBookingDto.getBooking_id());
        Optional<User> user = userRepository.findById(theBookingDto.getUser_id());
        Optional<Taxi> taxi = taxiRepository.findById(theBookingDto.getTaxi_id());

        Booking editBooking = new Booking();

        if (user.isPresent()) {
            editBooking.setUsers(user.get());
        } else {
            throw new EntryNotFoundException("User");
        }

        if (taxi.isPresent()) {
            editBooking.setTaxi(taxi.get());
        } else {
            throw new EntryNotFoundException("Taxi");
        }

        editBooking.setId(theBookingDto.getBooking_id());
        editBooking.setPickupLocation(theBookingDto.getPickupLocation());
        editBooking.setDestination(theBookingDto.getDestination());
        editBooking.setTime(theBookingDto.getTime());

        bookingRepository.save(editBooking);
    }


    @Override
    public Page<Booking> getPastBookings(int userId, Pageable pageable) {
        User user = null;
        Optional<User> optionalUser = userRepository.findById(userId);

        Date todaysDate = new Date();

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            Page<Booking> pastBookings = bookingRepository.findAllByUsersAndTimeBefore(user, todaysDate, pageable);
            return pastBookings;
        } else {
            throw new UserDoesNotExistException();
        }
    }


    @Override
    public Page<Booking> getReservedTaxis(Pageable pageable) {
        return bookingRepository.findReservedTaxi(pageable);
    }


    @Override
    public void setTaxiToBooking(int bookingId, int taxiId) {
        Booking booking = null;
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        Optional<Taxi> optionalTaxi = taxiRepository.findById(taxiId);

        if (optionalBooking.isPresent()) {
            booking = optionalBooking.get();
            booking.setTaxi(optionalTaxi.get());
            bookingRepository.save(booking);
        } else {
            throw new EntryNotFoundException("Booking");
        }
    }

    @Override
    public List<Booking> searchByQuery(String queryString) {
        RSQLVisitor<CriteriaQuery<Booking>, EntityManager> visitor = new JpaCriteriaQueryVisitor<>();
        CriteriaQuery<Booking> query;
        query = getCriteriaQuery(queryString, visitor);
        List<Booking> resultList = entityManager.createQuery(query).getResultList();
        if (resultList == null || resultList.isEmpty()){
            return Collections.emptyList();
        }
        return resultList;
    }



    @Override
    public <T> CriteriaQuery<T> getCriteriaQuery(String queryString, RSQLVisitor<CriteriaQuery<T>, EntityManager> visitor) {
        Node rootNode;
        CriteriaQuery<T> query;
        try {
            rootNode = new RSQLParser().parse(queryString);
            query = rootNode.accept(visitor, entityManager);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
        return query;
    }
}