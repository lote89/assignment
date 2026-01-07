package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class LegacyStoreManagerGateway {

  public void create(Store store) {
    createStoreOnLegacySystem(store);  
  }

  public void update(Store store) {
    updateStoreOnLegacySystem(store);
  }

  public void delete(Long id) {
    
    try {
      Path tempFile = Files.createTempFile("store-delete-" + id, ".txt");
      Files.write(tempFile, ("Store deleted: ID=" + id).getBytes());
      Files.delete(tempFile);
      System.out.println("Legacy delete emulated for Store ID: " + id);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void createStoreOnLegacySystem(Store store) {
    writeToFile(store, "CREATED");
  }

  public void updateStoreOnLegacySystem(Store store) {
    writeToFile(store, "UPDATED");
  }

  private void writeToFile(Store store, String operation) {
    try {
      Path tempFile = Files.createTempFile(store.name + "-" + operation, ".txt");
      String content = String.format("Store %s. [ name = %s ] [ items on stock = %d ]", 
          operation, store.name, store.quantityProductsInStock);
      Files.write(tempFile, content.getBytes());
      System.out.println(content);
      Files.delete(tempFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
