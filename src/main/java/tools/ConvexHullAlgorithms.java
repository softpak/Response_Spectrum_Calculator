/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import com.google.common.collect.Lists;
import java.util.List;
import softpak.gdms_fft.Station;

/**
 *
 * @author you
 */
public class ConvexHullAlgorithms {
         
    // To find orientation of ordered triplet (p, q, r). 
    // The function returns following values 
    // 0 --> p, q and r are colinear 
    // 1 --> Clockwise 
    // 2 --> Counterclockwise 
    public int orientation(Point p, Point q, Point r) 
    { 
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - 
                  (q.getX() - p.getX()) * (r.getY() - q.getY()); 
       
        if (val == 0) return 0;  // collinear 
        return (val > 0)? 1: 2; // clock or counterclock wise 
    } 
    
    public List<String> calcPoints(List<Station> st_list) {
        Point points[] = new Point[st_list.size()]; 
        for (int lc = 0; lc < st_list.size(); lc++) {
            double lon = Double.valueOf(st_list.get(lc).getLongitude());
            double lat = Double.valueOf(st_list.get(lc).getLatitude());
            String station = st_list.get(lc).getStationName();
            points[lc] = new Point(station, lon, lat); 
        }
        int n = points.length; 
        List<String> outline = Lists.newLinkedList();
        convexHull(points, n).stream().forEach(st -> outline.add(st.getStation()));
        return outline;
    }
    
    
    
    // Prints convex hull of a set of n points.
    public List<Point> convexHull(Point points[], int n) { 
        // Initialize Result 
        List<Point> hull = Lists.newLinkedList();

        // There must be at least 3 points 
        if (n < 3) {
            return hull;
        }
       
        // Find the leftmost point 
        int l = 0; 
        for (int i = 1; i < n; i++) 
            if (points[i].getX() < points[l].getX()) 
                l = i; 
       
        // Start from leftmost point, keep moving  
        // counterclockwise until reach the start point 
        // again. This loop runs O(h) times where h is 
        // number of points in result or output. 
        int p = l, q; 
        do { 
            // Add current point to result 
            hull.add(points[p]); 
       
            // Search for a point 'q' such that  
            // orientation(p, x, q) is counterclockwise  
            // for all points 'x'. The idea is to keep  
            // track of last visited most counterclock- 
            // wise point in q. If any point 'i' is more  
            // counterclock-wise than q, then update q. 
            q = (p + 1) % n; 
              
            for (int i = 0; i < n; i++) { 
                // If i is more counterclockwise than  
                // current q, then update q 
                if (orientation(points[p], points[i], points[q]) == 2) {
                    q = i; 
                }
            } 
       
            // Now q is the most counterclockwise with 
            // respect to p. Set p as q for next iteration,  
            // so that q is added to result 'hull' 
            p = q; 
       
        } while (p != l);  // While we don't come to first  
                           // point 
       
        // Print Result 
        /*
        for (Point temp : hull) {
            System.out.println("(" + temp.lon + ", " + temp.lat + ")"); 
        }*/
        return hull;
    } 
}
