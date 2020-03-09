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
   // Mat src = new Mat();
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
    private ArrayList<Referencia> a = new ArrayList<>();

    public CornerHarris() {
        /// Load source image and convert it to gray
       
        String filename = "C:\\Users\\PC-PUBG\\Documents\\WERO\\TT2\\engrane.jpg";
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
        
        
        ///Para buscar y dibujar circulos 
//        Mat circleOut = new Mat();
//        Imgproc.HoughCircles(srcFiltrado, circleOut, Imgproc.HOUGH_GRADIENT, (double)srcFiltrado.rows()/16, 100.0, 30.0, 1, 30);
//        
//        for(int x=0;x<1;x++){
//            double[] c = circleOut.get(0, x);
//            
//            //Centro del circulo
//               Point centro = new Point(Math.round(c[0]), Math.round(c[1]));
//               Imgproc.circle(src, centro, 1, new Scalar(0,100,100),3,8,0);
//              ///
//              
//              
//              int radio = (int) Math.round(c[2]);
//              Imgproc.circle(src, centro, radio, new Scalar(255,0,255),3,8,0);          
//        }
        
             

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
        // System.out.println("Hola");
        for (int i = 0; i < dstNorm.rows(); i++) {
            for (int j = 0; j < dstNorm.cols(); j++) {
                if ((int) dstNormData[i * dstNorm.cols() + j] > this.threshold) {
                   // Imgproc.circle(dstNormScaled, new Point(j, i), 5, new Scalar(0), 2, 8, 0);
                    ///System.out.println("i: "+i+" j: "+j);
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
                       if(distanciaEuclidiana(listaPuntos.get(i), listaPuntos.get(j))<=9){     
                              array[j]=1;                                        
                       }
                    }
                }           
          }



        
        //Imprimir puntos limpios
        
       
        int cont=0;
          for(int i=0;i<this.listaPuntos.size();i++){ 
            if(array[i]==0){
                Imgproc.circle(dstNormScaled, new Point(listaPuntos.get(i).getY(), listaPuntos.get(i).getX()), 5, new Scalar(0), 2, 8, 0);
                System.out.println("v "+listaPuntos.get(i).getX()+" "+listaPuntos.get(i).getY()+" 0");
                  //System.out.println("("+listaPuntos.get(i).getX()+","+listaPuntos.get(i).getY()+")");
                  cont++;
            } 

          }
          
           System.out.println("Puntos: Iniciales:"+this.listaPuntos.size()+" Finales: "+cont);
           System.out.println("Umbral: "+this.threshold);
            
           
          // generarCaras();
      Imgproc.circle(dstNormScaled, new Point(72, 190), 5, new Scalar(255,0,0), 2, 8, 0);
      Imgproc.circle(dstNormScaled, new Point(69, 244), 5, new Scalar(255,0,0), 2, 8, 0);
       
       this.generarCaras();
       //this.obtenerPixeles();
       

        cornerLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(dstNormScaled)));
        frame.repaint();
    }
    
       
    


     private void generarCaras(){
         
         for(int i=0;i<this.listaPuntos.size();i++){
            this.a.clear(); 
             for(int j=0;j<this.listaPuntos.size();j++){
                 if(j!=i){
                    a.add(new Referencia(j, CornerHarris.distanciaEuclidiana(this.listaPuntos.get(i), this.listaPuntos.get(j))));
                 }
             }
             ///Ordenar todos los puntos de menor a Mayor
             this.ordenar();
             
             //Elegir los tres puntos más cercanos y validar  valor RGB
             validarCara(i);
                          
       }
                
     } 
     
    
    private void validarCara(int ref){
        Punto puntoRef = this.listaPuntos.get(ref);
        for(int i=0;i<3;i++){
            Punto aux = this.listaPuntos.get(this.a.get(i).getRefencia());    
            ///Validar que no se encuentren en los mismo valores de x y y o dentro de un rangpo +- 5
            if(((puntoRef.getX()<=aux.getX()+5)&&(puntoRef.getX()>=aux.getX()-5))||((puntoRef.getY()<=aux.getY()+5)&&(puntoRef.getY()>=aux.getY()-5))){
                 //Aqui se cumplen las ca´racteristicas para validar por color y utilizar el punto medio 
                 System.out.println("Aqui se cumplen las caracteristicas");
                 System.out.println("Ref "+puntoRef.toString());
                 System.out.println("Aux "+aux.toString());
                 
                 double[] c1 = this.srcFiltrado.get(puntoRef.getY(), puntoRef.getX());
                 double[] c2 = this.srcFiltrado.get(aux.getY(), aux.getX());
                 
                 System.out.println("");
                 //Promediar el valor 
                 c1[0]=(c1[0]+c2[0])/2;
                // c1[1]=(c1[1]+c2[1])/2;
                // c1[2]=(c1[2]+c2[2])/2;
                 
                 System.out.println("");
                 
            }    
        }
    }
    
    private void obtenerPixeles(){
        for(int i=0;i<this.srcFiltrado.cols();i++){
            for(int j=0;j<this.srcFiltrado.rows();j++){
                 double[] c1 = this.srcFiltrado.get(i, j);
            }
        }
    }
    
     private  void ordenar(){ 
         for (int i=0; i < a.size()-1;i++){
              for(int j=0; j < a.size()-1;j++){
                  if (a.get(j).getDistancia()>a.get(j+1).getDistancia()){
                    Referencia aux = a.get(j);
                    a.set(j, a.get(j+1));
                    a.set(j+1, aux);
                  }
              }
         }
    }
     
     
     public static double distanciaEuclidiana(Punto a, Punto b){
        return Math.sqrt(Math.pow((double)a.getX()-b.getX(),2)+Math.pow((double)a.getY()-b.getY(),2));
    }
    
    public static Punto puntoMedio(Punto a, Punto b){
        return new Punto(Math.round((a.getX()+b.getX())/2), Math.round((a.getY()+b.getY())/2));
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


