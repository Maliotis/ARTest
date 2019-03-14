package com.maliotis.artest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.NodeParent;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.math.Vector3Evaluator;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.SkeletonNode;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ArFragment arFragment;
    private ModelRenderable andyRend,
                            bearRend,
                            catRend,
                            cowRend,
                            dogRend,
                            elephantRend,
                            ferretRend,
                            hippopotamusRend,
                            horseRend,
                            koalaBearRend,
                            lionRend,
                            reindeerRend,
                            wolverineRend;

    private ModelRenderable[] modelRenderables;

    ImageView bearImg, catImg, cowImg, dogImg, elephantImg, ferretImg, hippoImg, horseImg,
            koalaBearImg, lionImg, reindeerImg, wolverineImg;

    View arrayView[];
    ViewRenderable viewAnimal;

    private FloatingActionButton fabAndy, fabStartMoving;
    private AnchorNode startNode;
    private AnchorNode endNode;
    private NodeParent scene;
    private Node nodemodel;
    private Vector3 andyV,restV;
//    private AnimationData animationDanceData;
//    private ModelAnimator modelAnimator;
    ModelRenderable selected = bearRend; //Default bear is selected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        modelRenderables = new ModelRenderable[12];

        createVectors();

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);
        scene = arFragment != null ? arFragment.getArSceneView().getScene() : null;
        //view

        fabAndy = findViewById(R.id.floatingActionButton);
        fabStartMoving = findViewById(R.id.floatingActionButtonAnimation);
        bearImg = findViewById(R.id.bear);
        catImg = findViewById(R.id.cat);
        cowImg = findViewById(R.id.cow);
        dogImg = findViewById(R.id.dog);
        elephantImg = findViewById(R.id.elephant);
        ferretImg = findViewById(R.id.ferret);
        hippoImg = findViewById(R.id.hippopotamus);
        horseImg = findViewById(R.id.horse);
        koalaBearImg = findViewById(R.id.koala_bear);
        lionImg = findViewById(R.id.lion);
        reindeerImg = findViewById(R.id.reindeer);
        wolverineImg = findViewById(R.id.wolverine);

        setArrayView();
        setClickListener();
        setUpModel();
        //loadAnimation();

        arFragment.setOnTapArPlaneListener(this::onPlaneTap);

    }

//    private void startAnimation() {
//        modelAnimator.start();
//    }

