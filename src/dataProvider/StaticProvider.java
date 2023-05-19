package dataProvider;

import java.util.ArrayList;
import java.util.Iterator;

import org.testng.annotations.DataProvider;
import pedido.*;

public class StaticProvider {
	
	public static class ProductDataProvider {
		
		@DataProvider(name = "productProvider")
		public static Iterator<Product> createProduct(){
			//Kirin TA,Inedit Damm,Oyako Don,Fanta Taronja 30 ml
			ArrayList<Product> p = new ArrayList<Product>();
			p.add(new Product("Kirin TA", "1", "2.40"));
			p.add(new Product("Inedit Damm", "1", "2.40"));
			p.add(new Product("Oyako Don", "1", "2.5"));
			p.add(new Product("Fanta Taronja 30 ml", "1", "2.5"));
			return p.iterator();
		}
		
		
		public static ArrayList<Product> getProductProvider() {
			Iterator<Product> product = createProduct();
			ArrayList<Product> arrayProduct = new ArrayList<Product>();
			product.forEachRemaining(p -> {				
				arrayProduct.add(p);
			});	
			
			return arrayProduct;
		}
	}

}
