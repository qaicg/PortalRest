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
    
	public static double round (double value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}
	
	public static double sGetDecimalStringAnyLocaleAsDouble (String value) {

		Locale theLocale = null;
		
		if (Utils.isNullOrEmpty(value)) {
			return 0.0;
		}

		try {
			theLocale = getLocale();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		NumberFormat numberFormat = DecimalFormat.getInstance(theLocale);
		Number theNumber;
		try {
			theNumber = numberFormat.parse(value);
			return theNumber.doubleValue();
		} catch (ParseException e) {
			// The string value might be either 99.99 or 99,99, depending on Locale.
			// We can deal with this safely, by forcing to be a point for the decimal separator, and then using Double.valueOf ...
			//http://stackoverflow.com/questions/4323599/best-way-to-parsedouble-with-comma-as-decimal-separator
			String valueWithDot = value.replaceAll(",",".");

			try {
				return Double.valueOf(valueWithDot);
			} catch (NumberFormatException e2)  {
				// This happens if we're trying (say) to parse a string that isn't a number, as though it were a number!
				// If this happens, it should only be due to application logic problems.
				// In this case, the safest thing to do is return 0, having first fired-off a log warning.
				return 0.0;
			}
		}
	}
	
//	public static void log(String s) {
//		System.out.println(s);
//		Reporter.log(s + "<br>");
//	}
//	
//	public static Locale getLocale() {
//		String language = getNavigatorLanguage();
//		Locale locale = new Locale(language, language.toUpperCase());
//		return locale;
//	}
//	
//	public static String getNavigatorLanguage() {
//		JavascriptExecutor executor = (JavascriptExecutor) driver;
//        String language = executor.executeScript("return window.navigator.userlanguage || window.navigator.language").toString();
//        //espera(1500);
//		
//        return language;
//	}
}
