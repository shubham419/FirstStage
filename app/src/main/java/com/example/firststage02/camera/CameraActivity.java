package com.example.firststage02.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.room.RoomDatabase;


import com.example.firststage02.Database.ImageDatabase;
import com.example.firststage02.Database.ImageEntity;
import com.example.firststage02.R;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    private ExecutorService cameraExecutor;
    private ImageDatabase database;
    private ImageEntity entity;

    private PreviewView viewFinder;
    private GraphicOverlay graphicOverlay;
    private int REQUEST_CODE_PERMISSIONS = 10;
    private static final String TAG = "CameraApp";
    private final String[] REQUIRED_PERMISSIONS = new String[] {"Manifest.permission.CAMERA"};
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Objects.requireNonNull(getSupportActionBar()).hide();

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);

        } else {
            startCamera();
        }

        graphicOverlay = findViewById(R.id.graphicOverlay);
        viewFinder =  findViewById(R.id.viewFinder);
        cameraExecutor = Executors.newSingleThreadExecutor();
        database = ImageDatabase.getInstance(CameraActivity.this);

    }


    private void startCamera(){
//
//        Rational aspectRatio = new Rational(viewFinder.getWidth(), viewFinder.getHeight());
//        Size screen = new Size(viewFinder.getWidth(),viewFinder.getHeight());
//
//        PreviewConfig pConfig = new PreviewConfig.Builder()

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(()->{

            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                ImageCapture imageCapture =
                        new ImageCapture.Builder()
                                .build();


                imageAnalysis.setAnalyzer(cameraExecutor, new ObjectAnalyzer(graphicOverlay));

                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(
                        (LifecycleOwner)this,
                        cameraSelector,
                        imageAnalysis,
                        preview,
                        imageCapture
                );

                findViewById(R.id.click_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageCapture.takePicture(ContextCompat.getMainExecutor(CameraActivity.this),
                                new ImageCapture.OnImageCapturedCallback() {
                                    @Override
                                    public void onCaptureSuccess(@NonNull @NotNull ImageProxy image) {
                                     //    @SuppressLint("UnsafeOptInUsageError") Image img =  image.getImage();
                                        Bitmap bitmap = toBitmap(image);
                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                        byte[] img = byteArrayOutputStream.toByteArray();

                                        entity = new ImageEntity(img);
                                        new InsertTask(CameraActivity.this, entity).execute();
                                        Toast.makeText(CameraActivity.this, "Saved to Protected Database", Toast.LENGTH_SHORT).show();
                                     //    super.onCaptureSuccess(image);

                                         image.close();
                                    }

                                    @Override
                                    public void onError(@NonNull @NotNull ImageCaptureException exception) {
                                        Toast.makeText(CameraActivity.this, "error", Toast.LENGTH_SHORT).show();
                                       // super.onError(exception);

                                    }
                                });
                    }
                });


            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }


        }, ContextCompat.getMainExecutor(this));


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_LONG).show();

        } else{
            startCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    private Bitmap toBitmap(ImageProxy img){

        ByteBuffer byteBuffer = img.getPlanes()[0].getBuffer();
        byteBuffer.rewind();
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        byte[] clonedBytes = bytes.clone();

        //   Bitmap bitmapnew = getResizedBitmap(bitmap, 480,640);
       return BitmapFactory.decodeByteArray(clonedBytes,0,clonedBytes.length);
    }


//    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
//        int width = bm.getWidth();
//        int height = bm.getHeight();
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//        // CREATE A MATRIX FOR THE MANIPULATION
//        Matrix matrix = new Matrix();
//        // RESIZE THE BIT MAP
//        matrix.postScale(scaleWidth, scaleHeight);
//
//        // "RECREATE" THE NEW BITMAP
//        return Bitmap.createBitmap(
//                bm, 0, 0, width, height, matrix, false);
//    }

    private static class InsertTask extends AsyncTask<Void, Void , Boolean> {

        private WeakReference<CameraActivity> activityReference;
        private ImageEntity image;

        InsertTask(CameraActivity context, ImageEntity image) {
            activityReference = new WeakReference<>(context);
            this.image = image;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            activityReference.get().database.getImage().insert(image);
            return null;
        }
    }

}