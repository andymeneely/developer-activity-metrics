package edu.ncsu.csc.realsearch.apmeneel.devactivity.analysis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IterateOverDates {

	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static List<DateRange> getRanges(Date from, Date to, int numLevels) {
		List<DateRange> list = new ArrayList<DateRange>((int) Math.pow(2, numLevels) - 1);
		list.add(r(from, to));
		for (int level = 2; level <= numLevels; level++) {
			long timeChunk = (to.getTime() - from.getTime()) / level;
			for (int i = 0; i < level; i++) {
				list.add(r(from.getTime() + i * timeChunk, from.getTime() + (i + 1) * timeChunk));
			}
		}
		return list;
	}

	public static List<DateRange> getWindows(Date from, Date to, long windowWidthMS, long windowShiftMS,
			boolean keepSmallWindows) {
		List<DateRange> list = new ArrayList<DateRange>();
		for (long start = from.getTime(); start < to.getTime(); start += windowShiftMS) {
			long nextTo = start + windowWidthMS;
			boolean goodOne = true;
			if (nextTo > to.getTime()) {
				nextTo = to.getTime();
				goodOne = keepSmallWindows;
			}
			if (goodOne)
				list.add(r(start, nextTo));
		}
		if (list.size() > 0)
			list.get(list.size() - 1).to = to; // round off to last date
		return list;
	}

	private static DateRange r(Date from, Date to) {
		return new DateRange(from, to);
	}

	private static DateRange r(long from, long to) {
		return new DateRange(new Date(from), new Date(to));
	}

	public static class DateRange {
		private Date from;
		private Date to;

		public DateRange(Date from, Date to) {
			this.from = from;
			this.to = to;
		}

		public Date getFrom() {
			return from;
		}

		public Date getTo() {
			return to;
		}

		@Override
		public String toString() {
			return FORMAT.format(from) + " - " + FORMAT.format(to);
		}
	}

}
