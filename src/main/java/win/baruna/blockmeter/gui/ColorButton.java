package win.baruna.blockmeter.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cotton.gui.widget.WButton;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.DyeColor;
import org.lwjgl.opengl.GL11;
import win.baruna.blockmeter.MeasureBox;

import java.util.List;

public class ColorButton extends WButton {
    float[] color;
    boolean selected;

    public ColorButton(int colorIndex, List<ColorButton> buttons) {
        super(new LiteralText(""));
        this.color = DyeColor.values()[colorIndex].getColorComponents();

        if (MeasureBox.colorIndex == colorIndex) {
            selected = true;
        }

        this.setOnClick(() -> {
            MeasureBox.colorIndex = colorIndex;
            buttons.forEach((button) -> button.selected = false);
            this.selected = true;
        });
    }

    @Override
    public boolean canResize() {
        return true;
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        super.paint(matrices, x, y, mouseX, mouseY);


        float r = color[0];
        float g = color[1];
        float b = color[2];
        float a = 1f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
                                       GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);

        if (selected) {
            draw(buffer, x, y, 20, 20, 1f, 1f, 0f, 1f);
        }

        draw(buffer, x + 1, y + 1, 18, 18, r, g, b, a);
        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private void draw(BufferBuilder buffer, int left, int top, int width, int height, float r, float g, float b,
                      float a) {
        buffer.vertex((double) left, (double) (top + height), 0.0D).color(r, g, b, a).next();
        buffer.vertex((double) (left + width), (double) (top + height), 0.0D).color(r, g, b, a).next();
        buffer.vertex((double) (left + width), (double) top, 0.0D).color(r, g, b, a).next();
        buffer.vertex((double) left, (double) top, 0.0D).color(r, g, b, a).next();
    }
}
