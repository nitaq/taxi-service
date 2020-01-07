package com.internship.amazingtaxiservice.taxiservice.service;

import com.internship.amazingtaxiservice.taxiservice.model.*;
import com.internship.amazingtaxiservice.taxiservice.repository.StatusRepository;
import com.internship.amazingtaxiservice.taxiservice.repository.TaxiRepository;
import com.internship.amazingtaxiservice.taxiservice.utils.EntryNotFoundException;
import com.internship.amazingtaxiservice.taxiservice.rsql.jpa.JpaCriteriaCountQueryVisitor;
import com.internship.amazingtaxiservice.taxiservice.rsql.jpa.JpaCriteriaQueryVisitor;
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
import java.util.List;
import java.util.Optional;


@Service
public class TaxiServiceImpl implements TaxiService {

    private TaxiRepository taxiRepository;
    private StatusRepository statusRepository;

    private EntityManager entityManager;


    @Autowired
    public TaxiServiceImpl(TaxiRepository theTaxiRepository, StatusRepository statusRepository,EntityManager entityManager) {
        this.taxiRepository = theTaxiRepository;
        this.statusRepository = statusRepository;
        this.entityManager = entityManager;
    }


    @Override
    public Page<Taxi> findAll(Pageable pageable) {
        return taxiRepository.findAll(pageable);
    }


    @Override
    public Taxi findById(int theId) {
        Optional<Taxi> result = taxiRepository.findById(theId);

        Taxi theTaxi = null;

        if (result.isPresent()) {
            theTaxi = result.get();
        } else {
            throw new EntryNotFoundException("Taxi");
        }

        return theTaxi;
    }


    @Override
    public void save(Taxi theTaxi) {
        taxiRepository.save(theTaxi);
    }


    @Override
    public void deleteById(int theId) {
        taxiRepository.deleteById(theId);
    }


    @Override
    public void saveTaxiDto(TaxiDto theTaxiDto) {
        Optional<Status> status = statusRepository.findById(theTaxiDto.getStatus_id());

        Taxi taxi = new Taxi();

        taxi.setName(theTaxiDto.getName());
        taxi.setNumber(theTaxiDto.getNumber());
        if (status.isPresent()) {
            taxi.setStatus(status.get());
        } else{
            throw new EntryNotFoundException("Status");
        }

        taxiRepository.save(taxi);
    }


    @Override
    public void saveEditedDTO(EditTaxiDTO theTaxiDTO) {
        Optional<Taxi> taxi = taxiRepository.findById(theTaxiDTO.getTaxi_id());
        Optional<Status> status = statusRepository.findById(theTaxiDTO.getStatus_id());

        Taxi editedTaxi = new Taxi();

        editedTaxi.setId(theTaxiDTO.getTaxi_id());
        editedTaxi.setName(theTaxiDTO.getName());
        editedTaxi.setNumber(theTaxiDTO.getNumber());

        if(status.isPresent()) {
            editedTaxi.setStatus(status.get());
        } else {
            throw new EntryNotFoundException("Status");
        }

        taxiRepository.save(editedTaxi);
    }


    @Override
    public Page<Taxi> getTaxiByStatus(int id, Pageable pageable) {
        Optional<Status> status = statusRepository.findById(id);

        if (status.isPresent()) {
            Page<Taxi> taxis = taxiRepository.findByStatusId(status.get().getId(), pageable);
            return taxis;
        } else {
            throw new EntryNotFoundException("Status");
        }
    }

    @Override
    public List<Taxi> searchByQuery(String queryString) {
        RSQLVisitor<CriteriaQuery<Taxi>, EntityManager> visitor = new JpaCriteriaQueryVisitor<>();
        CriteriaQuery<Taxi> query;
        query = getCriteriaQuery(queryString, visitor);
        List<Taxi> resultList = entityManager.createQuery(query).getResultList();
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