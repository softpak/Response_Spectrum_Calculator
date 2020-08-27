/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.Serializable;
import java.util.Arrays;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

/**
 *
 * @author you
 */
public class Kriging implements Serializable{
    
    private double[] value, x_lon, y_lat;
    private String model;
    double sigma2, alpha;
    private boolean is_data_set = false;
    private int station_num = 0;
    GeodeticCalculator geoCalc = new GeodeticCalculator();
    
    public Kriging() {
        this.sigma2 = 0.0;
        this.alpha = 100.0;
    }
    
    public void set_station_num(int num) {
        this.station_num = num;
    }
    
    public void set_sigma2(double num) {
        this.sigma2 = num;
    }

    public void set_alpha(double num) {
        this.alpha = num;
    }
    
    public int get_station_num() {
        return this.station_num;
    }
    
    public double[] get_x_arr() {
        return this.x_lon;
    }
    
    public double[] get_y_arr() {
        return this.y_lat;
    }
    
    public void set_data(double[] value, double[] x_lon, double[] y_lat, String model, double sigma2, double alpha) {
        this.value = value;
        this.x_lon = x_lon;
        this.y_lat = y_lat;
        this.model = model;
        this.sigma2 = sigma2;
        this.alpha = alpha;
        this.is_data_set = true;
    }
    
    public void set_model(String model) {
        this.model = model;
        /*
        try {
            kriging = engine.get("kriging");
            this.variogram = invocable.invokeMethod(kriging, "set_model", model);
        } catch (ScriptException | NoSuchMethodException ex) {
            Logger.getLogger(Kriging.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
    }
    
    public void set_data(double[] value, double[] x_lon, double[] y_lat, String model) {
        this.value = value;
        this.x_lon = x_lon;
        this.y_lat = y_lat;
        this.model = model;
        this.sigma2 = 0.0D;//0.0
        this.alpha = 100D;//100
        this.is_data_set = true;
    }
    
    public void do_train() {
        if (this.is_data_set) {
            train(this.value, this.x_lon, this.y_lat, this.model, this.sigma2, this.alpha);

        } else {
            //set data first
            
            
        }
    }
        
    public void save_jsonfile(String fn) {
        //file name time data type
        
        
        
    }
    
    // Model prediction
    public double predict(double x_lon, double y_lat) {
        double[] k = new double[this.n];
	for(int i=0;i<this.n;i++) {
            /*
            com.grum.geocalc.Point predict_p = com.grum.geocalc.Point.at(Coordinate.fromDegrees(y_lat), Coordinate.fromDegrees(x_lon));
            com.grum.geocalc.Point i_p = com.grum.geocalc.Point.at(Coordinate.fromDegrees(this.y[i]), Coordinate.fromDegrees(this.x[i]));//seismic
            */
            GlobalCoordinates predict_p = new GlobalCoordinates(y_lat, x_lon);
            GlobalCoordinates i_p = new GlobalCoordinates(this.y[i], this.x[i]);
            GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, predict_p, i_p);
            
