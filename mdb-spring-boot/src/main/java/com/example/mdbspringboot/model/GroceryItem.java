package com.example.mdbspringboot.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document("GroceryItem")
public class GroceryItem {

	@Id
	private String id;

	@NotBlank(message = "El nombre es obligatorio")
	private String name;

	@Min(value = 1, message = "La cantidad mínima es 1")
	private int quantity;

	@DBRef
	@NotNull(message = "La categoría no puede ser nula")
	private Category category;

	public GroceryItem(String id, String name, int quantity, Category category) {
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.category = category;
	}

	// Getters y Setters...
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getItemQuantity() { return quantity; }
	public void setItemQuantity(int quantity) { this.quantity = quantity; }
	public Category getCategory() { return category; }
	public void setCategory(Category category) { this.category = category; }
}