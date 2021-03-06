/*
 * Copyright (C) 2014- k3b
 * 
 * This file is part of android.calendar.ics.adapter.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package de.k3b.android.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.k3b.android.Global;
import de.k3b.util.DateTimeUtil;

/**
 * Simulates the api of an android calendar content provider that is compatibel with Samsung-Android2.2 Calendar.
 * Used internally for testing on emulator that has no calendar-app and no calendar-provder.<br/><br/>
 * 
 * @author k3b
 */
public class ACalendarMock extends SQLiteOpenHelper {

    public static final int CURRENT_DB_VERSION = 3;

    /**
	 * Opens Mock-DB. Creates it if it does not exist.
	 */
	public ACalendarMock(final Context context) {
		super(context, "calendar.db", null, CURRENT_DB_VERSION);
		context.getDir("databases", Context.MODE_PRIVATE); // create dir if it does not exist
	}

	/**
	 * Creates mock database with events-table and sample data using the same columns that my android2.2 has.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
        if (Global.debugEnabled) {
            Log.d(ACalendar2IcsEngine.TAG,"Creating mock database");

        }
		db.execSQL("CREATE TABLE Events (_id INTEGER PRIMARY KEY," +
				"_sync_account TEXT,_sync_account_type TEXT,_sync_id TEXT,_sync_version TEXT,_sync_time TEXT,_sync_local_id INTEGER,_sync_dirty INTEGER,_sync_mark INTEGER," +
				"calendar_id INTEGER NOT NULL,htmlUri TEXT,title TEXT,eventLocation TEXT,description TEXT,eventStatus INTEGER,selfAttendeeStatus INTEGER NOT NULL DEFAULT 0," +
				"commentsUri TEXT,dtstart INTEGER,dtend INTEGER,eventTimezone TEXT,duration TEXT," +
				"allDay INTEGER NOT NULL DEFAULT 0,visibility INTEGER NOT NULL DEFAULT 0," +
				"transparency INTEGER NOT NULL DEFAULT 0,hasAlarm INTEGER NOT NULL DEFAULT 0," +
				"hasExtendedProperties INTEGER NOT NULL DEFAULT 0,rrule TEXT,rdate TEXT,exrule TEXT,exdate TEXT," +
				"originalEvent TEXT,originalInstanceTime INTEGER,originalAllDay INTEGER,lastDate INTEGER," +
				"hasAttendeeData INTEGER NOT NULL DEFAULT 0,guestsCanModify INTEGER NOT NULL DEFAULT 0," +
				"guestsCanInviteOthers INTEGER NOT NULL DEFAULT 1,guestsCanSeeGuests INTEGER NOT NULL DEFAULT 1," +
				"organizer STRING,deleted INTEGER NOT NULL DEFAULT 0,dtstart2 INTEGER,dtend2 INTEGER,eventTimezone2 TEXT,syncAdapterData TEXT)");
		
		db.execSQL("INSERT INTO Events(_ID, DTSTART,TITLE,DESCRIPTION,eventLocation,ORGANIZER,CALENDAR_ID) " +
				"VALUES(1,152,'TITLE','DESCRIPTION','eventLocation','ORGANIZER',1)");

		/*
		db.execSQL("CREATE TABLE Attendees (_id INTEGER PRIMARY KEY,event_id INTEGER,attendeeName TEXT,attendeeEmail TEXT,attendeeStatus INTEGER,attendeeRelationship INTEGER,attendeeType INTEGER)");
		db.execSQL("CREATE TABLE CalendarAlerts (_id INTEGER PRIMARY KEY,event_id INTEGER,begin INTEGER NOT NULL,end INTEGER NOT NULL,alarmTime INTEGER NOT NULL,creationTime INTEGER NOT NULL,receivedTime INTEGER NOT NULL,notifyTime INTEGER NOT NULL,state INTEGER NOT NULL,minutes INTEGER,UNIQUE (alarmTime, begin, event_id))");
		db.execSQL("CREATE TABLE CalendarCache (_id INTEGER PRIMARY KEY,key TEXT NOT NULL,value TEXT)");
		db.execSQL("CREATE TABLE CalendarMetaData (_id INTEGER PRIMARY KEY,localTimezone TEXT,minInstance INTEGER,maxInstance INTEGER)");
		db.execSQL("CREATE TABLE Calendars (_id INTEGER PRIMARY KEY,_sync_account TEXT,_sync_account_type TEXT,_sync_id TEXT,_sync_version TEXT,_sync_time TEXT,_sync_local_id INTEGER,_sync_dirty INTEGER,_sync_mark INTEGER,url TEXT,name TEXT,displayName TEXT,hidden INTEGER NOT NULL DEFAULT 0,color INTEGER,access_level INTEGER,selected INTEGER NOT NULL DEFAULT 1,sync_events INTEGER NOT NULL DEFAULT 0,location TEXT,timezone TEXT,ownerAccount TEXT, organizerCanRespond INTEGER NOT NULL DEFAULT 1)");
		db.execSQL("CREATE TABLE EventsRawTimes (_id INTEGER PRIMARY KEY,event_id INTEGER NOT NULL,dtstart2445 TEXT,dtend2445 TEXT,originalInstanceTime2445 TEXT,lastDate2445 TEXT,UNIQUE (event_id))");
		db.execSQL("CREATE TABLE ExtendedProperties (_id INTEGER PRIMARY KEY,event_id INTEGER,name TEXT,value TEXT)");
		db.execSQL("CREATE TABLE Instances (_id INTEGER PRIMARY KEY,event_id INTEGER,begin INTEGER,end INTEGER,startDay INTEGER,endDay INTEGER,startMinute INTEGER,endMinute INTEGER,UNIQUE (event_id, begin, end))");
		db.execSQL("CREATE TABLE _sync_state (_id INTEGER PRIMARY KEY,account_name TEXT NOT NULL,account_type TEXT NOT NULL,data TEXT,UNIQUE(account_name, account_type))");
		db.execSQL("CREATE TABLE _sync_state_metadata (version INTEGER)");
		db.execSQL("CREATE TABLE android_metadata (locale TEXT)");
		db.execSQL("CREATE INDEX attendeesEventIdIndex ON Attendees (event_id)");
		db.execSQL("CREATE INDEX calendarAlertsEventIdIndex ON CalendarAlerts (event_id)");
		db.execSQL("CREATE INDEX eventSyncAccountAndIdIndex ON Events (_sync_account_type, _sync_account, _sync_id)");
		db.execSQL("CREATE INDEX eventsCalendarIdIndex ON Events (calendar_id)");
		db.execSQL("CREATE INDEX extendedPropertiesEventIdIndex ON ExtendedProperties (event_id)");
		db.execSQL("CREATE INDEX instancesStartDayIndex ON Instances (startDay)");
		db.execSQL("CREATE INDEX remindersEventIdIndex ON Reminders (event_id)");
		db.execSQL("CREATE VIEW view_events AS SELECT Events._id AS _id,htmlUri,title,description,eventLocation,eventStatus,selfAttendeeStatus,commentsUri,dtstart,dtend,duration,eventTimezone,allDay,visibility,timezone,selected,access_level,transparency,color,hasAlarm,hasExtendedProperties,rrule,rdate,exrule,exdate,originalEvent,originalInstanceTime,originalAllDay,lastDate,hasAttendeeData,calendar_id,guestsCanInviteOthers,guestsCanModify,guestsCanSeeGuests,organizer,deleted,Events._sync_id AS _sync_id,Events._sync_version AS _sync_version,Events._sync_dirty AS _sync_dirty,Events._sync_account AS _sync_account,Events._sync_account_type AS _sync_account_type,Events._sync_time AS _sync_time,Events._sync_local_id AS _sync_local_id,Events._sync_mark AS _sync_mark,url,ownerAccount,sync_events FROM Events JOIN Calendars ON (Events.calendar_id=Calendars._id)");
		db.execSQL("CREATE TRIGGER calendar_cleanup DELETE ON Calendars BEGIN DELETE FROM Events WHERE calendar_id = old._id;END");
		db.execSQL("CREATE TRIGGER events_insert AFTER INSERT ON Events BEGIN UPDATE Events SET _sync_account=(SELECT _sync_account FROM Calendars WHERE Calendars._id=new.calendar_id),_sync_account_type=(SELECT _sync_account_type FROM Calendars WHERE Calendars._id=new.calendar_id) WHERE Events._id=new._id;END");
		db.execSQL("CREATE TRIGGER events_cleanup_delete DELETE ON Events BEGIN DELETE FROM Instances WHERE event_id = old._id;DELETE FROM EventsRawTimes WHERE event_id = old._id;DELETE FROM Attendees WHERE event_id = old._id;DELETE FROM Reminders WHERE event_id = old._id;DELETE FROM CalendarAlerts WHERE event_id = old._id;DELETE FROM ExtendedProperties WHERE event_id = old._id;END");
		*/

