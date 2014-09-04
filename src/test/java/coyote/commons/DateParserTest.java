package coyote.commons;

//import static org.junit.Assert.*;
import static org.junit.Assert.*;
import java.util.Date;

import org.junit.Test;

public class DateParserTest {

	@Test
	public void testParseDateTime() {
	Date datetime = DateParser.parse("12:00:00");
	assertNotNull(datetime);
	datetime = DateParser.parse("12:00:00.777");
	assertNotNull(datetime);
	datetime = DateParser.parse("12:00:00.77");
	assertNotNull(datetime);
	datetime = DateParser.parse("12:00:00.7");
	assertNotNull(datetime);
	datetime = DateParser.parse("12:00:00,777");
	assertNotNull(datetime);
	datetime = DateParser.parse("12:00:00,77");
	assertNotNull(datetime);
	datetime = DateParser.parse("12:00:00,7");
	assertNotNull(datetime);
	datetime = DateParser.parse("0:0:0,7");
	assertNotNull(datetime);
	datetime = DateParser.parse("0:0:0.7");
	assertNotNull(datetime);
	System.out.println(datetime);
	System.out.println(datetime.getTime());

	datetime = DateParser.parse("1/1/2006");
	assertNotNull(datetime);
	datetime = DateParser.parse("01/01/2006");
	assertNotNull(datetime);
	datetime = DateParser.parse("2006/1/1");
	assertNotNull(datetime);
	datetime = DateParser.parse("2006/01/01");
	assertNotNull(datetime);

	
	datetime = DateParser.parse("2006/01/01 01:01:01");
	assertNotNull(datetime);

	datetime = DateParser.parse("01/01/2006 01:01:01");
	assertNotNull(datetime);
	
	datetime = DateParser.parse("2006/01/01 01:01:01.777");
	assertNotNull(datetime);
	datetime = DateParser.parse("2006/01/01 01:01:01,777");
	assertNotNull(datetime);
	System.out.println(datetime);
	System.out.println(datetime.getTime());
	
	}

}
