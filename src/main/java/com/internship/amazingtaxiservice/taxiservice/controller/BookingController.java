package com.internship.amazingtaxiservice.taxiservice.controller;

import com.internship.amazingtaxiservice.taxiservice.model.Booking;
import com.internship.amazingtaxiservice.taxiservice.model.BookingDto;
import com.internship.amazingtaxiservice.taxiservice.model.EditBookingDTO;
import com.internship.amazingtaxiservice.taxiservice.service.BookingService;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/")
public class BookingController {

    private BookingService bookingService;

    public BookingController(BookingService theBookingService) {
        bookingService = theBookingService;
    }


    @ApiOperation(value = "Get booking by id")
    @GetMapping("/booking/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable int id) {
        Booking booking = bookingService.findById(id);
        return ResponseEntity.ok().body(booking);
    }


    @ApiOperation(value = "Get all bookings")
    @GetMapping("/bookings")
    public ResponseEntity<Page<Booking>> findAllBookings(@RequestHeader("Authorization") String bearerToken, Pageable pageable) {
        Page<Booking> bookings = bookingService.findAll(pageable);
        return ResponseEntity.ok().body(bookings);
    }


    @ApiOperation(value = "Delete Booking")
    @DeleteMapping("/booking/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable int id) {
        bookingService.deleteById(id);
        Void booking = null;
        return ResponseEntity.ok().build();
    }


    @ApiOperation(value = "Create Booking")
    @PostMapping("/booking")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        bookingService.save(booking);
        return ResponseEntity.ok().body(booking);
    }


    @ApiOperation(value = "Create BookingDTO")
    @PostMapping("/bookingDTO")
    public ResponseEntity<BookingDto> createBookingDTO(@RequestBody BookingDto bookingDto) {
        bookingService.saveDto(bookingDto);
        return ResponseEntity.ok().body(bookingDto);
    }


    @ApiOperation(value = "Update Booking")
    @PutMapping("/booking")
    public ResponseEntity<Booking> updateBooking(@RequestBody Booking booking) {
        bookingService.save(booking);
        return ResponseEntity.ok().body(booking);
    }


    @ApiOperation(value = "Update BookingDTO")
    @PutMapping("/editBookingDTO")
    public ResponseEntity<EditBookingDTO> updateBooking(@RequestBody EditBookingDTO booking) {
        bookingService.saveEditedDto(booking);
        return ResponseEntity.ok().body(booking);
    }


    @ApiOperation(value = "List of past bookings")
    @GetMapping("booking/pastBookings")
    public ResponseEntity<Page<Booking>> pastBookings(@RequestParam int userId, Pageable pageable) {
        return ResponseEntity.status(200).body(bookingService.getPastBookings(userId, pageable));
    }

    @ApiOperation(value = "List of reserved taxis")
    @GetMapping("booking/reservedTaxis")
    public ResponseEntity<Page<Booking>> reservedTaxis(Pageable pageable) {
        return ResponseEntity.status(200).body(bookingService.getReservedTaxis( pageable));
    }

    @PostMapping("/setTaxiToBooking")
    public ResponseEntity<Booking> setTaxiToBooking(@RequestParam int bookingId, @RequestParam int taxiId) {
        bookingService.setTaxiToBooking(bookingId, taxiId);
        return ResponseEntity.status(204).build();
    }


    @ApiOperation(value = "Search bookings")
    @GetMapping("/searchBooking")
    public ResponseEntity<List<Booking>> query(@RequestParam(value = "search") String query) {
        List<Booking> result = null;
        try {
            result = bookingService.searchByQuery(query);
        } catch (IllegalArgumentException iae){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return  ResponseEntity.status(HttpStatus.OK).body(result);
    }

}