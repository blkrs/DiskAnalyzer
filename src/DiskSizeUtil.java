
public class DiskSizeUtil {
	
	private static long kilo = 1024;
	private static long mega = kilo*kilo;
	private static long giga = mega * kilo;
	private static long tera = giga * kilo;

	public static String humanReadableSize(long my_size) {
		String txt = "";
		
		
		if (my_size < kilo)
		{
			txt = my_size + " bytes";
		} else if (my_size < mega)
		{
			txt = (long)(my_size/kilo) + "KB"; 
		}else if (my_size < giga)
		{
			txt = (long)(my_size/mega) + "MB"; 
		}else if (my_size < tera)
		{
			txt = (long)(my_size/giga) + "GB"; 
		}else 
		{
			txt = (long)(my_size/tera) + "TB"; 
		}
			
		return txt;
	}

}
