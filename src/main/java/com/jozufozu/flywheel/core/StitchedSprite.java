package com.jozufozu.flywheel.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class StitchedSprite {
	private static final Map<ResourceLocation, SingleAtlasSpriteHolder> ALL = new HashMap<>();

	protected final ResourceLocation atlasLocation;
	protected final ResourceLocation location;
	protected TextureAtlasSprite sprite;

	public StitchedSprite(ResourceLocation atlas, ResourceLocation location) {
		atlasLocation = atlas;
		this.location = location;
		ALL.computeIfAbsent(atlasLocation, SingleAtlasSpriteHolder::new).add(this);
	}

	public StitchedSprite(ResourceLocation location) {
		this(InventoryMenu.BLOCK_ATLAS, location);
	}

	public static void onTextureStitchPost(TextureAtlas atlas) {
		ResourceLocation atlasLocation = atlas.location();
		SingleAtlasSpriteHolder holder = ALL.get(atlasLocation);
		if (holder != null) {
			holder.loadSprites(atlas);
		}
	}

	protected void loadSprite(TextureAtlas atlas) {
		sprite = atlas.getSprite(location);
	}

	public ResourceLocation getAtlasLocation() {
		return atlasLocation;
	}

	public ResourceLocation getLocation() {
		return location;
	}

	public TextureAtlasSprite get() {
		return sprite;
	}

	private static class SingleAtlasSpriteHolder   {
		private final ResourceLocation atlasLocation;
		private final List<StitchedSprite> sprites = new ArrayList<>();

		private SingleAtlasSpriteHolder(ResourceLocation atlasLocation) {
			this.atlasLocation = atlasLocation;
		}

		public void add(StitchedSprite sprite) {
			sprites.add(sprite);
		}

		public void loadSprites(TextureAtlas atlas) {
			for (StitchedSprite sprite : sprites) {
				sprite.loadSprite(atlas);
			}
		}
	}
}
