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
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class CLookingPosArgument implements CPosArgument {
	public static final char CARET = '^';
	private final double x;
	private final double y;
	private final double z;

	public CLookingPosArgument(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public Vec3d toAbsolutePos(FabricClientCommandSource source) {
		return null;
	}

	public Vec2f toAbsoluteRotation(FabricClientCommandSource source) {
		return Vec2f.ZERO;
	}

	public boolean isXRelative() {
		return true;
	}

	public boolean isYRelative() {
		return true;
	}

	public boolean isZRelative() {
		return true;
	}

	public static CLookingPosArgument parse(StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		double x = readCoordinate(reader, cursor);
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			double y = readCoordinate(reader, cursor);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				double z = readCoordinate(reader, cursor);
				return new CLookingPosArgument(x, y, z);
			} else {
				reader.setCursor(cursor);
				throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
			}
		} else {
			reader.setCursor(cursor);
			throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
		}
	}

	private static double readCoordinate(StringReader reader, int startingCursorPos) throws CommandSyntaxException {
		if (!reader.canRead()) {
			throw CoordinateArgument.MISSING_COORDINATE.createWithContext(reader);
		} else if (reader.peek() != '^') {
			reader.setCursor(startingCursorPos);
			throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext(reader);
		}
		reader.skip();
		return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0D;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof CLookingPosArgument lookingPosArgument)) {
			return false;
		} else {
			return this.x == lookingPosArgument.x && this.y == lookingPosArgument.y && this.z == lookingPosArgument.z;
		}
	}

	public int hashCode() {
		return Objects.hash(this.x, this.y, this.z);
	}
}
