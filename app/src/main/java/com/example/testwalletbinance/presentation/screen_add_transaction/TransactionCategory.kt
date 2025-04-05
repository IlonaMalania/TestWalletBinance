package com.example.testwalletbinance.presentation.screen_add_transaction

enum class TransactionCategory(val displayName: String) {
    GROCERIES("Groceries"),
    TAXI("Taxi"),
    ELECTRONICS("Electronics"),
    RESTAURANT("Restaurant"),
    TOP_UP("Top Up"),
    OTHER("Other");

    override fun toString(): String = displayName
}