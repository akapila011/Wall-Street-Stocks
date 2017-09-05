package com.wallstreetstocks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by Abhz on 7/14/2017.
 */

public class LookupFragment extends Fragment {
    private static final String TAG = "LookupFragment";

    private Button searchBtn;
    private EditText tickerText;
    public ProgressDialog pDialog;

    private TextView timeTextView, tickerTextView, openTextView, closeTextView, highTextView, lowTextView, volumeTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_lookup, container, false);

        timeTextView = (TextView) view.findViewById(R.id.timeTextView);
        tickerTextView = (TextView) view.findViewById(R.id.tickerTextView);
        openTextView = (TextView) view.findViewById(R.id.openPriceTextView);
        closeTextView = (TextView) view.findViewById(R.id.closePriceTextView);
        highTextView = (TextView) view.findViewById(R.id.dailyHighTextView);
        lowTextView = (TextView) view.findViewById(R.id.dailyLowTextView);
        volumeTextView = (TextView) view.findViewById(R.id.volumeTextView);

        searchBtn = (Button) view.findViewById(R.id.searchButton);
        tickerText = (EditText) view.findViewById(R.id.tickerEditText);
        searchBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String tickerSymbol = tickerText.getText().toString().replace(" ", "");
                if (tickerSymbol.length() == 0) {    // Invalid ticker
                    Toast.makeText(getActivity(), "You must enter a ticker symbol before searching", Toast.LENGTH_SHORT).show();
                }
                else {
                    GetTickerApi gta = new GetTickerApi(tickerSymbol.toUpperCase());
                    gta.execute();
                }
            }
        });

        return view;
    }   // END onCreateView()

    public void setTickerInfo(String time, String ticker, String open, String close, String high, String low, String volume) {
        // Used when we are able to retrieve API data
        timeTextView.setText("Time (EST) : " + time);
        tickerTextView.setText("Ticker : " + ticker);
        openTextView.setText("Open : " + open + " $");
        closeTextView.setText("Close/Current : " + close + " $");
        highTextView.setText("Daily High : " + high + " $");
        lowTextView.setText("Daily Low : " + low + " $");
        volumeTextView.setText("Volume : " + volume + " shares");
    } // END setTickerInfo()

    public void setTickerInfo() {   // when no data available, reset UI
        timeTextView.setText("Time (EST) : ");
        tickerTextView.setText("Ticker : ");
        openTextView.setText("Open : ");
        closeTextView.setText("Close/Current : ");
        highTextView.setText("Daily High : ");
        lowTextView.setText("Daily Low : ");
        volumeTextView.setText("Volume : ");
    }   //// END setTickerInfo()

    // NESTED CLASSES
    public class GetTickerApi extends AsyncTask<Void, Void, String> {

        private String baseUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=5min&apikey=" + Config.API_KEY;
        String url = "";
        private String ticker;
        String time = "";
        String open = "";
        String close = "";
        String high = "";
        String low = "";
        String volume = "";
        String all = "";

        public GetTickerApi(String tSymbol) {
            ticker = tSymbol;
            url = String.format(baseUrl, ticker);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Retrieving " + ticker + " Quotes");
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();

        }   // END onPreExecute()

        protected String doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    all = jsonStr;
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject metadataObj = jsonObj.getJSONObject("Meta Data");
                    String refreshedTime = metadataObj.getString("3. Last Refreshed");
                    time = refreshedTime;
                    JSONObject timeSeries = jsonObj.getJSONObject("Time Series (5min)");
                    JSONObject latestQuote = timeSeries.getJSONObject(refreshedTime);
                    open = latestQuote.getString("1. open");
                    close = latestQuote.getString("4. close");
                    high = latestQuote.getString("2. high");
                    low = latestQuote.getString("3. low");
                    volume = latestQuote.getString("5. volume");
                    return "New data received";
                }
                catch (Exception ex){
                    return "There was a problem parsing the data";
                }
            }
            else {
                return "Could not retrieve data";
            }
        }   // END doInBackground()

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }


            if (result.equals("New data received")){
                setTickerInfo(time, ticker, open, close, high, low, volume);
            }
            else if (result.equals("Could not retrieve data")) {
                setTickerInfo();
                Toast.makeText(getActivity(), "No Internet: Could not retrieve data", Toast.LENGTH_SHORT).show();
            }
            else if (result.equals("There was a problem parsing the data")) {
                setTickerInfo();
                Toast.makeText(getActivity(), "Problem with received data. Ensure ticker is valid.", Toast.LENGTH_SHORT).show();
            }
            else {
                setTickerInfo();
                Toast.makeText(getActivity(), "The data source may be down at the moment", Toast.LENGTH_SHORT).show();
            }
        }   // END onPostExecute()

    }   // END GetSectorApi{}
}
