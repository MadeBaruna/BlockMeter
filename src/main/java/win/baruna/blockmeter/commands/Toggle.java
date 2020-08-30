package win.baruna.blockmeter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import win.baruna.blockmeter.BlockMeter;

public class Toggle implements Command<CottonClientCommandSource> {
    @Override
    public int run(final CommandContext<CottonClientCommandSource> context) throws CommandSyntaxException {
        final MinecraftClient client = MinecraftClient.getInstance();
        final PlayerEntity player = client.player;

        if (player == null) return 0;

        final ItemStack itemStack = player.getMainHandStack();
        final Item item = itemStack.getItem();

        if (BlockMeter.instance.currentItem != null && BlockMeter.instance.currentItem.equals(item)) {
            BlockMeter.instance.deactivate();
            context.getSource().sendFeedback(new TranslatableText("blockmeter.toggle.off"), true);
            return 1;
        }

        TranslatableText itemName = new TranslatableText(itemStack.getTranslationKey());
        itemName.formatted(Formatting.BOLD);

        BlockMeter.instance.activate(item);
        context.getSource()
                .sendFeedback(new TranslatableText("blockmeter.toggle.on", itemName).formatted(Formatting.GREEN), true);
        return 1;
    }
}