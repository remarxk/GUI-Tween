package com.remarxk.guitween.client.util;

import org.joml.Quaternionf;
import org.joml.Vector3f;

@FunctionalInterface
public interface Axis {
    Axis XN = (p_254437_) -> (new Quaternionf()).rotationX(-p_254437_);
    Axis XP = (p_254466_) -> (new Quaternionf()).rotationX(p_254466_);
    Axis YN = (p_254442_) -> (new Quaternionf()).rotationY(-p_254442_);
    Axis YP = (p_254103_) -> (new Quaternionf()).rotationY(p_254103_);
    Axis ZN = (p_254110_) -> (new Quaternionf()).rotationZ(-p_254110_);
    Axis ZP = (p_253997_) -> (new Quaternionf()).rotationZ(p_253997_);

    static Axis of(Vector3f axis) {
        return (p_254401_) -> (new Quaternionf()).rotationAxis(p_254401_, axis);
    }

    Quaternionf rotation(float var1);

    default Quaternionf rotationDegrees(float degrees) {
        return this.rotation(degrees * ((float)Math.PI / 180F));
    }
}