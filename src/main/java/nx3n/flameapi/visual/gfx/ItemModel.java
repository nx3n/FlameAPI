package nx3n.flameapi.visual.gfx;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import nx3n.flameapi.visual.Props;

/**
 * Simple built-in model renderer: renders an {@link ItemStack}.
 *
 * Props:
 * - item: "namespace:path" (defaults to minecraft:stone)
 * - count: int
 * - transform: one of GUI/FIXED/THIRD_PERSON_RIGHT_HAND/...
 */
public final class ItemModel implements Model {
    private final ItemStack stack;
    private final ItemDisplayContext transform;

    public ItemModel(Props props) {
        String itemId = props == null ? "minecraft:stone" : props.getString("item", "minecraft:stone");
        int count = props == null ? 1 : Math.max(1, props.getInt("count", 1));

        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item == null) item = net.minecraft.world.item.Items.STONE;
        this.stack = new ItemStack(item, count);

        // 1.20.1 uses ItemDisplayContext (NOT ItemTransforms.TransformType)
        String t = props == null ? "FIXED" : props.getString("transform", "FIXED");
        ItemDisplayContext parsed;
        try {
            parsed = ItemDisplayContext.valueOf(t);
        } catch (Throwable ignored) {
            parsed = ItemDisplayContext.FIXED;
        }
        this.transform = parsed;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        mc.getItemRenderer().renderStatic(stack, transform, packedLight, packedOverlay, pose, buffers, mc.level, 0);
    }
}
