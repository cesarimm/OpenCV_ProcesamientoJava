/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//cONVEX FULL
package opencvprueba;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

class CornerHarris {
    private Mat srcGray = new Mat();
    private Mat srcFiltrado = new Mat();
    private Mat dst = new Mat();
    private Mat dstNorm = new Mat();
    private Mat dstNormScaled = new Mat();
    private JFrame frame;
    private JLabel imgLabel;
    private JLabel cornerLabel;
    private ArrayList<Punto> listaPuntos;
    private static final int MAX_THRESHOLD = 255;
    private int threshold = 200;
    private static final Size BLUR_SIZE = new Size(3,3);
    private static final Size Filtrado_Size = new Size(5,5);

    public CornerHarris() {
        /// Load source image and convert it to gray
       
        String filename = "C:\\Users\\PC-PUBG\\Documents\\WERO\\TT2\\cuadrado.jpg";
        Mat src = Imgcodecs.imread(filename);
        if (src.empty()) {
            System.err.println("Cannot read image: " + filename);
            System.exit(0);
        }

        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
       // Imgproc.GaussianBlur(srcGray, srcFiltrado, Filtrado_Size, 0);
          Imgproc.medianBlur(srcGray, srcFiltrado, 5);
        //Aplicar el filtro canny
        Imgproc.Canny(srcFiltrado, srcGray, 50, 150);
        
        ///Para buscar circulos 
        Mat circleOut = new Mat();
        Imgproc.HoughCircles(srcFiltrado, circleOut, Imgproc.HOUGH_GRADIENT, (double)srcFiltrado.rows()/16, 100.0, 30.0, 1, 30);
        
        for(int x=0;x<1;x++){
            double[] c = circleOut.get(0, x);
            
            //Centro del circulo
               Point centro = new Point(Math.round(c[0]), Math.round(c[1]));
               Imgproc.circle(src, centro, 1, new Scalar(0,100,100),3,8,0);
              ///
              
              
              int radio = (int) Math.round(c[2]);
              Imgproc.circle(src, centro, radio, new Scalar(255,0,255),3,8,0);          
        }
        
        ///Para encpntrar contorno
//        
//         Mat cannyOutput = new Mat();
//        Imgproc.Canny(srcFiltrado, cannyOutput, threshold, threshold * 2);
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//        Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
//        for (int i = 0; i < contours.size(); i++) {
//            Scalar color = new Scalar(250, 0, 0);
//            Imgproc.drawContours(drawing, contours, i, color, 2, 0, hierarchy, 0, new Point());  
//        }
//        
//          srcGray = drawing;

        // Create and set up the window.
        frame = new JFrame("Harris corner detector demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(src);
        addComponentsToPane(frame.getContentPane(), img);
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window.
        frame.pack();
        frame.setVisible(true);
        update();
    }

    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));

        sliderPanel.add(new JLabel("Threshold: "));
        JSlider slider = new JSlider(0, MAX_THRESHOLD, threshold);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                threshold = source.getValue();
                update();
            }
        });
        sliderPanel.add(slider);
        pane.add(sliderPanel, BorderLayout.PAGE_START);

        JPanel imgPanel = new JPanel();
        imgLabel = new JLabel(new ImageIcon(img));
        imgPanel.add(imgLabel);
        Mat detectedEdges = new Mat();
        Mat blackImg = Mat.zeros(srcGray.size(), CvType.CV_8U);
      //  Imgproc.Canny(blackImg,detectedEdges,50,100);
        cornerLabel = new JLabel(new ImageIcon(HighGui.toBufferedImage(blackImg)));
        imgPanel.add(cornerLabel);

        pane.add(imgPanel, BorderLayout.CENTER);
    }

    private void update() {
         this.listaPuntos=new ArrayList<Punto>();
        dst = Mat.zeros(srcGray.size(), CvType.CV_32F);

        /// Detector parameters
        int blockSize = 2;
        int apertureSize = 3;
        double k = 0.0419;

        /// Detecting corners
        Imgproc.cornerHarris(srcGray, dst, blockSize, apertureSize, k);

        /// Normalizing
        Core.normalize(dst, dstNorm, 0, 255, Core.NORM_MINMAX);
        Core.convertScaleAbs(dstNorm, dstNormScaled);

        /// Drawing a circle around corners
        float[] dstNormData = new float[(int) (dstNorm.total() * dstNorm.channels())];
        dstNorm.get(0, 0, dstNormData);
         System.out.println("Hola");
        for (int i = 0; i < dstNorm.rows(); i++) {
            for (int j = 0; j < dstNorm.cols(); j++) {
                if ((int) dstNormData[i * dstNorm.cols() + j] > threshold) {
                   // Imgproc.circle(dstNormScaled, new Point(j, i), 5, new Scalar(0), 2, 8, 0);
                    System.out.println("i: "+i+" j: "+j);
                    Punto p = new Punto(i,j);
                    this.listaPuntos.add(p);                 
                }
            }
        }
        
        ///Limpiar los puntos
        int array[] = new int[listaPuntos.size()];
        
        
            for(int i=0;i<this.listaPuntos.size();i++){ 
                for(int j=i;j<this.listaPuntos.size();j++){
                    if(array[j]==0)
                    if(j!=i){
                       if(distanciaEuclidiana(listaPuntos.get(i), listaPuntos.get(j))<=5){     
                              array[j]=1;                                        
                       }
                    }
                }           
          }


//  Map<Integer, Integer> map = new HashMap<Integer, Integer>();
//        map.put(listaPuntos.get(0).getX(), listaPuntos.get(0).getY());
//  
//              for(int i=1;i<this.listaPuntos.size();i++){ 
//                for(int j=i;j<this.listaPuntos.size();j++){
//                       if(map.get(listaPuntos.get(j-1).getX())){
//                           
//                       }
//                    }
//                }           
          
  
        
///Barridos en x and y
   
//        for(int i=0;i<this.listaPuntos.size();i++){
//            for(int j=i;j<this.listaPuntos.size();j++){
//                if(j!=i){
//                    if((listaPuntos.get(j).getX()-listaPuntos.get(i).getX())<3&&listaPuntos.get(j).getX()-listaPuntos.get(i).getX()>=0){
//                        array[j]=1;
//                    }
//                }
//            }           
//        }
//        
//        for(int i=0;i<this.listaPuntos.size();i++){
//            for(int j=i;j<this.listaPuntos.size();j++){
//                if(j!=i){
//                    if((listaPuntos.get(j).getY()-listaPuntos.get(i).getY())<3&&listaPuntos.get(j).getY()-listaPuntos.get(i).getY()>=0){
//                        array2[j]=1;
//                    }
//                }
//            }           
//        }
        
        
//         for(int i=0;i<this.listaPuntos.size();i++){ 
//            for(int j=i;j<this.listaPuntos.size();j++){
//                if(j!=i){
//                    if(distanciaEuclidiana(listaPuntos.get(i), listaPuntos.get(j))<10)
//                    if(Math.abs(listaPuntos.get(j).getY()-listaPuntos.get(i).getY())<3){
//                           if(array[j]==1){
//                                array[j]=0;
//                                cont--;
//                           }
//                    }
//                }
//            }           
//        }
        
        //Imprimir puntos limpios
        
        System.out.println("Puntos: Iniciales:"+this.listaPuntos.size()+" Finales: "+(this.listaPuntos.size()));
        
          for(int i=0;i<this.listaPuntos.size();i++){ 
            if(array[i]==0){
                Imgproc.circle(dstNormScaled, new Point(listaPuntos.get(i).getY(), listaPuntos.get(i).getX()), 5, new Scalar(0), 2, 8, 0);
                System.out.println("i: "+listaPuntos.get(i).getX()+" j: "+listaPuntos.get(i).getY());
                  
            } 
//            if(array2[i]==0){
//                 if(array[i]!=0)
//                System.out.println("2i: "+listaPuntos.get(i).getX()+" j: "+listaPuntos.get(i).getY());
//                  
//            } 
          }
        
        
//        ArrayList<Punto> listaAux = (ArrayList<Punto>) this.listaPuntos.clone();
//        ArrayList<Punto> puntosFinales = new ArrayList<>();
//        
//         for(int i=0;i<listaAux.size();i++){
//              for(int j=0;j<listaAux.size();j++){
//                  if(i!=j){
//                      // System.out.println(listaAux.get(j).getX()-listaAux.get(i).getX());
//                       //if(listaAux.get(j).getX()-listaAux.get(i).getX()<3){
//                           
//                          
////                            if(distanciaEuclidiana(listaAux.get(i), listaAux.get(j))>10){
////                                listaAux.remove(j);
////                            }
//                       //}
//                 }          
//            }
//        }
        
        
//        for(int i=0;i<listaAux.size();i++){
//            Imgproc.circle(dstNormScaled, new Point(listaAux.get(i).getX(),  listaAux.get(i).getY()), 5, new Scalar(0), 2, 8, 0);
//        }
        
//        System.out.println("Elemntos sin eliminar: "+this.listaPuntos.size());
//        System.out.println("Elemntos eliminados: "+listaAux.size());
        
        Imgproc.circle(dstNormScaled, new Point(43, 246), 5, new Scalar(255,0,0), 2, 8, 0);

        cornerLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(dstNormScaled)));
        frame.repaint();
    }
    
       
    private double distanciaEuclidiana(Punto a, Punto b){
        return Math.sqrt(Math.pow((double)a.getX()-b.getX(),2)+Math.pow((double)a.getY()-b.getY(),2));
    }    
    
        public static void main(String[] args) {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CornerHarris();
            }
        });
       
            //System.out.println(CornerHarris.distanciaEuclidiana(new Punto(2,2), new Punto(4,4)));

    }
    
        
}



