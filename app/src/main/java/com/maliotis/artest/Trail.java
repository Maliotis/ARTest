package com.maliotis.artest;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.collision.Sphere;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;

public class Trail {
    private Vector3 distanceBetweenSpheres;
    private Vector3 v;
    private Vector3 v1;
    private Node nodeSphere;
    private Renderable[] renderable;

    Trail(Line line) {
        renderable = new Renderable[1];
        nodeSphere = new Node();
        distanceBetweenSpheres = new Vector3(0.2f, 0.2f, 0.2f);
        divisionVectors(line);

        MaterialFactory.makeOpaqueWithColor(line.getActivity(), new Color())
                .thenAccept(material -> {
                    renderable[0] = ShapeFactory.makeSphere(0.1f,
                            new Vector3(.06f, .06f, 0.06f),
                            material);
                });


        while (Math.abs(v.x) < Math.abs(line.getDifference().x)
                || Math.abs(v.y) < Math.abs(line.getDifference().y)
                || Math.abs(v.z) < Math.abs(line.getDifference().z)) {
            Node node = new Node();
            node.setParent(line.getAnchorNode());
            node.setRenderable(renderable[0]);
            node.setWorldRotation(line.getRotationFromAToB());
            node.setWorldPosition(v);

            v = Vector3.subtract(v, v1);
        }
    }

    private void divisionVectors(Line line) {
        float x = line.getDifference().x * distanceBetweenSpheres.x;
        float y = line.getDifference().y * distanceBetweenSpheres.y;
        float z = line.getDifference().z * distanceBetweenSpheres.z;
        v = line.getPoints()[0];
        v1 = new Vector3(x, y, z);
    }
}
