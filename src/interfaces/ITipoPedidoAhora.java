package interfaces;

public interface ITipoPedidoAhora extends ITipoPedido {
	public   int id = 1;
	public   String label = "Ahora";
	public   String elementXpath = "//app-when-deliver/div/div[2]/div/div/button[1]/div[1]";	
}
