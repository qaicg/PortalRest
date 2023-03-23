package Ventas;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Reservas.MailReading;
import utils.Data;
import utils.TestBase;

public class PedidoPorDiaHora extends TestBase {
	boolean isValidatedTicket = false;
	
	@Test(priority = 1)
	@Parameters({"urlEmail", "emailCliente", "passwordCliente", "pedidoConfirmadoString"})
	public void consultarPedido(@Optional("") String urlEmail, String emailCliente, String passwordCliente, String pedidoConfirmadoString) {
	
	}

}
