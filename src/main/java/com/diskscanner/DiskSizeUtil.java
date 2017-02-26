package com.diskscanner;

public class DiskSizeUtil {
	
	private static final long KILO = 1024;
	private static final long MEGA = KILO * KILO;
	private static final long GIGA = MEGA * KILO;
	private static final long TERA = GIGA * KILO;

	public static String humanReadableSize(long my_size) {
		String txt = "";
		if (my_size < KILO) {
			txt = my_size + " bytes";
		}
		else if (my_size < MEGA) {
			txt = (long)(my_size/ KILO) + "KB";
		}
		else if (my_size < GIGA) {
			txt = (long)(my_size/ MEGA) + "MB";
		}
		else if (my_size < TERA) {
			txt = (long)(my_size/ GIGA) + "GB";
		}
		else {
			txt = (long)(my_size/TERA) + "TB";
		}
		return txt;
	}

}
