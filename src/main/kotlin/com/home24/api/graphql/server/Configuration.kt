package com.home24.api.graphql.server

import com.home24.api.graphql.server.fetchers.*
import com.home24.api.graphql.server.loaders.ArticlesLoader
import com.home24.api.graphql.server.models.CatalogDetailedArticle
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.dataloader.DataLoaderRegistry
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestTemplate
import java.io.File


/**
 * @author Kirill Komarov
 */

@Configuration
class MyConfiguration(private val restTemplateBuilder: RestTemplateBuilder) {

    @Bean("CatalogRestClient")
    fun provideRestTemplate(): RestTemplate? {
        return restTemplateBuilder.rootUri("http://catalog-service-staging.home24.net/api/v1/articles")
                .additionalInterceptors(LogRequests())
                .build()
    }

    @Bean
    fun articlesLoader(restTemplate: RestTemplate) = DataLoader(ArticlesLoader(restTemplate), DataLoaderOptions.newOptions())

    class LogRequests : ClientHttpRequestInterceptor {
        private val logger = LoggerFactory.getLogger(LogRequests::class.java)
        override fun intercept(request: HttpRequest?, body: ByteArray?, execution: ClientHttpRequestExecution?): ClientHttpResponse? {
            logger.debug("Request {} {}", request?.method, request?.uri)
            return execution?.execute(request, body)
        }
    }

    @Bean
    fun dataLoaderInstrumentation(dataLoaderRegistry: DataLoaderRegistry) = DataLoaderInstrumentation(dataLoaderRegistry)

    @Bean
    fun dataLoaderRegistry() = DataLoaderRegistry()

    @Bean
    fun graphqlSchema(registry: DataLoaderRegistry, articlesLoader: DataLoader<String, CatalogDetailedArticle>): GraphQLSchema? {
        registry.register("articlesLoader", articlesLoader)

        val schemaParser = SchemaParser()
        val schemaGenerator = SchemaGenerator()
        val schema = schemaParser.parse(File(javaClass.getResource("/schema.graphqls").toURI()))

        return schemaGenerator.makeExecutableSchema(
                schema,
                RuntimeWiring.newRuntimeWiring()
                        .type("Query", { builder -> builder.dataFetcher("article", ArticleFetcher(articlesLoader)) })
                        .type("Article", { builder ->
                            builder.dataFetcher("sku", ArticleSkuFetcher())
                                    .dataFetcher("name", ArticleNameFetcher())
                                    .dataFetcher("accessories", ArticleAccessoriesFetcher(articlesLoader))
                                    .dataFetcher("related", ArticleRelatedFetcher())
                        })
                        .build()

        )
    }

}