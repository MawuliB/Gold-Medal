package com.codecademy.goldmedal.repositories;

import com.codecademy.goldmedal.model.Country;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends CrudRepository<Country, Long> {
    Optional<Country> findByName(String name);
    List<Country> findAllByOrderByGdpAsc();
    List<Country> findAllByOrderByGdpDesc();
    List<Country> findAllByOrderByNameAsc();
    List<Country> findAllByOrderByNameDesc();
    List<Country> findAllByOrderByPopulationAsc();
    List<Country> findAllByOrderByPopulationDesc();
    List<Country> findAllByOrderByCodeAsc();
}
