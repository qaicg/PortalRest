package Objects;

import org.openqa.selenium.WebElement;

public class ProductItem {
	
	private String nombre, moneda, foto, descripcion;
	private String precio;
	private WebElement boton,imagenElement,add,minus;
	private String familia;
	
	
	
	
	
	public WebElement getAddButton() {
		return add;
	}




	public void setAddButton(WebElement add) {
		this.add = add;
	}




	public WebElement getMinus() {
		return minus;
	}




	public void setMinus(WebElement minus) {
		this.minus = minus;
	}




	public WebElement getImagenElement() {
		return imagenElement;
	}




	public void setImagenElement(WebElement imagenElement) {
		this.imagenElement = imagenElement;
	}




	public String getFamilia() {
		return familia;
	}




	public void setFamilia(String familia) {
		this.familia = familia;
	}




	public String getDescripcion() {
		return descripcion;
	}




	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}




	public WebElement getBoton() {
		return boton;
	}




	public void setBoton(WebElement boton) {
		this.boton = boton;
	}




	public String getNombre() {
		return nombre;
	}




	public void setNombre(String nombre) {
		this.nombre = nombre;
	}




	public String getMoneda() {
		return moneda;
	}




	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}




	public String getFoto() {
		return foto;
	}




	public void setFoto(String foto) {
		this.foto = foto;
	}




	public String getPrecio() {
		return precio;
	}




	public void setPrecio(String string) {
		this.precio = string;
	}


	
	


	@Override
	public boolean equals(Object obj) {
		ProductItem product = (ProductItem) obj;
		return product.nombre.equalsIgnoreCase(this.nombre);
	}

	
	public ProductItem(String nombre) {
		super();
		this.nombre = nombre;
	}



	public ProductItem(String nombre, String descripcion, String moneda, String foto, String precio) {
		super();
		this.nombre = nombre;
		this.moneda = moneda;
		this.foto = foto;
		this.precio = precio;
		this.descripcion = descripcion;
	}


	public ProductItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
