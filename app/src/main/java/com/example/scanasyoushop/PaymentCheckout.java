package com.example.scanasyoushop;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.checkout.android_sdk.PaymentForm;
import com.checkout.android_sdk.Response.CardTokenisationFail;
import com.checkout.android_sdk.Response.CardTokenisationResponse;
import com.checkout.android_sdk.Utils.CardUtils;
import com.checkout.android_sdk.Utils.Environment;

public class PaymentCheckout extends AppCompatActivity {

    private PaymentForm mPaymentForm;
    PaymentForm.PaymentFormCallback mFormListener = new PaymentForm.PaymentFormCallback() {
        @Override
        public void onFormSubmit() {
            // form submit initiated; you can potentially display a loader
        }
        @Override
        public void onTokenGenerated(CardTokenisationResponse response) {
            // your token is here
            mPaymentForm.clearForm(); // this clears the Payment Form
        }
        @Override
        public void onError(CardTokenisationFail response) {
            // token request error
        }
        @Override
        public void onNetworkError(VolleyError error) {
            // network error
        }
        @Override
        public void onBackPressed() {
            // the user decided to leave the payment page
            mPaymentForm.clearForm(); // this clears the Payment Form
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_checkout);

        //https://github.com/checkout/frames-android/blob/master/app/src/main/java/checkout/checkout_android/DemoActivity.java
        mPaymentForm = findViewById(R.id.checkout_card_form);
        mPaymentForm
                .setFormListener(mFormListener)
                .setEnvironment(Environment.SANDBOX)
                .setKey("pk_test_6e40a700-d563-43cd-89d0-f9bb17d35e73")
                .setAcceptedCard(new CardUtils.Cards[]{CardUtils.Cards.VISA, CardUtils.Cards.MASTERCARD});

    }

}
