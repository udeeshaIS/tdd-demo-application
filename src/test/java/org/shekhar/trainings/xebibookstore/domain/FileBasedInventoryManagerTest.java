package org.shekhar.trainings.xebibookstore.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FileBasedInventoryManagerTest {

	private InventoryManager inventoryManager = new FileBasedInventoryManager("src/test/resources/books.txt");

	@Test
	public void existsShouldReturnTrueWhenBookExistsInInventory() throws Exception {
		assertTrue(inventoryManager.exists("OpenShift Cookbook"));
	}
	
	

}