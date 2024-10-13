package org.redeye.dmf;

import be.twofold.valen.core.math.*;

import java.util.*;

public class DMFTransform {
    public static final DMFTransform IDENTITY = new DMFTransform(new double[]{0, 0, 0}, new double[]{1, 1, 1}, new double[]{0, 0, 0, 1});

    public final double[] position;
    public final double[] scale;
    public final double[] rotation;

    public DMFTransform( double[] position,  double[] scale,  double[] rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public DMFTransform( Matrix4 matrix) {
        final Vector3 translation = matrix.translation();
        final Vector3 scale = matrix.scale();
        final Quaternion rotation = matrix.rotation();
        this.position = new double[]{translation.x(), translation.y(), translation.z()};
        this.scale = new double[]{scale.x(), scale.y(), scale.z()};
        this.rotation = new double[]{rotation.x(), rotation.y(), rotation.z(), rotation.w()};
    }

    public DMFTransform( Vector3 translation,  Vector3 scale,  Quaternion rotation) {
        this.position = new double[]{translation.x(), translation.y(), translation.z()};
        this.scale = new double[]{scale.x(), scale.y(), scale.z()};
        this.rotation = new double[]{rotation.x(), rotation.y(), rotation.z(), rotation.w()};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DMFTransform that = (DMFTransform) o;
        return Arrays.equals(position, that.position) && Arrays.equals(scale, that.scale) && Arrays.equals(rotation, that.rotation);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(position);
        result = 31 * result + Arrays.hashCode(scale);
        result = 31 * result + Arrays.hashCode(rotation);
        return result;
    }
}
