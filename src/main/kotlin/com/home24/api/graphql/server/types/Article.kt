package com.home24.api.graphql.server.types

/**
 * @author Kirill Komarov
 */
class Article() {
    var sku = ""
    var name = ""
    var accessoriesSkus: MutableList<String> = mutableListOf()
    var related: MutableList<Article> = mutableListOf()

    constructor(
            sku: String,
            name: String = "",
            accessoriesSkus: MutableList<String> = mutableListOf(),
            related: MutableList<Article> = mutableListOf()
    ) : this() {
        this.sku = sku
        this.name = name
        this.accessoriesSkus = accessoriesSkus
        this.related = related
    }
}