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
 * DSL for building Brigadier commands more easily in Kotlin.
 */

fun <S : ArgumentBuilder<FabricClientCommandSource, S>> S.literal(
    name: String,
    block: LiteralArgumentBuilder<FabricClientCommandSource>.() -> Unit = {}
): S {
    val builder = ClientCommandManager.literal(name)
    builder.block()
    return then(builder)
}

fun <S : ArgumentBuilder<FabricClientCommandSource, S>, T : Any> S.argument(
    name: String,
    type: ArgumentType<T>,
    block: RequiredArgumentBuilder<FabricClientCommandSource, T>.() -> Unit = {}
): S {
    val builder: RequiredArgumentBuilder<FabricClientCommandSource, T> = ClientCommandManager.argument(name, type)
    builder.block()
    return then(builder)
}

fun <S : ArgumentBuilder<FabricClientCommandSource, S>> S.execute(
    block: (CommandContext<FabricClientCommandSource>) -> Int
): S {
    return executes { block(it) }
}

fun <T : Any> RequiredArgumentBuilder<FabricClientCommandSource, T>.suggest(
    block: (CommandContext<FabricClientCommandSource>, SuggestionsBuilder) -> CompletableFuture<Suggestions>
): RequiredArgumentBuilder<FabricClientCommandSource, T> {
    return suggests(block)
}

/**
 * Simple suggestion helper for a list of strings.
 */
fun <T : Any> RequiredArgumentBuilder<FabricClientCommandSource, T>.suggestList(
    list: () -> Collection<String>
): RequiredArgumentBuilder<FabricClientCommandSource, T> {
    return suggests { _, builder ->
        list().forEach { builder.suggest(it) }
        builder.buildFuture()
    }
}

// Shortcut for word argument
fun word(): ArgumentType<String> = StringArgumentType.word()

// Shortcut for greedy string
fun greedyString(): ArgumentType<String> = StringArgumentType.greedyString()

// Helper to get string argument
fun CommandContext<FabricClientCommandSource>.getString(name: String): String = 
    StringArgumentType.getString(this, name)
