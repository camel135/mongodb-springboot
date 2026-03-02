package com.example.mdbspringboot.repository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import com.example.mdbspringboot.model.GroceryItem;

@Component
public class CustomItemRepositoryImpl implements CustomItemRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public void updateItemQuantity(String name, float newQuantity) {
		Query query = new Query(Criteria.where("name").is(name));
		Update update = new Update().set("quantity", newQuantity);
		mongoTemplate.updateFirst(query, update, GroceryItem.class);
	}

	@Override
	public void groupItemsByCategoryAndSumQuantity() {
		// Paso 4: Agregación corregida para trabajar con @DBRef usando Lookup
		LookupOperation lookup = LookupOperation.newLookup()
				.from("categories")      // Colección origen
				.localField("category.$id") // Campo referencia en GroceryItem
				.foreignField("_id")     // Campo en la colección Category
				.as("category_data");    // Nombre del array resultante

		Aggregation aggregation = Aggregation.newAggregation(
				lookup,
				Aggregation.unwind("category_data"), // Aplanamos el array del join
				Aggregation.group("category_data.name").sum("quantity").as("totalQuantity"),
				Aggregation.project("totalQuantity").and("_id").as("categoryName")
		);

		AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "GroceryItem", Document.class);

		System.out.println("--- ESTADÍSTICAS POR CATEGORÍA (CON JOIN) ---");
		results.getMappedResults().forEach(doc ->
				System.out.println("Categoría: " + doc.get("categoryName") + " | Total: " + doc.get("totalQuantity")));
	}
}