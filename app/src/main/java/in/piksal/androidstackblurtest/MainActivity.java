package in.piksal.androidstackblurtest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.enrique.stackblur.StackBlurManager;

import java.io.InputStream;
import java.security.spec.ECField;

//source of module StackBlur
// https://github.com/kikoso/android-stackblur

public class MainActivity extends Activity {

    //use lib
    StackBlurManager stackBlurManager;

    //view
    ImageView imageView;
    SeekBar seekBar;

    //async
    //souce https://github.com/kikoso/android-stackblur/blob/master/StackBlurDemo/src/com/example/stackblurdemo/BenchmarkActivity.java
    private BenchmarkTask benchmarkTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //view
        imageView = (ImageView) findViewById(R.id.imageView);
        seekBar = (SeekBar) findViewById(R.id.mSeekBar);

        Bitmap bitmap = getFromRaw();

        stackBlurManager = new StackBlurManager(bitmap);
        //stackBlurManager.process(100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               // stackBlurManager.process(progress);
                //imageView.setImageBitmap(stackBlurManager.returnBlurredImage());
                Log.i("progress", "p = " + progress);

                if (benchmarkTask !=null){
                    benchmarkTask.cancel(true);
                }
                benchmarkTask = new BenchmarkTask();
                benchmarkTask.execute(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    protected Bitmap getFromRaw()
    {
        try{
            InputStream in = getResources().openRawResource(R.raw.mai);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    private class BenchmarkTask extends AsyncTask<Integer, Void, Bitmap> {
        private int max = Integer.MIN_VALUE;
        private Bitmap outBitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            if(params.length != 1 || params[0] == null)
                throw new IllegalArgumentException("Pass only 1 Integer to BenchmarkTask");
            int blurAmount = params[0];
            Bitmap inBitmap, blurredBitmap = null;
            Paint paint = new Paint();

            inBitmap =getFromRaw();// Get my bit map

            outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), inBitmap.getConfig());
            Canvas canvas = new Canvas(outBitmap);

            StackBlurManager blurManager = new StackBlurManager(inBitmap);


            // Java
            blurredBitmap = blurManager.process(blurAmount);
            canvas.save();
            canvas.clipRect(0, 0, outBitmap.getWidth(), outBitmap.getHeight());
            canvas.drawBitmap(blurredBitmap, 0, 0, paint);
            canvas.restore();
            publishProgress();
            blurredBitmap.recycle();

            if(isCancelled())
                return outBitmap;

            return outBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            imageView.setImageBitmap(outBitmap);
        }
    }

}
