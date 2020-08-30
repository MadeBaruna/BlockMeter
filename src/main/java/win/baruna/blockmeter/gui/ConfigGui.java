package win.baruna.blockmeter.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import win.baruna.blockmeter.MeasureBox;

import java.util.ArrayList;
import java.util.List;

public class ConfigGui extends LightweightGuiDescription {
    @Override
    public void addPainters() {
    }

    public ConfigGui() {
        WGridPanel root = new WGridPanel(1);
        setRootPanel(root);

        TranslatableText on = new TranslatableText("options.on");
        TranslatableText off = new TranslatableText("options.off");

        TranslatableText keepColorStatus = MeasureBox.incrementColor ? off : on;
        WButton keepColorButton = new WButton(new TranslatableText("blockmeter.keepcolor", keepColorStatus));
        keepColorButton.setOnClick(() -> {
            MeasureBox.incrementColor = !MeasureBox.incrementColor;
            keepColorButton.setLabel(new TranslatableText("blockmeter.keepcolor", MeasureBox.incrementColor ? off : on));
        });
        root.add(keepColorButton, 0, 90, 170, 20);

        TranslatableText showDiagonalStatus = MeasureBox.innerDiagonal ? on : off;
        WButton showDiagonalButton = new WButton(new TranslatableText("blockmeter.diagonal", showDiagonalStatus));
        showDiagonalButton.setOnClick(() -> {
            MeasureBox.innerDiagonal = !MeasureBox.innerDiagonal;
            showDiagonalButton.setLabel(new TranslatableText("blockmeter.diagonal", MeasureBox.innerDiagonal ? on : off));
        });
        root.add(showDiagonalButton, 0, 112, 170, 20);

        List<ColorButton> buttons = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int index = i * 4 + j;
                ColorButton button = new ColorButton(index, buttons);
                buttons.add(button);
                root.add(button, 42 + (j*22), i * 22, 20, 20);
            }
        }

        root.validate(this);
    }
}
