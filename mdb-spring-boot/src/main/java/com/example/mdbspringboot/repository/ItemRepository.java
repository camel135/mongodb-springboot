package com.example.mdbspringboot.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.example.mdbspringboot.model.GroceryItem;

public interface ItemRepository extends MongoRepository<GroceryItem, String> {

	// Consulta personalizada: Solo trae el nombre y la cantidad (proyección)
	// El 1 indica que se incluya, el 0 (por defecto) que se excluya
	@Query(value="{'name':'?0'}", fields="{'name':1, 'itemQuantity':1}")
	GroceryItem findItemByName(String name);

	// Consulta por categoría con filtro de campos
	@Query(value="{'category.name': '?0'}", fields="{'name':1, 'itemQuantity':1, 'category':1}")
	List<GroceryItem> findAll(String category);

	// Paginación: El método mágico que nos permite traer los datos por trozos
	Page<GroceryItem> findAll(Pageable pageable);

	long count();
}