	    //k[i] = kriging_model(EarthCalc.vincentyDistance(predict_p, i_p),
            k[i] = kriging_model(geoCurve.getEllipsoidalDistance(),
					   nugget, 
					   range, 
					   sill, 
					   A);
        }
	return kriging_matrix_multiply(k, M, 1, n, 1)[0];
    }
    
    // Matrix algebra
    private double[] kriging_matrix_diag(double c, int n) {
        double[] Z = new double[n*n];
        for (int count = 0; count < Z.length; count++) {
            Z[count] = 0;
        }
	for(int count = 0 ; count < n ; count++) 
            Z[count*n+count] = c;
	return Z;
    }
    private double[] kriging_matrix_transpose(double[] X, int n, int m) {
	double[] Z = new double[m*n];
	for(int i=0;i<n;i++) {
	    for(int j=0;j<m;j++) {
		Z[j*n+i] = X[i*m+j];
            }
        }
	return Z;
    }
    private double[] kriging_matrix_scale(double[] X, double c, int n, int m) {
	for(int i=0;i<n;i++) {
	    for(int j=0;j<m;j++) {
		X[i*m+j] *= c;
            }
        }
        return X;
    }
    
    private double[] kriging_matrix_add(double[] X, double[] Y, int n, int m) {
	double[] Z = new double[n*m];
	for(int i=0;i<n;i++) {
	    for(int j=0;j<m;j++) {
		Z[i*m+j] = X[i*m+j] + Y[i*m+j];
            }
        }
	return Z;
    }
    
    // Naive matrix multiplication
    private double[] kriging_matrix_multiply(double[] X, double[] Y, int n, int m, int p) {
	double[] Z = new double[n*p];
	for(int i=0;i<n;i++) {
	    for(int j=0;j<p;j++) {
		Z[i*p+j] = 0;
		for(int k=0;k<m;k++) {
		    Z[i*p+j] += X[i*m+k]*Y[k*p+j];
                }
	    }
	}
	return Z;
    }
    
    // Cholesky decomposition
    private boolean kriging_matrix_chol(double[] X, int n) { 
	//var i, j, k, sum
        double[] p = new double[n];
	for(int i=0;i<n;i++) {
            p[i] = X[i*n+i];
        }
	for(int i=0;i<n;i++) {
	    for(int j=0;j<i;j++) {
		p[i] -= X[i*n+j]*X[i*n+j];
            }
            
	    if(p[i]<=0) {
                return false;
            }
	    p[i] = Math.sqrt(p[i]);
	    for(int j=i+1;j<n;j++) {
		for(int k=0;k<i;k++)
		    X[j*n+i] -= X[j*n+k]*X[i*n+k];
		X[j*n+i] /= p[i];
	    }
	}
	for(int i=0;i<n;i++) {
            X[i*n+i] = p[i];
        }
	return true;
    }
    
    // Inversion of cholesky decomposition
    private double[] kriging_matrix_chol2inv(double[] X, int n) {
	//var i, j, k, sum;
        double sum;
	for(int i=0;i<n;i++) {
	    X[i*n+i] = 1/X[i*n+i];
	    for(int j=i+1;j<n;j++) {
		sum = 0;
		for(int k=i;k<j;k++)
		    sum -= X[j*n+k]*X[k*n+i];
		X[j*n+i] = sum/X[j*n+j];
	    }
	}
	for(int i=0;i<n;i++)
	    for(int j=i+1;j<n;j++)
		X[i*n+j] = 0;
	for(int i=0;i<n;i++) {
	    X[i*n+i] *= X[i*n+i];
	    for(int k=i+1;k<n;k++)
		X[i*n+i] += X[k*n+i]*X[k*n+i];
	    for(int j=i+1;j<n;j++)
		for(int k=j;k<n;k++)
		    X[i*n+j] += X[k*n+i]*X[k*n+j];
	}
	for(int i=0;i<n;i++) {
	    for(int j=0;j<i;j++) {
		X[i*n+j] = X[j*n+i];
            }
        }
        return X;
    }
    // Inversion via gauss-jordan elimination
    private double[] kriging_matrix_solve(double[] X, int n) {
	var m = n;
	var b = new double[n*n];
	var indxc = new int[n];
	var indxr = new int[n];
	var ipiv = new double[n];

        int i, j, k, l, ll;
        int icol = 0;
        int irow = 0;
        double temp, pivinv, dum;
	//var big, , ;

	for(i=0;i<n;i++) 
	    for(j=0;j<n;j++) {
		if(i==j) b[i*n+j] = 1;
		else b[i*n+j] = 0;
	    }
	for(j=0;j<n;j++) ipiv[j] = 0;
	for(i=0;i<n;i++) {
	    double big = 0;
	    for(j=0;j<n;j++) {
		if(ipiv[j]!=1) {
		    for(k=0;k<n;k++) {
			if(ipiv[k]==0) {
			    if(Math.abs(X[j*n+k])>=big) {
				big = Math.abs(X[j*n+k]);
				irow = j;
				icol = k;
			    }
			}
		    }
		}
	    }
	    ++(ipiv[icol]);

	    if(irow!=icol) {
		for(l=0;l<n;l++) {
		    temp = X[irow*n+l];
		    X[irow*n+l] = X[icol*n+l];
		    X[icol*n+l] = temp;
		}
		for(l=0;l<m;l++) {
		    temp = b[irow*n+l];
		    b[irow*n+l] = b[icol*n+l];
		    b[icol*n+l] = temp;
		}
	    }
	    indxr[i] = irow;
	    indxc[i] = icol;

	    //if(X[icol*n+icol]==0) return false; // Singular
            if(X[icol*n+icol]==0) return X;

	    pivinv = 1 / X[icol*n+icol];
	    X[icol*n+icol] = 1;
	    for(l=0;l<n;l++) X[icol*n+l] *= pivinv;
	    for(l=0;l<m;l++) b[icol*n+l] *= pivinv;

	    for(ll=0;ll<n;ll++) {
		if(ll!=icol) {
		    dum = X[ll*n+icol];
		    X[ll*n+icol] = 0;
		    for(l=0;l<n;l++) X[ll*n+l] -= X[icol*n+l]*dum;
		    for(l=0;l<m;l++) b[ll*n+l] -= b[icol*n+l]*dum;
		}
	    }
	}
	for(l=(n-1);l>=0;l--) 
	    if(indxr[l]!=indxc[l]) {
		for(k=0;k<n;k++) {
		    temp = X[k*n+indxr[l]];
		    X[k*n+indxr[l]] = X[k*n+indxc[l]];
		    X[k*n+indxc[l]] = temp;
		}
	    }
	
	//return true;
        return X;
    }
    
    // Variogram models
    private double kriging_variogram_gaussian(double h, double nugget, double range, double sill, double A) {
	return nugget + ((sill-nugget)/range)*(1.0D - Math.exp(-(1.0D/A)*Math.pow(h/range, 2D)) );
    }
    private double kriging_variogram_exponential(double h, double nugget, double range, double sill, double A) {
	return nugget + ((sill-nugget)/range)*(1.0D - Math.exp(-(1.0D/A) * (h/range)) );
    }
    private double kriging_variogram_spherical(double h, double nugget, double range, double sill, double A) {
	if(h>range) return nugget + (sill-nugget)/range;
	return nugget + ((sill-nugget)/range)*
	( 1.5D*(h/range) - 0.5D*Math.pow(h/range, 3D) );
    }
    
    private double kriging_model(double h, double nugget, double range, double sill, double A) {
        switch(model) {
            case "gaussian":
                return kriging_variogram_gaussian(h, nugget, range, sill, A);
                //break;
            case "exponential":
                return kriging_variogram_exponential(h, nugget, range, sill, A);
                //break;
            case "spherical":
                return kriging_variogram_spherical(h, nugget, range, sill, A);
                //break;
            default:
                break;
	}
        return 0;
    }
    
    
    double[] t;
    double[] x;
    double[] y;
    double[] K;
    double[] M;
    double nugget = 0.0;
    double range = 0.0;
    double sill = 0.0;
    double A = 1D/3D;
    int n = 0;
    
    private void train(double[] t, double[] x, double[] y, String model, double sigma2, double alpha) {
        this.model = model;
        this.t = t;
        this.x = x;
        this.y = y;
        /*
	switch(model) {
	case "gaussian":
	    variogram.model = kriging_variogram_gaussian;
	    break;
	case "exponential":
	    variogram.model = kriging_variogram_exponential;
	    break;
	case "spherical":
	    variogram.model = kriging_variogram_spherical;
	    break;
	};*/
	// Lag distance/semivariance
	int i, j, k, l, n = t.length;
	var distance = new double[(n*n-n)/2][];
	for(i=0,k=0;i<n;i++)
	    for(j=0;j<i;j++,k++) {
		distance[k] = new double[2];
                //change to Vincenty fomula
                /*
		distance[k][0] = Math.pow(
		    Math.pow(x[i]-x[j], 2)+
		    Math.pow(y[i]-y[j], 2), 0.5);
                */
                /*
                com.grum.geocalc.Point i_p = com.grum.geocalc.Point.at(Coordinate.fromDegrees(y[i]), Coordinate.fromDegrees(x[i]));//seismic
                com.grum.geocalc.Point j_p = com.grum.geocalc.Point.at(Coordinate.fromDegrees(y[j]), Coordinate.fromDegrees(x[j]));//station
                */
                GlobalCoordinates i_p = new GlobalCoordinates(y[i], x[i]);
                GlobalCoordinates j_p = new GlobalCoordinates(y[j], x[j]);
                GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, i_p, j_p);
                
                //distance[k][0] = EarthCalc.vincentyDistance(i_p, j_p);
                distance[k][0] = geoCurve.getEllipsoidalDistance();
		distance[k][1] = Math.abs(t[i]-t[j]);
	    }
	Arrays.sort(distance, (double[] a, double[] b) -> Double.compare(a[0], b[0]));
        
        //System.out.println(distance);
	range = distance[(n*n-n)/2-1][0];
        
	// Bin lag distance
	var lags = ((n*n-n)/2)>30? 30:(n*n-n)/2;
	var tolerance = range/lags;
	var lag = new double[lags];
        var semi = new double[lags];
        for (int count = 0; count < lags; count++) {
            lag[count] = 0;
            semi[count] = 0;
        }
        
	if(lags<30) {
	    for(l=0;l<lags;l++) {
		lag[l] = distance[l][0];
		semi[l] = distance[l][1];
	    }
	} else {
	    for(i=0,j=0,k=0,l=0;i<lags&&j<((n*n-n)/2);i++,k=0) {
		while( distance[j][0]<=((i+1)*tolerance) ) {
		    lag[l] += distance[j][0];
		    semi[l] += distance[j][1];
		    j++;k++;
		    if(j>=((n*n-n)/2)) break;
		}
		if(k>0) {
		    lag[l] /= k;
		    semi[l] /= k;
		    l++;
		}
	    }
	    if(l<2) {
                //return variogram;
                System.out.println("Error: Not enough points");
                return;
            } // Error: Not enough points
	}

	// Feature transformation
	n = l;
	range = lag[n-1]-lag[0];
	var X = new double[2*n];//[1].rep(2*n);
        for (int count = 0; count < 2*n; count++) {
            X[count] = 1;
        }
	var Y = new double[n];
	var A = this.A;
	for(i=0;i<n;i++) {
	    switch(model) {
	    case "gaussian":
		X[i*2+1] = 1.0-Math.exp(-(1.0/A)*Math.pow(lag[i]/range, 2));
		break;
	    case "exponential":
		X[i*2+1] = 1.0-Math.exp(-(1.0/A)*lag[i]/range);
		break;
	    case "spherical":
		X[i*2+1] = 1.5*(lag[i]/range)-
		    0.5*Math.pow(lag[i]/range, 3);
		break;
	    };
	    Y[i] = semi[i];
	}

	// Least squares
	var Xt = kriging_matrix_transpose(X, n, 2);
	var Z = kriging_matrix_multiply(Xt, X, 2, n, 2);
	Z = kriging_matrix_add(Z, kriging_matrix_diag(1/alpha, 2), 2, 2);
	//var cloneZ = Z.slice(0);
        var cloneZ = Arrays.copyOf(Z, Z.length);
	if(kriging_matrix_chol(Z, 2))
	    Z = kriging_matrix_chol2inv(Z, 2);
	else {
	    kriging_matrix_solve(cloneZ, 2);
	    Z = cloneZ;
	}
	var W = kriging_matrix_multiply(kriging_matrix_multiply(Z, Xt, 2, 2, n), Y, 2, n, 1);

	// Variogram parameters
	nugget = W[0];
	sill = W[1]*range+nugget;
	n = x.length;

	// Gram matrix with prior
	n = x.length;
	var K = new double[n*n];
	for(i=0;i<n;i++) {
	    for(j=0;j<i;j++) {
                //change to Vincenty fomula
                /*
                com.grum.geocalc.Point i_p = com.grum.geocalc.Point.at(Coordinate.fromDegrees(y[i]), Coordinate.fromDegrees(x[i]));//seismic
                com.grum.geocalc.Point j_p = com.grum.geocalc.Point.at(Coordinate.fromDegrees(y[j]), Coordinate.fromDegrees(x[j]));//station*/
                GlobalCoordinates i_p = new GlobalCoordinates(y[i], x[i]);
                GlobalCoordinates j_p = new GlobalCoordinates(y[j], x[j]);
                GeodeticCurve geoCurve = geoCalc.calculateGeodeticCurve(Configs.reference, i_p, j_p);
                
		//K[i*n+j] = kriging_model(EarthCalc.vincentyDistance(i_p,j_p),
                K[i*n+j] = kriging_model(geoCurve.getEllipsoidalDistance(),
                                            nugget, 
                                            range, 
                                            sill, 
                                            A);
		K[j*n+i] = K[i*n+j];
	    }
	    K[i*n+i] = kriging_model(0, nugget, 
				        range, 
				        sill, 
				        A);
	}

	// Inverse penalized Gram matrix projected to target vector
	var C = kriging_matrix_add(K, kriging_matrix_diag(sigma2, n), n, n);
	//var cloneC = C.slice(0);
        var cloneC = Arrays.copyOf(C, C.length);
	if(kriging_matrix_chol(C, n))
	    C = kriging_matrix_chol2inv(C, n);
	else {
	    kriging_matrix_solve(cloneC, n);
	    C = cloneC;
	}

	// Copy unprojected inverted matrix as K
	//var K = C.slice(0);
        this.K = Arrays.copyOf(C, C.length);
	this.M = kriging_matrix_multiply(C, t, n, n, 1);
        this.n = n;
        //document.write(variogram);
        //return variogram;
        /*
        System.out.println("t: "+this.t);
        System.out.println("x: "+this.x);
        System.out.println("y: "+this.y);
        System.out.println("K: "+this.K);
        for (int a = 0; a < this.K.length; a++) {
            System.out.println(this.K[a]);
        }
        
        System.out.println("M: "+this.M);
        for (int a = 0; a < this.M.length; a++) {
            System.out.println(this.M[a]);
        }
        System.out.println("nugget: "+this.nugget);
        System.out.println("range: "+this.range);
        System.out.println("sill: "+this.sill);
        System.out.println("A: "+this.A);
        System.out.println("n: "+this.n);*/
    }
}
