package org.shekhar.trainings.xebibookstore.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.shekhar.trainings.xebibookstore.exceptions.BookNotInInventoryException;

public class ShoppingCartTest {

	private final InventoryManager inventoryManager = mock(InventoryManager.class);
	private final ShoppingCart cart = new ShoppingCart(inventoryManager);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void canAddMultipleBooksToTheShoppingCart() throws Exception {
		when(inventoryManager.exists("Effective Java")).thenReturn(true);
		when(inventoryManager.exists("OpenShift Cookbook")).thenReturn(true);

		cart.add("Effective Java");
		cart.add("OpenShift Cookbook");

		assertThat(cart.size(), is(equalTo(2)));

		verify(inventoryManager, times(1)).exists("Effective Java");
		verify(inventoryManager, times(1)).exists("OpenShift Cookbook");
		verifyNoMoreInteractions(inventoryManager);
	}

	@Test
	public void canAddMultipleBooksToTheShoppingCartInOneGo() throws Exception {
		when(inventoryManager.exists(anyString())).thenReturn(true);

		cart.add("Effective Java", "OpenShift Cookbook", "Java Concurrency in Practice");

		assertThat(cart.items(), hasSize(3));
		assertThat(cart.items(), hasItems("Effective Java", "OpenShift Cookbook", "Java Concurrency in Practice"));

		verify(inventoryManager, times(3)).exists(anyString());
		verify(inventoryManager, times(3)).exists(anyString());
	}

	@Test
	public void throwExceptionWhenBookAddedToTheCartDoesNotExistInInventory() throws Exception {
		String book = "Effective Java";
		when(inventoryManager.exists(book)).thenReturn(false);

		expectedException.expect(BookNotInInventoryException.class);
		expectedException.expectMessage(equalTo("Sorry, 'Effective Java' not in stock!!"));

		cart.add(book);
	}

	@Test
	public void throwExceptionWithMessageThatSpecifyAllBooksNotInInventory() throws Exception {
		when(inventoryManager.exists(anyString())).thenReturn(true, false, false);

		expectedException.expect(BookNotInInventoryException.class);
		expectedException.expectMessage(equalTo("Sorry, 'OpenShift Cookbook' not in stock!!\\nSorry, 'Java Concurrency in Practice' not in stock!!"));

		cart.add("Effective Java", "OpenShift Cookbook", "Java Concurrency in Practice");
	}

	@Test
	public void cartAmountIsEqualToSumOfAllItemPrices() throws Exception {
		when(inventoryManager.exists(anyString())).thenReturn(true);
		cart.add("OpenShift Cookbook", "Effective Java", "Clean Code");
		verify(inventoryManager, times(3)).exists(anyString());

		when(inventoryManager.bookPrice("OpenShift Cookbook")).thenReturn(45);
		when(inventoryManager.bookPrice("Effective Java")).thenReturn(30);
		when(inventoryManager.bookPrice("Clean Code")).thenReturn(55);

		int cartAmount = cart.amount();

		assertThat(cartAmount, is(equalTo(130)));
		
		verify(inventoryManager, times(3)).bookPrice(anyString());
		verifyNoMoreInteractions(inventoryManager);
	}

}