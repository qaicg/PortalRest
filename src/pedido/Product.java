package pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//import com.mysql.cj.protocol.Warning;

import Objects.ProductItem;
import graphql.Assert;
//import utils.TestBase;
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
	}
	
	public Product(String nombre) {
		super.setNombre(nombre);
	}
	
	public Product(String nombre, String precio) {
		super.setNombre(nombre);
		super.setPrecio(precio);
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
		double precio;
		double precioTotal;
		if(Utils.isNullOrEmpty(this.getPrecio()) || Utils.isNullOrEmpty(this.getUnidad())) {
			if(Utils.isNullOrEmpty(this.getPrecio())) {
				Utils.logStatic("Warning. No encontrado el precio del producto: " + getNombre());
				//getExtentTest().warning("Warning. No hemos encontrado el precio del producto: " + getNombre());
				//getExtentTest().error("Warning. No hemos encontrado el precio del producto: " + getNombre());
			} 
			
			if(Utils.isNullOrEmpty(this.getUnidad())) {
				Utils.logStatic("Warning. No hemos encontrado la unidad del producto: " + getNombre());
				//getExtentTest().warning("Warning. No hemos encontrado la unidad del producto: " + getNombre());
				//getExtentTest().error("Warning. No hemos encontrado la unidad del producto: " + getNombre());
			}
			this.precioPorUnidad = "0.00" + " €";
			Assert.assertTrue(true);
			return;
		}
		else {
			precio = Utils.sGetDecimalStringAnyLocaleAsDouble(this.getPrecio().split("€")[0]);
			precio = Utils.round(precio, 2);
			precioTotal = Utils.round((precio * Utils.sGetDecimalStringAnyLocaleAsDouble(this.getUnidad())), 2);
			
			this.precioPorUnidad = String.format("%.2f", precioTotal).replace(".", ",") + " €"; //String.format("%.2f", precioTotal) permite guardar los 2 decimales
			
			Utils.logStatic("El precio total de la compra del producto " + getNombre() + " --> " +precioPorUnidad);
		}
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
	
	//Devuelve el formato seleccionado	para el producto
	public Formato getProductFormatSelected() {
		return formatoSelected;
	}
	
	//Definir el formato seleccionado	para el producto
	public void setProductFormatSelected() {
		formatos.stream().forEach(form -> {
			if(form.isSelected()) {
				formatoSelected = form;
			}
		});	
		
		if(Objects.isNull(formatoSelected)) { //No hemos encontrado formato preseleccionado por defecto
			System.out.println("No hemos encontrado formato preseleccionado por defecto");
			System.out.println("Volver a preseleccionar el formato 0 de la lista de formatos ");
			setformatoSelectPorDefecto();
			setProductFormatSelected();
		}
	}
	
	//Definir el formato seleccionado	para el producto
	public void SetProductFormatSelected(Formato productFormatSelected) {
		formatoSelected = productFormatSelected;
		
	}
	
	//Definir el el formato uno a seleccionar por defecto
	public void setformatoSelectPorDefecto() {
		if(Objects.isNull(getProductFormatSelected())) {
			getFormatos().get(0).setSelected(true);
		}
	}
		
	//Definir el el formato uno a seleccionar por defecto
	public void setformatoSelectPorDefecto(int i) {
		if(Objects.isNull(getProductFormatSelected())) {
			getFormatos().get(i).setSelected(true);
		}
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
