/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.commons;

//import static org.junit.Assert.*;


import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;


/**
 * 
 */
public class CronEntryTest {
  static DecimalFormat MILLIS = new DecimalFormat("000");
  private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
  static SimpleDateFormat DATEFORMAT = new SimpleDateFormat(DEFAULT_DATE_FORMAT);




  /**
   * Get a formatted string representing the difference between the two times.
   * 
   * @param millis number of elapsed milliseconds.
   * 
   * @return formatted string representing weeks, days, hours minutes and seconds.
   */
  public static String formatElapsed(long millis) {
    if (millis < 0 || millis == Long.MAX_VALUE) {
      return "?";
    }

    long secondsInMilli = 1000;
    long minutesInMilli = secondsInMilli * 60;
    long hoursInMilli = minutesInMilli * 60;
    long daysInMilli = hoursInMilli * 24;
    long weeksInMilli = daysInMilli * 7;

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

    if (elapsedWeeks > 0) {
      b.append(elapsedWeeks);
      if (elapsedWeeks > 1)
        b.append(" wks ");
      else
        b.append(" wk ");
    }
    if (elapsedDays > 0) {
      b.append(elapsedDays);
      if (elapsedDays > 1)
        b.append(" days ");
      else
        b.append(" day ");

    }
    if (elapsedHours > 0) {
      b.append(elapsedHours);
      if (elapsedHours > 1)
        b.append(" hrs ");
      else
        b.append(" hr ");
    }
    if (elapsedMinutes > 0) {
      b.append(elapsedMinutes);
      b.append(" min ");
    }
    b.append(elapsedSeconds);
    if (millis > 0) {
      b.append(".");
      b.append(MILLIS.format(millis));
    }
    b.append(" sec");

    return b.toString();
  }




  @Test
  public void testDivisorPatterns() throws ParseException {
    CronEntry subject;

    // Every 15 minutes: 0, 15, 30, 45
    subject = CronEntry.parse("*/15 * * * *");
    assertTrue(subject.minutePasses(0));
    assertTrue(subject.minutePasses(15));
    assertTrue(subject.minutePasses(30));
    assertTrue(subject.minutePasses(45));
    assertFalse(subject.minutePasses(1));
    assertFalse(subject.minutePasses(14));

    // Range with divisor: minutes 10 through 20, every 2 minutes: 10, 12, 14, 16, 18, 20
    subject = CronEntry.parse("10-20/2 * * * *");
    assertTrue(subject.minutePasses(10));
    assertTrue(subject.minutePasses(12));
    assertTrue(subject.minutePasses(20));
    assertFalse(subject.minutePasses(9));
    assertFalse(subject.minutePasses(11));
    assertFalse(subject.minutePasses(21));

    // Every 3 hours
    subject = CronEntry.parse("* */3 * * *");
    assertTrue(subject.hourPasses(0));
    assertTrue(subject.hourPasses(3));
    assertTrue(subject.hourPasses(21));
    assertFalse(subject.hourPasses(1));
    assertFalse(subject.hourPasses(2));
  }


  @Test
  public void testListPatterns() throws ParseException {
    // List of minutes
    CronEntry subject = CronEntry.parse("1,15,30,45 * * * *");
    assertTrue(subject.minutePasses(1));
    assertTrue(subject.minutePasses(15));
    assertTrue(subject.minutePasses(30));
    assertTrue(subject.minutePasses(45));
    assertFalse(subject.minutePasses(0));
    assertFalse(subject.minutePasses(2));

    // List with ranges
    subject = CronEntry.parse("1,5-10,15,20-25 * * * *");
    assertTrue(subject.minutePasses(1));
    assertTrue(subject.minutePasses(5));
    assertTrue(subject.minutePasses(7));
    assertTrue(subject.minutePasses(10));
    assertTrue(subject.minutePasses(15));
    assertTrue(subject.minutePasses(20));
    assertTrue(subject.minutePasses(25));
    assertFalse(subject.minutePasses(0));
    assertFalse(subject.minutePasses(2));
    assertFalse(subject.minutePasses(11));
  }


