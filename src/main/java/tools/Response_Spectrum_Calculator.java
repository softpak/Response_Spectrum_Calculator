/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.Arrays;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author you
 */
public class Response_Spectrum_Calculator {
    
    
    
    public Response_Spectrum_Calculator() {
        
    }
    double[] hertz;
    double[] T;
    double[] Spv;
    double sample_rate;
    //Time Interval
    public double[] get_Acc_Response_Spectrum(double[] input, double sample_rate, double damping_ratio, double g, double end_period, double recordlength) {
        /*
        hertz = new double[input.length];
        for (int c = 0;  c < input.length; c++) {
            hertz[c] = (double)c * (1D/recordlength);
            //System.out.println(hertz[c]);
        }*/
        this.sample_rate = sample_rate;
        double m = 1D;    // 質量 ton
        double k = 0;
        double K = 0;    // 剛性 kN/m
        //System.out.println("no detrend: "+Arrays.toString(input));
        //input = detrend_least_square(hertz, input);
        //input = detrend_del_mean(input);
        //System.out.println("detrend: "+Arrays.toString(input));
        //double g = 9806.65D;
        int N = (int)Math.round(end_period/sample_rate);
        //System.out.println("N: "+N);
        //double T[] = new double[N];
        T = new double[N];
        double[] omega = new double[N];
        double[] Spa = new double[N];
        Spv = new double[N];
        double[] Sd = new double[N];
        T[0] = 0;
        double[] u = new double[input.length];
        double[] v = new double[input.length];
        double[] ac = new double[input.length];
        Arrays.fill(u, 0);
        Arrays.fill(v, 0);
        Arrays.fill(ac, 0);
        double gamma = 0.5D;//gamma
        double beta = 0.25D;//beta
        for (int j = 0; j < N; j++) {
            if (j < N-1) {
                T[j+1] = T[j] + sample_rate;
            }
            omega[j]=2*Math.PI/T[j];     //Natural Frequency
            k = Math.pow(omega[j], 2)*m;
            double c=2D*m*omega[j]*damping_ratio/100D;
            //K = k+3D*c/sample_rate+6D*m/Math.pow(sample_rate, 2);//kf
            K = k+gamma*c/(beta*sample_rate)+m/(beta*Math.pow(sample_rate, 2));//kf
            //kf = k + y*c/(b*dt) + m/(b*dt^2);
            
            //double a=6D*m/sample_rate+3D*c;
            double a=m/(beta*sample_rate)+gamma*c/beta;
            //a = m/(b*dt) + y*c/b;
            
            //double b=3D*m+sample_rate*c/2D;//b2
            double b = m/(2*beta)+sample_rate*(gamma/(2*beta) - 1)*c;
            //b2 = m/(2*b) + dt*(y/(2*b) - 1)*c;
            
            for (int i = 0 ; i < input.length-1; i++) {
                u[0]=0;
                v[0]=0;
                ac[0]=0;    
                //double df=-(input[i+1]-input[i])+a*v[i]+b*ac[i];
                double df= (input[i+1]-input[i])+a*v[i]+b*ac[i];
                double du=df/K;
                //double dv=3D*du/sample_rate-3D*v[i]-sample_rate*ac[i]/2D;
                double dv=gamma/(beta*sample_rate)*du-(gamma/beta)*v[i]+sample_rate*(1D - gamma/(2D*beta))*ac[i];
                //dv = y/(b*dt)*du - (y/b)*v(i) + dt*(1 - y/(2*b))*ac(i);
                
                //double dac=6D*(du-sample_rate*v[i])/Math.pow(sample_rate, 2)-3D*ac[i];
                double dac=du/(beta*Math.pow(sample_rate, 2))- v[i]/(beta*sample_rate) - ac[i]/(2D*beta);
                //da = du/(b*dt^2) - v(i)/(b*dt) - ac(i)/(2*b);
                
                u[i+1]=u[i]+du;
                v[i+1]=v[i]+dv;
                ac[i+1]=ac[i]+dac;
            }
            
            for (int uc = 0 ; uc < u.length; uc ++) {
                u[uc] = Math.abs(u[uc]);
            }
            Arrays.sort(u);
            Sd[j] = u[u.length-1];//max
            Spv[j] = Sd[j]*omega[j];
            Spa[j] = Sd[j]*Math.pow(omega[j], 2)/g;
        }
        
        for (int ic = 0 ; ic < input.length; ic ++) {
            input[ic] = Math.abs(input[ic]);
        }
        
        Arrays.sort(input);
        double input_max = input[input.length-1]/g;//max
        Spv[0] = 0;
        Spv[1] = 0;
        Spa[0] = input_max;
        Spa[1] = input_max;
        //smooth
        //5 times
        for (int st = 0;  st < 5; st++) {
            for (int c = 1;  c < Spa.length-1; c++) {
                Spa[c] = 0.25D*Spa[c-1] + 0.5D*Spa[c] + 0.25D*Spa[c+1];
                Spv[c] = 0.25D*Spv[c-1] + 0.5D*Spv[c] + 0.25D*Spv[c+1];
            }
        }
        return Spa;
    }
    
