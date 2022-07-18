package dte.masteriot.tfm.securitycamerademo;

import static java.lang.Thread.sleep;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class YUV_RGB_Converter {

    private String yuv_file; //Input YUV 4:2:0 file to convert in the current folder
    private double scale_factor; //Specifies how many more data is needed to read besides luminance. In YUV 4:2:0, it should be 3/2
                                // because if 1 is to get all luminance information, 1/4 more is needed for each chrominance, so in total: 1+1/2+1/2=3/2
                                // Visual representation:
    /*
    YUV 4:2:0 is stored in file as this structure if width and heigh are 4.
    Y Y Y Y
    Y Y Y Y
    Y Y Y Y
    Y Y Y Y
    U U
    U U
    Y Y
    Y Y
     */

    private int width;
    private int height;
    public YUV_RGB_Converter(String yuv_file, double scale_factor,int width,int height){
        this.yuv_file = yuv_file;
        this.scale_factor=scale_factor;
        this.width=width;
        this.height=height;
    }
    public void run() {
        File file = new File("./"+yuv_file);
        //Quantity of bytes to read per frame in yuv file: width*height*scale_factor
        int amount_to_read_per_frame=(int)Double.parseDouble(width*height*scale_factor+"");
        try {
            byte[] buffer = new byte[amount_to_read_per_frame];
            int[] rgbData = new int [width*height];
            long fileLength = 0;

            //Loop for each frame until nothing new to read
            while( (fileLength+amount_to_read_per_frame)<=file.length()){
                readFile(file,fileLength,buffer);
                fileLength=fileLength+amount_to_read_per_frame;
                //Conversor
                YUV_420_TO_RGB(rgbData,buffer,width, height);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("SecCam","YUV_RGB_Converter thread finished");
    }


    /**
     * Reads fileLength from file to buffer
     * @param file
     * @param fileLength
     * @param buffer
     * @throws IOException
     */
    public static void readFile(File file,Long fileLength,byte[] buffer) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        in.skip(fileLength);
        int read=0;
        if((read=in.read(buffer,0,buffer.length))!=buffer.length){
            System.out.println("Error reading. only read:"+read);
        }
        in.close();
    }

    /**
     * Function that converts an array of bytes in yuv420 format to an array of RGB integer
     * @param argb Output RGB array
     * @param yuv Input YUV420 array
     * @param width of the picture
     * @param height of the picture
     */
    public static void YUV_420_TO_RGB(int[] argb, byte[] yuv, int width, int height) {
        final int frameSize = width * height;

        final int ii = 0;
        final int ij = 0;
        final int di = +1;
        final int dj = +1;

        int a = 0;
        for (int i = 0, ci = ii; i < height; ++i, ci += di) {
            for (int j = 0, cj = ij; j < width; ++j, cj += dj) {
                //Convert pixel by pixel
                int y = (0xff & ((int) yuv[ci * width + cj]));
                int u = (0xff & ((int) yuv[frameSize + (ci >> 1 ) * width/2 + (cj >> 1 ) + 0]));
                int v = (0xff & ((int) yuv[frameSize+ frameSize/4 + (ci >> 1 ) * width/2 + (cj >> 1) + 0]));
                y = y < 16 ? 16 : y;

                int r = (int) (1.164f * (y - 16) + 1.596f * (v - 128));
                int g = (int) (1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = (int) (1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                argb[a++] = 0xff000000 | (r << 16) | (g << 8) | b;
            }
        }
    }
}
