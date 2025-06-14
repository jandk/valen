package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record VegetationWindData(
    float[] sharedBend,
    float[] sharedOscillation,
    float[] sharedSpeed,
    float[] sharedTurbulence,
    float[] sharedFlexibility,
    float sharedIndependence,
    float[] branch1Bend,
    float[] branch1Oscillation,
    float[] branch1Speed,
    float[] branch1Turbulence,
    float[] branch1Flexibility,
    float branch1Independence,
    float[] branch2Bend,
    float[] branch2Oscillation,
    float[] branch2Speed,
    float[] branch2Turbulence,
    float[] branch2Flexibility,
    float branch2Independence,
    float[] ripplePlanar,
    float[] rippleDirectional,
    float[] rippleSpeed,
    float[] rippleFlexibility,
    float rippleIndependence,
    float rippleShimmer,
    float branch1StretchLimit,
    float branch2StretchLimit,
    float sharedHeightStart,
    int doShared,
    int doBranch1,
    int doBranch2,
    int doRipple,
    int doShimmer,
    float windIndependence,
    Vector3 boundsMin,
    Vector3 boundsMax
) {
    public static VegetationWindData read(DataSource source) throws IOException {
        int count = source.readInt();
        float[] sharedBend = source.readFloats(count);
        float[] sharedOscillation = source.readFloats(count);
        float[] sharedSpeed = source.readFloats(count);
        float[] sharedTurbulence = source.readFloats(count);
        float[] sharedFlexibility = source.readFloats(count);
        float sharedIndependence = source.readFloat();
        float[] branch1Bend = source.readFloats(count);
        float[] branch1Oscillation = source.readFloats(count);
        float[] branch1Speed = source.readFloats(count);
        float[] branch1Turbulence = source.readFloats(count);
        float[] branch1Flexibility = source.readFloats(count);
        float branch1Independence = source.readFloat();
        float[] branch2Bend = source.readFloats(count);
        float[] branch2Oscillation = source.readFloats(count);
        float[] branch2Speed = source.readFloats(count);
        float[] branch2Turbulence = source.readFloats(count);
        float[] branch2Flexibility = source.readFloats(count);
        float branch2Independence = source.readFloat();
        float[] ripplePlanar = source.readFloats(count);
        float[] rippleDirectional = source.readFloats(count);
        float[] rippleSpeed = source.readFloats(count);
        float[] rippleFlexibility = source.readFloats(count);
        float rippleIndependence = source.readFloat();
        float rippleShimmer = source.readFloat();
        float branch1StretchLimit = source.readFloat();
        float branch2StretchLimit = source.readFloat();
        float sharedHeightStart = source.readFloat();
        int doShared = source.readInt();
        int doBranch1 = source.readInt();
        int doBranch2 = source.readInt();
        int doRipple = source.readInt();
        int doShimmer = source.readInt();
        float windIndependence = source.readFloat();
        Vector3 boundsMin = Vector3.read(source);
        Vector3 boundsMax = Vector3.read(source);

        return new VegetationWindData(
            sharedBend,
            sharedOscillation,
            sharedSpeed,
            sharedTurbulence,
            sharedFlexibility,
            sharedIndependence,
            branch1Bend,
            branch1Oscillation,
            branch1Speed,
            branch1Turbulence,
            branch1Flexibility,
            branch1Independence,
            branch2Bend,
            branch2Oscillation,
            branch2Speed,
            branch2Turbulence,
            branch2Flexibility,
            branch2Independence,
            ripplePlanar,
            rippleDirectional,
            rippleSpeed,
            rippleFlexibility,
            rippleIndependence,
            rippleShimmer,
            branch1StretchLimit,
            branch2StretchLimit,
            sharedHeightStart,
            doShared,
            doBranch1,
            doBranch2,
            doRipple,
            doShimmer,
            windIndependence,
            boundsMin,
            boundsMax
        );
    }
}
