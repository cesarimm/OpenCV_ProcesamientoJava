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
public class Prueba {
    
    private double distanciaEuclidiana(Punto a, Punto b){
        return Math.sqrt(Math.pow((double)a.getX()-b.getX(),2)+Math.pow((double)a.getY()-b.getY(),2));
    } 
      
    public static void main(String args[]){
       // Prueba p = new Prueba();
       // System.out.println(p.distanciaEuclidiana(new Punto(9,11), new Punto(9,199)));
       
       for(int i=1;i<125;i++){
           System.out.println("f "+i+" "+(i+1)+" "+(i+2)+" "+(i+3));
       }
    }
    
}
