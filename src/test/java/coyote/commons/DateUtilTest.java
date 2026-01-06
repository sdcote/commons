package coyote.commons;

//import static org.junit.Assert.*;


import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilTest {

    @Test
    public void testFormatElapsed() {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;
        long yearsInMilli = weeksInMilli * 52;

        assertNotNull((DateUtil.formatElapsed(yearsInMilli)));
        System.out.println(DateUtil.formatElapsed(yearsInMilli));
        assertTrue((DateUtil.formatElapsed(yearsInMilli).length() > 3));

        //		System.out.println(DateUtil.formatElapsed(yearsInMilli));
        //		System.out.println(DateUtil.formatElapsed(yearsInMilli*2));
        //		System.out.println(DateUtil.formatElapsed((long)(yearsInMilli*2.5)));

        //		System.out.println(DateUtil.formatElapsed(weeksInMilli));
        //		System.out.println(DateUtil.formatElapsed(weeksInMilli*2));
        //		System.out.println(DateUtil.formatElapsed(weeksInMilli*4));
        //		System.out.println(DateUtil.formatElapsed(weeksInMilli*6));
        //		System.out.println(DateUtil.formatElapsed((long)(weeksInMilli*6.5)));
        //		System.out.println(DateUtil.formatElapsed(1234567890L));

        assertEquals("2 wks 6 hrs 56 min 7.890 sec", DateUtil.formatElapsed(1234567890L));
    }

    @Test
    public void test() {

        Date date = DateUtil.parse("2017-06-16T13:32:19.504-04");
        assertNotNull(date);

        date = DateUtil.parse("2017-06-16T13:32:19.504");
        assertNotNull(date);

        date = DateUtil.parse("2017-06-16T13:32:19");
        assertNotNull(date);

        date = DateUtil.parse("2017-06-16T13:32:19");
        assertNotNull(date);

        date = DateUtil.parse("2017-06-16 13:32:19.504-04");
        assertNotNull(date);

        date = DateUtil.parse("2017-06-16 13:32:19.504");
        assertNotNull(date);

        date = DateUtil.parse("2017-06-16 13:32:19");
        assertNotNull(date);

        date = DateUtil.parse("2017-06-16");
        assertNotNull(date);

        String text = date.toString();
        date = DateUtil.parse(text);
        assertNotNull(date);

    }


}
