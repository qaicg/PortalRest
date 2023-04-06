package interfaces;

public interface ITipoPedido {
	/**
	 * id = 0 -> Hiopay
	 * id = 1 -> Pedido por Ahora 
	 * id = 2 -> Pedido por Más tarde con aviso por email
	 * id = 3 -> Pedido por día y hora
	 */
	public   int id = 0; 
	public   String label = "";
	public   String elementXpath = "";	
	
	public int getId();
	public void setId();
	
	public String getLabel();
	public void setLabel();
	
	public String getElementXpath();
	public void setElementXpath();
	
}
