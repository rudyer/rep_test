package dev.gabul.pagseguro_flutter.task;



import android.os.AsyncTask;
import androidx.annotation.NonNull;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagVoidData;
import dev.gabul.pagseguro_flutter.PlugPagManager;
import dev.gabul.pagseguro_flutter.TaskHandler;
import dev.gabul.pagseguro_flutter.helper.Bluetooth;

public class TerminalVoidPaymentTask extends AsyncTask<Void, String, PlugPagTransactionResult> {

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    private TaskHandler mHandler = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new void payment task.
     *
     * @param handler Handler used to report updates.
     */
    public TerminalVoidPaymentTask(@NonNull TaskHandler handler) {
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
    protected PlugPagTransactionResult doInBackground(Void... args) {
        PlugPagTransactionResult result = null;
        PlugPag plugpag = null;

        try {
            // Just update the Throbber
            this.publishProgress("");

            plugpag = PlugPagManager.getInstance().getPlugPag();
          //  plugpag.initBTConnection(new PlugPagDevice(Bluetooth.getTerminal()));
            result = plugpag.voidPayment(new PlugPagVoidData(".", "."));
        } catch (Exception e) {
            this.publishProgress(e.getMessage());
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (values != null && values.length > 0 && values[0] != null) {
            this.mHandler.onProgressPublished(
                    values[0],
                    new PlugPagVoidData(".", "."));
        }
    }

    @Override
    protected void onPostExecute(PlugPagTransactionResult plugPagTransactionResult) {
        super.onPostExecute(plugPagTransactionResult);
        this.mHandler.onTaskFinished(plugPagTransactionResult);
        this.mHandler = null;
    }
}