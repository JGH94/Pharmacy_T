package com.jucha.pharmacy.utils;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageTypeBitmapConvertFile {
    Context context;

    String image;

    public ImageTypeBitmapConvertFile(Context context){
        this.context = context;
    }

    public File getBitmap(String image) {
        File imgFile = null;
        this.image = image;
        try { //저는 bitmap 형태의 이미지로 가져오기 위해 아래와 같이 작업하였으며 Thumbnail을 추출하였습니다.
            Uri imageUri = Uri.parse("file://" + image);
//            Bitmap bitmap = ((BitmapDrawable) iv_edit.getDrawable()).getBitmap();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            //여기서는 ImageView에 setImageBitmap을 활용하여 해당 이미지에 그림을 띄우시면 됩니다.

            //            MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            imgFile = new File(image);
//            try {
//                imgFile.createNewFile();
//
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
//                byte[] bitmapdata = bos.toByteArray();
//
//                FileOutputStream fos = new FileOutputStream(imgFile);
//                fos.write(bitmapdata);
//                fos.flush();
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        } catch (Exception e) {
//            Logger.e("ERROR", e.getMessage().toString());
        }
        return imgFile;
    }


    public File imagePathToBitmap(String image) {
        File imgFile = null;
        Bitmap rotatebitmap;
        this.image = image;
        try {
            //            MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            imgFile = new File(image);
            try {
                imgFile.createNewFile();
                rotatebitmap = getRotatebitmap(image);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                rotatebitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//                FileOutputStream fos = new FileOutputStream(imgFile);
//                fos.write(bitmapdata);
//                fos.flush();
//                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
//            Logger.e("ERROR", e.getMessage().toString());
        }
        return imgFile;
    }

    public Bitmap getRotatebitmap(String image){
        Bitmap rotatebitmap = null;

        Bitmap bitmap = BitmapFactory.decodeFile(image);
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }

        rotatebitmap = rotate(bitmap, exifDegree);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rotatebitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//

        byte[] currentData = stream.toByteArray();

        new SaveImageTask().execute(currentData);

        return rotatebitmap;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;


            try {
                SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd_HHmmss");
                Date date = new Date();

                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/camtest");
                if (!path.exists()) {
                    path.mkdirs();
                }

                String fileName = day.format(date) + ".jpg";
                File outputFile = new File(path, fileName);

                image = outputFile.getPath();


                // 갤러리에 반영
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(outputFile));
                context.sendBroadcast(mediaScanIntent);

            } catch (Exception e){

            }

            return null;
        }

    }
}

