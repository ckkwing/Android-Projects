package com.jecfbagsx.android.utils;

import android.graphics.ColorMatrix;

public class CustomColorMatrix extends ColorMatrix {

	// constant for contrast calculations:  
    private static final double[] DELTA_INDEX = new double []{  
        0,    0.01, 0.02, 0.04, 0.05, 0.06, 0.07, 0.08, 0.1, 0.11,  
        0.12, 0.14, 0.15, 0.16, 0.17, 0.18, 0.20, 0.21, 0.22, 0.24,  
        0.25, 0.27, 0.28, 0.30, 0.32, 0.34, 0.36, 0.38, 0.40, 0.42,  
        0.44, 0.46, 0.48, 0.5, 0.53, 0.56, 0.59, 0.62, 0.65, 0.68,   
        0.71, 0.74, 0.77, 0.80, 0.83, 0.86, 0.89, 0.92, 0.95, 0.98,  
        1.0, 1.06, 1.12, 1.18, 1.24, 1.30, 1.36, 1.42, 1.48, 1.54,  
        1.60, 1.66, 1.72, 1.78, 1.84, 1.90, 1.96, 2.0, 2.12, 2.25,   
        2.37, 2.50, 2.62, 2.75, 2.87, 3.0, 3.2, 3.4, 3.6, 3.8,  
        4.0, 4.3, 4.7, 4.9, 5.0, 5.5, 6.0, 6.5, 6.8, 7.0,  
        7.3, 7.5, 7.8, 8.0, 8.4, 8.7, 9.0, 9.4, 9.6, 9.8,   
        10.0  
    }; 
    
	// identity matrix constant:  
    private static final float[] IDENTITY_MATRIX = new float [] {  
        1,0,0,0,0,  
        0,1,0,0,0,  
        0,0,1,0,0,  
        0,0,0,1,0, 
        0,0,0,0,1
    };  
    private static final int LENGTH = IDENTITY_MATRIX.length; 
    private float[] m_matrix = new float[LENGTH];
    public float[] getColorMatrixArray()
    {
    	return m_matrix;
    }
    
	// initialization:  
    public CustomColorMatrix(float[] matrix)  
    {  
    	super(matrix);
        copyMatrix(((matrix.length == LENGTH) ? matrix : IDENTITY_MATRIX));  
    } 
    
 // private methods:  
    // copy the specified matrix's values to this matrix:  
    protected void copyMatrix(float[] matrix)
    {  
        int length = LENGTH;  
        for (int i = 0; i < length; i++)  
        {  
        	m_matrix[i] = matrix[i];  
        }  
    }  
    
 // public methods:  
    public void reset()
    {  
    	for (int i = 0; i < LENGTH; i++) 
        {  
    		m_matrix[i] = IDENTITY_MATRIX[i];  
        }  
    } 
    
    // make sure values are within the specified range, hue has a limit of 180, others are 100:  
    protected int cleanValue(int val, int limit)  
    {  
        return Math.min(limit, Math.max(-limit, val));  
    } 
    
 // multiplies one matrix against another:  
    protected void multiplyMatrix(double[] matrix)  
    {  
        float[] col = new float[LENGTH];

        for (int i = 0; i < 5; i++)  
        {  
            for (int j = 0; j < 5; j++)  
            {  
                col[j] = m_matrix[j + i * 5];  
            }  
            for (int j = 0; j < 5; j++)  
            {  
                float val = 0;
                for (int k = 0; k < 5; k++)  
                {  
                    val += matrix[j + k * 5] * col[k];  
                }  
                m_matrix[j + i * 5] = val;  
            }  
        }  
    } 
    
    public void adjustColor(int brightness, int contrast, int saturation, int hue)  
    {  
//        adjustHue(hue);  
        adjustContrast(contrast);  
        adjustBrightness(brightness);  
        adjustSaturation(saturation);  
    } 
    
    public void adjustContrast(int val)  
    {  
    	val = cleanValue(val, 100);  
        if (val == 0)  
        {  
            return;  
        }  
        double x = 0;  
        if (val < 0)  
        {  
            x = 127.f + (float)val / 100.f * 127.f;
        }  
        else  
        {  
            x = (float)val % 1;  
            if (x == 0)  
            {  
                x = DELTA_INDEX[val];  
            }  
            else  
            {  
                //x = DELTA_INDEX[(p_val<<0)]; // this is how the IDE does it.  
                x = DELTA_INDEX[(val << 0)] * (1 - x) + DELTA_INDEX[(val << 0) + 1] * x; // use linear interpolation for more granularity.  
            }  
            x = x * 127.f + 127.f;  
        }  
        multiplyMatrix(new double[] {
             x/127.f,0,0,0,0.5*(127.f-x),  
             0,x/127.f,0,0,0.5*(127.f-x),  
             0,0,x/127.f,0,0.5*(127.f-x),  
             0,0,0,1,0,
             0,0,0,0,1,}
            );  
    }  
    
    public void adjustBrightness(int val)  
    {  
    	val = cleanValue(val, 100);  
        if (val == 0)  
        {  
            return;  
        }  
         multiplyMatrix(new double[]{  
             1,0,0,0,val,  
             0,1,0,0,val,  
             0,0,1,0,val,  
             0,0,0,1,0,
             0,0,0,0,1,
         });  
    } 
    
    public void adjustSaturation(int val)  
    {  
    	val = cleanValue(val, 100);  
        if (val == 0)  
        {  
            return;  
        }  
//        float x = 1 + ((val > 0) ? 3 * val / 100 : val / 100);
        float x = 1.f + (float)val / 100.f;
        double lumR = 0.3086;  
        double lumG = 0.6094;  
        double lumB = 0.0820;  
        multiplyMatrix(new double[]{  
             lumR*(1-x)+x,lumG*(1-x),lumB*(1-x),0,0,  
             lumR*(1-x),lumG*(1-x)+x,lumB*(1-x),0,0,  
             lumR*(1-x),lumG*(1-x),lumB*(1-x)+x,0,0,  
             0,0,0,1,0,
             0,0,0,0,1,}); 
    }
}
