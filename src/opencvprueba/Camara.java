/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencvprueba;

/**
 *
 * @author PC-PUBG
 */
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
 
public class Camara {
    public static void main(String[] args) {
        
        // Load Native Library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // image container object
        Mat imageArray = new Mat();
        // Video device acces
        VideoCapture videoDevice = new VideoCapture();
        // 0:Start default video device 1,2 etc video device id
        videoDevice.open(0);
        // is contected
        if (videoDevice.isOpened()) {
        // Get frame from camera
            videoDevice.read(imageArray);
            // image array
            System.out.println(imageArray.toString());
            // Release video device
            videoDevice.release();
        } else {
            System.out.println("Error.");
            
        }
    }
}