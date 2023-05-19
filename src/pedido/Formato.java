package pedido;

public class Formato {
	
	private String nombre;
	private String precio;
	private boolean isSelected;
		
	public Formato(String nombre, String precio) {
		super();
		this.nombre = nombre;
		this.precio = precio;
	}
	
	public Formato(String nombre, String precio, boolean isSelected) {
		super();
		this.nombre = nombre;
		this.precio = precio;
		this.isSelected = isSelected;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getPrecio() {
		return precio;
	}
	
	public void setPrecio(String precio) {
		this.precio = precio;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	
}
