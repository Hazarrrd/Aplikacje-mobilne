package com.example.promap

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FirestoreHandler {
    private val firestore = FirebaseFirestore.getInstance()


    fun createCity(name: String, X: Float, Y : Float) {
        createDocumentInCollectionWithData(
            "cities",
            name,
            mapOf("City" to name, "X" to X, "Y" to Y)
        )
    }

    fun getAllCities(): Task<QuerySnapshot> {
        return firestore.collection("/cities")
            .get()
    }

    private fun createDocumentInCollectionWithData(
        collectionName: String,
        documentName: String,
        data: Any
    ): Task<Void> {
        return firestore.collection(collectionName)
            .document(documentName)
            .set(data)
    }
}