        onUpgrade(db, 0, CURRENT_DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (Global.debugEnabled) {
            Log.d(ACalendar2IcsEngine.TAG,"Upradeing mock database from "+oldVersion+" to "+newVersion );
        }
		if (oldVersion < 2) {
			db.execSQL("CREATE TABLE Reminders (_id INTEGER PRIMARY KEY,event_id INTEGER,minutes INTEGER,method INTEGER NOT NULL DEFAULT 0)");
			
			db.execSQL("update Events set hasAlarm = 1 where _ID = 1");
			
			db.execSQL("INSERT INTO Reminders(_ID, event_id,minutes,method) " +
				"VALUES(1,1,30,1)");
			db.execSQL("INSERT INTO Reminders(_ID, event_id,minutes,method) " +
				"VALUES(2,1,120,1)");
		}

        if (oldVersion < 3) {
            db.execSQL("update Events " +
                    "set eventTimezone = 'Australia/Sydney', duration='P1D', " +
                    "dtstart='"+ DateTimeUtil.createDate(2000, 5, 1, 12, 34, 56).getTime() + "'," +
                    "dtend='"+ DateTimeUtil.createDate(2000, 5, 1, 17,12,34).getTime() + "'," +
                    "rrule = 'FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU', rdate ='19610901T045612Z,19630901T045612Z'," +
                    "exdate='19710901T045612Z,19730901T045612Z', organizer='mailto:max.mustermann@url.org'" +
                    "where _ID = 1");
        }
	}
}