  @Test
  public void testComplexPatterns() throws ParseException {
    // 0 0 1,15 * 1-5  -> Midnight on 1st and 15th, but only if it's Mon-Fri
    CronEntry subject = CronEntry.parse("0 0 1,15 * 1-5");
    Calendar cal = new GregorianCalendar(2023, Calendar.MAY, 1, 0, 0); // May 1, 2023 is Monday
    assertTrue(subject.mayRunAt(cal));

    cal.set(2023, Calendar.MAY, 15, 0, 0); // May 15, 2023 is Monday
    assertTrue(subject.mayRunAt(cal));

    cal.set(2023, Calendar.MAY, 2, 0, 0); // May 2 is Tuesday, but not 1st or 15th
    assertFalse(subject.mayRunAt(cal));

    cal.set(2023, Calendar.MAY, 14, 0, 0); // May 14, 2023 is Sunday
    assertFalse(subject.mayRunAt(cal));
  }


  @Test
  public void testLeapYear() throws ParseException {
    // Every day in February
    CronEntry subject = CronEntry.parse("0 0 * 2 *");

    // Feb 29, 2024 (Leap year)
    Calendar cal = new GregorianCalendar(2024, Calendar.FEBRUARY, 29, 0, 0);
    assertTrue(subject.mayRunAt(cal));

    // Feb 29, 2023 (Not a leap year - Calendar will roll over to March 1st usually, but let's see how mayRunAt handles it)
    cal.set(2023, Calendar.FEBRUARY, 29, 0, 0);
    // In GregorianCalendar, setting Feb 29 2023 results in March 1 2023
    assertEquals(Calendar.MARCH, cal.get(Calendar.MONTH));
    assertFalse(subject.mayRunAt(cal));
  }


  @Test
  public void testNextTimeEdgeCases() throws ParseException {
    // Run at 23:59 every day
    CronEntry subject = CronEntry.parse("59 23 * * *");

    Calendar cal = new GregorianCalendar(2023, Calendar.DECEMBER, 31, 23, 58, 0);
    cal.set(Calendar.MILLISECOND, 0);

    long next = subject.getNextTime(cal);
    Calendar nextCal = new GregorianCalendar();
    nextCal.setTimeInMillis(next);

    assertEquals(2023, nextCal.get(Calendar.YEAR));
    assertEquals(Calendar.DECEMBER, nextCal.get(Calendar.MONTH));
    assertEquals(31, nextCal.get(Calendar.DAY_OF_MONTH));
    assertEquals(23, nextCal.get(Calendar.HOUR_OF_DAY));
    assertEquals(59, nextCal.get(Calendar.MINUTE));

    // Next one should be Jan 1st 23:59 of NEXT year
    cal.setTimeInMillis(next);
    cal.add(Calendar.MINUTE, 1); // 00:00 Jan 1 2024

    next = subject.getNextTime(cal);
    nextCal.setTimeInMillis(next);
    assertEquals(2024, nextCal.get(Calendar.YEAR));
    assertEquals(Calendar.JANUARY, nextCal.get(Calendar.MONTH));
    assertEquals(1, nextCal.get(Calendar.DAY_OF_MONTH));
    assertEquals(23, nextCal.get(Calendar.HOUR_OF_DAY));
    assertEquals(59, nextCal.get(Calendar.MINUTE));
  }


  @Test
  public void testFillRange() {
    assertEquals("1,2,3,4,5,", CronEntry.fillRange("1-5"));
    assertEquals("0,1,2,", CronEntry.fillRange("0-2"));
    assertEquals("10,", CronEntry.fillRange("10-10"));
  }


  @Test
  public void testParseRangeParamInternal() {
    // Minutes: 0-59
    TreeSet<String> result = CronEntry.parseRangeParam("*/15", 59, 0);
    assertEquals(4, result.size());
    assertTrue(result.contains("0"));
    assertTrue(result.contains("15"));
    assertTrue(result.contains("30"));
    assertTrue(result.contains("45"));

    result = CronEntry.parseRangeParam("1-5", 59, 0);
    assertEquals(5, result.size());
    assertTrue(result.contains("1"));
    assertTrue(result.contains("5"));

    // Wrapped range is not supported by the current implementation based on inspection, 
    // it will throw Exception if it doesn't parse correctly or returns empty if logic doesn't match.
    // Let's test comma-separated ranges
    result = CronEntry.parseRangeParam("1-2,10-12", 59, 0);
    assertEquals(5, result.size());
    assertTrue(result.contains("1"));
    assertTrue(result.contains("2"));
    assertTrue(result.contains("10"));
    assertTrue(result.contains("11"));
    assertTrue(result.contains("12"));
  }


