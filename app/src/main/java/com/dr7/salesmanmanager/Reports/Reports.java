package com.dr7.salesmanmanager.Reports;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

import com.dr7.salesmanmanager.R;


public class Reports extends AppCompatActivity {


    Button customer_log_report;
    Button transactions_report;
    Button return_report;
    Button stock_request_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        customer_log_report = (Button) findViewById(R.id.customer_log_report);
        transactions_report = (Button) findViewById(R.id.transactions_report);
        return_report = (Button) findViewById(R.id.return_report);
        stock_request_report = (Button) findViewById(R.id.stock_request_report);

        customer_log_report.setOnClickListener(onClickListener);
        transactions_report.setOnClickListener(onClickListener);
        return_report.setOnClickListener(onClickListener);
        stock_request_report.setOnClickListener(onClickListener);

    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.customer_log_report:
                    Intent intent1 = new Intent(Reports.this, CustomerLogReport.class);
                    startActivity(intent1);
                    break ;

                case R.id.transactions_report:
                    Intent intent2 = new Intent(Reports.this, TransactionsReport.class);
                    startActivity(intent2);
                    break ;

                case R.id.return_report:
                    Intent intent3 = new Intent(Reports.this, PaymentDetailsReport.class);
                    startActivity(intent3);
                    break ;

                case R.id.stock_request_report:
                    Intent intent4 = new Intent(Reports.this, StockRequestReport.class);
                    startActivity(intent4);
                    break ;
            }

        }

    };
}

