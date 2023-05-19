package pedido;

import java.util.ArrayList;
import java.util.List;

import Objects.ProductItem;
import graphql.Assert;
import utils.Utils;

public class Product extends ProductItem {
	//unidad;
	private String unidad;
	
	//precioPorUnidad: unidad x precio
	private String precioPorUnidad;
	
	private List<Formato> formatos = new ArrayList<Formato>();
	
	private Formato formatoSelected = null;

	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Product(String nombre, String unidad, String precio) {
		super.setNombre(nombre);
		this.setUnidad(unidad);
		super.setPrecio(precio);
		//this.setPrecioPorUnidad();
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
		
		Utils.logStatic("El precio total de la compra del producto " + getNombre() + " --> " +precioPorUnidad);
	}
	
	public void setPrecioPorUnidad(String price) {
		
		if(price.split("€")[0] == "€") {
			String newPrice = price.split("€")[1] + price.split("€")[0];
			price = newPrice.replace(".", ",");
		}
		
		double precio = Utils.sGetDecimalStringAnyLocaleAsDouble(price.split("€")[0]);
		precio = Utils.round(precio, 2);
		this.precioPorUnidad = String.format("%.2f", price).replace(".", ",") + " €"; 
	}

	public List<Formato> getFormatos() {
		return formatos;
	}

	public void setFormatos(List<Formato> formatos) {
		this.formatos = formatos;
		
		//cambiar el precio del producto por el precio del formato seleccionado 

	}
	
	public void setFormatos(Formato formato) {
		this.formatos.add(formato);
	}
	
	//Devuelve el formato seleccionado
	public Formato getProductFormatSelected() {
		formatos.stream().forEach(form -> {
			if(form.isSelected()) {
				formatoSelected = form;
			}
		});
		
		return formatoSelected;
	}
	
	//El producto tiene formato, cambiar el precio por el precio del formato
	public void updatePriceProductByPriceFormat(List<Formato> formatos) {
		for(int i = 0; i < formatos.size(); i++) {
			if(formatos.get(i).isSelected()) {
				updatePriceProductByPriceFormat(formatos.get(i));
			}
		}
	}
	
	//El producto tiene formato, cambiar el precio por el precio del formato
	public void updatePriceProductByPriceFormat(Formato formato) {
		Utils.logStatic("El formato del producto " + getNombre()+ " selecionado -->  " + formato.getNombre() + " " + formato.getPrecio());
		
		Utils.logStatic("El precio del producto antes de cambiarlo " + getPrecio());
		
		if(formato.isSelected()) {
			this.setPrecio(formato.getPrecio());
		}
		
		Utils.logStatic("El precio del producto despues de cambiarlo " + getPrecio());
		
		this.setPrecioPorUnidad();
		
		Utils.logStatic("El precio por unidad del producto  " + getPrecioPorUnidad());
	}
	
	//Validar nombre de los formatos del producto
	public boolean validateProductFormatName(String formatNameToValidate) {
		boolean isValidated = false;
		for(int i = 0; i < getFormatos().size(); i++) {
			if(getFormatos().get(i).getNombre().contentEquals(formatNameToValidate)) {
				isValidated = true;
				break;
			}
		}
		
		return isValidated;
	}
	
}
