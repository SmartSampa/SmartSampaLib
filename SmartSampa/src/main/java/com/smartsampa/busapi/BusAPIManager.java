package com.smartsampa.busapi;

import com.smartsampa.gtfsapi.GtfsAPI;
import com.smartsampa.gtfsapi.GtfsDownloader;
import com.smartsampa.gtfsapi.GtfsHandler;
import com.smartsampa.olhovivoapi.OlhoVivoAPI;

/**
 * Created by ruan0408 on 12/03/2016.
 */
public class BusAPIManager {

    public static GtfsAPI gtfs;
    public static OlhoVivoAPI olhovivo;

    private static final BusAPIManager ourInstance = new BusAPIManager();

    private static String sptransLogin;
    private static String sptransPassword;
    private static String olhovivoKey;

    public static BusAPIManager getInstance() {
        return ourInstance;
    }

    private BusAPIManager() {}

    public void init() {
        GtfsDownloader gtfsDownloader = new GtfsDownloader(sptransLogin, sptransPassword);
        GtfsHandler gtfsHandler = new GtfsHandler(gtfsDownloader);
        gtfs = new GtfsAPI(gtfsHandler.getGtfsAcessor());
        olhovivo = new OlhoVivoAPI(olhovivoKey);
        olhovivo.authenticate();
    }

    public BusAPIManager setSptransLogin(String login) {
        sptransLogin = login;
        return this;
    }

    public BusAPIManager setSptransPassword(String password) {
        sptransPassword = password;
        return this;
    }

    public BusAPIManager setOlhovivoKey(String key) {
        olhovivoKey = key;
        return this;
    }

    public String getSptransLogin() {
        return sptransLogin;
    }

    public String getSptransPassword() {
        return sptransPassword;
    }

    public String getOlhovivoKey() {
        return olhovivoKey;
    }
}
