package com.home24.api.graphql.server.loaders

import com.home24.api.graphql.server.models.CatalogDetailedArticle
import com.home24.api.graphql.server.models.CatalogDetailedArticlesListResponse
import org.dataloader.BatchLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

/**
 * @author Kirill Komarov
 */
@Component
class ArticlesLoader(@Autowired val restClient: RestTemplate) : BatchLoader<String, CatalogDetailedArticle> {
    override fun load(keys: MutableList<String>?): CompletionStage<MutableList<CatalogDetailedArticle>> {
        val skus = keys?.joinToString(",") { k -> k.trim() }
        val responseEntity = restClient.getForEntity("/list/" + skus, CatalogDetailedArticlesListResponse::class.java)
        return CompletableFuture.supplyAsync { responseEntity.body.results.values.toMutableList() }
    }
}