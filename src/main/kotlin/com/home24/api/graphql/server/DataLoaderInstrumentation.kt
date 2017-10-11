package com.home24.api.graphql.server

import graphql.ExecutionResult
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.NoOpInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationExecutionStrategyParameters
import org.dataloader.DataLoaderRegistry
import java.util.concurrent.CompletableFuture

/**
 * @author Kirill Komarov
 */
class DataLoaderInstrumentation(private val dataLoaderRegistry: DataLoaderRegistry) : NoOpInstrumentation() {

    override fun beginExecutionStrategy(parameters: InstrumentationExecutionStrategyParameters?): InstrumentationContext<CompletableFuture<ExecutionResult>> {
        return InstrumentationContext { _, t ->
            if (t == null) {
                dataLoaderRegistry.dispatchAll()
            }
        }
    }

}