package Ventas;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.annotations.Test;

public class Vende {
  @Test (dataProvider = "test1")
  public void PruebasVenta() {
  }
  
  @DataProvider(name = "test1" )
  public static Object[][] primeNumbers() {
     return new Object[][] {{2, true}, {6, false}, {19, true}, {22, false}, {23, true}};
  }
}
