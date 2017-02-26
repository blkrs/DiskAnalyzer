package com.diskscanner;

public class DiskSizeUtil {
	
	private static int precision = 10;
	
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
			txt = (float)((long)precision*my_size/KILO)/precision + "KB";
		}
		else if (my_size < GIGA) {
			txt = (float)((long)precision*my_size/MEGA)/precision + "MB";
		}
		else if (my_size < TERA) {
			txt = (float)((long)precision*my_size/GIGA)/precision + "GB";
		}
		else {
			txt = (long)((long)precision*my_size/TERA)/precision + "TB";
		}
		return txt;
	}

}
