package com.nuls.naboxpro.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nuls.naboxpro.R;
import com.nuls.naboxpro.jsbridge.JsBridge;

public class WebJsTestFragment extends Fragment {

    private static final String TAG = "JSBridge";

    EditText editText;
    Button button;
    WebView webView;
    View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activities_layout,container,false);
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        editText  =view.findViewById(R.id.edit_address);
//        button  =view.findViewById(R.id.btn);
//        webView  =view.findViewById(R.id.webview);
//        new JsBridge(this.getActivity(),null, webView);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(TextUtils.isEmpty(editText.getText())){
//                    webView.loadUrl("http://192.168.31.94:8080/");
//                }else{
//                    webView.loadUrl(editText.getText().toString());
////                    webView.loadUrl("http://192.168.31.94:8080/wallet-test.html");
//                }
//            }
//        });


    }




}
