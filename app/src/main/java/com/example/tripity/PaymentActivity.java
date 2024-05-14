package com.example.tripity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentActivity extends AppCompatActivity {

    public static final String GOOGLE_PAY_PACKAGE_NAME= "com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_NAME = 123;
    Button payment;
    TextView amount;
    String  name,upiId, status, transactionNote;
    Uri uri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        payment = findViewById(R.id.payment);
        amount = findViewById(R.id.totalAmount);

        int total_amount = getIntent().getIntExtra("amount",0);

        name = "Deepak kumar";
        upiId = "dv1707071@oksbi";
        transactionNote ="tripity payment";

        amount.setText("₹ "+total_amount);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(total_amount>0){
                    uri = getUpiPaymentUri(name,upiId,transactionNote,Integer.toString(total_amount));
                    payWithGPay(total_amount);
                }
            }
        });


    }

    private void payWithGPay(int total) {
        if(total > 0) {
            if(isAppInstalled(this, GOOGLE_PAY_PACKAGE_NAME)) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
                startActivityForResult(intent, GOOGLE_PAY_REQUEST_NAME);
            } else {
                // Google Pay is not installed, show a dialog to prompt the user to install it
                showInstallGooglePayDialog();
            }
        } else {
            Toast.makeText(this, "Not a valid amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void showInstallGooglePayDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Google Pay is not installed. Do you want to install it?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Redirect user to Google Play Store to install Google Pay
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + GOOGLE_PAY_PACKAGE_NAME));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }


    public void onActivityResult(int reqCode,int resCode,Intent data){
        super.onActivityResult(reqCode,resCode,data);
        if(data!=null){
            status = data.getStringExtra("Status").toLowerCase();
        }
        if(RESULT_OK==reqCode && status.equals("success")){
            Toast.makeText(this, "Payment complete", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 1);
            Log.d("PaymentActivity", "Google Pay is installed.");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("PaymentActivity", "Google Pay is not installed.");
            return false;
        }
    }

//    private boolean isAppInstalled(Context context, String packageName) {
//        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
//        return intent != null;
//    }



    private static Uri getUpiPaymentUri(String name,String upiId, String transactionNote, String amount) {
        return new Uri.Builder().scheme("upi").authority("pay")
                .appendQueryParameter("pa",upiId)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("tn",transactionNote)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("ct","INR").build();

    }
}