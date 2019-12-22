package com.di7ak.instalikes.models

data class CategoriesModel(
    val categories: List<Category> = listOf()
)

data class Category(
    val name: String = "",
    var subcategories: List<Subcategory> = listOf()
)

data class Subcategory(
    val name: String = "",
    val tags: String = ""
)