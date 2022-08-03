package Ventas;

import java.sql.ResultSet;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.Data;
import utils.TestBase;

public class ValidacionPedidos extends TestBase {

  @Test(description="Se encarga de validar el pedido realizado en BBDD, pruebas de venta de la 1 a la 6", priority=1)
  @Parameters({"shop","email","totalEsperado"})
  public void validarPedidoSimpleBD(String shop, String customerMail, String netAmount) {
	espera(1000);
	if(customerMail.equalsIgnoreCase(""))customerMail=Data.getInstance().getNewUserMail();
  	System.out.println("Pedido a validar en BBDD"); //lastDocDocId
  	String SQL = "select dd.DocId,dd.ContactId,dd.Alias,dd.ShopId,dd.PosId,dd.CashBoxId,dd.Serie,dd.Number,dd.Z,dd.Date,dd.DocDate,dd.CurrencyId,dd.ExchangeRate, "
  			+ "dd.IsTaxIncluded ,dd.TaxesAmount ,dd.NetAmount,dd.BaseAmount ,dd.ServiceTypeId, dd.ServiceNumber from Doc__Doc dd, Con__Contact cc "
  			+ "where dd.DocId ="+Data.getInstance().getUltimoDocIdPlus()+"  and dd.ContactId =  cc.ContactId and cc.Email ='"+customerMail+"' and dd.IsTaxIncluded =1 "
  			+ "and dd.NetAmount = "+netAmount.replaceAll("€","").replaceAll(",",".").trim()+" and dd.ServiceTypeId = 1 and dd.ServiceNumber <> 0 and dd.ChannelId=1";
  	
  	ResultSet rs =  databaseConnection.ejecutarSQL(SQL,"DB"+shop); 

  	
  	 if (rs!=null) {
  		 try {		
  			if (rs.first()) {
  				log("Pedido encontrado y validado en BBDD -> DocId: " + Data.getInstance().getUltimoDocId() + " de "+ customerMail);
  			}else {
  				log("Error. No tenemos resultados para el pedido -> DocId: " + Data.getInstance().getUltimoDocId() + " de " + customerMail);
  				log(SQL);
  				Assert.assertTrue(false);
  			}
  			
  		} catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}finally {
  			databaseConnection.desconectar();
  		}
  	 }	  
  }
}
