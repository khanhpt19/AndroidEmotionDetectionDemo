package com.example.khanh.test1;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.contract.Scores;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public EmotionServiceClient emotionServiceClient = new EmotionServiceRestClient("17160e4f9907442c8ecd04d53fe5bfdf");
    private ImageView imageView;
    private Button btnLoad;
    private  Bitmap mBitmap;
    private ArrayList<Integer>manga=new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoad= (Button) findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manga.add(R.drawable.happy2);
                manga.add(R.drawable.happy1);
                manga.add(R.drawable.happy3);
                manga.add(R.drawable.neutral);
                manga.add(R.drawable.neutral2);
                manga.add(R.drawable.surprise);
                manga.add(R.drawable.surprise1);//surprise
                manga.add(R.drawable.fear);//fear
                manga.add(R.drawable.sadness);//sadness
                manga.add(R.drawable.sadness2);//sadness

                Random r= new Random();
                int n=r.nextInt(manga.size());

                mBitmap = BitmapFactory.decodeResource(getResources(),manga.get(n));
                imageView = (ImageView)findViewById(R.id.imageView);
                imageView.setImageBitmap(mBitmap);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                AsyncTask<InputStream,String,List<RecognizeResult>> emotionTask= new AsyncTask<InputStream,String,List<RecognizeResult>>() {
                    ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                    @Override
                    protected List<RecognizeResult> doInBackground(InputStream... params) {
                        try{
                            publishProgress("Chờ chút nhé....");
                            List<RecognizeResult> result = emotionServiceClient.recognizeImage(params[0]);
                            return result;
                        }
                        catch (Exception ex) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        mDialog.show();
                    }

                    @Override
                    protected void onPostExecute(List<RecognizeResult> recognizeResults) {
                        mDialog.dismiss();
                        for(RecognizeResult res : recognizeResults) {
                            String status = getEmo(res);

                            imageView.setImageBitmap(ImageHelper.drawRectOnBitmap(mBitmap,res.faceRectangle,status));
                        }
                    }
                    @Override
                    protected void onProgressUpdate(String... values) {
                        mDialog.setMessage(values[0]);
                    }
                };
                emotionTask.execute(inputStream);
            }
        });
    }



    private String getEmo(RecognizeResult res) {
        List<Double> list = new ArrayList<>();
        Scores scores = res.scores;

        list.add(scores.anger);
        list.add(scores.happiness);
        list.add(scores.contempt);
        list.add(scores.disgust);
        list.add(scores.fear);
        list.add(scores.neutral);
        list.add(scores.sadness);
        list.add(scores.surprise);

        Collections.sort(list);

        double maxNum = list.get(list.size() - 1);
        if(maxNum == scores.anger)
            return "Anger";
        else if(maxNum == scores.happiness)
            return "Happy";
        else if(maxNum == scores.contempt)
            return "Contemp";
        else if(maxNum == scores.disgust)
            return "Disgust";
        else if(maxNum == scores.fear)
            return "Fear";
        else if(maxNum == scores.neutral)
            return "Neutral";
        else if(maxNum == scores.sadness)
            return "Sadness";
        else if(maxNum == scores.surprise)
            return "Surprise";
        else
            return "Neutral";

    }

}
