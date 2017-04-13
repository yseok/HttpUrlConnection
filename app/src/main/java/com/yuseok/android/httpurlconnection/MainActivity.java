package com.yuseok.android.httpurlconnection;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button btnGet;
    EditText editUrl;
    TextView txtResult, txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGet = (Button) findViewById(R.id.btnUrl);
        editUrl = (EditText) findViewById(R.id.editUrl);
        txtResult = (TextView) findViewById(R.id.txtResult);
        txtTitle = (TextView) findViewById(R.id.txtTitle);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = editUrl.getText().toString();
                getUrl(urlString);
            }
        });
    }

    public void getUrl(String urlString) {
        if (!urlString.startsWith("http")) {
            urlString = "http://" + urlString;
        }

        new AsyncTask<String, Void, String>() {

            ProgressDialog dialog = new ProgressDialog(MainActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // 프로그레스바 시작
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("불러오는 중...");

                dialog.show();

            }

            @Override
            protected String doInBackground(String... params) {
                String urlString = params[0];
                try {
                    // 1. String을 url객체로 변환
                    URL url = new URL(urlString);
                    // 2.url로 네트워크 연결시작
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // 3. url연결에 대한 옵션 설정
                    connection.setRequestMethod("GET"); // 가.GET    : 데이터 요청시 사용하는 방식
                    // 나.POST   : 데이터 입력시 사용하는 방식
                    // 다.PUT    : 데이터 수정시
                    // 라.DELETE : 데이터 삭제시
                    // 4. 서버로부터 응답코드 회신
                    int responsCode = connection.getResponseCode();
                    if (responsCode == HttpURLConnection.HTTP_OK) {

                        // 4.1 서버연결로부터 스트림을 얻는다.
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        // 4.2 반복문을 돌면서 버퍼의 데이터를 읽어온다.
                        StringBuilder result = new StringBuilder();
                        String lineOfData = "";
                        while ((lineOfData = br.readLine()) != null) {
                            result.append(lineOfData);
                        }

                        return result.toString();
                    } else {
                        Log.e("HTTP Connection", "Erro Code = " + responsCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                // title 태그값 추출하기

                String title = result.substring(result.indexOf("<title>")+7,result.indexOf("</title>"));
                txtTitle.setText(title);

                // 결과값 메인 UI 세팅
                txtResult.setText(result);
                // 프로그레스바 종료
                dialog.dismiss();
            }
        }.execute(urlString);
    }
}
