package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
	
	//POR DEFECTO DEJAMOS PARAMETROS DE ENTORNO PRODUCCIÓN
	private static String urlConexion="jdbc:mysql://213.99.41.243:3306/";
	private String BBDD = "";
	private static String userName="cloud";
	private static String password="Ve8HhyGigiW4LWsV";
	private static Connection connection;
	private static PreparedStatement statement;
	
	//EN EL CASO DE TENER MAS DE UN ENTORNO DEFINIMOS LOS TIPOS AQUÍ
	public static int ENTORNOPRODUCION = 1;
	public static int ENTORNOTEST = 2;
	public int ENTORNODEFINIDO=0;
	
	public void defineEntorno(int entorno, String database) {
		
		if (entorno == ENTORNOPRODUCION) {
			urlConexion = "jdbc:mysql://213.99.41.243:3306/"+database;
			userName = "cloud";
			password = "Ve8HhyGigiW4LWsV";
			BBDD =  database;
		}
		
		else if (entorno == ENTORNOTEST) {
			urlConexion = "jdbc:mysql://213.99.41.246:3306/"+database;
			userName = "cloud";
			password = "bWD1SIxfvcoS6TrZ";
			BBDD =  database;
		}
		
		Data.getInstance().setBD(BBDD);
	}
	
	public void defineEntorno(String database) {
		
		if (ENTORNODEFINIDO == ENTORNOPRODUCION) {
			urlConexion = "jdbc:mysql://213.99.41.243:3306/"+database;
			userName = "cloud";
			password = "Ve8HhyGigiW4LWsV";
			BBDD =  database;
		}
		
		else if (ENTORNODEFINIDO == ENTORNOTEST) {
			urlConexion = "jdbc:mysql://213.99.41.246:3306/"+database;
			userName = "cloud";
			password = "bWD1SIxfvcoS6TrZ";
			BBDD =  database;
		}else {
			System.out.println("NO HAY UN ENTORNO DEFINIDO VÁLIDO");
		}
		
		Data.getInstance().setBD(BBDD);
	}
	
	
	
	//REALIZA CONEXIÓN AL ENTORNO CONFIGURADO
	public void conectar() {
		  try {
			  connection = DriverManager.getConnection(urlConexion, userName, password); 
			} catch (SQLException e) {
			    throw new IllegalStateException("Cannot connect the database!", e);
			} 
	}
	
	//EJECUTA LA SQL EN EL ENTORNO CONFIGURADO
	public ResultSet ejecutarSQL(String sql, String bd) {
			
		try {
			defineEntorno(bd);
			conectar();
		    statement = connection.prepareStatement(sql);
		    statement.execute(sql);
		    ResultSet rs = statement.executeQuery(sql);
		    return rs;
  
		} catch (Exception e) {
		    e.printStackTrace();
		    return null;
		} 
	}
	
	public int ejecutaUpdate(String sql, String bd) {
		
		try {
			defineEntorno(bd);
			conectar();
		    statement = connection.prepareStatement(sql);
		    return statement.executeUpdate(sql);

		} catch (Exception e) {
		    e.printStackTrace();
		    return 0;
		} 
	
	}
		
	//EJECUTA DESONCEXION DE BASE DE DATOS, SE DEBE LLAMAR AL TERMINAR DE USAR LA CONEXIÓN. NO SE PUEDE LLAMAR ANTES SDE LEER LOS DATOS DEVUELTOS POR LAS CONSULTAS.
	public void desconectar() {
		 try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