  @Test
  public void testParse() throws ParseException {
    CronEntry subject = null;

    // Test null pattern
    subject = CronEntry.parse(null);
    assertNotNull(subject);
    assertEquals("*", subject.getMinutePattern());
    assertEquals("*", subject.getHourPattern());
    assertEquals("*", subject.getDayPattern());
    assertEquals("*", subject.getMonthPattern());
    assertEquals("*", subject.getDayOfWeekPattern());
    assertTrue(subject.minutePasses(0));
    assertTrue(subject.minutePasses(59));

    // Test wildcard pattern
    String pattern = "* * * * *";
    subject = CronEntry.parse(pattern);
    assertEquals("*", subject.getMinutePattern());
    assertEquals("*", subject.getHourPattern());
    assertEquals("*", subject.getDayPattern());
    assertEquals("*", subject.getMonthPattern());
    assertEquals("*", subject.getDayOfWeekPattern());
    assertTrue(subject.minutePasses(30));
    assertTrue(subject.hourPasses(12));
    assertTrue(subject.dayPasses(15));
    assertTrue(subject.monthPasses(6));
    assertTrue(subject.weekDayPasses(3));

    // Test question mark pattern (should be treated as wildcard)
    pattern = "? ? ? ? ?";
    subject = CronEntry.parse(pattern);
    assertEquals("?", subject.getMinutePattern());
    assertTrue(subject.minutePasses(0));
    assertTrue(subject.hourPasses(0));

    // Test divisors
    pattern = "/15 3 * * ?";
    subject = CronEntry.parse(pattern);
    assertEquals("/15", subject.getMinutePattern());
    assertEquals("3", subject.getHourPattern());
    assertTrue(subject.minutePasses(0));
    assertTrue(subject.minutePasses(15));
    assertTrue(subject.minutePasses(30));
    assertTrue(subject.minutePasses(45));
    assertFalse(subject.minutePasses(1));
    assertTrue(subject.hourPasses(3));
    assertFalse(subject.hourPasses(4));

    // Test complex patterns
    pattern = "*/15 3 */2 * 1-6";
    subject = CronEntry.parse(pattern);
    assertEquals("*/15", subject.getMinutePattern());
    assertEquals("3", subject.getHourPattern());
    assertEquals("*/2", subject.getDayPattern());
    assertEquals("*", subject.getMonthPattern());
    assertEquals("1-6", subject.getDayOfWeekPattern());
    assertTrue(subject.minutePasses(0));
    assertTrue(subject.minutePasses(45));
    assertTrue(subject.hourPasses(3));
    assertTrue(subject.dayPasses(2));
    assertTrue(subject.dayPasses(4));
    assertFalse(subject.dayPasses(3));
    assertTrue(subject.weekDayPasses(1));
    assertTrue(subject.weekDayPasses(6));
    assertFalse(subject.weekDayPasses(0));

    // Test invalid pattern
    final String invalidPattern = "B A D * *";
    assertThrows(ParseException.class, () -> {
      CronEntry.parse(invalidPattern);
    }, "Did not detect invalid pattern of '" + invalidPattern + "'");

    // Test empty string pattern
    pattern = "";
    subject = CronEntry.parse(pattern);
    assertEquals("*", subject.getMinutePattern());
    assertTrue(subject.minutePasses(0));

    // Test pattern with too many fields (should ignore extra fields based on current implementation)
    pattern = "* * * * * * * * * * * * * *";
    subject = CronEntry.parse(pattern);
    assertEquals("*", subject.getMinutePattern());
    assertEquals("*", subject.getHourPattern());
    assertEquals("*", subject.getDayPattern());
    assertEquals("*", subject.getMonthPattern());
    assertEquals("*", subject.getDayOfWeekPattern());

    // Test missing fields (should default to wildcard)
    pattern = "5 10";
    subject = CronEntry.parse(pattern);
    assertEquals("5", subject.getMinutePattern());
    assertEquals("10", subject.getHourPattern());
    assertEquals("*", subject.getDayPattern());
    assertEquals("*", subject.getMonthPattern());
    assertEquals("*", subject.getDayOfWeekPattern());
    assertTrue(subject.minutePasses(5));
    assertFalse(subject.minutePasses(0));
    assertTrue(subject.hourPasses(10));
    assertTrue(subject.dayPasses(1));
  }




