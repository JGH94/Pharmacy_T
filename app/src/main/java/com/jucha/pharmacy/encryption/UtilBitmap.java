package com.jucha.pharmacy.encryption;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class UtilBitmap {
    public UtilBitmap() {
        // TODO Auto-generated constructor stub
    }
    /**
     * 바이트를 읽어서 비트맵으로 변환
     * @param data
     * @return
     */
    public static Bitmap byteTobitmap(byte[] data){
        Bitmap temp  = BitmapFactory.decodeByteArray(data, 0, data.length);
        return temp;
    }

}
