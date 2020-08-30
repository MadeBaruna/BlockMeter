package win.baruna.blockmeter;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public class LinesRenderLayer extends RenderLayer {
    public LinesRenderLayer(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    public static RenderLayer getRenderLayer(float lineWidth) {
        return of("lines_no_depth",
           VertexFormats.POSITION_COLOR, GL11.GL_LINES, 256,
           RenderLayer.MultiPhaseParameters.builder()
                   .lineWidth(new LineWidth(OptionalDouble.of(lineWidth)))
                   .transparency(TRANSLUCENT_TRANSPARENCY)
                   .texture(NO_TEXTURE)
                   .cull(DISABLE_CULLING)
                   .depthTest(ALWAYS_DEPTH_TEST)
                   .build(false));
    }
}