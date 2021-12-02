package dev.gabul.pagseguro_flutter.task;

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.text.TextUtils;


import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagActivationData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventListener;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagInitializationResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult;
import dev.gabul.pagseguro_flutter.PlugPagManager;
import dev.gabul.pagseguro_flutter.PreviousTransactions;
import dev.gabul.pagseguro_flutter.TaskHandler;
import dev.gabul.pagseguro_flutter.helper.Bluetooth;

public class PinpadPaymentTask
        extends AsyncTask<PlugPagPaymentData, String, Boolean>
        implements PlugPagEventListener {

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    private TaskHandler mHandler = null;
    private PlugPagPaymentData mPaymentData = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new terminal payment task.
     *
     * @param handler Handler used to report updates.
     */
    public PinpadPaymentTask(@NonNull TaskHandler handler) {
        if (handler == null) {
            throw new RuntimeException("TaskHandler reference cannot be null");
        }

        this.mHandler = handler;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Task execution
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mHandler.onTaskStart();
    }

    @Override
    protected Boolean doInBackground(PlugPagPaymentData... plugPagPaymentData) {
        PlugPagInitializationResult result = null;
        PlugPag plugpag = null;
        boolean res = false;
        if (plugPagPaymentData != null && plugPagPaymentData.length > 0 && plugPagPaymentData[0] != null) {
            plugpag = PlugPagManager.getInstance().getPlugPag();
          //  plugpag.setEventListener(this);
            this.mPaymentData = plugPagPaymentData[0];

            try {
                // Update the throbber
                this.publishProgress("Entrei na certa");

                // Perform payment
               // plugpag.initBTConnection(new PlugPagDevice(Bluetooth.getPinpad()));
                result = plugpag.initializeAndActivatePinpad(new PlugPagActivationData("403938"));
                res = plugpag.isAuthenticated();
            } catch (Exception e) {
                this.publishProgress(e.getMessage());
            } finally {
                //plugpag.setEventListener(null);
                this.publishProgress(String.valueOf(res));
            }

            this.mPaymentData = null;
        }

        return res;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (values != null && values.length > 0 && values[0] != null) {
            this.mHandler.onProgressPublished(values[0], this.mPaymentData);
        }
    }


//    protected void onPostExecute(boolean plugPagTransactionResult) {
//        super.onPostExecute(plugPagTransactionResult);
//
//        if ((plugPagTransactionResult != false))
//                {
//            PreviousTransactions.push(
//                    new String[]{
//                            String.valueOf(true)
//
//                    });
//        }
//
//        this.mHandler.onTaskFinished(plugPagTransactionResult);
//        this.mHandler = null;
//    }

    // -----------------------------------------------------------------------------------------------------------------
    // PlugPag event handling
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void onEvent(PlugPagEventData plugPagEventData) {
        this.publishProgress(PlugPagEventData.Companion.getDefaultMessage(plugPagEventData.getEventCode()));

       // return PlugPag.RET_OK;
    }

}