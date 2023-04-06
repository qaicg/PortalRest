package pedido;

import Objects.ProductItem;
import graphql.Assert;
import utils.Utils;

public class Product extends ProductItem {
	//unidad;
	private String unidad;
	//precioPorUnidad: unidad x precio
	private String precioPorUnidad;

	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getUnidad() {
		return unidad;
	}
	
	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}
	
	public String getPrecioPorUnidad() {
		return precioPorUnidad;
	}
	
	/** Calucalamos el precio total del producto a comprar */
	public void setPrecioPorUnidad() {
		if(Utils.isNullOrEmpty(this.getPrecio()) || Utils.isNullOrEmpty(this.getUnidad())) {
			if(Utils.isNullOrEmpty(this.getPrecio())) {
				Utils.logStatic("Error no hemos encontrado el precio del producto: " + getNombre());
			} 
			
			if(Utils.isNullOrEmpty(this.getUnidad())) {
				Utils.logStatic("Error no hemos encontrado la unidad del producto: " + getNombre());
			}
			
			Assert.assertTrue(false);
		}
		
		double precio = Utils.sGetDecimalStringAnyLocaleAsDouble(this.getPrecio().split("€")[0]);
		precio = Utils.round(precio, 2);
		double precioTotal = Utils.round((precio * Utils.sGetDecimalStringAnyLocaleAsDouble(this.getUnidad())), 2);
		
		this.precioPorUnidad = String.format("%.2f", precioTotal).replace(".", ",") + " €"; //String.format("%.2f", precioTotal) permite guardar los 2 decimales
		//this.precioPorUnidad = String.valueOf(precioTotal).replace(".", ",") + " €";
		
		Utils.logStatic("El precio total de la compra del producto " + getNombre() + " --> " +precioPorUnidad);
	}

}