    public double[] get_Velocity_Response_Spectrum() {
        return Spv;
    }
            
    /*
    def __newmark_beta_integration(self):
    """
    Newmark-β法により1自由度系の地震応答解析を行う.
    Notes
    -----
    * 非増分系で実装している.
    References
    ----------
    .. [1] 柴田明徳: 最新耐震構造解析(第3版), 森北出版, pp.97-108, 2014. 
       [2] 久田俊明, 野口裕久: 非線形有限要素法の基礎と応用, 丸善, pp.261-265, 1996.
    """
    k_m     = self.__k / self.__m
    omega   = math.sqrt(k_m)
    h       = self.__h
    c_m     = 2.0 * h * omega
    dt      = self.__dt
    beta    = self.__param["beta"]
    gamma   = self.__param["gamma"]

    # 相対速度
    acc_rel = np.zeros_like(self.__acc_grd, dtype=np.float64)

    # 初期値
    self.disp[0]    = 0.0
    self.vel[0]     = - self.__acc_grd[0] * dt
    self.acc_abs[0] = - 2.0 * h * omega * self.__acc_grd[0] * dt
    acc_rel[0]      = self.acc_abs[0] - self.__acc_grd[0]

    # 時間積分
    for i in range(0, self.__acc_grd.size - 1):
      acc_rel[i+1]      = - ( self.__acc_grd[i+1] + c_m*( self.vel[i] + dt*(1.0-gamma)*acc_rel[i] )                       \
                                                  + k_m*( self.disp[i] + dt*self.vel[i] + dt*dt*(0.5-beta)*acc_rel[i] ) ) \
                          / ( 1.0 + c_m*dt*gamma + k_m*dt*dt*beta )
      self.acc_abs[i+1] = acc_rel[i+1] + self.__acc_grd[i+1]
      self.disp[i+1]    = self.disp[i] + dt*self.vel[i] + dt*dt*( (0.5-beta)*acc_rel[i] + beta*acc_rel[i+1] )
      self.vel[i+1]     = self.vel[i] + dt*( (1.0-gamma)*acc_rel[i] + gamma*acc_rel[i+1] )
    */
    private double[] detrend_del_mean(double[] x) {
        double avg = Arrays.stream(x).average().orElse(Double.NaN);
        for (int i = 0; i < x.length; i++) {
            x[i] = x[i] - avg;
        }
        return x;
    }
    
    public double[] detrend_least_square(double[] x, double[] y) {
        if (x.length != y.length) {
            System.out.println(x.length +","+ y.length);
            throw new IllegalArgumentException("The x and y data elements needs to be of the same length");
        }
        SimpleRegression regression = new SimpleRegression();
        
        for (int i = 0; i < x.length; i++) {
            regression.addData(x[i], y[i]);
        }
        
        double slope = regression.getSlope();
        double intercept = regression.getIntercept();
        
        for (int i = 0; i < x.length; i++) {
            //y -= intercept + slope * x 
            y[i] -= intercept + (x[i] * slope);
        }
        return y;
    }
    
    public double[] getT() {
        return this.T;
    }
            
    public double[] cumtrapz(double y[]) {
        int n = y.length;
        double[] cumtrap = new double[n];

        cumtrap[0] = 0.0D;
        for (int i = 1; i < n - 1; i++) {
            cumtrap[i] = cumtrap[i - 1] + 0.5D * (y[i] + y[i - 1]);
        }
        cumtrap[n - 1] = cumtrap[n - 2] + 0.5D * (y[n - 1] + y[n - 2]);
        return cumtrap;
    }
    
    public double getPGV(double[] input) {
        double[] in = cumtrapz(input);
        for (int i = 0; i < in.length; i++) {
            in[i] = Math.abs(in[i]*this.sample_rate*this.sample_rate);
        }
        //find max
        double pgv_value = 0;
        int pgv_pos = 0;
        for (int i = 0 ; i < in.length ; i++) {
            if (in[i] > pgv_value) {
                pgv_value = in[i];
                pgv_pos = i;
            }
        }
        return pgv_value;
    }

}

