//package com.varun.seatlayout;
//
//import android.annotation.TargetApi;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.telephony.SmsMessage;
//import android.util.Log;
//import android.widget.Toast;
//
//import java.util.Random;
//
//public class MySmsReceiver extends BroadcastReceiver {
//    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
//    private static final String TAG = "SmsBroadcastReceiver";
//    static String msg, phoneNo = "";
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        //retrieves the general action to be performed and display on log
//        Random rand = new Random();
//        DbHandler dbHandler = new DbHandler(context);
//        int min = 1;
//        int max = 211;
//        Log.i(TAG, "Intent Received: " +intent.getAction());
//        if (intent.getAction()==SMS_RECEIVED)
//        {
//            //retrieves a map of extended data from the intent
//            Bundle dataBundle = intent.getExtras();
//            if (dataBundle!=null)
//            {
//                //creating PDU(Protocol Data Unit) object which is a protocol for transferring message
//                Object[] mypdu = (Object[])dataBundle.get("pdus");
//                final SmsMessage[] message = new SmsMessage[mypdu.length];
//
//                for (int i = 0; i<mypdu.length; i++)
//                {
//                    //for build versions >= API Level 23
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                    {
//                        String format = dataBundle.getString("format");
//                        //From PDU we get all object and SmsMessage Object using following line of code
//                        message[i] = SmsMessage.createFromPdu((byte[])mypdu[i], format);
//                    }
//                    else
//                    {
//                        //<API level 23
//                        message[i] = SmsMessage.createFromPdu((byte[])mypdu[i]);
//                    }
//                    msg = message[i].getMessageBody();
//                    phoneNo = message[i].getOriginatingAddress();
//                }
//                Toast.makeText(context, "Message: " +msg +"\nNumber: " +phoneNo, Toast.LENGTH_LONG).show();
//                String seatIds = dbHandler.seatIdFromPN(phoneNo);
//                Toast.makeText(context, seatIds, Toast.LENGTH_LONG).show();
//                String[] splitSeatIds = seatIds.split("\n");
//                Toast.makeText(context, splitSeatIds[0], Toast.LENGTH_LONG).show();
////                if (msg.equals("לא") || msg.equals("ל")  || msg.equals("no") || msg.equals("n")|| msg.equals("No") || msg.equals("N")){
//////                    String msgToSend = "זוהי הודעה מגבאי בית הכנסת : במסגרת שיפור השירות ולרווחת המתפללים ואורחיהם נרצה לדעת מהם המקומות פנויים שיהיו בשבת הקרובה בבית הכנסת, נשמח אם תוכל להחזיר הודעה עם מספר המושבים הפנויים שמוקצים לך ולמשפחתך בשבת הקרובה (מקסימום: " + splitSeatIds.length + ")";
////                    MainActivity.smsSendMessage(phoneNo, "number of chairs (max: " + splitSeatIds.length + ")"); // Write the max he has!
////                } // now i need to do here that it will check the phone nnumber in the DB and change the correct chairs
////                if (msg.equals("0") || msg.equals("1")|| msg.equals("2")|| msg.equals("3")|| msg.equals("4")|| msg.equals("5")|| msg.equals("6")|| msg.equals("7")|| msg.equals("8")|| msg.equals("9")|| msg.equals("10")){
////                    for (int i=0;i<Integer.parseInt(msg);i++){
////                        MainActivity.setMissing(Integer.parseInt(splitSeatIds[i]));
////                    }
////                }
//                int givenNum = Integer.parseInt(msg);
//                if (givenNum>=0 && givenNum<=splitSeatIds.length){
//                    for (int i=0;i<givenNum;i++){
////                        MainActivity.setMissing(Integer.parseInt(splitSeatIds[i]));
//                        dbHandler.UpdateUserStatus("green", Integer.parseInt(splitSeatIds[i]));
//                    }
//                }
//            }
//        }
//    }
//}
//
