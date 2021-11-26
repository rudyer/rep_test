package dev.gabul.pagseguro_flutter.task;


import android.os.AsyncTask;
import androidx.annotation.NonNull;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventListener;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagVoidData;
import dev.gabul.pagseguro_flutter.PlugPagManager;
import dev.gabul.pagseguro_flutter.TaskHandler;
import dev.gabul.pagseguro_flutter.helper.Bluetooth;

public class PinpadVoidPaymentTask
        extends AsyncTask<PlugPagVoidData, String, PlugPagTransactionResult>
        implements PlugPagEventListener {

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    private TaskHandler mHandler = null;
    private PlugPagVoidData mVoidPaymentData = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new void payment task.
     *
     * @param handler Handler used to report updates.
     */
    public PinpadVoidPaymentTask(@NonNull TaskHandler handler) {
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
    protected PlugPagTransactionResult doInBackground(PlugPagVoidData... voidData) {
        PlugPagTransactionResult result = null;
        PlugPag plugpag = null;

        if (voidData != null && voidData.length > 0 && voidData[0] != null) {
            this.mVoidPaymentData = voidData[0];
            plugpag = PlugPagManager.getInstance().getPlugPag();
            plugpag.setEventListener(this);

            try {
                // Update the throbber
                this.publishProgress("");

                // Perform void payment
                //plugpag.initBTConnection(new PlugPagDevice(Bluetooth.getPinpad()));
                result = plugpag.voidPayment(voidData[0]);
            } catch (Exception e) {
                this.publishProgress(e.getMessage());
            } finally {
                plugpag.setEventListener(null);
            }

            this.mVoidPaymentData = null;
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (values != null && values.length > 0 && values[0] != null) {
            this.mHandler.onProgressPublished(values[0], this.mVoidPaymentData);
        }
    }

    @Override
    protected void onPostExecute(PlugPagTransactionResult plugPagTransactionResult) {
        super.onPostExecute(plugPagTransactionResult);
        this.mHandler.onTaskFinished(plugPagTransactionResult);
        this.mHandler = null;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // PlugPag event handling
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void onEvent(PlugPagEventData plugPagEventData) {
        this.publishProgress(PlugPagEventData.Companion.getDefaultMessage(plugPagEventData.getEventCode()));

       // return PlugPag.RET_OK;
    }

}