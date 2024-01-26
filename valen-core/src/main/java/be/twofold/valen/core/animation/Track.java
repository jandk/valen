package be.twofold.valen.core.animation;

import be.twofold.valen.core.math.*;

import java.util.*;

public sealed interface Track<T> permits Track.Rotation, Track.Scale, Track.Translation {

    int boneId();

    List<KeyFrame<T>> keyFrames();

    record Rotation(
        int boneId,
        List<KeyFrame<Quaternion>> keyFrames
    ) implements Track<Quaternion> {
    }

    record Scale(
        int boneId,
        List<KeyFrame<Vector3>> keyFrames
    ) implements Track<Vector3> {
    }

    record Translation(
        int boneId,
        List<KeyFrame<Vector3>> keyFrames
    ) implements Track<Vector3> {
    }

}
