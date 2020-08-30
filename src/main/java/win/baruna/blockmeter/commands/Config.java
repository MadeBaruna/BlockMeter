package win.baruna.blockmeter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import win.baruna.blockmeter.gui.ConfigGui;
import win.baruna.blockmeter.gui.ConfigScreen;

public class Config implements Command<CottonClientCommandSource> {
    @Override
    public int run(CommandContext<CottonClientCommandSource> context) throws CommandSyntaxException {
        new Thread(() -> {
            try {
                Thread.sleep(100);
                MinecraftClient.getInstance().openScreen(new ConfigScreen(new ConfigGui()));
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();

        return 1;
    }
}
