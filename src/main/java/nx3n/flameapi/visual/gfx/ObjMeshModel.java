package nx3n.flameapi.visual.gfx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;
import org.joml.Matrix3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import nx3n.flameapi.visual.Props;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Simple OBJ mesh model.
 *
 * Why this exists:
 * - You can drop a high-poly .obj into assets and render it from visuals.
 * - This loader is intentionally tiny (no materials, no smoothing groups).
 *
 * Props:
 * - obj: ResourceLocation string, e.g. "mymod:models/mesh.obj" (required)
 * - texture: ResourceLocation string for entityCutout, e.g. "mymod:textures/mesh.png" (optional)
 * - color: ARGB int (optional)
 */
public final class ObjMeshModel implements Model {
    private static final Map<ResourceLocation, ObjMeshModel> CACHE = new HashMap<>();

    private final ResourceLocation texture;
    private final int argb;
    private final float[] vx, vy, vz, tu, tv, nx, ny, nz;

    // For large meshes we upload once to GPU. This keeps high-poly models fast.
    private volatile VertexBuffer vbo;
    private volatile boolean uploaded;

    private ObjMeshModel(ResourceLocation texture, int argb,
                         float[] vx, float[] vy, float[] vz,
                         float[] tu, float[] tv,
                         float[] nx, float[] ny, float[] nz) {
        this.texture = texture;
        this.argb = argb;
        this.vx = vx; this.vy = vy; this.vz = vz;
        this.tu = tu; this.tv = tv;
        this.nx = nx; this.ny = ny; this.nz = nz;
    }

    public static ObjMeshModel fromProps(Props p) {
        String objStr = p.getString("obj", "");
        if (objStr.isEmpty()) return null;
        ResourceLocation obj = rl(objStr);
        if (obj == null) return null;

        ResourceLocation tex = rl(p.getString("texture", ""));
        int color = p.getInt("color", 0xFFFFFFFF);

        // Cache by obj+texture+color in a cheap key.
        ResourceLocation key = new ResourceLocation(obj.getNamespace(), obj.getPath() + "|" + (tex == null ? "" : tex) + "|" + Integer.toHexString(color));
        synchronized (CACHE) {
            ObjMeshModel cached = CACHE.get(key);
            if (cached != null) return cached;
            ObjMeshModel loaded = load(obj, tex, color);
            if (loaded != null) CACHE.put(key, loaded);
            return loaded;
        }
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        // Heuristic: if it's big enough, upload to GPU once.
        if (vx.length >= 60000) { // ~20k triangles
            renderVbo(pose, packedLight, packedOverlay);
            return;
        }

        RenderType rt = (texture != null) ? RenderType.entityCutout(texture) : RenderType.entityCutoutNoCull(new ResourceLocation("minecraft", "textures/misc/white.png"));
        var vc = buffers.getBuffer(rt);

        int a = (argb >>> 24) & 0xFF;
        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = (argb) & 0xFF;

        Matrix4f mat = pose.last().pose();
        Matrix3f nmat = pose.last().normal();

        // Arrays store triangles, 1 vertex per index.
        for (int i = 0; i < vx.length; i++) {
            vc.vertex(mat, vx[i], vy[i], vz[i])
                .color(r, g, b, a)
                .uv(tu[i], tv[i])
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(nmat, nx[i], ny[i], nz[i])
                .endVertex();
        }
    }

