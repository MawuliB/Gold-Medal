package com.codecademy.goldmedal.repositories;

import com.codecademy.goldmedal.model.Country;
import com.codecademy.goldmedal.model.GoldMedal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoldMedalRepository extends CrudRepository<GoldMedal, Long> {
    List<GoldMedal> findAllByCountryOrderByYearAsc(String country);
    List<GoldMedal> findAllByCountryOrderByYearDesc(String country);
    List<GoldMedal> findAllByCountryOrderBySeasonAsc(String country);
    List<GoldMedal> findAllByCountryOrderBySeasonDesc(String country);
    List<GoldMedal> findAllByCountryOrderByCityAsc(String country);
    List<GoldMedal> findAllByCountryOrderByCityDesc(String country);
    List<GoldMedal> findAllByCountryOrderByNameAsc(String country);
    List<GoldMedal> findAllByCountryOrderByNameDesc(String country);
    List<GoldMedal> findAllByCountryOrderByEventAsc(String country);
    List<GoldMedal> findAllByCountryOrderByEventDesc(String country);

    List<GoldMedal> findAllBySeasonOrderByYearAsc(String season);
    List<GoldMedal> findAllByGender(String gender);
    List<GoldMedal> findAllByOrderByYear();
    List<GoldMedal> countAllBySeason(String season);
    List<GoldMedal> findAllBySeason(String season);

    List<GoldMedal> findAllByCountry(String country);


}
