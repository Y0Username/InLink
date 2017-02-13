package com.example.kingpushpakraj.inlink;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRActivity extends AppCompatActivity {

    private ImageView qrCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        if(getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        qrCode = (ImageView) findViewById(R.id.qr_code);
        String rt = getIntent().getStringExtra("code");
        new QRTask().execute(rt);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class QRTask extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            ProgressBar pb = (ProgressBar) findViewById(R.id.sp);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                bitmap = encodeAsBitmap(params[0]);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                qrCode.setImageBitmap(bitmap);
                qrCode.setVisibility(View.VISIBLE);
                ProgressBar pb = (ProgressBar) findViewById(R.id.sp);
                pb.setVisibility(View.INVISIBLE);
            }
        }

        private Bitmap encodeAsBitmap(String qr) throws WriterException {
            BitMatrix result;
            try {
                result = new MultiFormatWriter().encode(qr, BarcodeFormat.QR_CODE, 1000, 1000, null);
            } catch (IllegalArgumentException e) {
                return null;
            }

            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white);

                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, 1000, 0, 0, w, h);
            return bitmap;
        }
    }
}
