package mg.studio.weatherappdesign;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    private static final int NETWORKDISCONNECTED = 1;
    private static final int NETWORKCONNECTED = 2;
    private static final String TAG = "MainActivity";
    private ImageView mImageView;
    public TextView mDate;
    private TextView mWeekday;
    private Boolean isnet;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDate = (TextView) findViewById(R.id.tv_date);
        mWeekday = (TextView) findViewById(R.id.weekday);

        //handler.post(runnable);
    }


    public String getdate() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码


        return mMonth + "/" + mDay + "/" + mYear;
    }

    public String getweekday() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWeekday = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

        if ("1".equals(mWeekday)) {
            mWeekday = "SUNDAY";
        } else if ("2".equals(mWeekday)) {
            mWeekday = "MONDAY";
        } else if ("3".equals(mWeekday)) {
            mWeekday = "TUESDAY";
        } else if ("4".equals(mWeekday)) {
            mWeekday = "WEDNESDAY";
        } else if ("5".equals(mWeekday)) {
            mWeekday = "THURSDAY";
        } else if ("6".equals(mWeekday)) {
            mWeekday = "FRIDAY";
        } else if ("7".equals(mWeekday)) {
            mWeekday = "SATURDAY";
        }

        return mWeekday;
    }

    public void btnClick(View view) {

        mDate.setText(getdate());
        mWeekday.setText(getweekday());

        if (isNetworkConnected(this) == false) {
            Toast.makeText(MainActivity.this, "设备无网络连接", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "设备已连接网络", Toast.LENGTH_SHORT).show();
            new DownloadUpdate().execute();
        }
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private class DownloadUpdate extends AsyncTask<String, Void, String> {

        private android.util.Log log;

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://wthrcdn.etouch.cn/WeatherApi?citykey=101010100";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String buffer) {

            try {
                org.dom4j.Document document = DocumentHelper.parseText(buffer);
                org.dom4j.Element root;
                root = document.getRootElement();

                //List<org.dom4j.Element> list=root.elements();
                //log.i("list",root.getName());

                String temperature = root.element("wendu").getText();
                log.i("332", temperature);
                //Toast.makeText(MainActivity.this, temperature, Toast.LENGTH_SHORT).show();
                //String location=root.element("city").getText();
                //String date=root.element("forecast").element("weather").element("date").getText();
                ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temperature);
                //((TextView) findViewById(R.id.tv_location)).setText(location);

                //String week=date.substring(date.length()-3,date.length());
                //((TextView) findViewById(R.id.week)).setText(week);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

        }
    }
}
