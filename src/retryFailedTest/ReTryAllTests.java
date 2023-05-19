package retryFailedTest;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNG;

import utils.TestBase;

public class ReTryAllTests extends TestBase implements ITestListener {
	private static       int count  = 0;
	private final static int maxTry = 1;
	
	public void onTestFailure(ITestResult iTestResult) {
	    System.out.println("I am in onTestFailure method " +  getTestMethodName(iTestResult) + " failed");
	    if (count < maxTry) {
	        count++;
	        TestNG tng = new TestNG();
	        tng.setDefaultTestName("RETRY TEST");
	        Class[] classes1 = { iTestResult.getTestClass().getRealClass() };
	        tng.setTestClasses(classes1);
	        tng.addListener(new TestListener());
	        tng.run();
	    }
	}
	
	private static String getTestMethodName(ITestResult result) {
        return result.getMethod().getConstructorOrMethod().getName();
    }

}
