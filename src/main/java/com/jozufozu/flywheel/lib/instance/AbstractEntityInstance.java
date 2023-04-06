package com.jozufozu.flywheel.lib.instance;

import org.joml.FrustumIntersection;

import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.api.instance.EntityInstance;
import com.jozufozu.flywheel.api.instance.TickableInstance;
import com.jozufozu.flywheel.api.instance.controller.InstanceContext;
import com.jozufozu.flywheel.impl.instancing.manager.BlockEntityInstanceManager;
import com.jozufozu.flywheel.lib.box.ImmutableBox;
import com.jozufozu.flywheel.lib.box.MutableBox;
import com.jozufozu.flywheel.lib.light.TickingLightListener;
import com.mojang.math.Vector3f;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * The layer between an {@link Entity} and the Flywheel backend.
 * *
 * <br><br> There are a few additional features that overriding classes can opt in to:
 * <ul>
 *     <li>{@link DynamicInstance}</li>
 *     <li>{@link TickableInstance}</li>
 * </ul>
 * See the interfaces' documentation for more information about each one.
 *
 * <br> Implementing one or more of these will give an {@link AbstractEntityInstance} access
 * to more interesting and regular points within a tick or a frame.
 *
 * @param <E> The type of {@link Entity} your class is an instance of.
 */
public abstract class AbstractEntityInstance<E extends Entity> extends AbstractInstance implements EntityInstance<E>, TickingLightListener {
	protected final E entity;
	protected final MutableBox bounds;

	public AbstractEntityInstance(InstanceContext ctx, E entity) {
		super(ctx, entity.level);
		this.entity = entity;
		bounds = MutableBox.from(entity.getBoundingBox());
	}

	@Override
	public double distanceSquared(double x, double y, double z) {
		return entity.distanceToSqr(x, y, z);
	}

	@Override
	public ImmutableBox getVolume() {
		return bounds;
	}

	@Override
	public boolean tickLightListener() {
		AABB boundsNow = entity.getBoundingBox();

		if (bounds.sameAs(boundsNow)) {
			return false;
		}

		bounds.assign(boundsNow);

		updateLight();

		return true;
	}

	/**
	 * In order to accommodate for floating point precision errors at high coordinates,
	 * {@link BlockEntityInstanceManager}s are allowed to arbitrarily adjust the origin, and
	 * shift the world matrix provided as a shader uniform accordingly.
	 *
	 * @return The position this instance should be rendered at to appear in the correct location.
	 */
	public Vector3f getInstancePosition() {
		Vec3 pos = entity.position();
		return new Vector3f((float) (pos.x - renderOrigin.getX()), (float) (pos.y - renderOrigin.getY()), (float) (pos.z - renderOrigin.getZ()));
	}

	/**
	 * In order to accommodate for floating point precision errors at high coordinates,
	 * {@link BlockEntityInstanceManager}s are allowed to arbitrarily adjust the origin, and
	 * shift the world matrix provided as a shader uniform accordingly.
	 *
	 * @return The position this instance should be rendered at to appear in the correct location.
	 */
	public Vector3f getInstancePosition(float partialTicks) {
		Vec3 pos = entity.position();
		return new Vector3f((float) (Mth.lerp(partialTicks, entity.xOld, pos.x) - renderOrigin.getX()), (float) (Mth.lerp(partialTicks, entity.yOld, pos.y) - renderOrigin.getY()), (float) (Mth.lerp(partialTicks, entity.zOld, pos.z) - renderOrigin.getZ()));
	}

	public boolean isVisible(FrustumIntersection frustum) {
		AABB aabb = entity.getBoundingBox();
		return frustum.testAab((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ);
	}
}
