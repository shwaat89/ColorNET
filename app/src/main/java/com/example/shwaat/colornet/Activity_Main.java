package com.example.shwaat.colornet;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import java.io.InputStream;
import android.database.Cursor;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.content.Context;

public class Activity_Main extends ActionBarActivity {

    Intent pickPhoto;
    Button convertButton;
    TextView fileField;
    Bitmap theImgBitmap;
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        convertButton = (Button)findViewById(R.id.activity_main_convertButton);
        fileField = (TextView)findViewById(R.id.activity_main_fileDialog);
        pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void browseAction(View view)
    {
        startActivityForResult(pickPhoto , 1);
    }

    public void setImgBitmap(Bitmap bm)
    {
        theImgBitmap = bm;
    }

    public Bitmap getImgBitmap()
    {
        return theImgBitmap;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String path = getRealPathFromURI(selectedImage);
                    fileField.setText(path);
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                        setImgBitmap(yourSelectedImage);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        if(imageReturnedIntent != null) {
            convertButton.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Image Received.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    public void convertAction(View view)
    {
        Bitmap theBm = getImgBitmap();
        Bitmap theBmScaled = scaleDownBitmap(theBm, 200, getApplicationContext());
        Bitmap convertedBmp = toGrayscale(theBmScaled);
        byte[] bmpBytes = convertImgToByteArray(convertedBmp);
        Intent intent = new Intent(this, Activity_IV_Color.class);
        intent.putExtra("picture", bmpBytes);
        Toast.makeText(getApplicationContext(), "Converting..", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public byte[] convertImgToByteArray(Bitmap bmp)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    @Override
    public void onDestroy()
    {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

}
