package com.smartsampa.gtfsapi;

import com.smartsampa.utils.APIConnectionException;
import com.smartsampa.utils.SmartSampaDir;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.services.GtfsDao;
import org.onebusaway.gtfs.services.GtfsMutableDao;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

/**
 * Created by ruan0408 on 11/03/2016.
 */
public class GtfsHandler {

    private static final int UPDATE_INTERVAL_IN_DAYS = 3;//TODO reset gtfs download days to 3
    private static final long UPDATE_INTERVAL_IN_MILLIS = Duration.ofDays(UPDATE_INTERVAL_IN_DAYS).toMillis();
    private static final String GTFS_DIR_NAME = "gtfs.zip";
    private static final String GTFS_PATH = SmartSampaDir.getPath()+"/"+GTFS_DIR_NAME;

    private GtfsDownloader gtfsDownloader;

    public GtfsHandler(GtfsDownloader downloader) {
        gtfsDownloader = downloader;
    }

    public GtfsDao getGtfsDao() {
        ensureGtfsIsUsable();
        return loadGtfsAndGetDataAcessor();
    }

    private GtfsDao loadGtfsAndGetDataAcessor() {
        try {

            GtfsReader gtfsReader = new GtfsReader();
            GtfsMutableDao dao = new GtfsDaoImpl();

            gtfsReader.setInputLocation(new File(GTFS_PATH));

            gtfsReader.setEntityStore(dao);

            gtfsReader.run();
            return dao;
        } catch (Exception e) {
            e.printStackTrace();
            throw new APIConnectionException("The "+GTFS_DIR_NAME+" was not found");
        }
    }

    private void ensureGtfsIsUsable() {
        try {
            if (!SmartSampaDir.hasSubDir(GTFS_DIR_NAME) || gtfsDirNeedsUpdate())
                gtfsDownloader.downloadToDir(GTFS_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            if (!SmartSampaDir.hasSubDir(GTFS_DIR_NAME))
                throw new APIConnectionException("Couldn't download the GTFS files");
        }
    }

    private boolean gtfsDirNeedsUpdate() {
        File gtfsDir = new File(GTFS_PATH);
        long timeElapsedSinceModificationInMillis =
                System.currentTimeMillis() - gtfsDir.lastModified();

        return timeElapsedSinceModificationInMillis > UPDATE_INTERVAL_IN_MILLIS;
    }
}
