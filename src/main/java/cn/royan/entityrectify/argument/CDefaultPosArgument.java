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
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class CDefaultPosArgument implements CPosArgument {

	private final CoordinateArgument x;
	private final CoordinateArgument y;
	private final CoordinateArgument z;

	public CDefaultPosArgument(CoordinateArgument x, CoordinateArgument y, CoordinateArgument z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3d toAbsolutePos(FabricClientCommandSource source) {
		Vec3d vec3d = source.getPosition();
		return new Vec3d(this.x.toAbsoluteCoordinate(vec3d.x), this.y.toAbsoluteCoordinate(vec3d.y), this.z.toAbsoluteCoordinate(vec3d.z));
	}

	public Vec2f toAbsoluteRotation(FabricClientCommandSource source) {
		Vec2f vec2f = source.getRotation();
		return new Vec2f((float) this.x.toAbsoluteCoordinate(vec2f.x), (float) this.y.toAbsoluteCoordinate(vec2f.y));
	}

	public boolean isXRelative() {
		return this.x.isRelative();
	}

	public boolean isYRelative() {
		return this.y.isRelative();
	}

	public boolean isZRelative() {
		return this.z.isRelative();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof CDefaultPosArgument defaultPosArgument)) {
			return false;
		} else {
			if (this.x.equals(defaultPosArgument.x)) {
				return this.y.equals(defaultPosArgument.y) && this.z.equals(defaultPosArgument.z);
			}
			return false;
		}
	}

	public static CDefaultPosArgument parse(StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		CoordinateArgument coordinateArgument = CoordinateArgument.parse(reader);
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(reader);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(reader);
				return new CDefaultPosArgument(coordinateArgument, coordinateArgument2, coordinateArgument3);
			} else {
				reader.setCursor(cursor);
				throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
			}
		} else {
			reader.setCursor(cursor);
			throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
		}
	}

	public static CDefaultPosArgument parse(StringReader reader, boolean centerIntegers) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		CoordinateArgument coordinateArgument = CoordinateArgument.parse(reader, centerIntegers);
		if (reader.canRead() && reader.peek() == ' ') {
			reader.skip();
			CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(reader, false);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				CoordinateArgument coordinateArgument3 = CoordinateArgument.parse(reader, centerIntegers);
				return new CDefaultPosArgument(coordinateArgument, coordinateArgument2, coordinateArgument3);
			} else {
				reader.setCursor(cursor);
				throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
			}
		} else {
			reader.setCursor(cursor);
			throw Vec3ArgumentType.INCOMPLETE_EXCEPTION.createWithContext(reader);
		}
	}

	public static CDefaultPosArgument absolute(double x, double y, double z) {
		return new CDefaultPosArgument(new CoordinateArgument(false, x), new CoordinateArgument(false, y), new CoordinateArgument(false, z));
	}

	public static CDefaultPosArgument absolute(Vec2f vec) {
		return new CDefaultPosArgument(new CoordinateArgument(false, (double)vec.x), new CoordinateArgument(false, (double)vec.y), new CoordinateArgument(true, 0.0D));
	}

	public static CDefaultPosArgument zero() {
		return new CDefaultPosArgument(new CoordinateArgument(true, 0.0D), new CoordinateArgument(true, 0.0D), new CoordinateArgument(true, 0.0D));
	}

	@Override
	public int hashCode() {
		int hash = this.x.hashCode();
		hash = 31 * hash + this.y.hashCode();
		hash = 31 * hash + this.z.hashCode();
		return hash;
	}
}
