package win.baruna.blockmeter;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;

import net.minecraft.util.DyeColor;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeasureBox {
    private final BlockPos blockStart;
    private BlockPos blockEnd;
    public Box box;
    private final DimensionType dimension;
    private final DyeColor color;
    private boolean finished;
    public static int colorIndex = -1;
    public static boolean incrementColor = true;
    public static boolean innerDiagonal = false;

    MeasureBox(BlockPos block, DimensionType dimension) {
        this.blockStart = block;
        this.blockEnd = block;
        this.dimension = dimension;
        this.color = getNextColor();
        this.finished = false;

        this.setBoundingBox();
    }

    private void setBoundingBox() {
        final int ax = this.blockStart.getX();
        final int ay = this.blockStart.getY();
        final int az = this.blockStart.getZ();
        final int bx = this.blockEnd.getX();
        final int by = this.blockEnd.getY();
        final int bz = this.blockEnd.getZ();

        this.box = new Box(Math.min(ax, bx), Math.min(ay, by), Math.min(az, bz),
                           Math.max(ax, bx) + 1, Math.max(ay, by) + 1, Math.max(az, bz) + 1);
    }

    void setBlockEnd(BlockPos blockEnd) {
        this.blockEnd = blockEnd;
        this.setBoundingBox();
    }

    void render(DimensionType currentDimension, MatrixStack matrices, BufferBuilderStorage vertices, Camera camera, Matrix4f projection) {
        if (!dimension.equals(currentDimension)) return;

        final float[] color = this.color.getColorComponents();
        final float r = color[0];
        final float g = color[1];
        final float b = color[2];
        final float a = 0.95F;

        Vec3d pos = camera.getPos();
        double distance = box.getCenter().distanceTo(pos);
        float lineWidth = 2f;
        if (distance > 48) {
            lineWidth = 1.0f;
        }

        matrices.push();
        VertexConsumer buffer = vertices.getOutlineVertexConsumers().getBuffer(LinesRenderLayer.getRenderLayer(lineWidth));

        matrices.translate(-pos.x, -pos.y, -pos.z);

        WorldRenderer.drawBox(matrices, buffer, box, r, g, b, a);
        if (innerDiagonal) {
            Matrix4f matrix4f = matrices.peek().getModel();
            buffer.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ).color(r, g, b, a).next();
            buffer.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a).next();
        }


        matrices.pop();
        drawLength(matrices, vertices, camera, projection);
    }

    private void drawLength(MatrixStack matrices, BufferBuilderStorage vertices, Camera camera, Matrix4f projection) {
        final int lengthX = (int) box.getXLength();
        final int lengthY = (int) box.getYLength();
        final int lengthZ = (int) box.getZLength();

        final Vec3d boxCenter = box.getCenter();
        final double diagonalLength = new Vec3d(box.minX, box.minY, box.minZ)
                .distanceTo(new Vec3d(box.maxX, box.maxY, box.maxZ));

        final Vec3d pos = camera.getPos();

        final Frustum frustum = new Frustum(matrices.peek().getModel(), projection);
        frustum.setPosition(pos.x, pos.y, pos.z);

        Box boxT = box.expand(0.08f);

        List<Line> lines = new ArrayList<>();
        lines.add(new Line(new Box(boxT.minX, boxT.minY, boxT.minZ, boxT.minX, boxT.minY, boxT.maxZ), pos, frustum));
        lines.add(new Line(new Box(boxT.minX, boxT.maxY, boxT.minZ, boxT.minX, boxT.maxY, boxT.maxZ), pos, frustum));
        lines.add(new Line(new Box(boxT.maxX, boxT.minY, boxT.minZ, boxT.maxX, boxT.minY, boxT.maxZ), pos, frustum));
        lines.add(new Line(new Box(boxT.maxX, boxT.maxY, boxT.minZ, boxT.maxX, boxT.maxY, boxT.maxZ), pos, frustum));
        Collections.sort(lines);
        final Vec3d lineZ = lines.get(0).line.getCenter();

        lines.clear();
        lines.add(new Line(new Box(boxT.minX, boxT.minY, boxT.minZ, boxT.minX, boxT.maxY, boxT.minZ), pos, frustum));
        lines.add(new Line(new Box(boxT.minX, boxT.minY, boxT.maxZ, boxT.minX, boxT.maxY, boxT.maxZ), pos, frustum));
        lines.add(new Line(new Box(boxT.maxX, boxT.minY, boxT.minZ, boxT.maxX, boxT.maxY, boxT.minZ), pos, frustum));
        lines.add(new Line(new Box(boxT.maxX, boxT.minY, boxT.maxZ, boxT.maxX, boxT.maxY, boxT.maxZ), pos, frustum));
        Collections.sort(lines);
        final Vec3d lineY = lines.get(0).line.getCenter();

        lines.clear();
        lines.add(new Line(new Box(boxT.minX, boxT.minY, boxT.minZ, boxT.maxX, boxT.minY, boxT.minZ), pos, frustum));
        lines.add(new Line(new Box(boxT.minX, boxT.minY, boxT.maxZ, boxT.maxX, boxT.minY, boxT.maxZ), pos, frustum));
        lines.add(new Line(new Box(boxT.minX, boxT.maxY, boxT.minZ, boxT.maxX, boxT.maxY, boxT.minZ), pos, frustum));
        lines.add(new Line(new Box(boxT.minX, boxT.maxY, boxT.maxZ, boxT.maxX, boxT.maxY, boxT.maxZ), pos, frustum));
        Collections.sort(lines);
        final Vec3d lineX = lines.get(0).line.getCenter();

        matrices.push();
        matrices.translate(-pos.x, -pos.y, -pos.z);
        if (innerDiagonal) {
            drawText(matrices, vertices, camera, boxCenter, String.format("%.2f", diagonalLength));
        }
        drawText(matrices, vertices, camera, new Vec3d(lineX.x, lineX.y, lineX.z), String.valueOf(lengthX));
        drawText(matrices, vertices, camera, new Vec3d(lineY.x, lineY.y, lineY.z), String.valueOf(lengthY));
        drawText(matrices, vertices, camera, new Vec3d(lineZ.x, lineZ.y, lineZ.z), String.valueOf(lengthZ));
        matrices.pop();
    }

    private void drawText(MatrixStack matrices, BufferBuilderStorage vertices, Camera camera, Vec3d pos, String length) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        final float size = 0.02f;

        matrices.push();
        matrices.translate(pos.x, pos.y + size * 5.0, pos.z);
        matrices.multiply(camera.getRotation());
        matrices.scale(-size, -size, -size);
        matrices.translate(-textRenderer.getWidth(length) / 2f, 0, 0);
        textRenderer.draw(
                length,
                0,
                0,
                this.color.getSignColor(),
                true,
                matrices.peek().getModel(),
                vertices.getOutlineVertexConsumers(),
                true,
                this.color.getSignColor(),
                15728880
        );
        matrices.pop();
    }

    private DyeColor getNextColor() {
        final DyeColor selectedColor = DyeColor.byId(colorIndex);

        if (incrementColor) {
            colorIndex++;
        }

        if (colorIndex >= DyeColor.values().length) {
            colorIndex = 0;
        }

        return selectedColor;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished() {
        this.finished = true;
    }

    private static class Line implements Comparable<Line> {
        Box line;
        boolean isVisible;
        double distance;

        Line(Box line, Vec3d pos, Frustum frustum) {
            this.line = line;
            this.isVisible = frustum.isVisible(line);
            this.distance = line.getCenter().distanceTo(pos);
        }

        @Override
        public int compareTo(Line l) {
            if (isVisible) {
                return l.isVisible ? Double.compare(distance, l.distance) : -1;
            } else {
                return l.isVisible ? 1 : 0;
            }
        }
    }
}