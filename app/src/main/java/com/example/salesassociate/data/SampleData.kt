package com.example.salesassociate.data

import kotlinx.datetime.LocalDate

object SampleData {
    
    fun getSampleProducts(): List<Product> {
        return listOf(
            Product(
                item = "Aspirin",
                description = "Pain relief medicine for headaches and fever",
                units = "Box",
                category = "Medicines",
                amount = 50,
                costPerUnit = 5.99,
                wholesalePrice = 4.50,
                notes = "Popular item, always in demand",
                expiryDate = LocalDate.parse("2025-12-31"),
                barcode = "123456789",
                address = "aspirin.jpg"
            ),
            Product(
                item = "Vitamin C",
                description = "Immune support supplement",
                units = "Bottle",
                category = "Supplements",
                amount = 30,
                costPerUnit = 12.99,
                wholesalePrice = 9.75,
                notes = "High demand during flu season",
                expiryDate = LocalDate.parse("2025-06-30"),
                barcode = "987654321",
                address = "vitamin_c.jpg"
            ),
            Product(
                item = "Bandages",
                description = "First aid supplies for minor cuts",
                units = "Pack",
                category = "Medical Supplies",
                amount = 100,
                costPerUnit = 3.50,
                wholesalePrice = 2.80,
                notes = "Essential item for every pharmacy",
                expiryDate = LocalDate.parse("2026-01-15"),
                barcode = "456789123",
                address = "bandages.jpg"
            ),
            Product(
                item = "Paracetamol",
                description = "Fever and pain relief medication",
                units = "Box",
                category = "Medicines",
                amount = 75,
                costPerUnit = 4.99,
                wholesalePrice = 3.75,
                notes = "Basic medicine, always needed",
                expiryDate = LocalDate.parse("2025-08-20"),
                barcode = "789123456",
                address = "paracetamol.jpg"
            ),
            Product(
                item = "Omega-3",
                description = "Heart health supplement",
                units = "Bottle",
                category = "Supplements",
                amount = 25,
                costPerUnit = 18.99,
                wholesalePrice = 14.25,
                notes = "Premium supplement",
                expiryDate = LocalDate.parse("2025-10-15"),
                barcode = "321654987",
                address = "omega3.jpg"
            ),
            Product(
                item = "Thermometer",
                description = "Digital thermometer for temperature measurement",
                units = "Piece",
                category = "Medical Equipment",
                amount = 15,
                costPerUnit = 25.99,
                wholesalePrice = 19.50,
                notes = "High quality digital thermometer",
                expiryDate = LocalDate.parse("2027-05-10"),
                barcode = "147258369",
                address = "thermometer.jpg"
            )
        )
    }
    
    fun getSampleCustomers(): List<Customer> {
        return listOf(
            Customer(
                name = "John Smith",
                note = "Regular customer, prefers premium products",
                place = "Downtown",
                pharmacyName = "City Pharmacy"
            ),
            Customer(
                name = "Sarah Johnson",
                note = "Bulk orders for hospital supplies",
                place = "Medical District",
                pharmacyName = "Medical Supplies Plus"
            ),
            Customer(
                name = "Mike Wilson",
                note = "Small orders, pays cash",
                place = "Suburban Area",
                pharmacyName = "Neighborhood Drugstore"
            )
        )
    }
} 