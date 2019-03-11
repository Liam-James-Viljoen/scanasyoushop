package com.example.scanasyoushop;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;



import java.io.IOException;


public class MainMenu extends AppCompatActivity {

    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private CameraSource cameraSource;

    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

    }

    public void startScanning(View view) {
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        barcodeInfo = (TextView) findViewById(R.id.txtContent);

        BarcodeDetector detector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.CODE_128 | Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, detector).setRequestedPreviewSize(640, 480).build();

        if (!detector.isOperational()) {
            Log.e("Detector Malfunction", "Detector Broke");
            return;
        }
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                    } else {
                        try {
                            cameraSource.start(cameraView.getHolder());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }

                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() != 0) {
                        barcodeInfo.post(new Runnable() {
                            @Override
                            public void run() {
                                barcodeInfo.setText(barcodes.valueAt(0).displayValue);
                                Log.i("Barcode Information", barcodeInfo.toString());
                            }
                        });
                    }
                }
            });
    }
}
