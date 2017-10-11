package com.home24.api.graphql.server.fetchers

import com.home24.api.graphql.server.models.CatalogDetailedArticle
import com.home24.api.graphql.server.types.Article
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.PropertyDataFetcher
import org.dataloader.DataLoader
import java.util.concurrent.CompletableFuture

/**
 * @author Kirill Komarov
 */
class ArticleFetcher(private val articlesLoader: DataLoader<String, CatalogDetailedArticle>) : DataFetcher<CompletableFuture<Article>> {
    override fun get(environment: DataFetchingEnvironment?): CompletableFuture<Article>? {
        val sku = environment?.getArgument<String>("sku")
        return articlesLoader.load(sku).thenApply { ca -> Article(ca.sku, ca.name, ca.accessories, ca.parent!!.related) }
    }

}

class ArticleSkuFetcher : PropertyDataFetcher<CompletableFuture<String>>("sku") {
    override fun get(environment: DataFetchingEnvironment?): CompletableFuture<String>? {
        return CompletableFuture.completedFuture(environment?.getSource<Article>()?.sku)
    }
}

class ArticleNameFetcher : PropertyDataFetcher<CompletableFuture<String>>("name") {
    override fun get(environment: DataFetchingEnvironment?): CompletableFuture<String>? {
        return CompletableFuture.completedFuture(environment?.getSource<Article>()?.name)
    }
}

class ArticleAccessoriesFetcher(private val articlesLoader: DataLoader<String, CatalogDetailedArticle>) : PropertyDataFetcher<CompletableFuture<List<Article>>>("accessories") {
    override fun get(environment: DataFetchingEnvironment?): CompletableFuture<List<Article>>? {
        val accessoriesSkus = environment?.getSource<Article>()?.accessoriesSkus
        return articlesLoader.loadMany(accessoriesSkus).thenApply { accessories ->
                accessories.map { catalogDetailedArticle ->
                    Article(catalogDetailedArticle.sku, catalogDetailedArticle.name, catalogDetailedArticle.accessories)
                }
        }
    }
}

class ArticleRelatedFetcher : PropertyDataFetcher<List<Article>>("related") {
    override fun get(environment: DataFetchingEnvironment?): List<Article>? {
        val relatedArticles = environment?.getSource<Article>()?.related
        return relatedArticles?.map { article -> Article(article.sku, article.name, article.accessoriesSkus) }
    }
}