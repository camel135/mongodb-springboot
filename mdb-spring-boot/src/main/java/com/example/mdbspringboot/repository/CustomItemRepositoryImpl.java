package com.example.mdbspringboot.repository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.example.mdbspringboot.model.GroceryItem;
import com.mongodb.client.result.UpdateResult;

@Component
public class CustomItemRepositoryImpl implements CustomItemRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	public void updateItemQuantity(String name, float newQuantity) {
		Query query = new Query(Criteria.where("name").is(name));
		Update update = new Update();
		update.set("quantity", newQuantity);

		UpdateResult result = mongoTemplate.updateFirst(query, update, GroceryItem.class);

		if(result == null)
			System.out.println("No documents updated");
		else
			System.out.println(result.getModifiedCount() + " document(s) updated..");

	}

	@Override
	public void groupItemsByCategoryAndSumQuantity() {
		// Creamos el pipeline de agregación
		Aggregation aggregation = Aggregation.newAggregation(
				// 1. Agrupamos por el nombre de la categoría y sumamos la cantidad
				Aggregation.group("category.name").sum("itemQuantity").as("totalQuantity"),
				// 2. Renombramos el ID resultante a "categoryName" para que sea legible
				Aggregation.project("totalQuantity").and("_id").as("categoryName")
		);

		AggregationResults<Document> results =
				mongoTemplate.aggregate(aggregation, "groceryItems", org.bson.Document.class);

		System.out.println("--- ESTADÍSTICAS POR CATEGORÍA ---");
		results.getMappedResults().forEach(doc -> {
			System.out.println("Categoría: " + doc.get("categoryName") +
					" | Cantidad Total: " + doc.get("totalQuantity"));
		});
	}




}
