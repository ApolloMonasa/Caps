package top.wmsnp.caps.client.renderer;

public enum RenderMode {
    NONE(null),
    OUTLINE(new VeinLineRenderer()),
    FACE(new VeinFaceRenderer());

    private final VeinRenderer renderer;

    RenderMode(VeinRenderer renderer) {
        this.renderer = renderer;
    }

    public VeinRenderer getRenderer() {
        return renderer;
    }
}