  /**
   * Test method for {@link coyote.commons.CronEntry#mayRunAt(java.util.Calendar)}.
   */
  @Test
  public void testMayRunAt() {
    StringBuffer b = new StringBuffer();
    Calendar cal = new GregorianCalendar();

    CronEntry subject = null;
    try {
      subject = CronEntry.parse(null);

      // set the minute pattern to the current minute
      subject.setMinutePattern(Integer.toString(cal.get(Calendar.MINUTE)));
      subject.setHourPattern(Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
      subject.setDayPattern(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
      subject.setMonthPattern(Integer.toString(cal.get(Calendar.MONTH) + 1));
      subject.setDayOfWeekPattern(Integer.toString(cal.get(Calendar.DAY_OF_WEEK) - 1));

      //System.out.println( subject );
      assertTrue(subject.mayRunAt(cal));
    } catch (ParseException e) {
      fail(e.getMessage());
    }

  }




  /**
   * Test method for {@link coyote.commons.CronEntry#mayRunNow()}.
   */
  @Test
  public void testMayRunNow() {
    String pattern = "* * * * *";
    CronEntry subject = null;
    try {
      subject = CronEntry.parse(pattern);
      assertTrue(subject.mayRunNow());

      subject = CronEntry.parse(null);
      Calendar cal = new GregorianCalendar();
      subject.setMinutePattern(Integer.toString(cal.get(Calendar.MINUTE)));
      subject.setHourPattern(Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
      subject.setDayPattern(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
      subject.setMonthPattern(Integer.toString(cal.get(Calendar.MONTH) + 1));
      subject.setDayOfWeekPattern(Integer.toString(cal.get(Calendar.DAY_OF_WEEK) - 1));
      assertTrue(subject.mayRunNow());

      //System.out.println( subject );      
    } catch (ParseException e) {
      fail(e.getMessage());
    }
  }




  /**
   * Test method for {@link coyote.commons.CronEntry#getNextTime()}.
   */
  @Test
  public void testGetNextTime() {
    CronEntry subject = new CronEntry();
    long millis;
    Calendar cal = new GregorianCalendar();

    cal.set(Calendar.MONTH, 0); // Java Calendar: 0=Jan
    cal.set(Calendar.DAY_OF_MONTH, 15);
    cal.set(Calendar.HOUR_OF_DAY, 11);
    cal.set(Calendar.MINUTE, 57);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    try {
      // parse an entry which allows / accepts all dates and times
      subject = CronEntry.parse(null);

      // set the pattern to only allow February runs (one month later)
      subject.setMonthPattern(Integer.toString(2)); // Cron: 2=Feb
      //System.out.println( subject );

      // cannot run on 1/15
      assertFalse(subject.mayRunAt(cal));

      millis = subject.getNextTime(cal);
      long now = System.currentTimeMillis();

      //assertTrue( ( millis - now ) <= 3600000 );

      Date date = new Date(millis);
      //System.out.println( millis + " - " + date );
    } catch (ParseException e) {
      fail(e.getMessage());
    }
  }




  /**
   * Test method for {@link coyote.commons.CronEntry#getNextInterval()}.
   */
  @Test
  public void testGetNextInterval() {
    CronEntry subject = new CronEntry();
    long millis;
    Calendar cal = new GregorianCalendar();

    //System.out.println();
    //System.out.println( subject.dump() );

    // set the pattern to one hour in the future
    cal.add(Calendar.HOUR_OF_DAY, 1);
    subject.setHourPattern(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))); // adjustment
    assertFalse(subject.mayRunNow());
    millis = subject.getNextInterval();
    // System.out.println( millis + " - " + formatElapsed( millis ) );
    assertTrue(millis <= 3600000);
    assertTrue(millis >= 0);
    //System.out.println();

    //System.out.println( "\r\n30 minute test Part 1" );
    subject = new CronEntry();
    subject.setMinutePattern("0,30");
    //System.out.println( subject.dump() );
    millis = subject.getNextInterval();
    //System.out.println( millis + " - " + formatElapsed( millis ) );
    assertTrue( millis <= 1800000,"30mP1 " + millis + "!<=1800000");
    assertTrue(millis >= 0);
    //System.out.println();

    //System.out.println( "\r\n30 minute test Part 2" );
    subject = new CronEntry();
    subject.setMinutePattern("*/30");
    //System.out.println( subject.dump() );
    millis = subject.getNextInterval();
    //System.out.println( millis + " - " + formatElapsed( millis ) );
    assertTrue( millis <= 1800000,"30mP2 " + millis + "!<=1800000");
    assertTrue(millis >= 0);
    //System.out.println();

    //System.out.println( "\r\n5 minute test" );
    subject = new CronEntry();
    subject.setMinutePattern("*/5");
    //System.out.println( subject.dump() );
    millis = subject.getNextInterval();
    //System.out.println( millis + " - " + formatElapsed( millis ) );
    assertTrue( millis <= 300000,"15m " + millis + "!<=300000");
    assertTrue(millis >= 0);
    //System.out.println();

    subject = new CronEntry();
    int hr = cal.get(Calendar.HOUR_OF_DAY);
    hr = (hr < 23) ? hr + 1 : 0;
    String hrp = Integer.toString(hr);
    //System.out.println( "HRP:" + hrp );
    subject.setHourPattern(hrp); // adjustment
    //System.out.println( subject.dump() );
    millis = subject.getNextInterval();
    //System.out.println( millis + " - " + formatElapsed( millis ) );
    assertTrue( millis <= 86400000,"1d " + millis + "!<=86400000");
    assertTrue(millis >= 0);
    //System.out.println();

  }




  /**
   *
   */
  @Test
  public void testParseRangeParam() {
    CronEntry subject = new CronEntry();
    try {
      subject.setHourPattern("30");
      fail("There are not 30 hours in a day");
    } catch (Exception e) {}
  }




  /**
   * Test method for {@link coyote.commons.CronEntry#getNext(TreeSet, int, int)}.
   */
  @Test
  public void testGetNext() {

    TreeSet<String> timemap = new TreeSet<String>();
    timemap.add("0");
    timemap.add("30");

    CronEntry subject = new CronEntry();
    int next = subject.getNext(timemap, 45, 59);
    assertTrue(next == 0);
    next = subject.getNext(timemap, 60, 59);
    assertTrue(next == 0);
    next = subject.getNext(timemap, -1, 59); // proper call to get 0
    assertTrue(next == 0);
    next = subject.getNext(timemap, 0, 59); // using 0 will miss 0
    assertTrue(next == 30);
    next = subject.getNext(timemap, 1, 59); // using 1 will miss 0
    assertTrue(next == 30);

    timemap.clear();
    timemap.add("12");
    timemap.add("13");
    timemap.add("0");

    // this is the preferred way to check for the start of a new period
    next = subject.getNext(timemap, -1, 59);
    assertTrue(next == 0);

    next = subject.getNext(timemap, 1, 59);
    assertTrue(next == 12);

    next = subject.getNext(timemap, 11, 59);
    assertTrue(next == 12);

    next = subject.getNext(timemap, 11, 15);//wrong size
    assertTrue(next == 12);
    next = subject.getNext(timemap, 11, 13);//wrong size
    assertTrue(next == 12);

  }




  @Test
  public void anotherTest() {
    CronEntry subject = new CronEntry();
    long millis;
    Calendar now = new GregorianCalendar();
    //now.add( Calendar.MINUTE, -15 );
    //System.out.println( "NOW:      " + DATEFORMAT.format( now.getTime() ) + " - " + CronEntry.toPattern( now ) );
    //System.out.println();

    Calendar cal = new GregorianCalendar();

    int hr = cal.get(Calendar.HOUR_OF_DAY);
    hr = (hr < 23) ? hr + 1 : 0;
    String hrp = Integer.toString(hr);
    //System.out.println( "HRP:" + hrp );

    // set the pattern to one hour in the future
    subject.setHourPattern(hrp); // adjustment
    //System.out.println(subject.dump());

    millis = subject.getNextTime(now);
    Date result = new Date(millis);

    //System.out.println();
    //System.out.println( "RESULT:   " + DATEFORMAT.format( result ) );
    //System.out.println( "INTERVAL: " + millis + " - " + CronEntryTest.formatElapsed( millis - nowmillis ) );
  }




  @Test
  public void hourPattern() {
    CronEntry subject = new CronEntry();

    try {
      subject.setHourPattern("24");
      fail("Allows invalid hour pattern");
    } catch (IllegalArgumentException expected) {
      // should be too large
    }

    try {
      subject.setHourPattern("-1");
      fail("Allows invalid hour pattern");
    } catch (IllegalArgumentException expected) {
      // should be too small
    }
    try {
      CronEntry.parse("* 24 * * *");
      fail("Allows invalid hour pattern");
    } catch (ParseException expected) {
      // should be too large
    }

    try {
      CronEntry.parse("* -1 * * *");
      fail("Allows invalid hour pattern");
    } catch (ParseException expected) {
      // should be too small
    }

  }




  @Test
  public void minutePattern() {
    CronEntry subject = new CronEntry();

    try {
      subject.setMinutePattern("60");
      fail("Allows invalid minute pattern");
    } catch (IllegalArgumentException expected) {
      // should be too large
    }

    try {
      subject.setMinutePattern("-1");
      fail("Allows invalid minute pattern");
    } catch (IllegalArgumentException expected) {
      // should be too small
    }

    try {
      CronEntry.parse("60 * * * *");
      fail("Allows invalid minute pattern");
    } catch (ParseException expected) {
      // should be too large
    }

    try {
      CronEntry.parse("-1 * * * *");
      fail("Allows invalid minute pattern");
    } catch (ParseException expected) {
      // should be too small
    }

  }




  @Test
  public void dowPattern() {
    CronEntry subject = new CronEntry();

    try {
      subject.setDayOfWeekPattern("7");
      fail("Allows invalid day of week pattern");
    } catch (IllegalArgumentException expected) {
      // should be too large
    }

    try {
      subject.setDayOfWeekPattern("-1");
      fail("Allows invalid day of week pattern");
    } catch (IllegalArgumentException expected) {
      // should be too small
    }
  }




  @Test
  public void dayPattern() {
    CronEntry subject = new CronEntry();
    try {
      subject.setDayPattern("32");
      fail("Allows invalid day of week pattern");
    } catch (IllegalArgumentException expected) {
      // should be too large
    }

    try {
      subject.setDayPattern("-1");
      fail("Allows invalid day of week pattern");
    } catch (IllegalArgumentException expected) {
      // should be too small
    }
    try {
      CronEntry.parse("* * 32 * *");
      fail("Allows invalid day of month pattern");
    } catch (ParseException expected) {
      // should be too large
    }

    try {
      CronEntry.parse("* * -1 * *");
      fail("Allows invalid day of month pattern");
    } catch (ParseException expected) {
      // should be too small
    }
  }




  @Test
  public void monthPattern() {
    CronEntry subject = new CronEntry();

    try {
      subject.setMonthPattern("13");
      fail("Allows invalid month pattern");
    } catch (IllegalArgumentException expected) {
      // should be too large
    }
    try {
      subject.setMonthPattern("-1");
      fail("Allows invalid month pattern");
    } catch (IllegalArgumentException expected) {
      // should be too small
    }
    try {
      CronEntry.parse("* * * 13 *");
      fail("Allows invalid month pattern");
    } catch (ParseException expected) {
      // should be too large
    }

    try {
      CronEntry.parse("* * * -1 *");
      fail("Allows invalid month pattern");
    } catch (ParseException expected) {
      // should be too small
    }
  }




  @Test
  public void dayOfWeekPattern() {
    CronEntry subject = new CronEntry();

    try {
      subject.setDayOfWeekPattern("7");
      fail("Allows invalid day of week pattern");
    } catch (Exception expected) {
      // should be too large
    }

    try {
      subject.setDayOfWeekPattern("-1");
      fail("Allows invalid day of week pattern");
    } catch (Exception expected) {
      // should be too small
    }

    try {
      CronEntry.parse("* * * * 7");
      fail("Allows invalid month pattern");
    } catch (ParseException expected) {
      // should be too large
    }

    try {
      CronEntry.parse("* * * * -1");
      fail("Allows invalid month pattern");
    } catch (ParseException expected) {
      // should be too small
    }

  }

}