//    private void loadAnimation() {
//        animationDanceData = andyRend.getAnimationData(0);
//        modelAnimator = new ModelAnimator(animationDanceData, andyRend);
//    }

    private void createVectors() {
        andyV = new Vector3(1f,1f,1f);
        restV = new Vector3(15f,15f,15f);
    }


    void onPlaneTap(HitResult hitResult, Plane plane, MotionEvent motionEvent) {

        if (bearRend == null) {
            return;
        }

        Anchor anchor = hitResult.createAnchor();
//        AnchorNode anchorNode = new AnchorNode(anchor);
//        anchorNode.setParent(scene);

        if (startNode == null) {
            startNode = new AnchorNode(anchor);
            startNode.setParent(scene);
            nodemodel = createModel(startNode, selected);
        } else {
            endNode = new AnchorNode(anchor);
            endNode.setParent(scene);
            //nodemodel.setParent(endNode);
            startWalking(nodemodel);

//            AnchorNode node1 = new AnchorNode();
//            node1.setWorldPosition(new Vector3());
        }
    }

    /**
     * From StackOverflow
     * @link https://stackoverflow.com/questions/51337504/how-to-move-object-from-anchor-to-anchor
     * @param nodeModel the model(node) we want to move around
     */
    private void startWalking(Node nodeModel) {
        ObjectAnimator objectAnimation;
        objectAnimation = new ObjectAnimator();
        objectAnimation.setAutoCancel(true);
        objectAnimation.setTarget(nodeModel);

        // All the positions should be world positions
        // The first position is the start, and the second is the end.
        objectAnimation.setObjectValues(nodeModel.getWorldPosition(), endNode.getWorldPosition());
        //objectAnimation.setObjectValues(nodeModel.getWorldPosition(), new Vector3(0.51f,0.51f,0.51f));

        // Use setWorldPosition to position nodemodel.
        objectAnimation.setPropertyName("worldPosition");

        // The Vector3Evaluator is used to evaluator 2 vector3 and return the next
        // vector3.  The default is to use lerp.
        objectAnimation.setEvaluator(new Vector3Evaluator());
        // This makes the animation linear (smooth and uniform).
        objectAnimation.setInterpolator(new LinearInterpolator());
        // Duration in ms of the animation.
        objectAnimation.setDuration(900);
        objectAnimation.start();

        Line line = new Line(this, endNode, startNode.getWorldPosition(), endNode.getWorldPosition());
        Trail trail = new Trail(line);

        objectAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startNode = endNode;
                endNode = null;
            }
        });
    }

    /**
     * Setting up the models we want to use later on
     * Note: All models have to be set up(loaded) first before using them
     */
    private void setUpModel() {
        ModelRenderable.builder()
                .setSource(this,R.raw.andy_dance)
                .build()
                .thenAccept(modelRenderable -> {
                    andyRend = modelRenderable;
                    //loadAnimation();
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.bear)
                .build()
                .thenAccept(renderable -> {
                    bearRend = renderable;
                })
                .exceptionally(throwable -> {
                   Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                   return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.cat)
                .build()
                .thenAccept(renderable -> {
                    catRend = renderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.cow)
                .build()
                .thenAccept(renderable -> {
                    cowRend = renderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.dog)
                .build()
                .thenAccept(renderable -> {
                    dogRend = renderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.elephant)
                .build()
                .thenAccept(renderable -> {
                    elephantRend = renderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.ferret)
                .build()
                .thenAccept(renderable -> {
                    ferretRend = renderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.hippopotamus)
                .build()
                .thenAccept(renderable -> {
                    hippopotamusRend = renderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.horse)
                .build()
                .thenAccept(renderable -> {
                    horseRend = renderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.koala_bear)
                .build()
                .thenAccept(renderable -> {
                    koalaBearRend = renderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.lion)
                .build()
                .thenAccept(renderable -> {
                    lionRend = renderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.reindeer)
                .build()
                .thenAccept(renderable -> {
                    reindeerRend = renderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this,R.raw.wolverine)
                .build()
                .thenAccept(renderable -> {
                    wolverineRend = renderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this,"Unable to load renderable", Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    /**
     * Put the model in the environment by creating a Node obj
     * @param anchorNode it's the parent of our model and it will positioned on top of it
     * @param selected the renderable we actually want to use eg. bear, cat etc.
     * @return return the model(Node) so we can move it later
     */
    private SkeletonNode createModel(AnchorNode anchorNode, ModelRenderable selected) {
        SkeletonNode skeletonNode = new SkeletonNode();
        skeletonNode.setParent(anchorNode);

        if (selected == andyRend) {
            skeletonNode.setLocalScale(andyV);
        } else {
            skeletonNode.setLocalScale(restV);
        }

        skeletonNode.setRenderable(selected);
        int bones = selected.getBoneCount();
        getBoneNames(bones, selected);
        return skeletonNode;
    }

    private void getBoneNames(int bones, ModelRenderable modelRenderable) {
        for (int i = 0; i < bones; i++) {
            Log.d("Bone names",modelRenderable.getBoneName(i) + i);
        }
    }

    private void setClickListener() {
        for (View view : arrayView) {
            view.setOnClickListener(this::onClick);
        }
        fabAndy.setOnClickListener(this::onClick);
        fabStartMoving.setOnClickListener(this::onClick);
    }

    private void setArrayView() {
        arrayView = new View[]{
                bearImg, catImg, cowImg, dogImg, elephantImg, ferretImg, hippoImg, horseImg,
                koalaBearImg, lionImg, reindeerImg, wolverineImg
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButtonAnimation:
                //startAnimation();
            case R.id.floatingActionButton:
                selected = andyRend;
                break;
            case R.id.bear:
                selected = bearRend;
                break;
            case R.id.cat:
                selected = catRend;
                break;
            case R.id.cow:
                selected = cowRend;
                break;
            case R.id.dog:
                selected = dogRend;
                break;
            case R.id.elephant:
                selected = elephantRend;
                break;
            case R.id.ferret:
                selected = ferretRend;
                break;
            case R.id.hippopotamus:
                selected = hippopotamusRend;
                break;
            case R.id.horse:
                selected = horseRend;
                break;
            case R.id.koala_bear:
                selected = koalaBearRend;
                break;
            case R.id.lion:
                selected = lionRend;
                break;
            case R.id.reindeer:
                selected = reindeerRend;
                break;
            case R.id.wolverine:
                selected = wolverineRend;
                break;
            default:
                break;
        }
    }
}
