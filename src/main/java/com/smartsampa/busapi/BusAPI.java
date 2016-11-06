package com.smartsampa.busapi;

import com.smartsampa.gtfsapi.GtfsAPI;
import com.smartsampa.gtfsapi.GtfsDownloader;
import com.smartsampa.gtfsapi.GtfsHandler;
import com.smartsampa.gtfswrapper.GtfsAPIFacade;
import com.smartsampa.olhovivoapi.OlhovivoAPI;
import com.smartsampa.shapefileapi.ShapefileAPI;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ruan0408 on 30/04/2016.
 */
public final class BusAPI {

    private static final Pattern TRIP_ID_PATTERN = Pattern.compile("(\\w+-\\d+)-([12])");
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    private static String sptransLogin;
    private static String sptransPassword;
    private static String olhovivoKey;

    private static StaticAPI staticAPI;
    private static RealTimeAPI realTimeAPI;
    private BusAPI() {}

    public static void initialize() {
        GtfsDownloader gtfsDownloader = new GtfsDownloader(sptransLogin, sptransPassword);
        GtfsHandler gtfsHandler = new GtfsHandler(gtfsDownloader);
        GtfsAPI gtfs = new GtfsAPI(gtfsHandler.getGtfsDao());
        GtfsAPIFacade gtfsAPIFacade = new GtfsAPIFacade(gtfs);

        //TODO put this in the same architecture as the others
        ShapefileAPI shapefile = new ShapefileAPI("faixa_onibus/sirgas_faixa_onibus.shp");

        OlhovivoAPI olhovivo = new OlhovivoAPI(olhovivoKey);
        authenticateAtOlhovivoEveryTenMinutes(olhovivo);

        Provider.setGtfsAPIFacade(gtfsAPIFacade);
        Provider.setOlhovivoAPI(olhovivo);
        Provider.setShapefileAPI(shapefile);
        staticAPI = new StaticAPI();
        realTimeAPI = new RealTimeAPI();
    }

    public static void setSptransLogin(String login) {sptransLogin = login;}

    public static void setSptransPassword(String password) {sptransPassword = password;}

    public static void setOlhovivoKey(String key) {olhovivoKey = key;}

    public static Map<Trip, List<PredictedBus>> getPredictionsPerTrip(Stop stop) {
        return realTimeAPI.getPredictionsPerTrip(stop);
    }

    public static Map<Stop, List<PredictedBus>> getPredictionsPerStop(Trip trip) {
        return realTimeAPI.getPredictionsPerStop(trip);
    }

    public static List<PredictedBus> getPredictions(Trip trip, Stop stop) {
        return realTimeAPI.getPredictions(trip, stop);
    }

    public static Set<Bus> getAllRunningBuses(Trip trip) {
        return realTimeAPI.getAllRunningBuses(trip);
    }

    public static Set<Trip> getTripsByTerm(String term) { return staticAPI.getTripsByTerm(term); }

    public static Set<Stop> getStopsByTerm(String term) { return staticAPI.getStopsByTerm(term); }

    public static Stop getStopById(int id) { return staticAPI.getStopById(id);}

    public static Trip getTripById(String id) {
        String numberSign = getNumberSignFromTripId(id);
        Heading heading = getHeadingFromTripId(id);
        return staticAPI.getTrip(numberSign, heading);
    }

    public static Set<Trip> getTripsFromStop(Stop stop) { return staticAPI.getTripsFromStop(stop); }

    //TODO make this return complete stops
    public static List<Stop> getStopsFromTrip(Trip trip) { return staticAPI.getStopsFromTrip(trip); }

    public static List<Corridor> getAllCorridors() { return staticAPI.getAllCorridors(); }

    public static Corridor getCorridorByTerm(String term) { return staticAPI.getCorridorByTerm(term); }

    public static List<Stop> getStopsFromCorridor(Corridor corridor) { return staticAPI.getStopsFromCorridor(corridor); }

    public static List<BusLane> getAllBusLanes() { return staticAPI.getAllBusLanes(); }

    public static List<BusLane> getBusLanesByTerm(String term) { return staticAPI.getBusLanesByTerm(term); }

    private static String getNumberSignFromTripId(String tripId) {
        Matcher matcher = TRIP_ID_PATTERN.matcher(tripId);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static Heading getHeadingFromTripId(String tripId) {
        Matcher matcher = TRIP_ID_PATTERN.matcher(tripId);
        if (matcher.find()) {
            int heading = Integer.parseInt(matcher.group(2));
            return Heading.getHeadingFromInt(heading);
        }
        return null;
    }

    private static void authenticateAtOlhovivoEveryTenMinutes(OlhovivoAPI olhovivoAPI) {
        int initialDelay = 0;
        int interval = 10;
        scheduler.scheduleAtFixedRate(() -> {
            boolean result = olhovivoAPI.authenticate();
            System.out.println("Authentication is: " + result);
        }, initialDelay, interval, TimeUnit.MINUTES);
    }
}
