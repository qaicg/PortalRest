package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.testng.Assert;

//import com.mongodb.connection.Server;

import configuration.EnumServidor;
//import graphql.Assert;

public class DatabaseConnection {
		
	private static String urlConexion, userName, password;
	
	private String BBDD = "";
	
	private static Connection connection;
	
	public static Connection getConnection() {
		return connection;
	}

	private static PreparedStatement statement;
	
	public static PreparedStatement getStatement() {
		return statement;
	}

	public static void setStatement(PreparedStatement statement) {
		DatabaseConnection.statement = statement;
	}

	
	
	public void defineEntorno(int entorno, String database) {
		
		
		setConfigBBDD(database);
	}
	
	public void defineEntorno(String database) {
		
		
		
		setConfigBBDD(database);
	}
	
	public void defineEntorno(EnumServidor servidor, String database) {
		
		
		
		setConfigBBDD(database);
	}	
	
	private void setConfigBBDD(String database) {
		
		BBDD =  database;
		Data.getInstance().setBD(BBDD);
		
	}	
	//REALIZA CONEXIÓN AL ENTORNO CONFIGURADO
	public void conectar() {
		  try {
			  connection = DriverManager.getConnection(Data.getInstance().getConfigServer().getUrlConnexion()+Data.getInstance().getBD(),
					  Data.getInstance().getConfigServer().getUserName(), 
					  Data.getInstance().getConfigServer().getPassword()); 
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
	
	//EJECUTA LA SQL EN EL ENTORNO CONFIGURADO
	public boolean execute(String sql, String bd) {
			
		try {
			defineEntorno(bd);
			conectar();
		    statement = connection.prepareStatement(sql);
		    statement.execute();
		    return true;
  
		} catch (Exception e) {
		    e.printStackTrace();
		    return false;
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
		    return -1;
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
