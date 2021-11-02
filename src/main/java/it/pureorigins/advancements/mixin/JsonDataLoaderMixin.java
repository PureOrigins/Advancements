package it.pureorigins.advancements.mixin;

import com.google.gson.JsonElement;
import it.pureorigins.advancements.Advancements;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(JsonDataLoader.class)
public class JsonDataLoaderMixin {
  @Inject(method = "prepare", at = @At("HEAD"), cancellable = true)
  private void prepare(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<Map<Identifier, JsonElement>> callback) {
    if ((JsonDataLoader) (Object) this instanceof ServerAdvancementLoader loader) {
      callback.setReturnValue(Advancements.INSTANCE.getCompatibleAdvancements());
    }
  }
}
