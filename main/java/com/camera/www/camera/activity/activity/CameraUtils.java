package com.camera.www.camera.activity.activity;

import android.hardware.Camera;
import android.media.ExifInterface;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtils {
    public static Camera.Size getProperSize(List<Camera.Size> sizeList, float displayRatio)
    {
        //先对传进来的size列表进行排序
        Collections.sort(sizeList, new SizeComparator());

        Camera.Size result = null;
        for(Camera.Size size: sizeList)
        {
            float curRatio =  ((float)size.width) / size.height;
            if(curRatio - displayRatio == 0)
            {
                result = size;
            }
        }
        if(null == result)
        {
            for(Camera.Size size: sizeList)
            {
                float curRatio =  ((float)size.width) / size.height;
                if(curRatio == 3f/4)
                {
                    result = size;
                }
            }
        }
        return result;
    }

    static class SizeComparator implements Comparator<Camera.Size>
    {

        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            Camera.Size size1 = lhs;
            Camera.Size size2 = rhs;
            if(size1.width < size2.width
                    || size1.width == size2.width && size1.height < size2.height)
            {
                return -1;
            }
            else if(!(size1.width == size2.width && size1.height == size2.height))
            {
                return 1;
            }
            return 0;
        }

    }
    public static int readPictureDegree(String path) {

        int degree = 0;

        try {

            ExifInterface exifInterface = new ExifInterface(path);

            int orientation = exifInterface.getAttributeInt(

                    ExifInterface.TAG_ORIENTATION,

                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:

                    degree = 90;

                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:

                    degree = 180;

                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:

                    degree = 270;

                    break;

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

        return degree;

    }

}
