package com.codecademy.goldmedal.controller;

import com.codecademy.goldmedal.model.*;
import com.codecademy.goldmedal.repositories.CountryRepository;
import com.codecademy.goldmedal.repositories.GoldMedalRepository;
import org.apache.commons.text.WordUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/countries")
public class GoldMedalController {
    // TODO: declare references to your repositories
    private final GoldMedalRepository goldMedalRepository;
    private final CountryRepository countryRepository;

    // TODO: update your constructor to include your repositories
    public GoldMedalController(GoldMedalRepository goldMedalRepository, CountryRepository countryRepository) {
        this.goldMedalRepository = goldMedalRepository;
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public CountriesResponse getCountries(@RequestParam String sort_by, @RequestParam String ascending) {
        var ascendingOrder = ascending.equalsIgnoreCase("y");
        return new CountriesResponse(getCountrySummaries(sort_by.toLowerCase(), ascendingOrder));
    }

    @GetMapping("/{country}")
    public CountryDetailsResponse getCountryDetails(@PathVariable String country) {
        String countryName = WordUtils.capitalizeFully(country);
        return getCountryDetailsResponse(countryName);
    }

    @GetMapping("/{country}/medals")
    public CountryMedalsListResponse getCountryMedalsList(@PathVariable String country, @RequestParam String sort_by, @RequestParam String ascending) {
        String countryName = WordUtils.capitalizeFully(country);
        var ascendingOrder = ascending.equalsIgnoreCase("y");
        return getCountryMedalsListResponse(countryName, sort_by.toLowerCase(), ascendingOrder);
    }

    private CountryMedalsListResponse getCountryMedalsListResponse(String countryName, String sortBy, boolean ascendingOrder) {
        List<GoldMedal> medalsList;
        switch (sortBy) {
            case "year":
                medalsList = ascendingOrder ? this.goldMedalRepository.findAllByCountryOrderByYearAsc(countryName) : this.goldMedalRepository.findAllByCountryOrderByYearDesc(countryName);
                break;
            case "season":
                medalsList = ascendingOrder ? this.goldMedalRepository.findAllByCountryOrderBySeasonAsc(countryName) : this.goldMedalRepository.findAllByCountryOrderBySeasonDesc(countryName);
                break;
            case "city":
                medalsList = ascendingOrder ? this.goldMedalRepository.findAllByCountryOrderByCityAsc(countryName) : this.goldMedalRepository.findAllByCountryOrderByCityDesc(countryName);
                break;
            case "name":
                medalsList = ascendingOrder ? this.goldMedalRepository.findAllByCountryOrderByNameAsc(countryName) : this.goldMedalRepository.findAllByCountryOrderByNameDesc(countryName);
                break;
            case "event":
                medalsList = ascendingOrder ? this.goldMedalRepository.findAllByCountryOrderByEventAsc(countryName) : this.goldMedalRepository.findAllByCountryOrderByEventDesc(countryName);
                break;
            default:
                medalsList = new ArrayList<>();
                break;
        }

        return new CountryMedalsListResponse(medalsList);
    }

    private CountryDetailsResponse getCountryDetailsResponse(String countryName) {
        // TODO: get the country; this repository method should return a java.util.Optional
        var countryOptional = this.countryRepository.findByName(countryName);
        if (countryOptional.isEmpty()) {
            return new CountryDetailsResponse(countryName);
        }

        var country = countryOptional.get();
        // TODO: get the medal count
        var goldMedalCount = this.goldMedalRepository.findAllByOrderByYear().size();
        // TODO: get the collection of wins at the Summer Olympics, sorted by year in ascending order
        var summerWins = this.goldMedalRepository.findAllBySeasonOrderByYearAsc("Summer");
        var numberSummerWins = summerWins.size() > 0 ? summerWins.size() : null;
        // TODO: get the total number of events at the Summer Olympics
        var totalSummerEvents = this.goldMedalRepository.findAllBySeason("Summer").size();
        var percentageTotalSummerWins = totalSummerEvents != 0 && numberSummerWins != null ? (float) summerWins.size() / totalSummerEvents : null;
        var yearFirstSummerWin = summerWins.size() > 0 ? summerWins.get(0).getYear() : null;

        // TODO: get the collection of wins at the Winter Olympics
        var winterWins = this.goldMedalRepository.findAllBySeasonOrderByYearAsc("Winter");
        var numberWinterWins = winterWins.size() > 0 ? winterWins.size() : null;
        // TODO: get the total number of events at the Winter Olympics, sorted by year in ascending order
        var totalWinterEvents = this.goldMedalRepository.findAllBySeasonOrderByYearAsc("Winter").size();
        var percentageTotalWinterWins = totalWinterEvents != 0 && numberWinterWins != null ? (float) winterWins.size() / totalWinterEvents : null;
        var yearFirstWinterWin = winterWins.size() > 0 ? winterWins.get(0).getYear() : null;

        // TODO: get the number of wins by female athletes
        var numberEventsWonByFemaleAthletes = this.goldMedalRepository.findAllByGender("Men").size();
        // TODO: get the number of wins by male athletes
        var numberEventsWonByMaleAthletes = this.goldMedalRepository.findAllByGender("Women").size();

        return new CountryDetailsResponse(
                countryName,
                country.getGdp(),
                country.getPopulation(),
                goldMedalCount,
                numberSummerWins,
                percentageTotalSummerWins,
                yearFirstSummerWin,
                numberWinterWins,
                percentageTotalWinterWins,
                yearFirstWinterWin,
                numberEventsWonByFemaleAthletes,
                numberEventsWonByMaleAthletes);
    }

    private List<CountrySummary> getCountrySummaries(String sortBy, boolean ascendingOrder) {
        List<Country> countries;
        switch (sortBy) {
            case "name":
                countries = ascendingOrder ? this.countryRepository.findAllByOrderByNameAsc() : this.countryRepository.findAllByOrderByNameDesc();
                break;
            case "gdp":
                countries = ascendingOrder ? this.countryRepository.findAllByOrderByGdpAsc() : this.countryRepository.findAllByOrderByGdpDesc();
                break;
            case "population":
                countries = ascendingOrder ? this.countryRepository.findAllByOrderByPopulationAsc() : this.countryRepository.findAllByOrderByPopulationDesc();
                break;
            case "medals":
            default:
                countries = this.countryRepository.findAllByOrderByCodeAsc();
                break;
        }

        var countrySummaries = getCountrySummariesWithMedalCount(countries);

        if (sortBy.equalsIgnoreCase("medals")) {
            countrySummaries = sortByMedalCount(countrySummaries, ascendingOrder);
        }

        return countrySummaries;
    }

    private List<CountrySummary> sortByMedalCount(List<CountrySummary> countrySummaries, boolean ascendingOrder) {
        return countrySummaries.stream()
                .sorted((t1, t2) -> ascendingOrder ?
                        t1.getMedals() - t2.getMedals() :
                        t2.getMedals() - t1.getMedals())
                .collect(Collectors.toList());
    }

    private List<CountrySummary> getCountrySummariesWithMedalCount(List<Country> countries) {
        List<CountrySummary> countrySummaries = new ArrayList<>();
        for (var country : countries) {
            var goldMedalCount = this.goldMedalRepository.findAllByCountry(country.getName()).size();
            countrySummaries.add(new CountrySummary(country, goldMedalCount));
        }
        return countrySummaries;
    }
}
