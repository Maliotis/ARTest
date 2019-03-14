package com.maliotis.artest;

import android.app.Activity;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;

import java.lang.ref.WeakReference;

public class Line {
    private Vector3[] points;
    private Node line;
    private WeakReference<Activity> weak;
    private Activity activity;
    private AnchorNode anchorNode;


    private Vector3 difference;
    private Vector3 directionFromTopToBottom;
    private Quaternion rotationFromAToB;
    private Renderable[] renderable;

    Line(Activity activity, AnchorNode anchorNode, Vector3 point1, Vector3 point2) {
        points = new Vector3[2];
        weak = new WeakReference<>(activity);
        this.activity = activity;
        this.anchorNode = anchorNode;
        setPoints(point1, point2);
        line = new Node();
        createTheLine();
    }

    private void createTheLine() {
        difference = Vector3.subtract(points[0], points[1]);
        directionFromTopToBottom = difference.normalized();
        rotationFromAToB = Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());
        renderable = new Renderable[1];

        MaterialFactory.makeOpaqueWithColor(activity, new Color())
                .thenAccept(material -> {
                    renderable[0] = ShapeFactory.makeCube(new Vector3(.01f, .01f, difference.length()),
                            Vector3.zero(), material);
                });


        line.setParent(anchorNode);
        line.setRenderable(renderable[0]);
        line.setWorldPosition(Vector3.add(points[0], points[1]).scaled(0.5f));
        line.setWorldRotation(rotationFromAToB);
    }

    public Vector3[] getPoints() {
        return points;
    }

    private void setPoints(Vector3 point1, Vector3 point2) {
        points[0] = point1;
        points[1] = point2;
    }

    public Node getLine() {
        return line;
    }

    public Vector3 getDifference() {
        return difference;
    }

    public Vector3 getDirectionFromTopToBottom() {
        return directionFromTopToBottom;
    }

    public Quaternion getRotationFromAToB() {
        return rotationFromAToB;
    }

    public Renderable[] getRenderable() {
        return renderable;
    }

    public Activity getActivity() {
        return activity;
    }

    public AnchorNode getAnchorNode() {
        return anchorNode;
    }
}
