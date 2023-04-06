package pedido;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graphql.Assert;
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
	
    private Map<String, String> avisoPorEmail = new HashMap<>(); //Notificación por mail para recoger el pedido realizado para más tarde
    
    
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
//			for(int i = 0; i <= product.size(); i++) {
//				this.setProduct(product.get(i));
//			}
			product.forEach(p ->  {
				this.setProduct(p);
			});
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