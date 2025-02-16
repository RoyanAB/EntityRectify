//The MIT License (MIT)
//
//		Copyright (c) 2021 xpple
//
//		Permission is hereby granted, free of charge, to any person obtaining a copy
//		of this software and associated documentation files (the "Software"), to deal
//		in the Software without restriction, including without limitation the rights
//		to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//		copies of the Software, and to permit persons to whom the Software is
//		furnished to do so, subject to the following conditions:
//
//		The above copyright notice and this permission notice shall be included in
//		all copies or substantial portions of the Software.
//
//		THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//		IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//		FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//		AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//		LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//		OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//		THE SOFTWARE.
package cn.royan.entityrectify.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandSource.RelativePosition;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class CBlockPosArgumentType implements ArgumentType<CPosArgument> {

	private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.pos.unloaded"));
	public static final SimpleCommandExceptionType OUT_OF_WORLD_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.pos.outofworld"));
	public static final SimpleCommandExceptionType OUT_OF_BOUNDS_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.pos.outofbounds"));

	public static CBlockPosArgumentType blockPos() {
		return new CBlockPosArgumentType();
	}

	public static BlockPos getCLoadedBlockPos(final CommandContext<FabricClientCommandSource> context, final String name) throws CommandSyntaxException {
		ClientWorld clientWorld = context.getSource().getWorld();
		return getCLoadedBlockPos(context, clientWorld, name);
	}

	public static BlockPos getCLoadedBlockPos(final CommandContext<FabricClientCommandSource> context, final ClientWorld world, final String name) throws CommandSyntaxException {
		BlockPos blockPos = getCBlockPos(context, name);
		ChunkPos chunkPos = new ChunkPos(blockPos);
		if (!world.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z)) {
			throw UNLOADED_EXCEPTION.create();
		} else if (!world.isInBuildLimit(blockPos)) {
			throw OUT_OF_WORLD_EXCEPTION.create();
		} else {
			return blockPos;
		}
	}

	public static BlockPos getCBlockPos(final CommandContext<FabricClientCommandSource> context, final String name) {
		return context.getArgument(name, CPosArgument.class).toAbsoluteBlockPos(context.getSource());
	}

	public static BlockPos getCValidBlockPos(CommandContext<FabricClientCommandSource> context, String name) throws CommandSyntaxException {
		BlockPos blockPos = getCBlockPos(context, name);
		if (!World.isValid(blockPos)) {
			throw OUT_OF_BOUNDS_EXCEPTION.create();
		}
		return blockPos;
	}

	@Override
	public CPosArgument parse(final StringReader stringReader) throws CommandSyntaxException {
		return stringReader.canRead() && stringReader.peek() == '^' ? CLookingPosArgument.parse(stringReader) : CDefaultPosArgument.parse(stringReader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		if (!(context.getSource() instanceof CommandSource)) {
			return Suggestions.empty();
		}
		String string = builder.getRemaining();
		Collection<RelativePosition> collection;
		if (!string.isEmpty() && string.charAt(0) == '^') {
			collection = Collections.singleton(RelativePosition.ZERO_LOCAL);
		} else {
			collection = ((CommandSource) context.getSource()).getBlockPositionSuggestions();
		}

		return CommandSource.suggestPositions(string, collection, builder, CommandManager.getCommandValidator(this::parse));
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
