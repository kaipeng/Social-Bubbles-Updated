package com.social.bubbles;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import android.util.Log;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    public static int dist(double lat1, double lon1, double lat2, double lon2){
    	//
    	return 1;
    }
	public static Date timeToDate(String time){
		if(time.contains("T")){
			int a,b,c,d,e,f, g;
			a=Integer.valueOf(time.substring(0, 4));
			if(time.substring(5, 6).equalsIgnoreCase("0"))
				b=Integer.valueOf(time.substring(6, 7));
			else b=Integer.valueOf(time.substring(5, 7));
			
			if(time.substring(8, 9).equalsIgnoreCase("0"))
				c=Integer.valueOf(time.substring(9, 10));
			else c=Integer.valueOf(time.substring(8, 10));
			
			if(time.substring(11, 12).equalsIgnoreCase("0"))
				d=Integer.valueOf(time.substring(12, 13));
			else d=Integer.valueOf(time.substring(11, 13));
			
			if(time.substring(14, 15).equalsIgnoreCase("0"))
				e=Integer.valueOf(time.substring(15, 16));
			else e=Integer.valueOf(time.substring(14, 16));
			
			if(time.substring(17, 18).equalsIgnoreCase("0"))
				f=Integer.valueOf(time.substring(18, 19));
			else f=Integer.valueOf(time.substring(17, 19));
			
			if(time.substring(17, 18).equalsIgnoreCase("0"))
				f=Integer.valueOf(time.substring(18, 19));
			else f=Integer.valueOf(time.substring(17, 19));
			
			int tzo = (new Date().getTimezoneOffset())/60;
			
			Log.d("TIME CONVERSION from " +time, " to " +a+"y "+b+"m " + c+"d " + (d-tzo) +":" + e+ ":" + f);
			Log.d("Date is: ", new Date(a-1900,b-1,c,d-tzo,e,f).toString());

			return new Date(a-1900,b-1,c,d-tzo,e,f);
		}
		else return new Date();
	}
    
}