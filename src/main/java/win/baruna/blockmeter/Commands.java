package win.baruna.blockmeter;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.cottonmc.clientcommands.*;
import win.baruna.blockmeter.commands.Config;
import win.baruna.blockmeter.commands.Toggle;
import win.baruna.blockmeter.commands.Undo;

public class Commands implements ClientCommandPlugin {
    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        LiteralCommandNode<CottonClientCommandSource> root = ArgumentBuilders
                .literal("blockmeter")
                .executes(new Toggle())
                .build();

        LiteralCommandNode<CottonClientCommandSource> undo = ArgumentBuilders
                .literal("undo")
                .executes(new Undo())
                .build();

        LiteralCommandNode<CottonClientCommandSource> config = ArgumentBuilders
                .literal("config")
                .executes(new Config())
                .build();

        root.addChild(undo);
        root.addChild(config);

        dispatcher.getRoot().addChild(root);

        LiteralCommandNode<CottonClientCommandSource> rootAlias = ArgumentBuilders
                .literal("meter")
                .executes(new Toggle())
                .redirect(root)
                .build();
        dispatcher.getRoot().addChild(rootAlias);
    }
}