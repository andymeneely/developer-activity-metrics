package edu.ncsu.csc.realsearch.apmeneel.devactivity.test;

import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.LoadSVNtoDB;

public class SVNDateParseTest {

	@Test
	public void actualDate() throws Exception {
		String dateStr = "2009-08-25T18:06:59.123456Z";
		Date date = LoadSVNtoDB.parseDate(dateStr);
		GregorianCalendar greg = new GregorianCalendar();
		greg.setTimeInMillis(date.getTime());
		assertEquals(2009, greg.get(GregorianCalendar.YEAR));
		assertEquals(GregorianCalendar.AUGUST, greg.get(GregorianCalendar.MONTH));
		assertEquals(25, greg.get(GregorianCalendar.DAY_OF_MONTH));
		assertEquals(18, greg.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(6, greg.get(GregorianCalendar.MINUTE));
		assertEquals(59, greg.get(GregorianCalendar.SECOND));

	}

	@Test
	public void anotherActualDate() throws Exception {
		String dateStr = "2008-09-11T19:11:20.483514Z";
		Date date = LoadSVNtoDB.parseDate(dateStr);
		GregorianCalendar greg = new GregorianCalendar();
		greg.setTimeInMillis(date.getTime());
		assertEquals(2008, greg.get(GregorianCalendar.YEAR));
		assertEquals(GregorianCalendar.SEPTEMBER, greg.get(GregorianCalendar.MONTH));
		assertEquals(11, greg.get(GregorianCalendar.DAY_OF_MONTH));
		assertEquals(19, greg.get(GregorianCalendar.HOUR_OF_DAY));
		assertEquals(11, greg.get(GregorianCalendar.MINUTE));
		assertEquals(20, greg.get(GregorianCalendar.SECOND));

	}
}
