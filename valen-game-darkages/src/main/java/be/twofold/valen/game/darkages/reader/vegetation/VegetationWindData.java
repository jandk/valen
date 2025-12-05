package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;

public record VegetationWindData(
    Floats sharedBend,
    Floats sharedOscillationStruct,
    Floats sharedSpeedStruct,
    Floats sharedTurbulenceStruct,
    Floats sharedFlexibilityStruct,
    float sharedIndependenceStruct,
    Floats branch1Bend,
    Floats branch1OscillationStruct,
    Floats branch1SpeedStruct,
    Floats branch1TurbulenceStruct,
    Floats branch1FlexibilityStruct,
    float branch1IndependenceStruct,
    Floats branch2Bend,
    Floats branch2OscillationStruct,
    Floats branch2SpeedStruct,
    Floats branch2TurbulenceStruct,
    Floats branch2FlexibilityStruct,
    float branch2IndependenceStruct,
    Floats ripplePlanar,
    Floats rippleDirectionalStruct,
    Floats rippleSpeedStruct,
    Floats rippleFlexibilityStruct,
    float rippleIndependenceStruct,
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
        Floats sharedBend = reader.readFloats(count);
        Floats sharedOscillation = reader.readFloats(count);
        Floats sharedSpeed = reader.readFloats(count);
        Floats sharedTurbulence = reader.readFloats(count);
        Floats sharedFlexibility = reader.readFloats(count);
        float sharedIndependence = reader.readFloat();
        Floats branch1Bend = reader.readFloats(count);
        Floats branch1Oscillation = reader.readFloats(count);
        Floats branch1Speed = reader.readFloats(count);
        Floats branch1Turbulence = reader.readFloats(count);
        Floats branch1Flexibility = reader.readFloats(count);
        float branch1Independence = reader.readFloat();
        Floats branch2Bend = reader.readFloats(count);
        Floats branch2Oscillation = reader.readFloats(count);
        Floats branch2Speed = reader.readFloats(count);
        Floats branch2Turbulence = reader.readFloats(count);
        Floats branch2Flexibility = reader.readFloats(count);
        float branch2Independence = reader.readFloat();
        Floats ripplePlanar = reader.readFloats(count);
        Floats rippleDirectional = reader.readFloats(count);
        Floats rippleSpeed = reader.readFloats(count);
        Floats rippleFlexibility = reader.readFloats(count);
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
