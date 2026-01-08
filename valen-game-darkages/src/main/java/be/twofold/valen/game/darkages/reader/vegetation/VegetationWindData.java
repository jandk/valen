package be.twofold.valen.game.darkages.reader.vegetation;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

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
    public static VegetationWindData read(BinarySource source) throws IOException {
        int count = source.readInt();
        Floats sharedBend = source.readFloats(count);
        Floats sharedOscillation = source.readFloats(count);
        Floats sharedSpeed = source.readFloats(count);
        Floats sharedTurbulence = source.readFloats(count);
        Floats sharedFlexibility = source.readFloats(count);
        float sharedIndependence = source.readFloat();
        Floats branch1Bend = source.readFloats(count);
        Floats branch1Oscillation = source.readFloats(count);
        Floats branch1Speed = source.readFloats(count);
        Floats branch1Turbulence = source.readFloats(count);
        Floats branch1Flexibility = source.readFloats(count);
        float branch1Independence = source.readFloat();
        Floats branch2Bend = source.readFloats(count);
        Floats branch2Oscillation = source.readFloats(count);
        Floats branch2Speed = source.readFloats(count);
        Floats branch2Turbulence = source.readFloats(count);
        Floats branch2Flexibility = source.readFloats(count);
        float branch2Independence = source.readFloat();
        Floats ripplePlanar = source.readFloats(count);
        Floats rippleDirectional = source.readFloats(count);
        Floats rippleSpeed = source.readFloats(count);
        Floats rippleFlexibility = source.readFloats(count);
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
