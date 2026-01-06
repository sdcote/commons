/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


/**
 * Class DateUtil
 */
public class DateUtil {

  /** Field SECONDS_PER_MINUTE */
  public static final long SECONDS_PER_MINUTE = 60 * 60;

  /** Field SECONDS_PER_HOUR */
  public static final long SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60;

  /** Field SECONDS_PER_DAY */
  public static final long SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;

  /** Field SECONDS_PER_WEEK */
  public static final long SECONDS_PER_WEEK = SECONDS_PER_DAY * 7;

  private static final TimeZone GMT = TimeZone.getTimeZone( "GMT" );
  private static final String fulldays[] = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

  private static final String days[] = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

  private static final String fullmonths[] = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

  private static final String months[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

  private static DecimalFormat MILLIS = new DecimalFormat( "000" );




  /**
   * Private constructor because everything is static
   */
  private DateUtil() {}




  /**
   * Method RFC822Format
   *
   * <p>From RFC 822:
   * <pre>
   * 5.  DATE AND TIME SPECIFICATION
   *
   * 5.1.  SYNTAX
   *
   * date-time   =  [ day &quot;,&quot; ] date time        ; dd mm yy
   *                                             ;  hh:mm:ss zzz
   *
   * day         =  &quot;Mon&quot;  / &quot;Tue&quot; /  &quot;Wed&quot;  / &quot;Thu&quot;
   *             /  &quot;Fri&quot;  / &quot;Sat&quot; /  &quot;Sun&quot;
   *
   * date        =  1*2DIGIT month 2DIGIT        ; day month year
   *                                             ;  e.g. 20 Jun 82
   *
   * month       =  &quot;Jan&quot;  /  &quot;Feb&quot; /  &quot;Mar&quot;  /  &quot;Apr&quot;
   *             /  &quot;May&quot;  /  &quot;Jun&quot; /  &quot;Jul&quot;  /  &quot;Aug&quot;
   *             /  &quot;Sep&quot;  /  &quot;Oct&quot; /  &quot;Nov&quot;  /  &quot;Dec&quot;
   *
   * time        =  hour zone                    ; ANSI and Military
   *
   * hour        =  2DIGIT &quot;:&quot; 2DIGIT [&quot;:&quot; 2DIGIT]
   *                                             ; 00:00:00 - 23:59:59
   *
   * zone        =  &quot;UT&quot;  / &quot;GMT&quot;                ; Universal Time
   *                                             ; North American : UT
   *             /  &quot;EST&quot; / &quot;EDT&quot;                ;  Eastern:  - 5/ - 4
   *             /  &quot;CST&quot; / &quot;CDT&quot;                ;  Central:  - 6/ - 5
   *             /  &quot;MST&quot; / &quot;MDT&quot;                ;  Mountain: - 7/ - 6
   *             /  &quot;PST&quot; / &quot;PDT&quot;                ;  Pacific:  - 8/ - 7
   *             /  1ALPHA                       ; Military: Z = UT;
   *                                             ;  A:-1; (J not used)
   *                                             ;  M:-12; N:+1; Y:+12
   *             / ( (&quot;+&quot; / &quot;-&quot;) 4DIGIT )        ; Local differential
   *                                             ;  hours+min. (HHMM)
   *
   * 5.2.  SEMANTICS
   *
   * If included, day-of-week must be the day implied by the date
   * specification.
   *
   * Time zone may be indicated in several ways.  &quot;UT&quot; is Univer Time (formerly
   * called &quot;Greenwich Mean Time&quot;); &quot;GMT&quot; is permitted as a reference to
   * Universal Time. The  military  standard uses a single character for each
   * zone. &quot;Z&quot; is Universal Time. &quot;A&quot; indicates one hour earlier, and &quot;M&quot;
   * indicates 12  hours  earlier; &quot;N&quot;  is  one  hour  later, and &quot;Y&quot; is 12
   * hours later. The letter &quot;J&quot; is not used. The other remaining two forms are
   * taken from ANSI standard X3.51-1975. One allows explicit indication of the
   * amount of offset from UT; the other uses common  3-character strings for
   * indicating time zones in North America.
   * </pre>
   * 
   *
   * @param date
   *
   * @return the formatted date
   */
  public static String RFC822Format( Date date ) {
    Calendar cal = GregorianCalendar.getInstance( GMT );

    if ( date != null ) {
      cal.setTime( date );
    }

    StringBuffer retval = new StringBuffer( 29 );
    retval.append( days[cal.get( Calendar.DAY_OF_WEEK ) - 1] );
    retval.append( ", " );
    retval.append( StringUtil.zeropad( cal.get( Calendar.DAY_OF_MONTH ), 2 ) );
    retval.append( " " );
    retval.append( months[cal.get( Calendar.MONTH )] );
    retval.append( " " );
    retval.append( StringUtil.zeropad( cal.get( Calendar.YEAR ), 4 ) );
    retval.append( " " );
    retval.append( StringUtil.zeropad( cal.get( Calendar.HOUR_OF_DAY ), 2 ) );
    retval.append( ":" );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MINUTE ), 2 ) );
    retval.append( ":" );
    retval.append( StringUtil.zeropad( cal.get( Calendar.SECOND ), 2 ) );
    retval.append( " GMT" );

    return retval.toString();
  }




  /**
   * Method RFC822Format
   *
   * @return the formatted date
   */
  public static String RFC822Format() {
    return RFC822Format( new Date() );
  }




  /**
   * Return the current time in ISO8601 format adjusted to standard time.
   *
   * @return the formatted date
   */
  public static String ISO8601Format() {
    return ISO8601Format( new Date(), true, TimeZone.getDefault() );
  }




  /**
   * Return the ISO8601 formatted date-time string for the given number of
   * milliseconds past the epoch.
   *
   * @param millis the number of milliseconds past the epoch
   *
   * @return Properly ISO8601-formatted time string.
   */
  public static String ISO8601Format( long millis ) {
    return ISO8601Format( new Date( millis ), true, TimeZone.getDefault() );
  }




  /**
   * Return the given Date object in ISO8601 format adjusted to standard time.
   *
   * @param date the Date object to format
   *
   * @return Properly ISO8601-formatted time string.
   */
  public static String ISO8601Format( Date date ) {
    return ISO8601Format( date, true, TimeZone.getDefault() );
  }




  /**
   * Return the given Date object in ISO8601 format adjusted to standard time.
   *
   * @param date the Date object to format
   * @param adjust Adjust the time to standard time by removing the Daylight
   *          Savings Time offset.
   *
   * @return Properly ISO8601-formatted time string.
   */
  public static String ISO8601Format( Date date, boolean adjust ) {
    return ISO8601Format( date, adjust, TimeZone.getDefault() );
  }




  /**
   * Return the given Date object in ISO8601 format adjusted to standard GMT.
   *
   * <p>Take the given date, remove daylight savings time and the offset from
   * GMT resulting in a universally coordinated time in ISO8601 format.
   *
   * @param date the Date object to format
   *
   * @return Properly ISO8601-formatted time string.
   */
  public static String ISO8601GMT( Date date ) {
    return ISO8601Format( date, true, GMT );
  }




  /**
   * Return the given Date in ISO8601 format optionally adjusting to standard
   * time.
   *
   * @param date the Date object to format
   * @param adjust Adjust the time to standard time by removing the Daylight
   *        Savings Time offset.
   * @param tz
   *
   * @return Properly ISO8601-formatted time string.
   */
  public static String ISO8601Format( Date date, boolean adjust, TimeZone tz ) {
    Calendar cal = GregorianCalendar.getInstance();

    if ( tz != null ) {
      cal.setTimeZone( tz );
    }

    if ( date != null ) {
      cal.setTime( date );
    } else {
      cal.setTime( new Date() );
    }

    if ( adjust ) {
      // Adjust Daylight time to standard time
      cal.add( Calendar.HOUR_OF_DAY, ( cal.get( Calendar.DST_OFFSET ) / ( 60 * 60 * 1000 ) ) * -1 );
    }

    StringBuffer retval = new StringBuffer();
    retval.append( StringUtil.zeropad( cal.get( Calendar.YEAR ), 4 ) );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MONTH ) + 1, 2 ) );
    retval.append( StringUtil.zeropad( cal.get( Calendar.DATE ), 2 ) );
    retval.append( "T" );
    retval.append( StringUtil.zeropad( cal.get( Calendar.HOUR_OF_DAY ), 2 ) );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MINUTE ), 2 ) );
    retval.append( StringUtil.zeropad( cal.get( Calendar.SECOND ), 2 ) );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MILLISECOND ), 3 ) );

    int offset = ( cal.get( Calendar.ZONE_OFFSET ) / 1000 );
    int hours = offset / ( 60 * 60 );
    int minutes = offset - ( hours * ( 60 * 60 ) );

    if ( offset == 0 ) {
      retval.append( "Z" );
    } else {
      if ( offset < 0 ) {
        retval.append( "-" );

        hours *= -1;
      } else {
        retval.append( "+" );
      }

      retval.append( StringUtil.zeropad( hours, 2 ) );
      retval.append( StringUtil.zeropad( minutes, 2 ) );
    }

    return retval.toString();
  }




  /**
   * Display the given number of elapsed milliseconds in Hours, minutes, and
   * seconds delimited with the colon ':' character.
   *
   * <p>This returns a number like 290636:7:20 representing 290,636 hours past
   * the epoch.
   *
   * @param millis The number of milliseconds representing an elapsed time
   *
   * @return A string representing the elapsed time in HHHHHH:MM:SS format.
   */
  public static String HMS( long millis ) {
    long hours = millis / 3600000;
    long hourRemainder = millis % 3600000;
    long minutes = hourRemainder / 60000;
    long minRemainder = hourRemainder % 60000;
    long seconds = minRemainder / 1000;

    return hours + ":" + minutes + ":" + seconds;
  }




  /**
   * Display the given number of elapsed milliseconds in Hours, minutes,
   * seconds and milliseconds delimited with the colon ':' character.
   *
   * <p>This returns a number like 290636:7:20 representing 290,636 hours past
   * the epoch.
   *
   * @param millis The number of milliseconds representing an elapsed time
   *
   * @return A string representing the elapsed time in HH:MM:SS.mmm format.
   */
  public static String HMSm( long millis ) {
    long hours = millis / 3600000;
    long hourRemainder = millis % 3600000;
    long minutes = hourRemainder / 60000;
    long minRemainder = hourRemainder % 60000;
    long seconds = minRemainder / 1000;
    long milliseconds = minRemainder % 1000;

    return hours + ":" + minutes + ":" + seconds + "." + milliseconds;
  }




  /**
   * Display the given number of elapsed milliseconds in Days, Hours, minutes
   * and seconds delimited with the colon ':' character.
   *
   * @param millis The number of milliseconds representing an elapsed time
   *
   * @return A string representing the elapsed time in DD:HH:MM:SS format.
   */
  public static String DHMS( long millis ) {
    long days = millis / 86400000;
    long dayRemainder = millis % 86400000;
    long hours = dayRemainder / 3600000;
    long hourRemainder = dayRemainder % 3600000;
    long minutes = hourRemainder / 60000;
    long minRemainder = hourRemainder % 60000;
    long seconds = minRemainder / 1000;

    return days + ":" + hours + ":" + minutes + ":" + seconds;
  }




  /**
   * Display the given number of elapsed milliseconds in Days, Hours, minutes,
   * seconds and milliseconds delimited with the colon ':' character.
   *
   * @param millis The number of milliseconds representing an elapsed time
   *
   * @return A string representing the elapsed time in DD:HH:MM:SS.mmm format.
   */
  public static String DHMSm( long millis ) {
    long days = millis / 86400000;
    long dayRemainder = millis % 86400000;
    long hours = dayRemainder / 3600000;
    long hourRemainder = dayRemainder % 3600000;
    long minutes = hourRemainder / 60000;
    long minRemainder = hourRemainder % 60000;
    long seconds = minRemainder / 1000;
    long milliseconds = minRemainder % 1000;

    return days + ":" + hours + ":" + minutes + ":" + seconds + "." + milliseconds;
  }




  /**
   * Get a formatted string representing the difference between the two times.
   * 
   * <p>The output is in the format of X wks X days X hrs X min X.XXX sec.
   * 
   * @param millis number of elapsed milliseconds.
   * 
   * @return formatted string representing weeks, days, hours minutes and seconds .
   */
  public static String formatElapsed( long millis ) {
    if ( millis < 0 || millis == Long.MAX_VALUE ) {
      return "?";
    }

    long secondsInMilli = 1000;
    long minutesInMilli = secondsInMilli * 60;
    long hoursInMilli = minutesInMilli * 60;
    long daysInMilli = hoursInMilli * 24;
    long weeksInMilli = daysInMilli * 7;
    double monthsInMilli = weeksInMilli * 4.33333333;
    long yearsInMilli = weeksInMilli * 52;

    long elapsedyears = millis / yearsInMilli;
    millis = millis % yearsInMilli;

    long elapsedmonths = millis / (long)monthsInMilli;
    millis = millis % (long)monthsInMilli;

    long elapsedWeeks = millis / weeksInMilli;
    millis = millis % weeksInMilli;

    long elapsedDays = millis / daysInMilli;
    millis = millis % daysInMilli;

    long elapsedHours = millis / hoursInMilli;
    millis = millis % hoursInMilli;

    long elapsedMinutes = millis / minutesInMilli;
    millis = millis % minutesInMilli;

    long elapsedSeconds = millis / secondsInMilli;
    millis = millis % secondsInMilli;

    StringBuilder b = new StringBuilder();

    if ( elapsedyears > 0 ) {
      b.append( elapsedyears );
      if ( elapsedyears > 1 )
        b.append( " yrs " );
      else
        b.append( " yr " );
    }
    if ( elapsedmonths > 0 ) {
      b.append( elapsedmonths );
      if ( elapsedmonths > 1 )
        b.append( " mths " );
      else
        b.append( " mth " );
    }
    if ( elapsedWeeks > 0 ) {
      b.append( elapsedWeeks );
      if ( elapsedWeeks > 1 )
        b.append( " wks " );
      else
        b.append( " wk " );
    }
    if ( elapsedDays > 0 ) {
      b.append( elapsedDays );
      if ( elapsedDays > 1 )
        b.append( " days " );
      else
        b.append( " day " );

    }
    if ( elapsedHours > 0 ) {
      b.append( elapsedHours );
      if ( elapsedHours > 1 )
        b.append( " hrs " );
      else
        b.append( " hr " );
    }
    if ( elapsedMinutes > 0 ) {
      b.append( elapsedMinutes );
      b.append( " min " );
    }
    if ( elapsedSeconds > 0 ) {

      b.append( elapsedSeconds );
      if ( millis > 0 ) {
        b.append( "." );
        b.append( MILLIS.format( millis ) );
      }
      b.append( " sec" );
    }

    return b.toString();
  }




  /**
   * Formats the entire date in an ISO8601 compatible format without time zone
   * offset data.
   *
   * @param date the date object to convert.
   *
   * @return String representing the ISO8601 compatible representation of the
   *         date in yyyy-MM-ddTHH:mm:ss,SSS format.
   */
  public static String toExtended( Date date ) {
    SimpleDateFormat formatter = new SimpleDateFormat( "yyyy'-'MM'-'dd'T'HH':'mm':'ss','SSS" );
    return formatter.format( date );
  }




  /**
   * Method toExtendedGMT
   *
   * @param date
   *
   * @return the formatted date
   */
  public static String toExtendedGMT( Date date ) {
    Calendar cal = GregorianCalendar.getInstance( GMT );

    if ( date != null ) {
      cal.setTime( date );
    }

    StringBuffer retval = new StringBuffer( 23 );
    retval.append( StringUtil.zeropad( cal.get( Calendar.YEAR ), 4 ) );
    retval.append( '-' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MONDAY ), 2 ) );
    retval.append( '-' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.DAY_OF_MONTH ), 2 ) );
    retval.append( 'T' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.HOUR_OF_DAY ), 2 ) );
    retval.append( ':' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MINUTE ), 2 ) );
    retval.append( ':' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.SECOND ), 2 ) );
    retval.append( ',' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MILLISECOND ), 3 ) );

    return retval.toString();
  }




  /**
   * Formats the date-only portion of the given date in ISO8601 compatible
   * format without delimiters.
   *
   * @param date the date object to convert.
   *
   * @return String representing the date-only portion of the date in yyyyMMdd
   *         format.
   */
  public static String toBasicDate( Date date ) {
    SimpleDateFormat formatter = new SimpleDateFormat( "yyyyMMdd" );
    return formatter.format( date );
  }




  /**
   * Method toBasicGMTDate
   *
   * @param date
   *
   * @return the formatted date
   */
  public static String toBasicGMTDate( Date date ) {
    Calendar cal = GregorianCalendar.getInstance( GMT );

    if ( date != null ) {
      cal.setTime( date );
    }

    StringBuffer retval = new StringBuffer( 8 );
    retval.append( StringUtil.zeropad( cal.get( Calendar.YEAR ), 4 ) );
    retval.append( StringUtil.zeropad( cal.get( Calendar.DAY_OF_MONTH ), 2 ) );
    retval.append( StringUtil.zeropad( cal.get( Calendar.DAY_OF_MONTH ), 2 ) );

    return retval.toString();
  }




  /**
   * Formats the date-only portion of the given date in ISO8601 compatible
   * format with delimiters.
   *
   * @param date the date object to convert.
   *
   * @return String representing the date-only portion of the date in yyyy-MM-dd
   *         format.
   */
  public static String toExtendedDate( Date date ) {
    SimpleDateFormat formatter = new SimpleDateFormat( "yyyy'-'MM'-'dd" );
    return formatter.format( date );
  }




  /**
   * Method toExtendedGMTDate
   *
   * @param date
   *
   * @return the formatted date
   */
  public static String toExtendedGMTDate( Date date ) {
    Calendar cal = GregorianCalendar.getInstance( GMT );

    if ( date != null ) {
      cal.setTime( date );
    }

    StringBuffer retval = new StringBuffer( 10 );
    retval.append( StringUtil.zeropad( cal.get( Calendar.YEAR ), 4 ) );
    retval.append( '-' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MONDAY ), 2 ) );
    retval.append( '-' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.DAY_OF_MONTH ), 2 ) );

    return retval.toString();
  }




  /**
   * Formats the time portion of the given date in ISO8601 compatible format
   * without delimiters.
   *
   * @param date the date object to convert.
   *
   * @return String representing the time portion of the date in HHmmss format.
   */
  public static String toBasicTime( Date date ) {
    SimpleDateFormat formatter = new SimpleDateFormat( "HHmmss" );
    return formatter.format( date );
  }




  /**
   * Method toBasicGMTTime
   *
   * @param date
   *
   * @return the formatted date
   */
  public static String toBasicGMTTime( Date date ) {
    Calendar cal = GregorianCalendar.getInstance( GMT );

    if ( date != null ) {
      cal.setTime( date );
    }

    StringBuffer retval = new StringBuffer( 7 );
    retval.append( StringUtil.zeropad( cal.get( Calendar.HOUR_OF_DAY ), 2 ) );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MINUTE ), 2 ) );
    retval.append( StringUtil.zeropad( cal.get( Calendar.SECOND ), 2 ) );
    retval.append( 'Z' );

    return retval.toString();
  }




  /**
   * Formats the time portion of the given date in ISO8601 compatible format
   * with delimiters.
   *
   * @param date the date object to convert.
   *
   * @return String representing the time portion of the date in HH:mm:ss
   *         format.
   */
  public static String toExtendedTime( Date date ) {
    SimpleDateFormat formatter = new SimpleDateFormat( "HH':'mm':'ss" );
    return formatter.format( date );
  }




  /**
   * Method toExtendedGMTTime
   *
   * @param date
   *
   * @return the formatted time
   */
  public static String toExtendedGMTTime( Date date ) {
    Calendar cal = GregorianCalendar.getInstance( GMT );

    if ( date != null ) {
      cal.setTime( date );
    }

    StringBuffer retval = new StringBuffer( 9 );
    retval.append( StringUtil.zeropad( cal.get( Calendar.HOUR_OF_DAY ), 2 ) );
    retval.append( ':' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MINUTE ), 2 ) );
    retval.append( ':' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.SECOND ), 2 ) );
    retval.append( 'Z' );

    return retval.toString();
  }




  /**
   * Formats the time portion of the given date in ISO8601 compatible format
   * without ':' delimiters but includes milliseconds delimited with the ','
   * character.
   *
   * @param date the date object to convert.
   *
   * @return String representing the time portion of the date in HHmmss,SSS
   *         format.
   */
  public static String toBasicTimestamp( Date date ) {
    SimpleDateFormat formatter = new SimpleDateFormat( "HHmmss','SSS" );
    return formatter.format( date );
  }




  /**
   * Method toBasicGMTTimestamp
   *
   * @param date
   *
   * @return the formatted time
   */
  public static String toBasicGMTTimestamp( Date date ) {
    Calendar cal = GregorianCalendar.getInstance( GMT );

    if ( date != null ) {
      cal.setTime( date );
    }

    StringBuffer retval = new StringBuffer( 11 );
    retval.append( StringUtil.zeropad( cal.get( Calendar.HOUR_OF_DAY ), 2 ) );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MINUTE ), 2 ) );
    retval.append( StringUtil.zeropad( cal.get( Calendar.SECOND ), 2 ) );
    retval.append( ',' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MILLISECOND ), 3 ) );
    retval.append( 'Z' );

    return retval.toString();
  }




  /**
   * Formats the time portion of the given date in ISO8601 compatible format
   * with ':' delimiters and includes milliseconds delimited with the ','
   * character.
   *
   * @param date the date object to convert.
   *
   * @return String representing the time portion of the date in HH:mm:ss,SSS
   *         format.
   */
  public static String toExtendedTimestamp( Date date ) {
    SimpleDateFormat formatter = new SimpleDateFormat( "HH':'mm':'ss','SSS" );
    return formatter.format( date );
  }




  /**
   * Method toExtendedGMTTimestamp
   *
   * @param date
   *
   * @return the formatted time
   */
  public static String toExtendedGMTTimestamp( Date date ) {
    Calendar cal = GregorianCalendar.getInstance( GMT );

    if ( date != null ) {
      cal.setTime( date );
    }

    StringBuffer retval = new StringBuffer( 13 );
    retval.append( StringUtil.zeropad( cal.get( Calendar.HOUR_OF_DAY ), 2 ) );
    retval.append( ':' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MINUTE ), 2 ) );
    retval.append( ':' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.SECOND ), 2 ) );
    retval.append( ',' );
    retval.append( StringUtil.zeropad( cal.get( Calendar.MILLISECOND ), 3 ) );
    retval.append( 'Z' );

    return retval.toString();
  }




  /**
   * Calculate the epoc time in seconds of a particular point in time minus a
   * number of weeks.
   *
   * @param before a long representing the number of seconds since the epoch.
   * @param weeks The number of weeks to subtract from the given time
   *
   * @return a long representing the number of seconds since the epoch.
   */
  public static long subtractWeeks( long before, int weeks ) {
    return before - weeks * SECONDS_PER_WEEK;
  }




  /**
   * Calculate the epoc time in seconds of a particular point in time minus a
   * number of days.
   *
   * @param before a long representing the number of seconds since the epoch.
   * @param days The number of days to subtract from the given time
   *
   * @return a long representing the number of seconds since the epoch.
   */
  public static long subtractDays( long before, int days ) {
    return before - days * SECONDS_PER_DAY;
  }




  /**
   * Calculate the epoc time in seconds of a particular point in time minus a
   * number of hours.
   *
   * @param before a long representing the number of seconds since the epoch.
   * @param hours The number of hours to subtract from the given time
   *
   * @return a long representing the number of seconds since the epoch.
   */
  public static long subtractHours( long before, int hours ) {
    return before - hours * SECONDS_PER_HOUR;
  }




  /**
   * Return a Timestamp for right now
   *
   * @return Timestamp for right now
   */
  public static java.sql.Timestamp nowTimestamp() {
    return new java.sql.Timestamp( System.currentTimeMillis() );
  }




  /**
   * Return a Date for right now
   *
   * @return Date for right now
   */
  public static java.util.Date nowDate() {
    return new java.util.Date();
  }




  /**
   * Method getDayStart
   *
   * @param stamp
   *
   * @return the timestamp of the start of the day
   */
  public static java.sql.Timestamp getDayStart( java.sql.Timestamp stamp ) {
    return getDayStart( stamp, 0 );
  }




  /**
   * Method getDayStart
   *
   * @param stamp
   * @param daysLater
   *
   * @return the time stamp of the start of the day
   */
  public static java.sql.Timestamp getDayStart( java.sql.Timestamp stamp, int daysLater ) {
    Calendar tempCal = Calendar.getInstance();

    tempCal.setTime( new java.util.Date( stamp.getTime() ) );
    tempCal.set( tempCal.get( Calendar.YEAR ), tempCal.get( Calendar.MONTH ), tempCal.get( Calendar.DAY_OF_MONTH ), 0, 0, 0 );
    tempCal.add( Calendar.DAY_OF_MONTH, daysLater );

    return new java.sql.Timestamp( tempCal.getTime().getTime() );
  }




  /**
   * Method getNextDayStart
   *
   * @param stamp
   *
   * @return the time stamp of the start of the next day
   */
  public static java.sql.Timestamp getNextDayStart( java.sql.Timestamp stamp ) {
    return getDayStart( stamp, 1 );
  }




  /**
   * Method getDayEnd
   *
   * @param stamp
   *
   * @return the time stamp of the end of the day
   */
  public static java.sql.Timestamp getDayEnd( java.sql.Timestamp stamp ) {
    return getDayEnd( stamp, 0 );
  }




  /**
   * Method getDayEnd
   *
   * @param stamp
   * @param daysLater
   *
   * @return the timestamp of the end of the day
   */
  public static java.sql.Timestamp getDayEnd( java.sql.Timestamp stamp, int daysLater ) {
    Calendar tempCal = Calendar.getInstance();

    tempCal.setTime( new java.util.Date( stamp.getTime() ) );
    tempCal.set( tempCal.get( Calendar.YEAR ), tempCal.get( Calendar.MONTH ), tempCal.get( Calendar.DAY_OF_MONTH ), 23, 59, 59 );
    tempCal.add( Calendar.DAY_OF_MONTH, daysLater );

    return new java.sql.Timestamp( tempCal.getTime().getTime() );
  }




  /**
   * Converts a date String into a java.sql.Date
   *
   * @param date The date String: MM/DD/YYYY
   *
   * @return A java.sql.Date made from the date String
   */
  public static java.sql.Date toSqlDate( String date ) {
    java.util.Date newDate = toDate( date, "00:00:00" );

    if ( newDate != null ) {
      return new java.sql.Date( newDate.getTime() );
    } else {
      return null;
    }
  }




  /**
   * Makes a java.sql.Date from separate Strings for month, day, year
   *
   * @param monthStr The month String
   * @param dayStr The day String
   * @param yearStr The year String
   *
   * @return A java.sql.Date made from separate Strings for month, day, year
   */
  public static java.sql.Date toSqlDate( String monthStr, String dayStr, String yearStr ) {
    java.util.Date newDate = toDate( monthStr, dayStr, yearStr, "0", "0", "0" );

    if ( newDate != null ) {
      return new java.sql.Date( newDate.getTime() );
    } else {
      return null;
    }
  }




  /**
   * Makes a java.sql.Date from separate ints for month, day, year
   *
   * @param month The month int
   * @param day The day int
   * @param year The year int
   *
   * @return A java.sql.Date made from separate ints for month, day, year
   */
  public static java.sql.Date toSqlDate( int month, int day, int year ) {
    java.util.Date newDate = toDate( month, day, year, 0, 0, 0 );

    if ( newDate != null ) {
      return new java.sql.Date( newDate.getTime() );
    } else {
      return null;
    }
  }




  /**
   * Converts a time String into a java.sql.Time
   *
   * @param time The time String: either HH:MM or HH:MM:SS
   *
   * @return A java.sql.Time made from the time String
   */
  public static java.sql.Time toSqlTime( String time ) {
    java.util.Date newDate = toDate( "1/1/1970", time );

    if ( newDate != null ) {
      return new java.sql.Time( newDate.getTime() );
    } else {
      return null;
    }
  }




  /**
   * Makes a java.sql.Time from separate Strings for hour, minute, and second.
   *
   * @param hourStr The hour String
   * @param minuteStr The minute String
   * @param secondStr The second String
   *
   * @return A java.sql.Time made from separate Strings for hour, minute, and
   *         second.
   */
  public static java.sql.Time toSqlTime( String hourStr, String minuteStr, String secondStr ) {
    java.util.Date newDate = toDate( "0", "0", "0", hourStr, minuteStr, secondStr );

    if ( newDate != null ) {
      return new java.sql.Time( newDate.getTime() );
    } else {
      return null;
    }
  }




  /**
   * Makes a java.sql.Time from separate ints for hour, minute, and second.
   *
   * @param hour The hour int
   * @param minute The minute int
   * @param second The second int
   *
   * @return A java.sql.Time made from separate ints for hour, minute, and
   *         second.
   */
  public static java.sql.Time toSqlTime( int hour, int minute, int second ) {
    java.util.Date newDate = toDate( 0, 0, 0, hour, minute, second );

    if ( newDate != null ) {
      return new java.sql.Time( newDate.getTime() );
    } else {
      return null;
    }
  }




  /**
   * Converts a date and time String into a Timestamp
   *
   * @param dateTime A combined data and time string in the format "MM/DD/YYYY
   *          HH:MM:SS", the seconds are optional
   *
   * @return The corresponding Timestamp
   */
  public static java.sql.Timestamp toTimestamp( String dateTime ) {
    java.util.Date newDate = toDate( dateTime );

    if ( newDate != null ) {
      return new java.sql.Timestamp( newDate.getTime() );
    } else {
      return null;
    }
  }




  /**
   * Converts a date String and a time String into a Timestamp
   *
   * @param date The date String: MM/DD/YYYY
   * @param time The time String: either HH:MM or HH:MM:SS
   *
   * @return A Timestamp made from the date and time Strings
   */
  public static java.sql.Timestamp toTimestamp( String date, String time ) {
    java.util.Date newDate = toDate( date, time );

    if ( newDate != null ) {
      return new java.sql.Timestamp( newDate.getTime() );
    } else {
      return null;
    }
  }




  /**
   * Makes a Timestamp from separate Strings for month, day, year, hour, minute,
   * and second.
   *
   * @param monthStr The month String
   * @param dayStr The day String
   * @param yearStr The year String
   * @param hourStr The hour String
   * @param minuteStr The minute String
   * @param secondStr The second String
   *
   * @return A Timestamp made from separate Strings for month, day, year, hour,
   *         minute, and second.
   */
  public static java.sql.Timestamp toTimestamp( String monthStr, String dayStr, String yearStr, String hourStr, String minuteStr, String secondStr ) {
    java.util.Date newDate = toDate( monthStr, dayStr, yearStr, hourStr, minuteStr, secondStr );

    if ( newDate != null ) {
      return new java.sql.Timestamp( newDate.getTime() );
    } else {
      return null;
    }
  }




  /**
   * Makes a Timestamp from separate ints for month, day, year, hour, minute,
   * and second.
   *
   * @param month The month int
   * @param day The day int
   * @param year The year int
   * @param hour The hour int
   * @param minute The minute int
   * @param second The second int
   *
   * @return A Timestamp made from separate ints for month, day, year, hour,
   *         minute, and second.
   */
  public static java.sql.Timestamp toTimestamp( int month, int day, int year, int hour, int minute, int second ) {
    java.util.Date newDate = toDate( month, day, year, hour, minute, second );

    if ( newDate != null ) {
      return new java.sql.Timestamp( newDate.getTime() );
    } else {
      return null;
    }
  }




  /**
   * Converts a date and time String into a Date
   *
   * @param dateTime A combined data and time string in the format "MM/DD/YYYY
   *          HH:MM:SS", the seconds are optional
   *
   * @return The corresponding Date
   */
  public static java.util.Date toDate( String dateTime ) {
    // dateTime must have one space between the date and time...
    String date = dateTime.substring( 0, dateTime.indexOf( " " ) );
    String time = dateTime.substring( dateTime.indexOf( " " ) + 1 );

    return toDate( date, time );
  }




  /**
   * Converts a date String and a time String into a Date
   *
   * @param date The date String: MM/DD/YYYY
   * @param time The time String: either HH:MM or HH:MM:SS
   *
   * @return A Date made from the date and time Strings
   */
  public static java.util.Date toDate( String date, String time ) {
    if ( ( date == null ) || ( time == null ) ) {
      return null;
    }

    String month;
    String day;
    String year;
    String hour;
    String minute;
    String second;

    int dateSlash1 = date.indexOf( "/" );
    int dateSlash2 = date.lastIndexOf( "/" );

    if ( ( dateSlash1 <= 0 ) || ( dateSlash1 == dateSlash2 ) ) {
      return null;
    }

    int timeColon1 = time.indexOf( ":" );
    int timeColon2 = time.lastIndexOf( ":" );

    if ( timeColon1 <= 0 ) {
      return null;
    }

    month = date.substring( 0, dateSlash1 );
    day = date.substring( dateSlash1 + 1, dateSlash2 );
    year = date.substring( dateSlash2 + 1 );
    hour = time.substring( 0, timeColon1 );

    if ( timeColon1 == timeColon2 ) {
      minute = time.substring( timeColon1 + 1 );
      second = "0";
    } else {
      minute = time.substring( timeColon1 + 1, timeColon2 );
      second = time.substring( timeColon2 + 1 );
    }

    return toDate( month, day, year, hour, minute, second );
  }




  /**
   * Makes a Date from separate Strings for month, day, year, hour, minute, and
   * second.
   *
   * @param monthStr The month String
   * @param dayStr The day String
   * @param yearStr The year String
   * @param hourStr The hour String
   * @param minuteStr The minute String
   * @param secondStr The second String
   *
   * @return A Date made from separate Strings for month, day, year, hour,
   *         minute, and second.
   */
  public static java.util.Date toDate( String monthStr, String dayStr, String yearStr, String hourStr, String minuteStr, String secondStr ) {
    int month, day, year, hour, minute, second;

    try {
      month = Integer.parseInt( monthStr );
      day = Integer.parseInt( dayStr );
      year = Integer.parseInt( yearStr );
      hour = Integer.parseInt( hourStr );
      minute = Integer.parseInt( minuteStr );
      second = Integer.parseInt( secondStr );
    } catch ( Exception e ) {
      return null;
    }

    return toDate( month, day, year, hour, minute, second );
  }




  /**
   * Makes a Date from separate ints for month, day, year, hour, minute, and
   * second.
   *
   * @param month The month int
   * @param day The day int
   * @param year The year int
   * @param hour The hour int
   * @param minute The minute int
   * @param second The second int
   *
   * @return A Date made from separate ints for month, day, year, hour, minute,
   *         and second.
   */
  public static java.util.Date toDate( int month, int day, int year, int hour, int minute, int second ) {
    Calendar calendar = Calendar.getInstance();

    try {
      calendar.set( year, month - 1, day, hour, minute, second );
    } catch ( Exception e ) {
      return null;
    }

    return new java.util.Date( calendar.getTime().getTime() );
  }




  /**
   * Makes a date String in the format MM/DD/YYYY from a Date
   *
   * @param date The Date
   *
   * @return A date String in the format MM/DD/YYYY
   */
  public static String toDateString( java.util.Date date ) {
    if ( date == null ) {
      return "";
    }

    Calendar calendar = Calendar.getInstance();

    calendar.setTime( date );

    int month = calendar.get( Calendar.MONTH ) + 1;
    int day = calendar.get( Calendar.DAY_OF_MONTH );
    int year = calendar.get( Calendar.YEAR );
    String monthStr;
    String dayStr;
    String yearStr;

    if ( month < 10 ) {
      monthStr = "0" + month;
    } else {
      monthStr = "" + month;
    }

    if ( day < 10 ) {
      dayStr = "0" + day;
    } else {
      dayStr = "" + day;
    }

    yearStr = "" + year;

    return monthStr + "/" + dayStr + "/" + yearStr;
  }




  /**
   * Makes a time String in the format HH:MM:SS from a Date. If the seconds are
   * 0, then the output is in HH:MM.
   *
   * @param date The Date
   *
   * @return A time String in the format HH:MM:SS or HH:MM
   */
  public static String toTimeString( java.util.Date date ) {
    if ( date == null ) {
      return "";
    }

    Calendar calendar = Calendar.getInstance();

    calendar.setTime( date );

    return ( toTimeString( calendar.get( Calendar.HOUR_OF_DAY ), calendar.get( Calendar.MINUTE ), calendar.get( Calendar.SECOND ) ) );
  }




  /**
   * Makes a time String in the format HH:MM:SS from a separate ints for hour,
   * minute, and second.
   *
   * <p>If the seconds are 0, then the output is in HH:MM.
   *
   * @param hour The hour int
   * @param minute The minute int
   * @param second The second int
   *
   * @return A time String in the format HH:MM:SS or HH:MM
   */
  public static String toTimeString( int hour, int minute, int second ) {
    String hourStr;
    String minuteStr;
    String secondStr;

    if ( hour < 10 ) {
      hourStr = "0" + hour;
    } else {
      hourStr = "" + hour;
    }

    if ( minute < 10 ) {
      minuteStr = "0" + minute;
    } else {
      minuteStr = "" + minute;
    }

    if ( second < 10 ) {
      secondStr = "0" + second;
    } else {
      secondStr = "" + second;
    }

    if ( second == 0 ) {
      return hourStr + ":" + minuteStr;
    } else {
      return hourStr + ":" + minuteStr + ":" + secondStr;
    }
  }




  /**
   * Makes a combined date and time string in the format "MM/DD/YYYY HH:MM:SS"
   * from a Date.
   *
   * <p>If the seconds are 0 they are left off.
   *
   * @param date The Date
   *
   * @return A combined data and time string in the format "MM/DD/YYYY HH:MM:SS"
   *         where the seconds are left off if they are 0.
   */
  public static String toDateTimeString( java.util.Date date ) {
    if ( date == null ) {
      return "";
    }

    String dateString = toDateString( date );
    String timeString = toTimeString( date );

    if ( ( dateString != null ) && ( timeString != null ) ) {
      return dateString + " " + timeString;
    } else {
      return "";
    }
  }




  /**
   * Makes a Timestamp for the beginning of the month
   *
   * @return A Timestamp of the beginning of the month
   */
  public static java.sql.Timestamp monthBegin() {
    Calendar mth = Calendar.getInstance();

    mth.set( Calendar.DAY_OF_MONTH, 1 );
    mth.set( Calendar.HOUR_OF_DAY, 0 );
    mth.set( Calendar.MINUTE, 0 );
    mth.set( Calendar.SECOND, 0 );
    mth.set( Calendar.AM_PM, Calendar.AM );

    return new java.sql.Timestamp( mth.getTime().getTime() );
  }




  /**
   * Print only the most significant portion of the time.
   *
   * <p>This is the two most significant units of time. Form will be something
   * like "3h 26m" indicating 3 hours 26 minutes and some insignificant number
   * of seconds. Formats are Xd Xh (days-hours), Xh Xm (Hours-minutes), Xm Xs
   * (minutes-seconds) and Xs (seconds).
   *
   * @param seconds number of elapsed seconds NOT milliseconds.
   *
   * @return formatted string
   */
  public static String formatSignificantElapsedTime( long seconds ) {
    final long days = seconds / 86400;
    StringBuffer buffer = new StringBuffer();

    if ( days > 0 ) // Display days and hours
    {
      buffer.append( days );
      buffer.append( "d " );
      buffer.append( ( ( seconds / 3600 ) % 24 ) ); // hours
      buffer.append( "h" );

      return buffer.toString();
    }

    final int hours = (int)( ( seconds / 3600 ) % 24 );

    if ( hours > 0 ) // Display hours and minutes
    {
      buffer.append( hours );
      buffer.append( "h " );
      buffer.append( ( ( seconds / 60 ) % 60 ) ); // minutes
      buffer.append( "m" );

      return buffer.toString();
    }

    final int minutes = (int)( ( seconds / 60 ) % 60 );

    if ( minutes > 0 ) // Display minutes and seconds
    {
      buffer.append( minutes );
      buffer.append( "m " );
      buffer.append( ( seconds % 60 ) ); // seconds
      buffer.append( "s" );

      return buffer.toString();
    }

    final int secs = (int)( seconds % 60 );
    buffer.append( secs ); // seconds
    buffer.append( "s" );

    return buffer.toString();

  }




  /**
   * Calculate the phase of the moon for a given date.
   *
   * <p>Code heavily influenced by hacklib.c in
   * <a href="http://www.nethack.org/">Nethack</a>
   *
   * <p>The Algorithm:
   *
   * <pre>
   * moon period = 29.53058 days &tilde;= 30, year = 365.2422 days
   *
   * days moon phase advances on first day of year compared to preceding year
   *  = 365.2422 - 12*29.53058 &tilde;= 11
   *
   * years in Metonic cycle (time until same phases fall on the same days of
   *  the month) = 18.6 &tilde;= 19
   *
   * moon phase on first day of year (epact) &tilde;= (11*(year%19) + 18) % 30
   *  (18 as initial condition for 1900)
   *
   * current phase in days = first day phase + days elapsed in year
   *
   * 6 moons &tilde;= 177 days
   * 177 &tilde;= 8 reported phases * 22
   * + 11/22 for rounding
   * </pre>
   *
   * @param cal
   * @return The phase of the moon as a number between 0 and 7 with 0 meaning
   *         new moon and 4 meaning full moon.
   */
  public static int getPhaseOfMoon( Calendar cal ) {
    int dayOfTheYear = cal.get( Calendar.DAY_OF_YEAR );
    int yearInMetonicCycle = ( ( cal.get( Calendar.YEAR ) - 1900 ) % 19 ) + 1;
    int epact = ( 11 * yearInMetonicCycle + 18 ) % 30;

    if ( ( ( epact == 25 ) && ( yearInMetonicCycle > 11 ) ) || ( epact == 24 ) ) {
      epact++;
    }

    return ( ( ( ( ( dayOfTheYear + epact ) * 6 ) + 11 ) % 177 ) / 22 ) & 7;
  }




  /**
   * Return a gregorian calendar instance for the current date and set to the
   * time represented by the given time string.
   *
   * <p>The argument MUST be in HH:MM:SS, HH:MM or SS format.
   *
   * <p>The returned calendar object can then be used to roll dates up and down
   * with the proper conversions being made; i.e. 12/31 + one day should result
   * in a calendar representation of 1/1.
   *
   * @param time The string in HH:MM:SS format.
   *
   * @return The gregorian calendar instance for today with the time set to the
   *         time represented by argument.
   */
  public static Calendar getCalendar( String time ) {
    if ( time != null ) {
      String text = time.trim();

      if ( text.length() > 0 ) {
        String hrs;
        String mns;
        String scs = "00";
        int ptr = text.lastIndexOf( ':' );

        if ( ptr > -1 ) {
          mns = text.substring( ptr + 1 );
          hrs = text.substring( 0, ptr );

          ptr = hrs.lastIndexOf( ':' );

          if ( ptr > -1 ) {
            scs = mns;
            mns = hrs.substring( ptr + 1 );
            hrs = hrs.substring( 0, ptr );
          }

          try {
            int hours = Integer.parseInt( hrs );

            if ( ( hours > 23 ) || ( hours < 0 ) ) {
              throw new IllegalArgumentException( "Hours are out or range" );
            }

            int mins = Integer.parseInt( mns );

            if ( ( mins > 59 ) || ( mins < 0 ) ) {
              throw new IllegalArgumentException( "Minutes are out or range" );
            }

            int secs = Integer.parseInt( scs );

            if ( ( secs > 59 ) || ( secs < 0 ) ) {
              throw new IllegalArgumentException( "Seconds are out or range" );
            }

            // grab a calendar using the current localized time
            Calendar cal = GregorianCalendar.getInstance();

            // Set all the parsed values
            cal.set( Calendar.HOUR_OF_DAY, hours );
            cal.set( Calendar.MINUTE, mins );
            cal.set( Calendar.SECOND, secs );
            cal.set( Calendar.MILLISECOND, 0 );

            // return our calendar
            return cal;
          } catch ( NumberFormatException e ) {
            throw new IllegalArgumentException( "Time is not in HH:MM format" );
          }

        } else {
          // only seconds
          int secs = Integer.parseInt( text );

          if ( ( secs > 59 ) || ( secs < 0 ) ) {
            throw new IllegalArgumentException( "Seconds are out or range" );
          }

          // grab a calendar using the current localized time
          Calendar cal = GregorianCalendar.getInstance();

          // Set all the parsed values
          cal.set( Calendar.SECOND, secs );
          cal.set( Calendar.MILLISECOND, 0 );

          // return our calendar
          return cal;
        }
      } // if text !""
    }

    return null;
  }




  /**
   * Return the epoch time in milliseconds when the time specified by the
   * given string next occurs.
   *
   * <p>The string is parsed as HH:MM:SS, HH:MM or SS
   *
   * @param time
   *
   * @return the epoch milliseconds
   */
  public static long getNextTime( String time ) {
    Calendar cal = getCalendar( time );
    long now = System.currentTimeMillis();

    if ( cal != null ) {
      if ( cal.getTimeInMillis() < now ) {
        if ( time.length() > 2 ) {
          cal.add( Calendar.DATE, 1 );
        } else {
          cal.add( Calendar.MINUTE, 1 );
        }
      }

      return cal.getTimeInMillis();
    }

    return now;
  }




  /**
   * Method getLastTime
   *
   * @param time
   *
   * @return the epoch milliseconds
   */
  public static long getLastTime( String time ) {
    Calendar cal = getCalendar( time );
    long now = System.currentTimeMillis();

    if ( cal != null ) {
      if ( cal.getTimeInMillis() > now ) {
        if ( time.length() > 2 ) {
          cal.add( Calendar.DATE, -1 );
        } else {
          cal.add( Calendar.MINUTE, -1 );
        }
      }

      return cal.getTimeInMillis();
    }

    return now;
  }




  /**
   * Method main
   *
   * @param args
   */
  public static void main( String[] args ) {
    System.out.println( "ISO8601: '" + ISO8601Format() + "'" );
    System.out.println( "ISO8601 GMT: '" + ISO8601GMT( new Date() ) + "'" );
    System.out.println( "RFC822: '" + RFC822Format() + "'" );

    System.out.println();

    String timeText = "00:00";
    Calendar cal = getCalendar( timeText );
    System.out.println( "Calendar '" + timeText + "' occurs = " + cal.getTime() );
    cal.add( Calendar.DATE, +30 );
    System.out.println( "          added 30 days = " + cal.getTime() );
    System.out.println();

    // One day differences
    timeText = "22:11";

    System.out.println( "Next Time '" + timeText + "' occurs = " + new Date( getNextTime( timeText ) ) );
    System.out.println( "Last Time '" + timeText + "' occured = " + new Date( getLastTime( timeText ) ) );

    timeText = "3:15:23";

    System.out.println( "Next Time '" + timeText + "' occurs = " + new Date( getNextTime( timeText ) ) );
    System.out.println( "Last Time '" + timeText + "' occured = " + new Date( getLastTime( timeText ) ) );

    // one minute difference
    timeText = "37";

    System.out.println( "Next Time '" + timeText + "' occurs = " + new Date( getNextTime( timeText ) ) );
    System.out.println( "Last Time '" + timeText + "' occured = " + new Date( getLastTime( timeText ) ) );
  }











  private static final DateFormat _DATE_TIME_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
  private static final DateFormat _DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd" );
  private static final DateFormat _TIME_FORMAT = new SimpleDateFormat( "HH:mm:ss" );


  private static final List<String> formatStrings = Arrays.asList(
          "yyyy-MM-dd'T'HH:mm:ss.SSSX",
          "yyyy-MM-dd'T'HH:mm:ss.SSS",
          "yyyy-MM-dd'T'HH:mm:ss",
          "yyyy-MM-dd HH:mm:ss.SSSX",
          "yyyy-MM-dd HH:mm:ss.SSS",
          "yyyy-MM-dd HH:mm:ss",
          "yyyy-MM-dd",
          "MM-dd-yyyy HH:mm:ss.SSSX",
          "MM-dd-yyyy HH:mm:ss.SSS",
          "MM-dd-yyyy HH:mm:ss",
          "MM/dd/yyyy HH:mm:ss",
          "EEE MMM dd HH:mm:ss zzz yyyy",
          "HH:mm:ss.SSSX",
          "HH:mm:ss.SSS",
          "HH:mm:ss",
          "HH:mm",
          "d/MMM/y:H:m:s Z", // Apache log format
          "M/y",
          "M-y",
          "M/d/y",
          "M-d-y",
          "y/M/d",
          "y-M-d");




  public static Date parseDateTime( String token ) {
    Date retval = null;
    try {
      retval = _DATE_TIME_FORMAT.parse( token );
    } catch ( final Exception e ) {
      retval = null;
    }
    return retval;
  }




  public static Date parseDate( String token ) {
    Date retval = null;
    try {
      retval = _DATE_FORMAT.parse( token );
    } catch ( final Exception e ) {
      retval = null;
    }
    return retval;
  }




  public static Date parseTime( String token ) {
    Date retval = null;
    try {
      retval = _TIME_FORMAT.parse( token );
    } catch ( final Exception e ) {
      retval = null;
    }
    return retval;
  }




  /**
   * This method tries several different formats to parsing a date.
   *
   * <p>This method is useful if the actual format of the date is not known.
   *
   * @param text the date time string to parse
   *
   * @return the Date reference if parsing was successful or null if the text
   *         could not be parsed into a date.
   */
  public static Date parse( String text ) {
    for ( String formatString : formatStrings ) {
      try {
        return new SimpleDateFormat( formatString ).parse( text );
      } catch ( ParseException e ) {
        // ignore failed attempt
      }
    }
    return null;
  }








}