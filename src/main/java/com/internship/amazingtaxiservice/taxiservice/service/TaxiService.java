package com.internship.amazingtaxiservice.taxiservice.service;

import com.internship.amazingtaxiservice.taxiservice.model.EditTaxiDTO;
import com.internship.amazingtaxiservice.taxiservice.model.Taxi;
import com.internship.amazingtaxiservice.taxiservice.model.TaxiDto;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;


public interface TaxiService {

    Page<Taxi> findAll(Pageable pageable);

    Taxi findById(int theId);

    void save(Taxi theTaxi);

    void deleteById(int theId);

    void saveTaxiDto(TaxiDto taxiDto);

    void saveEditedDTO(EditTaxiDTO theTaxiDTO);

    Page<Taxi> getTaxiByStatus(int id, Pageable pageable);

    List<Taxi> searchByQuery(String queryString);

    <T> CriteriaQuery<T> getCriteriaQuery(String queryString, RSQLVisitor<CriteriaQuery<T>, EntityManager> visitor);
}
