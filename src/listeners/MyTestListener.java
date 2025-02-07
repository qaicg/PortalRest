package listeners;

import org.testng.*;
import org.testng.xml.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class MyTestListener implements ITestListener {
    private final Map<XmlTest, List<String>> failedTestsMap = new HashMap<>();
    private XmlSuite originalSuite;

    @Override
    public void onStart(ITestContext context) {
        // Guardamos la suite original
        this.originalSuite = context.getSuite().getXmlSuite();
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        ITestContext context = result.getTestContext();
        XmlTest originalTest = context.getCurrentXmlTest();
        String failedMethod = result.getMethod().getMethodName();
        failedTestsMap.computeIfAbsent(originalTest, k -> new ArrayList<>()).add(failedMethod);

    }
    
    @Override
    public void onFinish(ITestContext context) {
        if (!failedTestsMap.isEmpty()) {
        	generateRetrySuite();
        }
            
        }
        private void generateRetrySuite() {
            XmlSuite retrySuite = new XmlSuite();
            retrySuite.setName(originalSuite.getName() + "_Retry");
            retrySuite.setParallel(originalSuite.getParallel());
            retrySuite.setThreadCount(1);
            
            Map<String,String> originalParameters = originalSuite.getParameters();
            originalParameters.put("suiteName", retrySuite.getName());
            
            retrySuite.setParameters(originalParameters);
                        
            for (Map.Entry<XmlTest, List<String>> entry : failedTestsMap.entrySet()) {
            	
            	 XmlTest originalTest = entry.getKey();
                 List<String> failedMethods = entry.getValue();

                 // Crear nuevo test basado en el original
                 XmlTest retryTest = new XmlTest(retrySuite);
                 retryTest.setName(originalTest.getName() + "_Retry");
                 retryTest.setParallel(originalTest.getParallel());
                 retryTest.setThreadCount(originalTest.getThreadCount());
                 retryTest.setPreserveOrder(originalTest.getPreserveOrder());
                 retryTest.setParameters(originalTest.getLocalParameters());
            
            
                 List<XmlClass> classes = new ArrayList<>();
                 for (XmlClass originalClass : originalTest.getXmlClasses()) {
                     List<XmlInclude> methods = new ArrayList<>();
                     for (String methodName : failedMethods) {
                         methods.add(new XmlInclude(methodName));
                     }
                     XmlClass retryClass = new XmlClass(originalClass.getName());
                     //retryClass.setIncludedMethods(methods);
                     classes.add(retryClass);
                 }
                 retryTest.setXmlClasses(classes);
                 try (FileWriter writer = new FileWriter("src/failed/retry_suite_"+retrySuite.getName()+".xml")) {
                     writer.write(retrySuite.toXml());
                     System.out.println("Retry suite generated: retry_suite.xml");
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }
    }
            