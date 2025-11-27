package com.adrs.controller;

import com.adrs.model.District;
import com.adrs.model.Province;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for location-related operations.
 * Provides endpoints for retrieving provinces and districts.
 */
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);
    private static final String VALUE_KEY = "value";
    private static final String LABEL_KEY = "label";
    private static final String PROVINCE_KEY = "province";

    /**
     * Gets all available provinces.
     *
     * @return list of provinces with their enum names and display names
     */
    @GetMapping("/provinces")
    public ResponseEntity<List<Map<String, String>>> getProvinces() {
        logger.debug("Fetching all provinces");
        
        List<Map<String, String>> provinces = Arrays.stream(Province.values())
                .map(province -> {
                    Map<String, String> provinceMap = new HashMap<>();
                    provinceMap.put(VALUE_KEY, province.name());
                    provinceMap.put(LABEL_KEY, province.getDisplayName());
                    return provinceMap;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(provinces);
    }

    /**
     * Gets all districts that belong to a specific province.
     *
     * @param provinceName the name of the province
     * @return list of districts in the province
     */
    @GetMapping("/districts")
    public ResponseEntity<List<Map<String, String>>> getDistrictsByProvince(
            @RequestParam String provinceName) {
        logger.debug("Fetching districts for province: {}", provinceName);
        
        try {
            Province province = Province.valueOf(provinceName);
            List<Map<String, String>> districts = District.getDistrictsByProvince(province)
                    .stream()
                    .map(district -> {
                        Map<String, String> districtMap = new HashMap<>();
                        districtMap.put(VALUE_KEY, district.name());
                        districtMap.put(LABEL_KEY, district.getDisplayName());
                        return districtMap;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(districts);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid province name: {}", provinceName);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets all districts regardless of province.
     *
     * @return list of all districts
     */
    @GetMapping("/districts/all")
    public ResponseEntity<List<Map<String, String>>> getAllDistricts() {
        logger.debug("Fetching all districts");
        
        List<Map<String, String>> districts = Arrays.stream(District.values())
                .map(district -> {
                    Map<String, String> districtMap = new HashMap<>();
                    districtMap.put(VALUE_KEY, district.name());
                    districtMap.put(LABEL_KEY, district.getDisplayName());
                    districtMap.put(PROVINCE_KEY, district.getProvince().name());
                    return districtMap;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(districts);
    }
}
