package pedido;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Formato {
	
	private String nombre;
	private String precio;
	private boolean isSelected;
	
	private List<Modificador> modificadorList = new ArrayList<Modificador>();
	
	public Formato() {
		super();
	}	
	
	public Formato(String nombre) {
		super();
		this.nombre = nombre;
	}
		
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

	public List<Modificador> getModificadorList() {
		return modificadorList;
	}

	public void setModificadorList(List<Modificador> modificadorList) {
		this.modificadorList = modificadorList;
	}
	
	public void setModificadorList(Modificador modificador) {
		this.modificadorList.add(modificador) ;
	}	
	
}
