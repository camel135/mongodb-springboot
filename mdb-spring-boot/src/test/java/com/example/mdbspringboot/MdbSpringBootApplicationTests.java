package com.example.mdbspringboot;

import static org.junit.jupiter.api.Assertions.*;

import com.example.mdbspringboot.model.Category;
import com.example.mdbspringboot.model.GroceryItem;
import com.example.mdbspringboot.repository.ItemRepository;
import com.example.mdbspringboot.repository.CategoryRepository;
import com.example.mdbspringboot.repository.CustomItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
@Transactional // Revierte los cambios en la base de datos tras cada test
class MdbSpringBootApplicationTests {

	@Autowired
	private ItemRepository itemRepo;

	@Autowired
	private CategoryRepository categoryRepo;

	@Autowired
	private CustomItemRepository customRepo;

	@BeforeEach
	void setup() {
		// Limpiamos las colecciones para que cada test sea independiente
		itemRepo.deleteAll();
		categoryRepo.deleteAll();
	}

	@Test
	void testCreateAndFindWithDBRef() {
		// 1. Guardar categoría
		Category cat = categoryRepo.save(new Category("snacks", "Aperitivos"));

		// 2. Guardar Item con referencia @DBRef
		GroceryItem item = new GroceryItem("ID1", "Test Cookie", 10, cat);
		itemRepo.save(item);

		// 3. Verificar recuperación y resolución de la referencia
		GroceryItem found = itemRepo.findItemByName("Test Cookie");
		assertNotNull(found);
		assertNotNull(found.getCategory());
		assertEquals("snacks", found.getCategory().getName());
	}

	@Test
	void testPaginationAndSorting() {
		Category cat = categoryRepo.save(new Category("test", "desc"));
		itemRepo.save(new GroceryItem("A", "Apple", 5, cat));
		itemRepo.save(new GroceryItem("B", "Banana", 10, cat));
		itemRepo.save(new GroceryItem("C", "Cherry", 15, cat));

		// Probar paginación: Página 0, tamaño 2, ordenado por nombre ASC
		Page<GroceryItem> page = itemRepo.findAll(PageRequest.of(0, 2, Sort.by("name").ascending()));

		assertEquals(2, page.getContent().size());
		assertEquals("Apple", page.getContent().get(0).getName());
		assertEquals(3, page.getTotalElements());
	}

	@Test
	void testCustomUpdateQuantity() {
		Category cat = categoryRepo.save(new Category("test", "desc"));
		itemRepo.save(new GroceryItem("U1", "Update Me", 5, cat));

		// Usar el repositorio personalizado para actualizar la cantidad
		customRepo.updateItemQuantity("Update Me", 99);

		GroceryItem updated = itemRepo.findItemByName("Update Me");
		assertEquals(99, updated.getItemQuantity());
	}

	@Test
	void testAggregationRunsSuccessfully() {
		Category cat = categoryRepo.save(new Category("aggr", "desc"));
		itemRepo.save(new GroceryItem("AG1", "Item A", 10, cat));
		itemRepo.save(new GroceryItem("AG2", "Item B", 20, cat));

		// Ejecutar agregación (Paso 4)
		assertDoesNotThrow(() -> customRepo.groupItemsByCategoryAndSumQuantity());
	}
}