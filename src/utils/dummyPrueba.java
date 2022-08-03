package utils;

import org.testng.annotations.Test;
import java.sql.ResultSet;
import java.sql.SQLException;


public class dummyPrueba {
  @Test
  public void testConexion()  {
	 DatabaseConnection databaseConnection = new DatabaseConnection();
	 databaseConnection.ENTORNODEFINIDO=DatabaseConnection.ENTORNOTEST;
	
	 ResultSet rs =  databaseConnection.ejecutarSQL("Select * from Product","DB48777"); // PRUEBA PARA OBTENER LOS PRODUCTOS DE LA BBDD
	 
	 if (rs!=null) {
		 
		 try {
			while (rs.next()) {
			        String x = rs.getString("Name");
			        System.out.println(x);		   
			    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			databaseConnection.desconectar();
		}
	 }
	
	 
  }
}