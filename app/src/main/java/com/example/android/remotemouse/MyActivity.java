package com.example.android.remotemouse;

import android.app.Activity;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class MyActivity extends Activity {


    public enum MouseEvents {
        Single_left_click , Double_left_click , Right_click
        , Two_finger_touch , Three_finger_touch
    }
    boolean one = false , two = false , move = false , three = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


//        View v = findViewById(R.id.mainView);
    }

    public void onTwoFingersTap(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

         SparseArray<PointF> mActivePointers = new SparseArray<PointF>();
        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {

            case MotionEvent.ACTION_DOWN:
                if(one == false){
                    one = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {


                if(two == false){
                    two = true;
                }
                else {
                    three = true;
                }

                break;
            }
            case MotionEvent.ACTION_MOVE: { // a pointer was moved
                move = true;
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                if(three == true){
                    if(move){
                        Toast.makeText(this ,"3 fingers drag", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(this ,"3 fingers", Toast.LENGTH_SHORT).show();
                        sendMouseEvent(MouseEvents.Three_finger_touch);
                    }
                }
                else if(two == true){
                    if(move){
                        Toast.makeText(this ,"2 fingers drag", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(this ,"2 fingers", Toast.LENGTH_SHORT).show();
                        sendMouseEvent(MouseEvents.Right_click);
                    }
                }
                else{
                    if(move){
                        Toast.makeText(this ,"1 fingers drag", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(this ,"1 fingers", Toast.LENGTH_SHORT).show();
                        sendMouseEvent(MouseEvents.Single_left_click);
                    }
                }
                one = false;
                two = false;
                move = false;
                three = false;

                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
            {

                break;
            }
            case MotionEvent.ACTION_CANCEL: {

                break;
            }
        }


        return true;
    }

/*
This is a wrapper to either call SendMouseClickEvent or SendMouseDragEvent
 */
    void sendMouseEvent(MouseEvents mE){

            new Hi().execute(mE);
    }

    public void sendClickEvent(MouseEvents mE){
        try {
            Socket sock = new Socket("10.252.244.203"  ,8080);
            DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            outToServer.writeBytes(mE.toString() + '\n');

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class Hi  extends AsyncTask<MouseEvents, Void, Void> {


        @Override
        protected Void doInBackground(MouseEvents... mE) {
            sendClickEvent(mE[0]);

            return null;
        }
    }
}
