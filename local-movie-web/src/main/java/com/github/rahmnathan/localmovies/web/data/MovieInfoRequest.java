package com.github.rahmnathan.localmovies.web.data;

public class MovieInfoRequest {
    private int page;
    private int resultsPerPage;
    private String client;
    private String order;
    private String path;
    private String deviceId;
    private String pushToken;

    public MovieInfoRequest() {
    }

    public MovieInfoRequest(int page, int resultsPerPage, String client, String order, String path, String deviceId, String pushToken) {
        this.page = page;
        this.resultsPerPage = resultsPerPage;
        this.client = client;
        this.order = order;
        this.path = path;
        this.deviceId = deviceId;
        this.pushToken = pushToken;
    }

    public String getClient() {
        return client;
    }

    public String getOrder() {
        return order;
    }

    public int getPage() {
        return page;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public String getPath() {
        return path;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getPushToken() {
        return pushToken;
    }

    @Override
    public String toString() {
        return "MovieInfoRequest{" +
                "page=" + page +
                ", resultsPerPage=" + resultsPerPage +
                ", client='" + client + '\'' +
                ", order='" + order + '\'' +
                ", path='" + path + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", pushToken='" + pushToken + '\'' +
                '}';
    }
}
