package utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

public class Utils extends TestBase {
	/** Creates a new instance of Utils */
    public Utils() {
    	super();
    }
    
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }
    	
	public static double sGetDecimalStringAnyLocaleAsDouble (String value) {

		
		if (Utils.isNullOrEmpty(value)) {
			return 0.0;
		}

		NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
		Number number;
		try {
			number = format.parse(value);
			double d = number.doubleValue();
			return d;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}

		
		
	}
	
}