    private void renderVbo(PoseStack pose, int packedLight, int packedOverlay) {
        ensureUploaded(packedLight, packedOverlay);
        if (vbo == null) return;

        ResourceLocation tex = (texture != null) ? texture : new ResourceLocation("minecraft", "textures/misc/white.png");
        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
        RenderSystem.setShaderTexture(0, tex);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        vbo.bind();
        vbo.drawWithShader(pose.last().pose(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();

        RenderSystem.enableCull();
    }

    private void ensureUploaded(int packedLight, int packedOverlay) {
        if (uploaded) return;
        // Upload must happen on render thread. If we're not there, just skip; next frame will upload.
        if (!RenderSystem.isOnRenderThread()) return;
        synchronized (this) {
            if (uploaded) return;
            try {
                if (vbo == null) vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);

                int a = (argb >>> 24) & 0xFF;
                int r = (argb >>> 16) & 0xFF;
                int g = (argb >>> 8) & 0xFF;
                int b = (argb) & 0xFF;

                BufferBuilder bb = Tesselator.getInstance().getBuilder();
                // 1.20.1: use a real default format that exists.
                bb.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
                for (int i = 0; i < vx.length; i++) {
                    bb.vertex(vx[i], vy[i], vz[i])
                        .color(r, g, b, a)
                        .uv(tu[i], tv[i])
                        .uv2(packedLight)
                        .endVertex();
                }

                vbo.upload(bb.end());
                uploaded = true;
            } catch (Throwable t) {
                // If upload fails we fall back to CPU path next time.
                vbo = null;
                uploaded = false;
            }
        }
    }

    private static ObjMeshModel load(ResourceLocation obj, ResourceLocation tex, int color) {
        try {
            Resource res = Minecraft.getInstance().getResourceManager().getResource(obj).orElse(null);
            if (res == null) return null;

            List<float[]> positions = new ArrayList<>();
            List<float[]> uvs = new ArrayList<>();
            List<float[]> norms = new ArrayList<>();

            List<int[]> faces = new ArrayList<>(); // flattened triplets (v,vt,vn)

            try (BufferedReader br = new BufferedReader(new InputStreamReader(res.open(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\\s+");
                    if (parts.length == 0) continue;
                    switch (parts[0]) {
                        case "v" -> {
                            if (parts.length >= 4) {
                                positions.add(new float[]{f(parts[1]), f(parts[2]), f(parts[3])});
                            }
                        }
                        case "vt" -> {
                            if (parts.length >= 3) {
                                uvs.add(new float[]{f(parts[1]), 1.0f - f(parts[2])});
                            }
                        }
                        case "vn" -> {
                            if (parts.length >= 4) {
                                float x = f(parts[1]), y = f(parts[2]), z = f(parts[3]);
                                float len = Mth.sqrt(x * x + y * y + z * z);
                                if (len > 1e-6f) {
                                    x /= len; y /= len; z /= len;
                                }
                                norms.add(new float[]{x, y, z});
                            }
                        }
                        case "f" -> {
                            // Triangulate fan: f a b c d -> (a,b,c) (a,c,d)
                            if (parts.length >= 4) {
                                int[] first = parseIdx(parts[1]);
                                int[] prev = parseIdx(parts[2]);
                                for (int i = 3; i < parts.length; i++) {
                                    int[] cur = parseIdx(parts[i]);
                                    faces.add(first);
                                    faces.add(prev);
                                    faces.add(cur);
                                    prev = cur;
                                }
                            }
                        }
                        default -> {
                            // ignore
                        }
                    }
                }
            }

            int n = faces.size();
            if (n == 0) return null;

            float[] vx = new float[n];
            float[] vy = new float[n];
            float[] vz = new float[n];
            float[] tu = new float[n];
            float[] tv = new float[n];
            float[] nx = new float[n];
            float[] ny = new float[n];
            float[] nz = new float[n];

            for (int i = 0; i < n; i++) {
                int[] idx = faces.get(i);
                int vi = idx[0];
                int ti = idx[1];
                int ni = idx[2];

                float[] v = (vi >= 0 && vi < positions.size()) ? positions.get(vi) : new float[]{0, 0, 0};
                float[] t = (ti >= 0 && ti < uvs.size()) ? uvs.get(ti) : new float[]{0, 0};
                float[] nn = (ni >= 0 && ni < norms.size()) ? norms.get(ni) : new float[]{0, 1, 0};

                vx[i] = v[0]; vy[i] = v[1]; vz[i] = v[2];
                tu[i] = t[0]; tv[i] = t[1];
                nx[i] = nn[0]; ny[i] = nn[1]; nz[i] = nn[2];
            }

            return new ObjMeshModel(tex, color, vx, vy, vz, tu, tv, nx, ny, nz);
        } catch (Throwable t) {
            return null;
        }
    }

    private static float f(String s) {
        try { return Float.parseFloat(s); } catch (Throwable t) { return 0f; }
    }

    private static int[] parseIdx(String token) {
        // formats: v, v/vt, v//vn, v/vt/vn
        String[] p = token.split("/");
        int v = p.length > 0 ? idx(p[0]) : -1;
        int t = (p.length > 1 && !p[1].isEmpty()) ? idx(p[1]) : -1;
        int n = (p.length > 2 && !p[2].isEmpty()) ? idx(p[2]) : -1;
        return new int[]{v, t, n};
    }

    private static int idx(String s) {
        // OBJ indices are 1-based
        try { return Integer.parseInt(s.trim()) - 1; } catch (Throwable t) { return -1; }
    }

    private static ResourceLocation rl(String s) {
        if (s == null || s.isBlank()) return null;
        try { return new ResourceLocation(s.trim()); } catch (Throwable t) { return null; }
    }
}
