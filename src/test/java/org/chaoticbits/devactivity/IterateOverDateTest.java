package org.chaoticbits.devactivity;

import static org.chaoticbits.devactivity.analysis.IterateOverDates.FORMAT;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.chaoticbits.devactivity.analysis.IterateOverDates;
import org.chaoticbits.devactivity.analysis.IterateOverDates.DateRange;
import org.junit.Test;

public class IterateOverDateTest {

	@Test
	public void oneLevel() throws Exception {
		List<DateRange> ranges = IterateOverDates.getRanges(FORMAT.parse("2010-01-20"),
				FORMAT.parse("2010-05-19"), 1);
		assertEquals(1, ranges.size());
		assertEquals("2010-01-20", FORMAT.format(ranges.get(0).getFrom()));
		assertEquals("2010-05-19", FORMAT.format(ranges.get(0).getTo()));
	}

	@Test
	public void twoLevels() throws Exception {
		List<DateRange> ranges = IterateOverDates.getRanges(FORMAT.parse("2010-01-20"),
				FORMAT.parse("2010-05-19"), 2);
		assertEquals(3, ranges.size());
		assertEquals("2010-01-20", FORMAT.format(ranges.get(0).getFrom()));
		assertEquals("2010-05-19", FORMAT.format(ranges.get(0).getTo()));

		assertEquals("2010-01-20", FORMAT.format(ranges.get(1).getFrom()));
		assertEquals("2010-03-20", FORMAT.format(ranges.get(1).getTo()));

		assertEquals("2010-03-20", FORMAT.format(ranges.get(2).getFrom()));
		assertEquals("2010-05-19", FORMAT.format(ranges.get(2).getTo()));

	}

	@Test
	public void fourLevels() throws Exception {
		List<DateRange> ranges = IterateOverDates.getRanges(FORMAT.parse("2010-01-20"),
				FORMAT.parse("2010-05-19"), 4);
		assertEquals(10, ranges.size());
	}

	@Test
	public void windowsBigShift() throws Exception {
		long thirtyDays = 2592000000L;
		List<DateRange> ranges = IterateOverDates.getWindows(FORMAT.parse("2010-01-20"),
				FORMAT.parse("2010-05-19"), thirtyDays, thirtyDays, true);
		assertEquals(4, ranges.size());
		assertEquals("2010-01-20", FORMAT.format(ranges.get(0).getFrom()));
		assertEquals("2010-02-19", FORMAT.format(ranges.get(0).getTo()));
		assertEquals("2010-05-19", FORMAT.format(ranges.get(3).getTo()));
	}

	@Test
	public void windowsShortShift() throws Exception {
		long thirtyDays = 2592000000L;
		long ninetyDays = 7776000000L;
		List<DateRange> ranges = IterateOverDates.getWindows(FORMAT.parse("2010-01-20"),
				FORMAT.parse("2010-05-19"), ninetyDays, thirtyDays, true);
		assertEquals(4, ranges.size());
		assertEquals("2010-01-20", FORMAT.format(ranges.get(0).getFrom()));
		assertEquals("2010-04-20", FORMAT.format(ranges.get(0).getTo()));
		assertEquals("2010-02-19", FORMAT.format(ranges.get(1).getFrom()));
		assertEquals("2010-05-19", FORMAT.format(ranges.get(1).getTo()));
		assertEquals("2010-03-21", FORMAT.format(ranges.get(2).getFrom()));
		assertEquals("2010-05-19", FORMAT.format(ranges.get(2).getTo()));
		assertEquals("2010-04-20", FORMAT.format(ranges.get(3).getFrom()));
		assertEquals("2010-05-19", FORMAT.format(ranges.get(3).getTo()));

	}
	@Test
	public void windowsShortShiftNoKeep() throws Exception {
		long thirtyDays = 2592000000L;
		long ninetyDays = 7776000000L;
		List<DateRange> ranges = IterateOverDates.getWindows(FORMAT.parse("2010-01-20"),
				FORMAT.parse("2010-05-19"), ninetyDays, thirtyDays, false);
		assertEquals(1, ranges.size());
		assertEquals("2010-01-20", FORMAT.format(ranges.get(0).getFrom()));
		assertEquals("2010-05-19", FORMAT.format(ranges.get(0).getTo()));
	}
}
