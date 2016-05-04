package es.nekosoft.myhabits.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.nekosoft.myhabits.model.GeofencesDTO;

public class ConstGeofences {

    public final static String UNI_ID_01 = "Universidad de Alicante";
    public final static GeofencesDTO uni_01 = new GeofencesDTO(UNI_ID_01, 38.384078, -0.5137296000000333, GeofencesDTO.RADIUS_NORMAL, GeofencesDTO.TYPE_UNIVERSITY);

    public final static String PARKING_ID_01 = "Behind house";
    public final static GeofencesDTO parking_01 = new GeofencesDTO(PARKING_ID_01, 38.389960565796784, -0.5164754390716553, GeofencesDTO.RADIUS_NORMAL, GeofencesDTO.TYPE_PARKING);
    public final static String PARKING_ID_02 = "University suth";
    public final static GeofencesDTO parking_02 = new GeofencesDTO(PARKING_ID_02, 38.38165810911134, -0.5100488662719727, GeofencesDTO.RADIUS_LARGE, GeofencesDTO.TYPE_PARKING);

    public final static String SUPER_ID_01 = "Mercadona rotonda";
    public final static GeofencesDTO super_01 = new GeofencesDTO(SUPER_ID_01, 38.39079463504007, -0.5195023119449615, GeofencesDTO.RADIUS_LARGE, GeofencesDTO.TYPE_SUPERMARKET);
    public final static String SUPER_ID_02 = "Hiperber";
    public final static GeofencesDTO super_02 = new GeofencesDTO(SUPER_ID_02, 38.3931428630307, -0.5183221398146998, GeofencesDTO.RADIUS_LARGE, GeofencesDTO.TYPE_SUPERMARKET);
    public final static String SUPER_ID_03 = "Dia";
    public final static GeofencesDTO super_03 = new GeofencesDTO(SUPER_ID_03, 38.393321552299305, -0.5180847643168818, GeofencesDTO.RADIUS_LARGE, GeofencesDTO.TYPE_SUPERMARKET);

    public final static String CAFEBAR_ID_01 = "Mad Pilots";
    public final static GeofencesDTO cafebar_01 = new GeofencesDTO(CAFEBAR_ID_01, 38.39076415177461, -0.5154588816913019, GeofencesDTO.RADIUS_NORMAL, GeofencesDTO.TYPE_CAFEBAR);
    public final static String CAFEBAR_ID_02 = "La Bodeguita de Triana";
    public final static GeofencesDTO cafebar_02 = new GeofencesDTO(CAFEBAR_ID_02, 38.39096650495881, -0.5199971791807911, GeofencesDTO.RADIUS_NORMAL, GeofencesDTO.TYPE_CAFEBAR);

    public final static String RESTAURANT_ID_01 = "CS2";
    public final static GeofencesDTO restaurant_01 = new GeofencesDTO(RESTAURANT_ID_01, 38.382967358637856, -0.5132152136866353, GeofencesDTO.RADIUS_LARGE, GeofencesDTO.TYPE_RESTAURANT);
    public final static String RESTAURANT_ID_02 = "100 montaditos";
    public final static GeofencesDTO restaurant_02 = new GeofencesDTO(RESTAURANT_ID_02, 38.382692978585546, -0.5065552890300751, GeofencesDTO.RADIUS_SMALL, GeofencesDTO.TYPE_RESTAURANT);
    public final static String RESTAURANT_ID_03 = "Burger King";
    public final static GeofencesDTO restaurant_03 = new GeofencesDTO(RESTAURANT_ID_03, 38.38244593052832, -0.5063232776228688, GeofencesDTO.RADIUS_SMALL, GeofencesDTO.TYPE_RESTAURANT);
    public final static String RESTAURANT_ID_04 = "CC UA";
    public final static GeofencesDTO restaurant_04 = new GeofencesDTO(RESTAURANT_ID_04, 38.382369720634316, -0.5132085084915161, GeofencesDTO.RADIUS_SMALL, GeofencesDTO.TYPE_RESTAURANT);

    public final static String LIBRARY_ID_01 = "UA main library";
    public final static GeofencesDTO library_id_01 = new GeofencesDTO(LIBRARY_ID_01, 38.38333950479041, -0.5118472874164581, GeofencesDTO.RADIUS_LARGE, GeofencesDTO.TYPE_LIBRARY);

    public final static String TRAM_ID_01 = "Universitat";
    public final static GeofencesDTO tram_01 = new GeofencesDTO(TRAM_ID_01, 38.38690738295905, -0.5107636749744415, GeofencesDTO.RADIUS_SMALL, GeofencesDTO.TYPE_TRAM);
    public final static String TRAM_ID_02 = "San Vicent del Raspeig";
    public final static GeofencesDTO tram_02 = new GeofencesDTO(TRAM_ID_02, 38.39203025695036, -0.5169796943664551, GeofencesDTO.RADIUS_SMALL, GeofencesDTO.TYPE_TRAM);

    public static final List<GeofencesDTO> geoList;
    public static final List<String> removeList;

    static
    {
        geoList = new ArrayList<GeofencesDTO>();
        geoList.add(uni_01);
        geoList.add(parking_01);
        geoList.add(parking_02);
        geoList.add(super_01);
        geoList.add(super_02);
        geoList.add(super_03);
        geoList.add(cafebar_01);
        geoList.add(cafebar_02);
        geoList.add(restaurant_01);
        geoList.add(restaurant_02);
        geoList.add(restaurant_03);
        geoList.add(restaurant_04);
        geoList.add(library_id_01);
        geoList.add(tram_01);
        geoList.add(tram_02);

        removeList = new ArrayList<String>();
        removeList.add(UNI_ID_01);
        removeList.add(SUPER_ID_01);
        removeList.add(SUPER_ID_02);
        removeList.add(SUPER_ID_03);
        removeList.add(CAFEBAR_ID_01);
        removeList.add(CAFEBAR_ID_02);
        removeList.add(RESTAURANT_ID_01);
        removeList.add(RESTAURANT_ID_02);
        removeList.add(RESTAURANT_ID_03);
        removeList.add(RESTAURANT_ID_04);
        removeList.add(LIBRARY_ID_01);
        removeList.add(TRAM_ID_01);
        removeList.add(TRAM_ID_02);
    }

}
