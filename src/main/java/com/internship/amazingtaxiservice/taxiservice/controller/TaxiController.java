package com.internship.amazingtaxiservice.taxiservice.controller;

import com.internship.amazingtaxiservice.taxiservice.model.EditTaxiDTO;
import com.internship.amazingtaxiservice.taxiservice.model.Taxi;
import com.internship.amazingtaxiservice.taxiservice.model.TaxiDto;
import com.internship.amazingtaxiservice.taxiservice.service.TaxiService;
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
public class TaxiController {

    private TaxiService taxiService;

    public TaxiController(TaxiService theTaxiService) {
        taxiService = theTaxiService;
    }


    @ApiOperation(value = "Get taxi by id")
    @GetMapping("/taxi/{id}")
    public ResponseEntity<Taxi> getTaxi(@PathVariable int id) {
        Taxi taxi = taxiService.findById(id);
        return ResponseEntity.ok().body(taxi);
    }


    @ApiOperation(value = "Get all taxis")
    @GetMapping("/taxis")
    public ResponseEntity<Page<Taxi>> findAllTaxis(Pageable pageable) {
        Page<Taxi> taxis = taxiService.findAll(pageable);
        return ResponseEntity.ok().body(taxis);
    }


    @ApiOperation(value = "Create Taxi")
    @PostMapping("/taxi")
    public ResponseEntity<Taxi> createTaxi(@RequestBody Taxi taxi) {
        taxiService.save(taxi);
        return ResponseEntity.ok().body(taxi);
    }


    @ApiOperation(value = "Create TaxiDTO")
    @PostMapping("/taxiDto")
    public ResponseEntity<TaxiDto> createTaxiDto(@RequestBody TaxiDto taxiDto) {
        taxiService.saveTaxiDto(taxiDto);
        return ResponseEntity.ok().body(taxiDto);
    }


    @ApiOperation(value = "Update Taxi")
    @PutMapping("/taxi")
    public ResponseEntity<Taxi> updateTaxi(@RequestBody Taxi taxi) {
       taxiService.save(taxi);
       return ResponseEntity.ok().body(taxi);
    }


    @ApiOperation(value = "Update TaxiDTO")
    @PutMapping("/taxiDTO")
    public ResponseEntity<EditTaxiDTO> updateTaxi(@RequestBody EditTaxiDTO taxi) {
        taxiService.saveEditedDTO(taxi);
        return ResponseEntity.ok().body(taxi);
    }


    @ApiOperation(value = "Delete Taxi")
    @DeleteMapping("/taxi/{id}")
    public ResponseEntity<Void> deleteTaxi(@PathVariable int id) {
        taxiService.deleteById(id);
        Void taxi = null;
        return ResponseEntity.ok().build();
    }


    @ApiOperation(value = "List of free taxis")
    @GetMapping("/taxi/taxisByStatus")
    public ResponseEntity<Page<Taxi>> freeTaxis(@RequestParam int id, Pageable pageable) {
        return ResponseEntity.status(200).body(taxiService.getTaxiByStatus(id, pageable));
    }


    @ApiOperation(value = "Search taxis")
    @GetMapping("/searchTaxi")
    public ResponseEntity<List<Taxi>> query(@RequestParam(value = "search") String query) {
        List<Taxi> result = null;
        try {
            result = taxiService.searchByQuery(query);
        } catch (IllegalArgumentException iae){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return  ResponseEntity.status(HttpStatus.OK).body(result);
    }

}