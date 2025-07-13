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
    public static VegetationWindData read(BinaryReader reader) throws IOException {
        int count = reader.readInt();
        float[] sharedBend = reader.readFloats(count);
        float[] sharedOscillation = reader.readFloats(count);
        float[] sharedSpeed = reader.readFloats(count);
        float[] sharedTurbulence = reader.readFloats(count);
        float[] sharedFlexibility = reader.readFloats(count);
        float sharedIndependence = reader.readFloat();
        float[] branch1Bend = reader.readFloats(count);
        float[] branch1Oscillation = reader.readFloats(count);
        float[] branch1Speed = reader.readFloats(count);
        float[] branch1Turbulence = reader.readFloats(count);
        float[] branch1Flexibility = reader.readFloats(count);
        float branch1Independence = reader.readFloat();
        float[] branch2Bend = reader.readFloats(count);
        float[] branch2Oscillation = reader.readFloats(count);
        float[] branch2Speed = reader.readFloats(count);
        float[] branch2Turbulence = reader.readFloats(count);
        float[] branch2Flexibility = reader.readFloats(count);
        float branch2Independence = reader.readFloat();
        float[] ripplePlanar = reader.readFloats(count);
        float[] rippleDirectional = reader.readFloats(count);
        float[] rippleSpeed = reader.readFloats(count);
        float[] rippleFlexibility = reader.readFloats(count);
        float rippleIndependence = reader.readFloat();
        float rippleShimmer = reader.readFloat();
        float branch1StretchLimit = reader.readFloat();
        float branch2StretchLimit = reader.readFloat();
        float sharedHeightStart = reader.readFloat();
        int doShared = reader.readInt();
        int doBranch1 = reader.readInt();
        int doBranch2 = reader.readInt();
        int doRipple = reader.readInt();
        int doShimmer = reader.readInt();
        float windIndependence = reader.readFloat();
        Vector3 boundsMin = Vector3.read(reader);
        Vector3 boundsMax = Vector3.read(reader);

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
