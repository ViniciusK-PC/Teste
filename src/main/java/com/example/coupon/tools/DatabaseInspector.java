package com.example.coupon.tools;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class DatabaseInspector {
    public static void main(String[] args) {
        String uri = "mongodb+srv://viniciuskb35_db_user:0bImSAypxl6CZhRj@cluster.j1abcw7.mongodb.net/coupon_db";
        
        System.out.println("============================================================");
        System.out.println("          INSPECTING MONGODB CLOUD (JAVA NATIVE)");
        System.out.println("============================================================");

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("coupon_db");

            System.out.println("\n[TABELAS / COLECOES ENCONTRADAS]:");
            database.listCollectionNames().forEach(name -> System.out.println(" - " + name));

            System.out.println("\n[DADOS NA COLECAO 'coupons']:");
            List<Document> coupons = database.getCollection("coupons").find().limit(20).into(new ArrayList<>());
            
            if (coupons.isEmpty()) {
                System.out.println(" >> Nenhum cupom encontrado.");
            } else {
                System.out.println("------------------------------------------------------------");
                for (Document doc : coupons) {
                    System.out.printf(" ID: %-15s | Codigo: %-7s | Valor: %-8s | Expira: %s%n",
                            doc.get("_id"),
                            doc.get("code"),
                            doc.get("discountValue"),
                            doc.get("expirationDate"));
                }
                System.out.println("------------------------------------------------------------");
            }
        } catch (Exception e) {
            System.err.println("ERRO AO CONECTAR NO MONGODB: " + e.getMessage());
        }
        System.out.println("\n============================================================");
    }
}
