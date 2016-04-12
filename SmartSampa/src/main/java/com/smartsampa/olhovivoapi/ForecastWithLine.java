package com.smartsampa.olhovivoapi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by ruan0408 on 13/02/2016.
 */
public class ForecastWithLine {

    private String currentTime;
    private BusStopNow[] busStops;

    @JsonCreator
    protected ForecastWithLine(@JsonProperty("hr") String currentTime,
                               @JsonProperty("ps") BusStopNow[] busStops) {

        this.currentTime = currentTime;
        this.busStops = busStops;
    }

    /**
     * @return The time this response was sent.
     */
    public String getCurrentTime() {
        return currentTime;
    }

    /**
     * @return The current situation of the bus stops on the related bus line.
     */
    public BusStopNow[] getBusStops() {
        return busStops;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("currentTime", currentTime)
                .append("busStops", busStops)
                .toString();
    }
}
