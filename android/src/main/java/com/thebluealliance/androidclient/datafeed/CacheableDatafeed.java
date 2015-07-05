package com.thebluealliance.androidclient.datafeed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.database.DatabaseWriter;
import com.thebluealliance.androidclient.datafeed.maps.TeamRankExtractor;
import com.thebluealliance.androidclient.datafeed.maps.TeamStatsExtractor;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class CacheableDatafeed implements APIv2 {

    private APIv2 mRetrofitAPI;
    private APICache mAPICache;
    private DatabaseWriter mWriter;

    // TODO add callback to retrofit results to store new data in db

    @Inject
    public CacheableDatafeed(
      @Named("retrofit") APIv2 retrofitAPI,
      @Named("cache") APICache apiCache,
      DatabaseWriter writer) {
        mRetrofitAPI = retrofitAPI;
        mAPICache = apiCache;
        mWriter = writer;
    }

    @Override
    public Observable<List<Team>> fetchTeamPage(int pageNum) {
        return null;
    }

    @Override
    public Observable<Team> fetchTeam(String teamKey) {
        Observable<Team> apiData = mRetrofitAPI.fetchTeam(teamKey);
        apiData.subscribe(mWriter.teamWriter.get());
        return mAPICache.fetchTeam(teamKey).concatWith(apiData);
    }

    @Override
    public Observable<List<Event>> fetchTeamEvents(String teamKey, int year) {
        Observable<List<Event>> apiData = mRetrofitAPI.fetchTeamEvents(teamKey, year);
        apiData.subscribe(mWriter.eventListWriter.get());
        return mAPICache.fetchTeamEvents(teamKey, year).concatWith(apiData);
    }

    @Override
    public Observable<List<Award>> fetchTeamAtEventAwards(String teamKey, String eventKey) {
        Observable<List<Award>> apiData = mRetrofitAPI.fetchTeamAtEventAwards(teamKey, eventKey);
        apiData.subscribe(mWriter.awardListWriter.get());
        return mAPICache.fetchTeamAtEventAwards(teamKey, eventKey).concatWith(apiData);
    }

    @Override
    public Observable<List<Match>> fetchTeamAtEventMatches(String teamKey, String eventKey) {
        Observable<List<Match>> apiData = mRetrofitAPI.fetchTeamAtEventMatches(teamKey, eventKey);
        apiData.subscribe(mWriter.matchListWriter.get());
        return mAPICache.fetchTeamAtEventMatches(teamKey, eventKey).concatWith(apiData);
    }

    public Observable<JsonArray> fetchTeamAtEventRank(String teamKey, String eventKey) {
        TeamRankExtractor extractor = new TeamRankExtractor(teamKey);
        return fetchEventRankings(eventKey).map(extractor);
    }

    @Override
    public Observable<List<Integer>> fetchTeamYearsParticipated(String teamKey) {
        return null;
    }

    @Override
    public Observable<List<Media>> fetchTeamMediaInYear(String teamKey, int year) {
        Observable<List<Media>> apiData = mRetrofitAPI.fetchTeamMediaInYear(teamKey, year);
        apiData.subscribe(mWriter.mediaListWriter.get());
        return mAPICache.fetchTeamMediaInYear(teamKey, year).concatWith(apiData);
    }

    @Override
    public Observable<List<Event>> fetchTeamEventHistory(String teamKey) {
        return null;
    }

    @Override
    public Observable<List<Award>> fetchTeamEventAwards(String teamKey) {
        return null;
    }

    @Override
    public Observable<List<Event>> fetchEventsInYear(int year) {
        return null;
    }

    @Override
    public Observable<Event> fetchEvent(String eventKey) {
        Observable<Event> apiData = mRetrofitAPI.fetchEvent(eventKey);
        apiData.subscribe(mWriter.eventWriter.get());
        return mAPICache.fetchEvent(eventKey).concatWith(apiData);
    }

    @Override
    public Observable<List<Team>> fetchEventTeams(String eventKey) {
        Observable<List<Team>> apiData = mRetrofitAPI.fetchEventTeams(eventKey);
        apiData.subscribe(mWriter.teamListWriter.get());
        return mAPICache.fetchEventTeams(eventKey).concatWith(apiData);
    }

    @Override
    public Observable<JsonArray> fetchEventRankings(String eventKey) {
        return mAPICache.fetchEventRankings(eventKey).concatWith(
          mRetrofitAPI.fetchEventRankings(eventKey));
    }

    @Override
    public Observable<List<Match>> fetchEventMatches(String eventKey) {
        Observable<List<Match>> apiData = mRetrofitAPI.fetchEventMatches(eventKey);
        apiData.subscribe(mWriter.matchListWriter.get());
        return mAPICache.fetchEventMatches(eventKey).concatWith(apiData);
    }

    @Override
    public Observable<JsonObject> fetchEventStats(String eventKey) {
        return mAPICache.fetchEventStats(eventKey).concatWith(
          mRetrofitAPI.fetchEventStats(eventKey));
    }

    public Observable<JsonObject> fetchTeamAtEventStats(String eventKey, String teamKey) {
        TeamStatsExtractor extractor = new TeamStatsExtractor(teamKey);
        return mAPICache.fetchEventStats(eventKey).map(extractor)
          .concatWith(mRetrofitAPI.fetchEventStats(eventKey).map(extractor));
    }

    @Override
    public Observable<List<Award>> fetchEventAwards(String eventKey) {
        Observable<List<Award>> apiData = mRetrofitAPI.fetchEventAwards(eventKey);
        apiData.subscribe(mWriter.awardListWriter.get());
        return mAPICache.fetchEventAwards(eventKey).concatWith(apiData);
    }

    @Override
    public Observable<JsonObject> fetchEventDistrictPoints(String eventKey) {
        return mAPICache.fetchEventDistrictPoints(eventKey).concatWith(
          mRetrofitAPI.fetchEventDistrictPoints(eventKey));
    }

    @Override
    public Observable<List<District>> fetchDistrictList(int year) {
        return null;
    }

    @Override
    public Observable<List<Event>> fetchDistrictEvents(String districtShort, int year) {
        return null;
    }

    @Override
    public Observable<JsonArray> fetchDistrictRankings(String districtShort, int year) {
        return null;
    }

    @Override
    public Observable<Match> fetchMatch(String matchKey) {
        return null;
    }
}
