package com.pyt.postyourfun.Payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.pyt.postyourfun.Image.ImageDownloadManager;
import com.pyt.postyourfun.Image.ImageDownloadMangerInterface;
import com.pyt.postyourfun.constants.Constants;

import org.json.JSONException;

import java.math.BigDecimal;

/**
 * Created by Simon on 7/15/2015.
 */
public class PaymentController {

    private static PayPalConfiguration config =
            new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(Constants.PAYPAL_CLIENT_ID);

    private String imageUrl = "", thumbUrl = "";
    private ImageDownloadMangerInterface callback = null;

    private static PaymentController sharedInstance = null;

    public static PaymentController sharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new PaymentController();
        }
        return sharedInstance;
    }

    public void startPaypalService(Activity activity) {
        Intent intent = new Intent(activity, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        activity.startService(intent);
    }

    public void stopPaypalService(Activity activity) {
        activity.stopService(new Intent(activity, PayPalService.class));
    }

    public void buyImage(Activity activity, float imagePrice, String currencyUnit, String imageTitle) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(imagePrice), currencyUnit, imageTitle, PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(activity, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        activity.startActivityForResult(intent, 0);
    }

    public void buyImage(Fragment fragment, float imagePrice, String currencyUnit, String imageUrl, String thumbUrl, ImageDownloadMangerInterface callback) {
        this.imageUrl = imageUrl;
        this.thumbUrl = thumbUrl;
        this.callback = callback;
        String imageTitle = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        PayPalPayment payment = new PayPalPayment(new BigDecimal(imagePrice), currencyUnit, imageTitle, PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(fragment.getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        fragment.startActivityForResult(intent, 0);
    }

    public void buyImage(Activity fragment, float imagePrice, String currencyUnit, String imageUrl, String thumbUrl, ImageDownloadMangerInterface callback) {
        this.imageUrl = imageUrl;
        this.thumbUrl = thumbUrl;
        this.callback = callback;
        String imageTitle = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        PayPalPayment payment = new PayPalPayment(new BigDecimal(imagePrice), currencyUnit, imageTitle, PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(fragment, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        fragment.startActivityForResult(intent, 0);
    }

    public boolean activityResult(int requestCode, int resultCode, Intent data, Context context) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.d("Buying Image: ", confirm.toJSONObject().toString(4));
//                     TODO: send 'confirm' to your server for verification.
//                    see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
//                    for more details.
                    ImageDownloadManager.getSharedInstance().downloadImage(imageUrl, thumbUrl, context, callback);
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("Buying Image: ", "User canceled buying image.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.d("Buying Image: ", "Invalid payment or payment configuration was submitted.");
        }
        return false;
    }
}
