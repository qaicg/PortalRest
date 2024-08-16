package Ventas;

import java.sql.ResultSet;

import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.Data;
import utils.TestBase;

public class ValidacionPedidos extends TestBase {

  @Test(description="Se encarga de validar el pedido realizado en BBDD, pruebas de venta de la 1 a la 6", priority=1, groups = { "checkPedidoDB" })
  @Parameters({"shop","email","totalEsperado", "tipoServicio", "mesa", "totalEsperadoMasCargos", "repartoPermitido"})
  public void validarPedidoSimpleBD(String shop, String customerMail, String netAmount, int tipoServicio, @Optional ("") String mesa,
		  @Optional ("") String totalEsperadoMasCargos, @Optional ("") String repartoPermitido) {
	espera(5000); //#OSCAR -> Esperamos 5 segundos porque a veces tarda más en llegar el pedido. Evitamos falsos errores.
	if(customerMail.equalsIgnoreCase(""))customerMail=Data.getInstance().getNewUserMail();
  	System.out.println("Pedido a validar en BBDD"); //lastDocDocId
  	if(!totalEsperadoMasCargos.equalsIgnoreCase(""))netAmount=totalEsperadoMasCargos; //SI TIENE CARGOS USAMOS EL IMPORTE CON LOS CARGOS SUMADOS
  	if(repartoPermitido.contentEquals("false"))tipoServicio = 2; // SI ESTA PRUEBA VIENE DE UN DELIVERY CON REPARTO NO PERMITIDO, SE CONTINUA EN TIPO SERVICIO PARA RECOGER
  	
  	String SQL = "select dd.DocId,dd.ContactId,dd.Alias,dd.ShopId,dd.PosId,dd.CashBoxId,dd.Serie,dd.Number,dd.Z,dd.Date,dd.DocDate,dd.CurrencyId,dd.ExchangeRate, "
  			+ "dd.IsTaxIncluded ,dd.TaxesAmount ,dd.NetAmount,dd.BaseAmount ,dd.ServiceTypeId, dd.ServiceNumber from Doc__Doc dd, Con__Contact cc "
  			+ "where dd.DocId ="+Data.getInstance().getUltimoDocIdPlus()+"  and dd.ContactId =  cc.ContactId  and dd.IsTaxIncluded =1 "
  			+ "and dd.NetAmount = "+netAmount.replaceAll("€","").replaceAll(",",".").trim()
  			+" and dd.ServiceTypeId = "+tipoServicio+" and dd.ServiceNumber <> 0 and dd.ChannelId=1";
  	
  	ResultSet rs =  databaseConnection.ejecutarSQL(SQL,"DB"+shop); 

  	
  	 if (rs!=null) {
  		 try {		
  			if (rs.first()) {
  				log("Pedido encontrado y validado en BBDD -> DocId: " + Data.getInstance().getUltimoDocIdPlus() + " de "+ customerMail + " en " + "DB"+shop);
  			}else {
  				log("Error. No tenemos resultados para el pedido -> DocId: " + Data.getInstance().getUltimoDocIdPlus() + " de " + customerMail + " en " + "DB"+shop);
  				log(SQL);
  				Assert.assertTrue(false);
  			}
  			
  		} catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			log("Error validación pedidos 47");
  			Assert.assertTrue(false);
  		}finally {
  			databaseConnection.desconectar();
  		}
  	 }else {
  		log("Error validación pedidos 53");
  		 Assert.assertTrue(false);
  	 }
  }
}
