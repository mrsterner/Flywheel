package com.jozufozu.flywheel.impl;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.jozufozu.flywheel.Flywheel;
import com.jozufozu.flywheel.api.backend.Backend;
import com.jozufozu.flywheel.api.event.ReloadRenderersEvent;
import com.jozufozu.flywheel.backend.Backends;
import com.jozufozu.flywheel.config.FlwConfig;
import com.jozufozu.flywheel.impl.instancing.InstancedRenderDispatcher;
import com.jozufozu.flywheel.lib.backend.SimpleBackend;
import com.mojang.logging.LogUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.CrashReportCallables;

public final class BackendManagerImpl {
	private static final Logger LOGGER = LogUtils.getLogger();

	private static final Backend OFF_BACKEND = SimpleBackend.builder()
			.engineMessage(new TextComponent("Disabled Flywheel").withStyle(ChatFormatting.RED))
			.engineFactory(level -> {
				throw new IllegalStateException("Cannot create engine when backend is off.");
			})
			.supported(() -> true)
			.register(Flywheel.rl("off"));

	private static final Backend DEFAULT_BACKEND = findDefaultBackend();

	private static Backend backend;

	@Nullable
	public static Backend getBackend() {
		return backend;
	}

	public static boolean isOn() {
		return backend != null && backend != OFF_BACKEND;
	}

	public static Backend getOffBackend() {
		return OFF_BACKEND;
	}

	public static Backend getDefaultBackend() {
		return DEFAULT_BACKEND;
	}

	private static Backend findDefaultBackend() {
		// TODO: Automatically select the best default config based on the user's driver
		// TODO: Figure out how this will work if custom backends are registered and without hardcoding the default backends
		return Backends.INDIRECT;
	}

	public static void onReloadRenderers(ReloadRenderersEvent event) {
		refresh(event.getLevel());
	}

	public static void refresh(@Nullable ClientLevel level) {
		backend = chooseBackend();

		if (level != null) {
			InstancedRenderDispatcher.resetInstanceWorld(level);
		}
	}

	private static Backend chooseBackend() {
		var preferred = FlwConfig.get().getBackend();
		var actual = preferred.findFallback();

		if (preferred != actual) {
			LOGGER.warn("Flywheel backend fell back from '{}' to '{}'", Backend.REGISTRY.getIdOrThrow(preferred), Backend.REGISTRY.getIdOrThrow(actual));
		}

		return actual;
	}

	public static void init() {
		CrashReportCallables.registerCrashCallable("Flywheel Backend", () -> {
			if (backend == null) {
				return "Uninitialized";
			}
			var backendId = Backend.REGISTRY.getId(backend);
			if (backendId == null) {
				return "Unregistered";
			}
			return backendId.toString();
		});
	}

	private BackendManagerImpl() {
	}
}
