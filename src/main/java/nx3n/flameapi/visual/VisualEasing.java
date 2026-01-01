package nx3n.flameapi.visual;

public enum VisualEasing {
    LINEAR {
        @Override
        public float apply(float t) {
            return t;
        }
    },
    IN_QUAD {
        @Override
        public float apply(float t) {
            return t * t;
        }
    },
    OUT_QUAD {
        @Override
        public float apply(float t) {
            return t * (2f - t);
        }
    },
    IN_OUT_QUAD {
        @Override
        public float apply(float t) {
            if (t < 0.5f) {
                return 2f * t * t;
            }
            return -1f + (4f - 2f * t) * t;
        }
    };

    public abstract float apply(float t);
}
