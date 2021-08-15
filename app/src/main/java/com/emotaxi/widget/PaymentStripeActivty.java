/*
package com.emotaxi.widget;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mysmalljobs.R;
import com.mysmalljobs.util.Const;
import com.mysmalljobs.util.Constant;
import com.mysmalljobs.util.DataManager;
import com.mysmalljobs.util.NetworkAvailablity;
import com.mysmalljobs.util.SharedPreference;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PaymentStripeActivty extends AppCompatActivity {
    CardInputWidget card_input;
    private Stripe stripe;
    private String stripe_token;
    Button btn_pay;
    private String response, amount, project_id;
    TextView txt_admin_charge, tv_min_commission;
    ImageView img_back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_stripe);
        card_input = findViewById(R.id.card_input);
        btn_pay = findViewById(R.id.btn_pay);
        txt_admin_charge = findViewById(R.id.txt_admin_charge);
        tv_min_commission = findViewById(R.id.tv_min_commission);
        img_back_arrow = findViewById(R.id.img_back_arrow);
        amount = getIntent().getStringExtra("amount");
        project_id = getIntent().getStringExtra("project_id");
        stripe = new Stripe(this, "pk_test_51HgpOcL86vNnnoiZrSgDdpLfzK04VMqp01yxWySOQqK8Eq74J618TPtF6nw1t8Rq5JrC7Emd00lPbmdDiM5pIpuI00ldwFp1Kc");
        if (SharedPreference.getSharedPrefData(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this, Constant.country).equalsIgnoreCase("GB")
                || SharedPreference.getSharedPrefData(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this, Constant.country).equalsIgnoreCase("United Kingdom")) {
            Log.e("TAG Amount", amount);
            if (Double.parseDouble(amount) > 0.50) {
                txt_admin_charge.setText("GBP " + amount);
            } else {
                txt_admin_charge.setText("GBP " + "0.50");
            }
        } else {
            if (Double.parseDouble(amount) > 0.50) {
                txt_admin_charge.setText("GHC " + amount);
            } else {
                txt_admin_charge.setText("GHC " + "0.50");
            }
            String message = String.format(getString(R.string.minimum_commission), "GBP",
                    SharedPreference.getSharedPrefData(getApplicationContext(), "admin_chrg") + "%");
            tv_min_commission.setText(message);
        }


        img_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreference.getSharedPrefData(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this, Constant.country).equalsIgnoreCase("GB")
                        || SharedPreference.getSharedPrefData(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this, Constant.country).equalsIgnoreCase("Ghana")) {
                    callGhanaPayment();
                } else {
                    if (card_input.getCard() == null) {
                        Toast.makeText(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this, "Please Enter Card Details", Toast.LENGTH_SHORT).show();
                    } else if (card_input.getCard().getNumber().equalsIgnoreCase("")) {
                        Toast.makeText(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this, "Please Enter Valid Card Number", Toast.LENGTH_SHORT).show();
                    } else {
                        generateToken(card_input.getCard());
                    }
                }

            }
        });
    }

    private void callGhanaPayment() {
        String base64uid = Base64.encodeToString(SharedPreference.getSharedPrefData(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this, Constant.user_id).getBytes(), Base64.DEFAULT);
        String base64plid = Base64.encodeToString(project_id.getBytes(), Base64.DEFAULT);
        String base64amount = Base64.encodeToString(amount.getBytes(), Base64.DEFAULT);
        String paypal_data = "http://www.mysmalljobs.com/smalljobsapp/paystack_pay_commision_to_admin.php?" + "u="
                + base64uid + "&a=" + base64amount + "&p=" + base64plid;
        Log.e("TAG Url", paypal_data);
        startActivity(new Intent(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this, PaySubscriptionActivity.class).putExtra("url", paypal_data));
    }


    public void generateToken(Card card) {
        DataManager.getInstance().showProgressDialog(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this);
        Stripe stripe = new Stripe(this, "pk_test_51HgpOcL86vNnnoiZrSgDdpLfzK04VMqp01yxWySOQqK8Eq74J618TPtF6nw1t8Rq5JrC7Emd00lPbmdDiM5pIpuI00ldwFp1Kc");
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        Log.e("Stripe_token", token.getId());
                        stripe_token = token.getId().toString();
                        Log.e("TAG", stripe_token);
                        if (NetworkAvailablity.checkNetworkStatus(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this)) {
                            JSONObject jsonObjectSignUp = new JSONObject();
                            try {
                                jsonObjectSignUp.put("user_id", SharedPreference.getSharedPrefData(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this, Constant.user_id));
                                jsonObjectSignUp.put("amount", amount);
                                jsonObjectSignUp.put("stripeToken", stripe_token);
                                jsonObjectSignUp.put("project_id", project_id);
                                Log.e("Payment Request", jsonObjectSignUp.toString());
                                PaymentStripeAsynck paymentStripeAsynck = new PaymentStripeAsynck();
                                paymentStripeAsynck.setAllData(jsonObjectSignUp.toString());
                                paymentStripeAsynck.execute();
                            } catch (JSONException e) {
                            }
                        } else {
                            DataManager.getInstance().hideProgress();
                            Toast.makeText(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this, "Please check your network connection", Toast.LENGTH_LONG).show();
                        }
                    }

                    public void onError(Exception error) {
                        DataManager.getInstance().hideProgress();
                        Log.e("Stripe_error", error.getLocalizedMessage());
                        Toast.makeText(com.mysmalljobs.serviceprovider.activity.PaymentStripeActivty.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    private class PaymentStripeAsynck extends AsyncTask<Void, Void, String> {
        private String Data;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        public void setAllData(String Data) {
            this.Data = Data;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return paymentStripe(Data);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.e("Payment Response", response);
            DataManager.getInstance().hideProgress();
            try {
                JSONObject jsonObject1 = new JSONObject(response);
                String statuss = jsonObject1.optString("code");
                if (statuss.equalsIgnoreCase("1")) {
                    alertDialog(jsonObject1.optString("message"));
                } else if (statuss.equalsIgnoreCase("0")) {
                    alertDialog(jsonObject1.optString("message"));
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private void alertDialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        alertDialog.show();
    }


    private String paymentStripe(String json) throws MalformedURLException {
        URL url = new URL(Const.BASE_URL + Const.METHOD_STRIPE_PAY);
        Log.e("History Url", url.toString());
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Const.POST);
            conn.setConnectTimeout(50000);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json);
            wr.flush();
            wr.close();
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
        } catch (IOException e) {
            response = e.toString();
        } catch (Exception e) {

            response = e.toString();
        }
        return response;
    }
}*/
