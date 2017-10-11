package com.home24.api.graphql.server.models

import com.home24.api.graphql.server.types.Article

/**
 * @author Kirill Komarov
 */
class CatalogDetailedArticle {
    var sku = ""
    var name = ""
    var accessories: MutableList<String> = mutableListOf()
    var related: MutableList<Article> = mutableListOf()
    var parent: CatalogDetailedArticle? = null
}