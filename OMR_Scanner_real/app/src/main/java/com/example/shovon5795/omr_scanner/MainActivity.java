package com.example.shovon5795.omr_scanner;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import static android.R.attr.src;
import static android.R.id.message;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR;
import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.HoughCircles;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.adaptiveThreshold;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.resize;




public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    int rowIncrement = 30;
    int colIncrement = 55;
    int imgColStart = 60;
    private static final String TAG = "mytag";
    JavaCameraView javaCameraView;
    Button button;
    Mat mRgba, imgGray, imSize, imClahe, athres, imgCanny, imClahe2, circles, ht, lines;

    SharedPreferences sharedPreferences;

    ImageView image;
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS: {
                    javaCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }

        }
    };

    TextView type;

    String getType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();

        type = (TextView)findViewById(R.id.type);

        getType=bundle.getString("type");

        type.setText(getType);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);


        button = (Button) findViewById(R.id.button);
        image = (ImageView) findViewById(R.id.image);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent img = new Intent(); //Your Intent
                //img.setAction(MediaStore.ACTION_IMAGE_CAPTURE); //the intents action to capture the image
                //startActivityForResult(img,1);//start the activity adding any code .1 in this example]

                Bitmap bitmap = Bitmap.createBitmap(imgGray.cols(), imgGray.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(imgGray, bitmap);
                image.setImageBitmap(bitmap);
                //Start

                int traverse[]={1,1,1,1,1,1,1,1};
                int answer[]={-1,-1,-1,-1,-1,-1,-1,-1};


                boolean result=false;
                int imgRow = 0, imgCol = imgColStart;

                for(int i=0; i<4; i++){
                    for(int j=0; j<8; j++){

                        if(traverse[j]==1){

                            result = detectAnswer(imgGray, imgRow, imgCol);

                            if(result && answer[j]==-1) {

                                traverse[j]=0;
                                answer[j]= i+1;

                            }

                        }

                        imgRow += rowIncrement;
                        result = false;

                    }

                    imgRow = 0;
                    imgCol += colIncrement;

                }



                if(getType.equals("Main OMR")){
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    //intent.putExtra("Result", Arrays.toString(answer));
                    intent.putExtra("type","Student OMR");
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("main",Arrays.toString(answer));
                    editor.commit();
                    startActivity(intent);
                }
                else if(getType.equals("Student OMR")){
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    //intent.putExtra("Result", Arrays.toString(answer));
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("student",Arrays.toString(answer));
                    editor.commit();
                    startActivity(intent);
                }
                //Log.d("Answer", Arrays.toString(answer));
            }

        });


        javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Opencv not loaded");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "Opencv Loaded successfully");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallBack);
        }

    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        imgGray = new Mat(height, width, CvType.CV_8UC1);
        imSize = new Mat(height, width, CvType.CV_8UC1);
        imClahe = new Mat(height, width, CvType.CV_8UC1);
        athres = new Mat(height, width, CvType.CV_8UC1);
        imgCanny = new Mat(height, width, CvType.CV_8UC1);
        imClahe2 = new Mat(height, width, CvType.CV_8UC1);
        circles = new Mat(height, width, CvType.CV_8UC1);
        ht = new Mat(height, width, CvType.CV_8UC1);
        lines = new Mat(height, width, CvType.CV_8UC1);


    }

    @Override
    public void onCameraViewStopped() {

        mRgba.release();
        imSize.release();
        imgGray.release();
        imClahe.release();
        athres.release();
        imgCanny.release();
        imClahe2.release();
        //athres2.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Mat mRg = mRgba.t();
        Core.flip(mRgba.t(), mRg, 1);
        resize(mRg, mRg, mRgba.size());
        Imgproc.cvtColor(mRg, imSize, Imgproc.COLOR_BGR2GRAY);


        Size size = new Size(320, 240);
        resize(imSize, imgGray, size);

        Size s = new Size(3, 3);
        GaussianBlur(imgGray, imgGray, s, 0);
        adaptiveThreshold(imgGray, imgGray, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 75, 15);
        Core.bitwise_not(imgGray, imgGray);

        //Imgproc.Canny(imgGray, imgCanny, 80, 100, 3);

        int threshold = 80;
        int minLineSize = 400;
        int lineGap = 10;

        Imgproc.HoughLinesP(imgGray, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);

        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);


            Imgproc.line(imgGray, start, end, new Scalar(255, 0, 0), 3);

        }


        Imgproc.dilate(imgGray, imgGray, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));*/

        //(120,300,45) (0,240,30)




        return mRg;


    }

    public boolean detectAnswer(Mat imgGray, int imgRow, int imgCol){

        Integer sum=0;
        Integer threshold = 350;
        double[] data;
        for(int i=imgRow; i<imgRow+rowIncrement; i++)
        {
            for(int j=imgCol; j<imgCol+colIncrement; j++) {
                data = imgGray.get(i,j);
                if(data[0] > 128)
                    sum += 1;
            }
        }

        Log.d("Result", sum.toString());
        return sum>threshold?true:false;
    }

}