package com.jozufozu.flywheel.util.transform;


import com.mojang.math.Axis;

import net.minecraft.core.Direction;

import net.minecraft.core.Rotations;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface Rotate<Self> {

	Self multiply(Quaternionf quaternion);

	default Self rotate(Direction axis, float radians) {
		if (radians == 0)
			return (Self) this;
		return multiply(axis.step().rotation(radians));
	}

	default Self rotate(double angle, Direction.Axis axis) {
		Axis vec = axis == Direction.Axis.X ? Axis.XP : axis == Direction.Axis.Y ? Axis.YP : Axis.ZP;
		return multiply(vec, angle);
	}

	default Self rotateX(double angle) {
		return multiply(Axis.XP, angle);
	}

	default Self rotateY(double angle) {
		return multiply(Axis.YP, angle);
	}

	default Self rotateZ(double angle) {
		return multiply(Axis.ZP, angle);
	}

	default Self rotateXRadians(double angle) {
		return multiplyRadians(Axis.XP, angle);
	}

	default Self rotateYRadians(double angle) {
		return multiplyRadians(Axis.YP, angle);
	}

	default Self rotateZRadians(double angle) {
		return multiplyRadians(Axis.ZP, angle);
	}

	default Self multiply(Axis axis, double angle) {
		if (angle == 0)
			return (Self) this;
		return multiply(axis.rotationDegrees((float) angle));
	}

	default Self multiplyRadians(Axis axis, double angle) {
		if (angle == 0)
			return (Self) this;
		return multiply(axis.rotation((float) angle));
	}

	default Self rotateToFace(Direction facing) {
		switch (facing) {
		case SOUTH -> multiply(Axis.YP.rotationDegrees(180));
		case WEST -> multiply(Axis.YP.rotationDegrees(90));
		case NORTH -> multiply(Axis.YP.rotationDegrees(0));
		case EAST -> multiply(Axis.YP.rotationDegrees(270));
		case UP -> multiply(Axis.XP.rotationDegrees(90));
		case DOWN -> multiply(Axis.XN.rotationDegrees(90));
		}
		return (Self) this;
	}
}
