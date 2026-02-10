package top.teanli.lightfalling.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import java.util.concurrent.CompletableFuture

/**
 * <p> DSL for building Brigadier commands more easily in Kotlin. </p>
 * @author TeAnli
 */
/**
 * Helper functions to build command trees with a more Kotlin-friendly syntax.
 *
 * Example usage:
 * ```
 * literal("mycommand") {
 *      //Add your arguments and execution logic here
 * }
 */
fun <S : ArgumentBuilder<FabricClientCommandSource, S>> S.literal(
    name: String,
    block: LiteralArgumentBuilder<FabricClientCommandSource>.() -> Unit = {}
): S {
    val builder = ClientCommandManager.literal(name)
    builder.block()
    return then(builder)
}

/**
 * Adds an argument to the command.
 *
 * Example usage:
 * ```
 * // in literal block
 * argument("argName", StringArgumentType.word()) {
 *     // Add suggestions or execution logic here
 * }
 * ```
 */
fun <S : ArgumentBuilder<FabricClientCommandSource, S>, T : Any> S.argument(
    name: String,
    type: ArgumentType<T>,
    block: RequiredArgumentBuilder<FabricClientCommandSource, T>.() -> Unit = {}
): S {
    val builder: RequiredArgumentBuilder<FabricClientCommandSource, T> = ClientCommandManager.argument(name, type)
    builder.block()
    return then(builder)
}

/**
 * Adds an execution block to the command.
 *
 * Example usage:
 * ```
 * // in argument block
 * execute { context ->
 *     // Your execution logic here
 *     1 // Return a result code
 * }
 */
fun <S : ArgumentBuilder<FabricClientCommandSource, S>> S.execute(
    block: (CommandContext<FabricClientCommandSource>) -> Int
): S {
    return executes { block(it) }
}

/**
 * Simple suggestion helper for custom suggestions.
 * The lambda provides the command context and suggestions builder.
 *
 * Example usage:
 * ```
 * // in argument block
 * suggest { context, builder ->
 *    // Custom suggestion logic
 *    builder.suggest("option1")
 *    builder.suggest("option2")
 *    builder.buildFuture()
 * }
 * ```
 * @return RequiredArgumentBuilder<FabricClientCommandSource, T>
 */
fun <T : Any> RequiredArgumentBuilder<FabricClientCommandSource, T>.suggest(
    block: (CommandContext<FabricClientCommandSource>, SuggestionsBuilder) -> CompletableFuture<Suggestions>
): RequiredArgumentBuilder<FabricClientCommandSource, T> {
    return suggests(block)
}

/**
 * Simple suggestion helper for a list of strings.
 * The lambda provides the command context if needed.
 *
 * Example usage:
 * ```
 * // in argument block
 * suggestList { context ->
 *    listOf("option1", "option2", "option3")
 * }
 * ```
 *
 * @return RequiredArgumentBuilder<FabricClientCommandSource, T>
 */
fun <T : Any> RequiredArgumentBuilder<FabricClientCommandSource, T>.suggestList(
    list: (CommandContext<FabricClientCommandSource>) -> Collection<String>
): RequiredArgumentBuilder<FabricClientCommandSource, T> {
    return suggests { context, builder ->
        list(context).forEach { builder.suggest(it) }
        builder.buildFuture()
    }
}

/**
 * Shortcut for word string argument
 * Word string captures a single word (non-space characters) from the input.
 * @return ArgumentType<String>
 */
fun word(): ArgumentType<String> = StringArgumentType.word()

/**
 * Shortcut for greedy string argument
 * Greedy string captures the rest of the input, including spaces, until the end of the command.
 * @return ArgumentType<String>
 */
fun greedyString(): ArgumentType<String> = StringArgumentType.greedyString()


/**
 * Retrieves a string argument from the command context.
 * @param name The name of the argument.
 * @return The string value of the argument.
 */
fun CommandContext<FabricClientCommandSource>.getString(name: String): String =
    StringArgumentType.getString(this, name)
