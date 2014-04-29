package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.SimpleTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Date;

/**
 * File created by phil on 4/28/14.
 */
public class Database extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 0;
    public static final String DATABASE_NAME = "the-blue-alliance-android-database",

        TABLE_AWARDS                    = "awards",
        TABLE_EVENTS                    = "events",
        TABLE_MATCHES                   = "matches",
        TABLE_MEDIA                     = "media",
        TABLE_TEAMS                     = "teams";

    protected SQLiteDatabase db;
    private static Database instance;

    public Database(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        db = getWritableDatabase();
    }

    /**
     * USE THIS METHOD TO GAIN DATABASE REFERENCES!!11!!!
     * This makes sure that db accesses stay thread-safe
     * (which becomes important with multiple AsyncTasks working simultaneously).
     * Should work, per http://touchlabblog.tumblr.com/post/24474750219/single-sqlite-connection
     * @param context Context used to create Database object, if necessary
     * @return Your synchronized reference to use.
     */
    public static synchronized Database getInstance(Context context){
        if(instance == null){
            instance = new Database(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_AWARDS = "CREATE TABLE " + TABLE_AWARDS + "("
                + Awards.KEY        + " TEXT PRIMARY KEY, "
                + Awards.EVENTKEY   + " TEXT, "
                + Awards.NAME       + " TEXT, "
                + Awards.YEAR       + " INTEGER, "
                + Awards.TYPE       + " INTEGER, "
                + Awards.WINNER     + " TEXT, "
                + Awards.LASTUPDATE + " TIMESTAMP "
                + ")";
        db.execSQL(CREATE_AWARDS);

        String CREATE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + "("
                + Events.KEY        + " TEXT PRIMARY KEY, "
                + Events.NAME       + " TEXT, "
                + Events.SHORTNAME  + " TEXT, "
                + Events.ABBREVIATION+" TEXT, "
                + Events.TYPE       + " INTEGER, "
                + Events.DISTRICT   + " INTEGER, "
                + Events.START      + " TIMESTAMP, "
                + Events.END        + " TIMESTAMP, "
                + Events.LOCATION   + " TEXT, "
                + Events.OFFICIAL   + " INTEGER, "
                + Events.WEBSITE    + " TEXT , "
                + Events.RANKINGS   + " TEXT, "
                + Events.STATS      + " TEXT, "
                + Events.LASTUPDATE + " TIMESTAMP"
                + ")";
        db.execSQL(CREATE_EVENTS);

        String CREATE_MATCHES = "CREATE TABLE " + TABLE_MATCHES + "("
                + Matches.KEY       + " TEXT PRIMARY KEY,"
                + Matches.TYPE      + " INTEGER, "
                + Matches.MATCHNUM  + " INTEGER, "
                + Matches.SETNUM    + " INTEGER, "
                + Matches.ALLIANCES + " TEXT, "
                + Matches.TIME      + " TEXT, "
                + Matches.VIDEOS    + " TEXT, "
                + Matches.LASTUPDATE+ " TIMESTAMP"
                + ")";
        db.execSQL(CREATE_MATCHES);

        String CREATE_MEDIAS = "CREATE TABLE " + TABLE_MEDIA + "("
                + Medias.TYPE       + " INTEGER, "
                + Medias.FOREIGNKEY + " TEXT, "
                + Medias.DETAILS    + " TEXT, "
                + Medias.YEAR       + " INTEGER, "
                + Medias.TEAMKEY    + " TEXT, "
                + Medias.LASTUPDATE + " TIMESTAMP"
                + ")";
        db.execSQL(CREATE_MEDIAS);

        String CREATE_TEAMS = "CREATE TABLE " + TABLE_TEAMS + "("
                + Teams.KEY         + " TEXT PRIMARY KEY, "
                + Teams.NAME        + " TEXT, "
                + Teams.NICKNAME    + " TEXT, "
                + Teams.LOCATION    + " TEXT, "
                + Teams.EVENTS      + " TEXT, "
                + Teams.WEBSITE     + " TEXT, "
                + Teams.LASTUPDATE  + " TIMESTAMP"
                + ")";
        db.execSQL(CREATE_TEAMS);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO implement some upgrade code
    }

    public class Awards implements DatabaseTable<Award>{

        /* Awards are not yet implemented yet in the API.
         * So we can hang off in implementing this class, for now
         */

        public static final String  KEY                 = "awardKey",       //text
                                    NAME                = "awardName",      //text
                                    YEAR                = "year",           //int
                                    EVENTKEY            = "eventKey",       //text
                                    TYPE                = "awardType",      //int (from award list enum)
                                    WINNER              = "awardWinner",    //string (JsonArray.toString)
                                    LASTUPDATE          = "lastUpdated";    //timestamp

        @Override
        public long add(Award in) {
            return 0;
        }

        @Override
        public Award get(String key) {
            return null;
        }

        @Override
        public boolean exists(String key) {
            return false;
        }

        @Override
        public int update(Award in) {
            return 0;
        }
    }
    public class Events implements DatabaseTable<Event>{
        public static final String  KEY                 = "eventKey",       //text
                                    NAME                = "eventName",      //text
                                    SHORTNAME           = "eventShort",     //text
                                    ABBREVIATION        = "eventAbbrev",    //text
                                    TYPE                = "eventType",      //int (from event types enum)
                                    DISTRICT            = "eventDistrict",  //int (from district enum)
                                    START               = "eventStart",     //timestamp
                                    END                 = "eventEnd",       //timestamp
                                    LOCATION            = "location",       //text
                                    OFFICIAL            = "eventOfficial",  //int(1) - boolean representation
                                    WEBSITE             = "eventWebsite",   //text
                                    WEBCASTS            = "eventWebcast",   //text (JsonArray.toString)
                                    RANKINGS            = "eventRankings",  //text (JsonArray.toString)
                                    STATS               = "eventStats",     //text (JsonArray.toString)
                                    LASTUPDATE          = "lastUpdated";    //timestamp

        @Override
        public long add(Event in) {
            if(!exists(in.getEventKey())){
                return db.insert(TABLE_EVENTS, null, in.getParams());
            }else{
                return update(in);
            }
        }
        @Override
        public Event get(String key) {
            Cursor cursor = db.query(TABLE_EVENTS,new String[]{KEY,NAME,SHORTNAME,ABBREVIATION,TYPE,DISTRICT,START,END,LOCATION,OFFICIAL,WEBSITE,WEBCASTS,RANKINGS,STATS},
                    KEY + "=?",new String[]{key},null,null,null,null);
            if(cursor != null && cursor.moveToFirst()){
                Event event = new Event();
                event.setEventKey(cursor.getString(0));
                event.setEventName(cursor.getString(1));
                event.setShortName(cursor.getString(2));
                event.setAbbreviation(cursor.getString(3));
                event.setEventType(Event.TYPE.values()[cursor.getInt(4)]);
                event.setEventDistrict(Event.DISTRICT.values()[cursor.getInt(5)]);
                event.setStartDate(new Date(cursor.getLong(6)));
                event.setEndDate(new Date(cursor.getLong(7)));
                event.setLocation(cursor.getString(8));
                event.setOfficial(cursor.getInt(9) == 1);
                event.setWebsite(cursor.getString(10));
                event.setWebcasts(JSONManager.getasJsonArray(cursor.getString(11)));
                event.setStats(JSONManager.getasJsonObject(cursor.getString(12)));
                event.setLastUpdated(cursor.getLong(13));

                return event;
            }else{
                Log.w(Constants.LOG_TAG,"Failed to find event in database with key "+key);
                return null;
            }
        }
        /* Only get some of the details for this event */
        public SimpleEvent getSimple(String key){
            Cursor cursor = db.query(TABLE_EVENTS,new String[]{KEY,NAME,TYPE,DISTRICT,START,END,LOCATION,OFFICIAL,STATS},
                    KEY + "=?",new String[]{key},null,null,null,null);
            if(cursor != null && cursor.moveToFirst()){
                SimpleEvent event = new SimpleEvent();
                event.setEventKey(cursor.getString(0));
                event.setEventName(cursor.getString(1));
                event.setEventType(Event.TYPE.values()[cursor.getInt(2)]);
                event.setEventDistrict(Event.DISTRICT.values()[cursor.getInt(3)]);
                event.setStartDate(new Date(cursor.getLong(4)));
                event.setEndDate(new Date(cursor.getLong(5)));
                event.setLocation(cursor.getString(6));
                event.setOfficial(cursor.getInt(7) == 1);
                event.setLastUpdated(cursor.getLong(8));

                return event;
            }else{
                Log.w(Constants.LOG_TAG,"Failed to find event in database with key "+key);
                return null;
            }
        }
        public ArrayList<Event> getAll(int week){
            //return all events happening during the given competition week
            //TODO implement this (and think of a good way to calculate the time bounds for a given competition week (week of year - 8)
            return null;
        }
        @Override
        public boolean exists(String key) {
            Cursor cursor = db.query(TABLE_EVENTS,new String[]{KEY},KEY + "=?", new String[]{key},null,null,null,null);
            return cursor != null && cursor.moveToFirst();
        }
        @Override
        public int update(Event in) {
            return db.update(TABLE_EVENTS,in.getParams(),KEY + "=?",new String[]{in.getEventKey()});
        }
    }
    public class Matches implements DatabaseTable<Match>{
        public static final String  KEY                 = "matchKey",       //text
                                    TYPE                = "matchType",      //int (from match type enum)
                                    MATCHNUM            = "matchNumber",    //int
                                    SETNUM              = "matchSet",       //int
                                    ALLIANCES           = "alliances",      //text (flattened json dict of some sort, depends on year)
                                    TIME                = "matchTime",      //time string from schedule
                                    VIDEOS              = "matchVideo",     //text (flattened json array)
                                    LASTUPDATE          = "lastUpdated";   //timestamp

        @Override
        public long add(Match in) {
            if(!exists(in.getKey())){
                return db.insert(TABLE_MATCHES, null, in.getParams());
            }else{
                return update(in);
            }
        }
        @Override
        public Match get(String key) {
            Cursor cursor = db.query(TABLE_MATCHES,new String[]{KEY,TYPE,MATCHNUM,SETNUM,ALLIANCES,TIME,VIDEOS,LASTUPDATE},
                    KEY + "=?",new String[]{key},null,null,null,null);
            if(cursor != null && cursor.moveToFirst()){
                Match match = new Match();
                match.setKey(cursor.getString(0));
                match.setType(Match.TYPE.values()[cursor.getInt(1)]);
                match.setMatchNumber(cursor.getInt(2));
                match.setSetNumber(cursor.getInt(3));
                match.setAlliances(JSONManager.getasJsonObject(cursor.getString(4)));
                match.setTime(cursor.getString(5));
                match.setVideos(JSONManager.getasJsonObject(cursor.getString(6)));
                match.setLastUpdated(cursor.getLong(7));

                return match;
            }else{
                Log.w(Constants.LOG_TAG,"Failed to find match in database with key "+key);
                return null;
            }
        }
        @Override
        public boolean exists(String key) {
            Cursor cursor = db.query(TABLE_MATCHES,new String[]{KEY},KEY + "=?", new String[]{key},null,null,null,null);
            return cursor != null && cursor.moveToFirst();
        }
        @Override
        public int update(Match in) {
            return db.update(TABLE_MATCHES,in.getParams(),KEY + "=?",new String[]{in.getKey()});
        }
    }
    public class Medias implements DatabaseTable<Media>{

        /* NOT YET IMPLEMENTED IN API
         * Holding off until it is...
         */

        public static final String  TYPE                = "mediaType",      //int (from enum)
                                    FOREIGNKEY          = "mediaKey",       //text
                                    DETAILS             = "details",        //text, json dict of details
                                    YEAR                = "year",           //int
                                    TEAMKEY             = "teamKey",        //text
                                    LASTUPDATE          = "lastUpdated";    //timestamp

        @Override
        public long add(Media in) {
            return 0;
        }

        @Override
        public Media get(String key) {
            return null;
        }

        @Override
        public boolean exists(String key) {
            return false;
        }

        @Override
        public int update(Media in) {
            return 0;
        }
    }
    public class Teams implements DatabaseTable<Team>{
        public static final String  KEY                 = "teamKey",        //text
                                    NAME                = "teamName",       //text (full team name)
                                    NICKNAME            = "teamNick",       //text (team nickname)
                                    LOCATION            = "location",       //text
                                    EVENTS              = "teamEvents",     //text (json array of events, with dict of matches competed in)
                                    WEBSITE             = "teamWebsite",    //text
                                    LASTUPDATE          = "lastUpdated";    //timestamp

        @Override
        public long add(Team in) {
            if(!exists(in.getTeamKey())){
                return db.insert(TABLE_TEAMS,null,in.getParams());
            }else{
                return update(in);
            }
        }
        @Override
        public Team get(String key) {
            Cursor cursor = db.query(TABLE_MATCHES, new String[]{KEY, NAME, NICKNAME, LOCATION, WEBSITE, EVENTS, LASTUPDATE},
                    KEY + "=?",new String[]{key},null, null, null, null);
            if(cursor != null && cursor.moveToFirst()){
                Team team = new Team();
                team.setTeamKey(cursor.getString(0));
                team.setFullName(cursor.getString(1));
                team.setNickname(cursor.getString(2));
                team.setLocation(cursor.getString(3));
                team.setWebsite(cursor.getString(4));
                team.setEvents(JSONManager.getasJsonArray(cursor.getString(5)));
                team.setLastUpdated(cursor.getLong(6));

                return team;
            }else{
                Log.w(Constants.LOG_TAG,"Failed to find team in database with key "+key);
                return null;
            }
        }
        public SimpleTeam getSimple(String key){
            Cursor cursor = db.query(TABLE_MATCHES, new String[]{KEY, NAME, NICKNAME, LOCATION, LASTUPDATE},
                    KEY + "=?",new String[]{key},null, null, null, null);
            if(cursor != null && cursor.moveToFirst()){
                SimpleTeam team = new SimpleTeam();
                team.setTeamKey(cursor.getString(0));
                team.setFullName(cursor.getString(1));
                team.setNickname(cursor.getString(2));
                team.setLocation(cursor.getString(3));
                team.setLastUpdated(cursor.getLong(4));

                return team;
            }else{
                Log.w(Constants.LOG_TAG,"Failed to find team in database with key "+key);
                return null;
            }
        }
        @Override
        public boolean exists(String key) {
            Cursor cursor = db.query(TABLE_TEAMS,new String[]{KEY},KEY + "=?",new String[]{key},null,null,null,null);
            return cursor != null && cursor.moveToFirst();
        }
        @Override
        public int update(Team in) {
            return db.update(TABLE_TEAMS,in.getParams(),KEY + "=?",new String[]{in.getTeamKey()});
        }
    }
}
