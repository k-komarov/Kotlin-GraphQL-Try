package com.home24.api.graphql.server.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSetter

/**
 * @author Kirill Komarov
 */
@JsonIgnoreProperties(allowSetters = true)
class CatalogDetailedArticlesListResponse {
    var results: MutableMap<String, CatalogDetailedArticle> = mutableMapOf()
        @JsonSetter
        set(value) {
            if (value is List<*>) {
                field = mutableMapOf()
            }
            field = value
        }
}