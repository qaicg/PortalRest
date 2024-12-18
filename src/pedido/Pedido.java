package pedido;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.mysql.cj.util.StringUtils;

import utils.Utils;

public class Pedido {
	
	String numeroPedido;
	List<Product> product = new ArrayList<Product>();
	String servicio; // el servicio en Local, en la mesa...
	String fecha;
	String entrega;
	TipoPedido tipoPedido;
	String precioTotal;
	String propina;	
	
	String totalUnidades;
	
    public String getTotalUnidades() {
		return totalUnidades;
	}

	public void setTotalUnidades(String totalUnidades) {
		this.totalUnidades = totalUnidades;
	}
	
	
	public void setTotalUnidades() {
		int quantity = 0;
		for(Product p: product) {
			quantity +=  !StringUtils.isNullOrEmpty(p.getUnidad()) ? Integer.parseInt(p.getUnidad()) : 1;
		}
		
		this.totalUnidades = String.valueOf(quantity);
		
		System.out.println("Total unidades --> " + this.totalUnidades);
	}

	private Map<String, String> avisoPorEmail = new HashMap<String, String>(); //Notificación por mail para recoger el pedido realizado para más tarde
    
    
    public void setAvisoPorEmail(String remitenteEmail, String asuntoDelEmail, String mensajeDelMail) {
    	avisoPorEmail.put("remitenteEmail", remitenteEmail);
    	avisoPorEmail.put("asuntoDelEmail", asuntoDelEmail);
    	avisoPorEmail.put("mensajeDelMail", mensajeDelMail);
    }
    
    public Map<String, String> getAvisoPorEmail() {
    	return avisoPorEmail;
    }
	
	public String getNumeroPedido() {
		return numeroPedido;
	}
	
	public void setNumeroPedido(String numeroPedido) {
		this.numeroPedido = numeroPedido;
	}
	
	public List<Product> getProduct() {
		return product;
	}
	
	public void setProduct(List<Product> product) {
		if(product.size() > 0) {
			product.forEach(p ->  {
				this.setProduct(p);
			});
			
			this.setTotalUnidades();
		}
		else {
			Utils.logStatic("Error: No hemos encontrado producto en el pedido");
			Assert.assertTrue(false);
		}
		
	}
	
	public void setProduct(Product product) {
		this.product.add(product);
		
		if(!Utils.isNullOrEmpty(product.getPrecio())) {
			String sPrecioTotal = this.addProductPriceToPedido(product.getPrecio());
			this.setPrecioTotal(sPrecioTotal);
			
			Utils.logStatic("El precio total del pedido: " + this.getPrecioTotal());
			
		}
		else {
			Utils.logStatic("Error: El product "+ product.getNombre() +" no té preu.");
			Assert.assertTrue(false);
		}
	}
	
	public String getServicio() {
		return servicio;
	}
	
	public void setServicio(String servicio) {
		this.servicio = servicio;
	}
	
	public String getFecha() {
		return fecha;
	}
	
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	
	public String getEntrega() {
		return entrega;
	}
	
	public void setEntrega(String entrega) {
		this.entrega = entrega;
	}
	
	public TipoPedido getTipoPedido() {
		return tipoPedido;
	}
	
	public void setTipoPedido(TipoPedido tipoPedido) {
		this.tipoPedido = tipoPedido;
	}
	
	public String getPrecioTotal() {
		return precioTotal;
	}
	
	public void setPrecioTotal(String precioTotal) {
		
		if(precioTotal.split("€")[0] == "€") {
			String newPrice = precioTotal.split("€")[1] + precioTotal.split("€")[0];
			
			if(newPrice.indexOf(".") != -1)
				precioTotal = newPrice.replace(".", ",");
		}
		
		this.precioTotal = precioTotal;
	}
	
	public String getPropina() {
		return propina;
	}
	
	public void setPropina(String propina) {
		this.propina = propina;
	}
	
	//Añadir el precio del producto al del pedido
	private String addProductPriceToPedido(String price) {
		String totalPrice = null;
		double precioTotal;
		
		if(Utils.isNullOrEmpty(this.getPrecioTotal())) {
			precioTotal = Utils.round(Utils.sGetDecimalStringAnyLocaleAsDouble(price), 2);
		}
		else {
			//String sPtotal = this.getPrecioTotal().split("")
					
			double sPtotal = Utils.sGetDecimalStringAnyLocaleAsDouble(this.getPrecioTotal().split("€")[0]);
			
			sPtotal = Utils.round(sPtotal, 2);
			
			precioTotal = Utils.round((sPtotal + Utils.sGetDecimalStringAnyLocaleAsDouble(price)), 2);
			
		}
		
		totalPrice = String.format("%.2f", precioTotal).replace(".", ",") + " €"; //String.format("%.2f", precioTotal) permite guardar los 2 decimales
		
		Utils.logStatic("El precio total del pedido "  + " --> " + totalPrice);
		
		return totalPrice;
	}
}