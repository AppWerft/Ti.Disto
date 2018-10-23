package leica.ch.quickstartapp;

import android.app.Activity;
import android.widget.TextView;

import ch.leica.sdk.Defines;

public class UIHelper {
    public UIHelper(){

    }
    void setLog(Activity activity, final TextView textView, final String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Get the SDK version

                textView.setText(textView.getText() + "\n"+"* "+message);
            }
        });
    }

    void setMeasurements(Activity activity, final TextView textView, final String id, final String message) {
        if (message != null
                && message.isEmpty() == false
                && message.equals(Defines.defaultStringValue) == false
                && message.equals(String.valueOf(Defines.defaultDoubleValue)) == false
                && message.equals(String.valueOf(Defines.defaultFloatValue)) == false
                && message.equals(String.valueOf(Defines.defaultIntValue)) == false
                && message.equals(String.valueOf(Defines.defaultShortValue)) == false) {


            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Get the SDK version

                    textView.setText(textView.getText() + "\n" + "* "+id+": "+ message);
                }
            });
        }
    }

    void setMeasurements(Activity activity, final TextView textView, final String id, final float message) {
        try{
            setMeasurements(activity, textView, id, String.valueOf(message));
        }catch(Exception e){

        }
    }
    void setMeasurements(Activity activity, final TextView textView, final String id, final double message) {
        try{
            setMeasurements(activity, textView, id, String.valueOf(message));
        }catch(Exception e){

        }
    }
    void setMeasurements(Activity activity, final TextView textView, final String id, final short message) {
        try{
            setMeasurements(activity, textView, id, String.valueOf(message));
        }catch(Exception e){

        }
    }


    void setTitle(Activity activity, final TextView textView, final String message){
        if(message != null && message.isEmpty() == false) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Get the SDK version

                    textView.setText(message);
                }
            });
        }
    }
}
