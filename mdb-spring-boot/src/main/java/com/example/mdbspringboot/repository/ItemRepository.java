package com.example.mdbspringboot.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.example.mdbspringboot.model.GroceryItem;
import com.example.mdbspringboot.model.Category;

public interface ItemRepository extends MongoRepository<GroceryItem, String> {

	@Query(value="{'name':'?0'}", fields="{'name':1, 'quantity':1}")
	GroceryItem findItemByName(String name);

	// Corregido: Ahora busca por la referencia del objeto categoría
	List<GroceryItem> findAllByCategory(Category category);

	Page<GroceryItem> findAll(Pageable pageable);
}