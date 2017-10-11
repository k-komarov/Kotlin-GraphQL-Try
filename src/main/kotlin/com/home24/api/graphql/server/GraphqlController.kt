package com.home24.api.graphql.server

import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author Kirill Komarov
 */
@RestController
class GraphqlController(
        private val schema: GraphQLSchema,
        private val dataLoaderInstrumentation: DataLoaderInstrumentation) {

    @RequestMapping("/graphql")
    fun graphql(@RequestBody executionInput: MyExecutionInput): ExecutionResult? {
        val graphQL = GraphQL.newGraphQL(schema)
                .instrumentation(dataLoaderInstrumentation)
                .build()

        return graphQL.execute(executionInput)
    }
}