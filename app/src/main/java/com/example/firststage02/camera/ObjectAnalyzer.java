package com.example.firststage02.camera;

import android.annotation.SuppressLint;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ObjectAnalyzer implements ImageAnalysis.Analyzer {

    public GraphicOverlay graphicOverlay;


    public ObjectAnalyzer(GraphicOverlay graphicOverlay) {
        this.graphicOverlay = graphicOverlay;
    }

    public LocalModel model = new LocalModel.Builder()
            .setAssetFilePath("model.tflite")
            .build();

    CustomObjectDetectorOptions customObjectDetectorOptions =
            new CustomObjectDetectorOptions.Builder(model)
                    .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                    .enableClassification()
                    .setClassificationConfidenceThreshold(0.5f)
                    .setMaxPerObjectLabelCount(2)
                    .build();

//
//    ObjectDetectorOptions options = new ObjectDetectorOptions.Builder()
//            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
//            .enableClassification()  // Optional
//            .build();

    ObjectDetector objectDetector = ObjectDetection.getClient(customObjectDetectorOptions);

    private int lensFacing = CameraSelector.LENS_FACING_BACK;

    @Override
    public void analyze(@NonNull @NotNull ImageProxy image) {

        boolean isImageFliped = CameraSelector.LENS_FACING_FRONT == lensFacing;
        int RotationalDegree = image.getImageInfo().getRotationDegrees();

        if(RotationalDegree==0 || RotationalDegree==180){
            graphicOverlay.setImageSourceInfo(image.getWidth(),image.getHeight(),isImageFliped);
        }
        else {
            graphicOverlay.setImageSourceInfo(image.getHeight(),image.getWidth(),isImageFliped);
        }

        @SuppressLint("UnsafeOptInUsageError") Image img = image.getImage();

        if(img != null){
            InputImage inputImage = InputImage.fromMediaImage(img, RotationalDegree);

            objectDetector.process(inputImage).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    image.close();
                }
            }).addOnSuccessListener(new OnSuccessListener<List<DetectedObject>>() {
                @Override
                public void onSuccess(@NonNull @NotNull List<DetectedObject> detectedObjects) {
                    graphicOverlay.clear();

                    for (DetectedObject detectedObject : detectedObjects){
                        ObjectGraphic objGraph = new ObjectGraphic(graphicOverlay,detectedObject);
                        graphicOverlay.add(objGraph);
                    }
                    graphicOverlay.postInvalidate();

                    image.close();
                }
            });

        }

    }
}
