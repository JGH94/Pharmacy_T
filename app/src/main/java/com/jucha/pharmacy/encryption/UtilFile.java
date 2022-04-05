package com.jucha.pharmacy.encryption;


import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UtilFile {
    public UtilFile() {
    }

    /**
     * 파일을 읽어서 바이트로 변환
     * @param Path
     * @return
     * @throws IOException
     */
    public static byte[] fileToByte(InputStream is) throws IOException {

        byte[] data = new byte[is.available()];
        is.read(data);
        return data;
    }
    /**
     * 스트림 가져옴
     * @param path
     * @return
     */
    public static InputStream getStream(String path){
        Log.d(UtilFile.class.getName(),"getStream = "+path);
        try {
            InputStream us = new FileInputStream(path);
            return us;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }


    /**
     *
     * 폴더에 파일로 쓰기
     * @param path
     * @param name
     * @param data
     */
    public static void writeFile(String path,String name, byte[] data){
        Log.d(UtilFile.class.getName(),"writeFile = "+path+"/"+name);
        try {
            File writefile = new File(path);
            if(!writefile.isDirectory()){
                Log.d(UtilFile.class.getName(),"make = "+path+"/"+name);
                writefile.mkdirs();
                writefile.createNewFile();
            }
            FileOutputStream fo = new FileOutputStream(writefile+"/"+name);
            fo.write(data);
            fo.flush();
            fo.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
