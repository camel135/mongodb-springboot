package com.example.mdbspringboot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.example.mdbspringboot.model.Category;
import com.example.mdbspringboot.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.example.mdbspringboot.model.GroceryItem;
import com.example.mdbspringboot.repository.CustomItemRepository;
import com.example.mdbspringboot.repository.ItemRepository;

@SpringBootApplication
@EnableMongoRepositories
public class MdbSpringBootApplication implements CommandLineRunner {

    @Autowired
    ItemRepository groceryItemRepo;

    @Autowired
    CustomItemRepository customRepo;

    @Autowired
    CategoryRepository categoryRepo;

    List<GroceryItem> itemList = new ArrayList<GroceryItem>();

    public static void main(String[] args) {
        SpringApplication.run(MdbSpringBootApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // 1. Limpieza de datos previos
        groceryItemRepo.deleteAll();
        categoryRepo.deleteAll();

        System.out.println("-------------CREATE GROCERY ITEMS-------------------------------\n");
        createGroceryItems();

        System.out.println("\n----------------SHOW ALL GROCERY ITEMS---------------------------\n");
        showAllGroceryItems();

        System.out.println("\n-----------PAGINACIÓN Y ORDENACIÓN (PASO 3)----------------------\n");
        showAllGroceryItemsWithPagination();

        System.out.println("\n--------------GET ITEM BY NAME-----------------------------------\n");
        getGroceryItemByName("Whole Wheat Biscuit");

        System.out.println("\n-----------GET ITEMS BY CATEGORY---------------------------------\n");
        // Nota: Con @DBRef, el filtrado por nombre se hace mejor en memoria o con Aggregation
        getItemsByCategory("millets");

        System.out.println("\n-----------UPDATE CATEGORY NAME OF ALL GROCERY ITEMS----------------\n");
        updateCategoryName("snacks");

        System.out.println("\n-----------UPDATE QUANTITY OF A GROCERY ITEM------------------------\n");
        updateItemQuantity("Bonny Cheese Crackers Plain", 10);

        System.out.println("\n----------DELETE A GROCERY ITEM----------------------------------\n");
        deleteGroceryItem("Kodo Millet");

        System.out.println("\n------------FINAL COUNT OF GROCERY ITEMS-------------------------\n");
        findCountOfGroceryItems();

        System.out.println("\n-----------AGREGACIONES (PASO 4)---------------------------------\n");
        customRepo.groupItemsByCategoryAndSumQuantity();

        System.out.println("\n-------------------THANK YOU---------------------------");
    }

    // --- OPERACIONES CRUD ---

    void createGroceryItems() {
        System.out.println("Data creation started...");

        // Guardamos categorías primero para que tengan ID
        Category snacks = categoryRepo.save(new Category("snacks", "Aperitivos variados"));
        Category millets = categoryRepo.save(new Category("millets", "Cereales y granos"));
        Category spices = categoryRepo.save(new Category("spices", "Especias del mundo"));

        // Guardamos los items usando el constructor (ID, Nombre, Cantidad, Categoría)
        groceryItemRepo.save(new GroceryItem("WWB", "Whole Wheat Biscuit", 5, snacks));
        groceryItemRepo.save(new GroceryItem("KM", "XYZ Kodo Millet", 2, millets));
        groceryItemRepo.save(new GroceryItem("DWR", "Dried Whole Red Chilli", 2, spices));
        groceryItemRepo.save(new GroceryItem("HPM", "Healthy Pearl Millet", 1, millets));
        groceryItemRepo.save(new GroceryItem("BCC", "Bonny Cheese Crackers Plain", 6, snacks));

        System.out.println("Data creation complete...");
    }

    public void showAllGroceryItems() {
        itemList = groceryItemRepo.findAll();
        itemList.forEach(item -> System.out.println(getItemDetails(item)));
    }

    public void showAllGroceryItemsWithPagination() {
        System.out.println("--- MOSTRANDO PAGINADO (Página 0, Tamaño 2, Orden ASC) ---");
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());
        Page<GroceryItem> page = groceryItemRepo.findAll(pageable);

        System.out.println("Página actual: " + page.getNumber());
        System.out.println("Total de elementos: " + page.getTotalElements());

        page.getContent().forEach(item ->
                System.out.println("Item: " + item.getName() + " | Cantidad: " + item.getItemQuantity())
        );
    }

    public void getGroceryItemByName(String name) {
        System.out.println("Getting item by name: " + name);
        GroceryItem item = groceryItemRepo.findItemByName(name);
        if(item != null) {
            System.out.println(getItemDetails(item));
        } else {
            System.out.println("Item no encontrado.");
        }
    }

    public void getItemsByCategory(String categoryName) {
        System.out.println("Getting items for the category: " + categoryName);
        // Debido al uso de @DBRef, filtramos en Java para asegurar precisión si el Repository falla
        List<GroceryItem> allItems = groceryItemRepo.findAll();
        allItems.stream()
                .filter(item -> item.getCategory() != null && categoryName.equals(item.getCategory().getName()))
                .forEach(item -> System.out.println("Name: " + item.getName() + ", Quantity: " + item.getItemQuantity()));
    }

    public void findCountOfGroceryItems() {
        long count = groceryItemRepo.count();
        System.out.println("Number of documents in the collection = " + count);
    }

    public void updateCategoryName(String oldCategoryName) {
        System.out.println("Updating items from category '" + oldCategoryName + "' to 'munchies'");

        // Creamos la nueva categoría
        Category newCat = categoryRepo.save(new Category("munchies", "Aperitivos crujientes"));

        List<GroceryItem> allItems = groceryItemRepo.findAll();
        List<GroceryItem> itemsToUpdate = new ArrayList<>();

        allItems.forEach(item -> {
            if (item.getCategory() != null && oldCategoryName.equals(item.getCategory().getName())) {
                item.setCategory(newCat);
                itemsToUpdate.add(item);
            }
        });

        if (!itemsToUpdate.isEmpty()) {
            groceryItemRepo.saveAll(itemsToUpdate);
            System.out.println("Successfully updated " + itemsToUpdate.size() + " items.");
        }
    }

    public void updateItemQuantity(String name, float newQuantity) {
        System.out.println("Updating quantity for " + name);
        customRepo.updateItemQuantity(name, newQuantity);
    }

    public void deleteGroceryItem(String name) {
        // En tu ejemplo se pasaba el nombre, pero deleteById usa la ID.
        // Buscamos el item primero para obtener su ID.
        GroceryItem item = groceryItemRepo.findItemByName(name);
        if(item != null) {
            groceryItemRepo.deleteById(item.getId());
            System.out.println("Item '" + name + "' deleted.");
        }
    }

    public String getItemDetails(GroceryItem item) {
        String catName = (item.getCategory() != null) ? item.getCategory().getName() : "N/A";
        return String.format("Item Name: %s, Quantity: %d, Category: %s",
                item.getName(), item.getItemQuantity(), catName);
    }
}