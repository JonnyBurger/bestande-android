package jonnyburger.bestande;

import android.os.Bundle;
import android.os.ResultReceiver;

import android.os.Handler;

public class FetchGradeReceiver extends ResultReceiver {
    private Receiver receiver;

    public FetchGradeReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
       receiver.onReceiveResult(resultCode, resultData);
    }
}
