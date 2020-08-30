package win.baruna.blockmeter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import win.baruna.blockmeter.gui.ConfigGui;
import win.baruna.blockmeter.gui.ConfigScreen;

import java.util.ArrayList;
import java.util.List;

public class BlockMeter implements ClientModInitializer {
    public static BlockMeter instance;

    public final Logger logger = LogManager.getLogger("BlockMeter");
    public Item currentItem = null;
    private final List<MeasureBox> boxes = new ArrayList<>();

    public BlockMeter() {
        instance = this;
    }

    @Override
    public void onInitializeClient() {
        UseBlockCallback.EVENT.register((playerEntity, world, hand, hitResult) -> addBox(playerEntity, hitResult));

        UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
            if (playerEntity.isSneaking() && playerEntity.getMainHandStack().getItem().equals(currentItem)) {
                MinecraftClient.getInstance().openScreen(new ConfigScreen(new ConfigGui()));
                return TypedActionResult.fail(ItemStack.EMPTY);
            }

            return TypedActionResult.pass(ItemStack.EMPTY);
        });


        ClientTickEvents.END_CLIENT_TICK.register(e -> {
            if (currentItem != null && boxes.size() > 0) {
                MeasureBox lastBox = boxes.get(boxes.size() - 1);
                if (!lastBox.isFinished()) {
                    HitResult rayHit = e.crosshairTarget;

                    if (rayHit != null && rayHit.getType() == HitResult.Type.BLOCK) {
                        BlockHitResult blockHitResult = (BlockHitResult) rayHit;
                        lastBox.setBlockEnd(new BlockPos(blockHitResult.getBlockPos()));
                    }
                }
            }
        });

        logger.info("Initialized!");
    }

    private ActionResult addBox(PlayerEntity playerEntity, BlockHitResult hitResult) {
        if (currentItem == null) return ActionResult.PASS;

        if (playerEntity.getMainHandStack().getItem().equals(currentItem)) {
            if (playerEntity.isSneaking()) {
                undo();
                return ActionResult.FAIL;
            }

            BlockPos block = hitResult.getBlockPos();

            if (boxes.size() > 0) {
                MeasureBox lastBox = boxes.get(boxes.size() - 1);

                if (lastBox.isFinished()) {
                    final MeasureBox box = new MeasureBox(block, playerEntity.world.getDimension());
                    boxes.add(box);
                } else {
                    lastBox.setBlockEnd(block);
                    lastBox.setFinished();
                }

                System.out.println("last box " + lastBox.box);
            } else {
                final MeasureBox box = new MeasureBox(block, playerEntity.world.getDimension());
                System.out.println("new box " + box.box);
                boxes.add(box);
            }

            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }

    public void renderOverlay(MatrixStack matrices, BufferBuilderStorage vertices, Camera camera, Matrix4f projection) {
        if (currentItem == null) return;

        final MinecraftClient instance = MinecraftClient.getInstance();

        if (instance.player == null) return;

        final DimensionType currentDimension = instance.player.world.getDimension();
        boxes.forEach(box -> box.render(currentDimension, matrices, vertices, camera, projection));
    }

    public void activate(Item item) {
        currentItem = item;
    }

    public void deactivate() {
        currentItem = null;
        clear();
    }

    public void undo() {
        if (boxes.size() > 0) {
            boxes.remove(boxes.size() - 1);
            MeasureBox.colorIndex--;
        }
    }

    public void clear() {
        boxes.clear();
        MeasureBox.colorIndex = 0;
        currentItem = null;
    }
}
