package win.baruna.blockmeter.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import win.baruna.blockmeter.BlockMeter;

public class Undo implements Command<CottonClientCommandSource> {
    @Override
    public int run(CommandContext<CottonClientCommandSource> context) throws CommandSyntaxException {
        BlockMeter.instance.undo();
        return 1;
    }
}
