package com.example.networkanalyzer;
import android.content.Context;
import android.widget.TextView;
public class PingApplication {

    TextView pingTextView;

    private final Context context;

    public PingApplication(Context context, TextView pv){
        this.context = context;
        this.pingTextView = pv;
    }

}
