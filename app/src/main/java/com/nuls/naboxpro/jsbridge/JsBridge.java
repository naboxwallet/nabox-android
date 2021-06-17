package com.nuls.naboxpro.jsbridge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.alibaba.fastjson.JSON;
import com.nuls.naboxpro.common.UserDao;
import com.nuls.naboxpro.db.WalletInfoDaoHelper;
import com.nuls.naboxpro.entity.ChainConfigBean;
import com.nuls.naboxpro.entity.WalletInfo;
import com.nuls.naboxpro.enums.CoinTypeEnum;
import com.nuls.naboxpro.net.Api;
import com.nuls.naboxpro.net.BaseResponse;
import com.nuls.naboxpro.net.api.RetrofitClient;
import com.nuls.naboxpro.net.observable.JsonRpcObserver;
import com.nuls.naboxpro.net.observable.RecObserver;
import com.nuls.naboxpro.utils.ExtKt;

import org.jetbrains.annotations.NotNull;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthEstimateGas;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.nuls.core.exception.NulsException;
import io.nuls.core.model.StringUtils;
import java8.util.Lists;
import java8.util.Maps;
import network.nerve.core.crypto.HexUtil;
import network.nerve.core.model.DateUtils;
import network.nerve.heterogeneous.context.BnbContext;
import network.nerve.heterogeneous.context.EthContext;
import network.nerve.heterogeneous.context.HtContext;
import network.nerve.heterogeneous.core.HtgWalletApi;
import network.nerve.heterogeneous.core.MetaMaskWalletApi;
import network.nerve.heterogeneous.core.WalletApi;
import network.nerve.heterogeneous.model.EthSendTransactionPo;

public class JsBridge {

    private static final String TAG = "JSBridge";

    //    private static final String JS_URL = "http://192.168.31.94:8080/";
//    private static final String JS_URL = "https://release.nabox.io/js/";
    private static final String JS_URL = "https://release.nabox.io/js_beta/";

    //锁
    Lock lock = new ReentrantLock();

    Context context;
    WebView webView;
    FragmentManager fragmentManager;

    final static int CALLBACK = 1;

    private HashMap<Long, String> eth_getTransactionReceiptMap,eth_blockNumberMap,eth_gasPriceMap;
    private HashMap<Long,String> eth_callMap;


    Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback((JsonRpc) msg.obj);
                }
            });
        }
    };


    /**
     * 获取链接状态
     */
    @JavascriptInterface
    public void onConnect(String arg) {
        Log.i(TAG, "onConnect " + arg);
//        String nowSelectChain = UserDao.INSTANCE.getDefaultChain();
        try {
            JsonRpc baseJsonRpc = JSON.parseObject(arg, JsonRpc.class);
            ExtKt.showChoiceWalletAndChainPop(context, UserDao.INSTANCE.getDefaultChain(), null, false, (walletInfo, assetsEntity) -> callback(JsonRpc.success(baseJsonRpc.getId(),
                    Maps.of("chainId", "0x3", "connected", true))));
        } catch (Exception e) {
            Log.e(TAG, "onConnect error : ", e.fillInStackTrace());
            JsonRpc baseJsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(baseJsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage())));
        }
    }

    @JavascriptInterface
    public void net_version(String arg) {
        Log.i(TAG, "net_version " + arg);
        try {
            JsonRpc baseJsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.success(baseJsonRpc.getId(), "3"));
        } catch (Exception e) {
            Log.e(TAG, "net version error : ", e.fillInStackTrace());
            JsonRpc baseJsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(baseJsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage())));
        }
    }


    /**
     * EIP-1102 eth_requestAccounts
     *
     * @param arg
     */
    @JavascriptInterface
    public void eth_requestAccounts(String arg) {
        //如果用户没有获取授权，是无法进入dapp页面的，所以这里只需要获取到默认钱包地址就行了
        Log.i(TAG, "call eth_requestAccounts " + arg);
        try {
            JsonRpc baseJsonRpc = JSON.parseObject(arg, JsonRpc.class);
            String address = ExtKt.getAddressByCoinType(WalletInfoDaoHelper.loadDefaultWallet(), UserDao.INSTANCE.getDefaultChain());

            int chainId = 0;
            List<ChainConfigBean> chainList = UserDao.INSTANCE.getChainConfig();
            for (int i = 0; i < chainList.size(); i++) {
                if (chainList.get(i).getChain().equals(UserDao.INSTANCE.getDefaultChain())) {
                    chainId = chainList.get(i).getNativeId();
                    break;
                }
            }

            Map<String, Object> map = new HashMap<>();
            map.put("address", address);
            map.put("chainId", "0x" + Integer.toHexString(chainId));
            callback(JsonRpc.success(baseJsonRpc.getId(), map));
        } catch (Exception e) {
            Log.e(TAG, "eth_requestAccounts error : ", e.fillInStackTrace());
            JsonRpc baseJsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(baseJsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage())));
        }

    }


    /**
     * EIP-1102 eth_accounts
     *
     * @param arg
     */
    @JavascriptInterface
    public void eth_accounts(String arg) {

        try {
            eth_requestAccounts(arg);
        } catch (Exception e) {
            Log.e(TAG, "eth_accounts error : ", e.fillInStackTrace());
            JsonRpc baseJsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(baseJsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage())));
        }
    }

    /**
     * EIP-1102 eth_accounts
     *
     * @param arg
     */
    @JavascriptInterface
    public void eth_chainId(String arg) {
        try {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            List<ChainConfigBean> chainList = UserDao.INSTANCE.getChainConfig();
            for (int i = 0; i < chainList.size(); i++) {
                if (chainList.get(i).getChain().equals(UserDao.INSTANCE.getDefaultChain())) {
                    callback(JsonRpc.success(jsonRpc.getId(), "0x" + Integer.toHexString(chainList.get(i).getNativeId())));
                    return;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "eth_chainId error : ", e.fillInStackTrace());
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage())));
        }

    }

    /**
     * EIP-1102 eth_accounts
     *
     * @param arg
     */
    @JavascriptInterface
    public void eth_gasPrice(String arg) {
        try {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            if (eth_gasPriceMap != null) {
                for (Long key : eth_gasPriceMap.keySet()) {
                    if (System.currentTimeMillis() - key > 60 * 1000) {
                        callback(JsonRpc.success(jsonRpc.getId(), eth_gasPriceMap.get(key)));

                        return;
                    }
                }
            }
            eth_gasPriceMap = null;
            Map<String, Object> map = new HashMap<>();
            map.put("chain", UserDao.INSTANCE.getDefaultChain());
            map.put("id", jsonRpc.getId());
            map.put("method", "eth_gasPrice");
            map.put("args", jsonRpc.getParams());
            RetrofitClient.getInstance().invokePostBody(context, Api.ETH_CALL, map).subscribe(new JsonRpcObserver<EthCallResponse<String>>(context, false, false) {
                @Override
                public void onSuccess(EthCallResponse<String> data) {
                    if (data.getResult() != null) {
                        Log.i("jsBridge", "**************success");
                        if (eth_gasPriceMap == null) {
                            eth_gasPriceMap = new HashMap<>();
                        }
                        eth_gasPriceMap.put(System.currentTimeMillis(), data.getResult());
                        callback(JsonRpc.success(jsonRpc.getId(), data.getResult()));
                    }
                }

                @Override
                public void onFail(@NotNull String msg, int code) {
                    Log.i("jsBridge", "**************error********" + msg);
                    //透传失败应该返回  约定好的错误码和错误信息
//               callback(JsonRpc.fail(jsonRpc.getId(), new JsonRpc.JsonRpcError(0,msg)));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "eth_gasPrice error : ", e.fillInStackTrace());
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage())));
        }

    }


    @JavascriptInterface
    public void eth_estimateGas(String arg) {
        Log.i(TAG, "call eth_estimateGas " + arg);

        try {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            TransferReq req = jsonRpc.getParams().getJSONObject(0).toJavaObject(TransferReq.class);
            MetaMaskWalletApi walletApi = getWalletApi();
            BigInteger gas = hexToBigInteger(req.getGas());
            BigInteger gasPrice = hexToBigInteger(req.getGasPrice());
            BigInteger value = hexToBigInteger(req.getValue());
            EthEstimateGas po = walletApi.ethEstimateGas(req.getFrom(), req.getTo(), gasPrice, gas, value, req.getData());
            if (po.getError() != null) {
                callback(JsonRpc.fail(jsonRpc.getId(), new JsonRpc.JsonRpcError(po.getError().getCode(), po.getError().getMessage(), po.getError().getData())));
            } else {
                callback(JsonRpc.success(jsonRpc.getId(), "0x" + po.getAmountUsed().toString(16)));
            }
        } catch (JsonRpcException e) {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), e.error));
        } catch (NulsException e) {
            Log.e(TAG, "eth call error : ", e.fillInStackTrace());
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getCustomMessage())));
        } catch (Exception e) {
            Log.e(TAG, "eth call error : ", e.fillInStackTrace());
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage())));
        }
    }

    /**
     * @param arg
     */
    @JavascriptInterface
    public void eth_call(String arg) {
//        lock.lock();
        //新增 每隔10S允许请求一次
        Log.i(TAG, "call eth_call " + arg);

        try {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            if(eth_callMap!=null){
                for (Long key:eth_callMap.keySet()){
                    if (System.currentTimeMillis() - key <= 10 * 1000) {
                        callback(JsonRpc.success(jsonRpc.getId(), eth_callMap.get(key)));
                        return;
                    }
                }
            }
            boolean latest = true;
            if (jsonRpc.getParams().size() > 1) {
                latest = "latest".equals(jsonRpc.getParams().getString(1));
            }
            TransferReq req = jsonRpc.getParams().getJSONObject(0).toJavaObject(TransferReq.class);
            MetaMaskWalletApi walletApi = getWalletApi();
            BigInteger gas = hexToBigInteger(req.getGas());
            BigInteger gasPrice = hexToBigInteger(req.getGasPrice());
            BigInteger value = hexToBigInteger(req.getValue());
            EthCall po = walletApi.ethCall(req.getFrom(), req.getTo(), gasPrice, gas, value, req.getData(), latest);
            eth_callMap = null;
           if(eth_callMap==null&& !po.isReverted()){
               eth_callMap=new HashMap<>();
               eth_callMap.put(System.currentTimeMillis(),po.getValue());
           }
            if (po.isReverted()) {
                callback(JsonRpc.fail(jsonRpc.getId(), new JsonRpc.JsonRpcError(po.getError().getCode(), po.getError().getMessage(), po.getError().getData())));
            } else {
                callback(JsonRpc.success(jsonRpc.getId(), po.getValue()));
            }
        } catch (JsonRpcException e) {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), e.error));
        } catch (NulsException e) {
            Log.e(TAG, "eth call error : ", e.fillInStackTrace());
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getCustomMessage())));
        } catch (Exception e) {
            Log.e(TAG, "eth call error : ", e.fillInStackTrace());
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage())));
        }
        finally {
//            lock.unlock();
        }
    }

    /**
     * @param arg
     */
    @JavascriptInterface
    public void eth_getBalance(String arg) {
        Log.i(TAG, "call eth_getBalance " + arg);

        try {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            String address = jsonRpc.getParams().getString(0);
            String blockNumber = "latest";
            if (jsonRpc.getParams().size() > 1) {
                blockNumber = jsonRpc.getParams().getString(1);
            }
            WalletApi walletApi = getWalletApi();
            BigInteger balance = walletApi.getBalance(address).toBigInteger();
            Log.i(TAG, "获取到余额: " + balance);
            callback(JsonRpc.success(jsonRpc.getId(), "0x" + balance.toString(16)));
        } catch (JsonRpcException e) {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), e.error));
        } catch (Exception e) {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError()));
        }
    }

    @JavascriptInterface
    public void eth_sign(String arg) {

    }

    @JavascriptInterface
    public void eth_getTransactionReceipt(String arg) {


        try {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            if (eth_getTransactionReceiptMap != null) {
                for (Long key : eth_getTransactionReceiptMap.keySet()) {
                    if (System.currentTimeMillis() - key <= 60 * 1000) {
                        callback(JsonRpc.success(jsonRpc.getId(), eth_getTransactionReceiptMap.get(key)));
                        return;
                    }
                }
            }
            eth_getTransactionReceiptMap = null;
            Map<String, Object> map = new HashMap<>();
            map.put("chain", UserDao.INSTANCE.getDefaultChain());
            map.put("id", jsonRpc.getId());
            map.put("method", "eth_getTransactionReceipt");
            map.put("args", jsonRpc.getParams());
            RetrofitClient.getInstance().invokePostBody(context, Api.ETH_CALL, map).subscribe(new JsonRpcObserver<EthCallResponse<String>>(context, false, false) {
                @Override
                public void onSuccess(EthCallResponse<String> data) {
                    if (data.getResult() != null) {
                        Log.i("jsBridge", "**************success");
                        if (eth_getTransactionReceiptMap == null) {
                            eth_getTransactionReceiptMap = new HashMap<>();
                        }
                        eth_getTransactionReceiptMap.put(System.currentTimeMillis(), data.getResult());
                        callback(JsonRpc.success(jsonRpc.getId(), data.getResult()));
                    }
                }

                @Override
                public void onFail(@NotNull String msg, int code) {
                    Log.i("jsBridge", "**************error********" + msg);
                    //透传失败应该返回  约定好的错误码和错误信息
//               callback(JsonRpc.fail(jsonRpc.getId(), new JsonRpc.JsonRpcError(0,msg)));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "eth_getTransactionReceipt error : ", e.fillInStackTrace());
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage())));
        }

    }

    @JavascriptInterface
    public void eth_blockNumber(String arg) {

        try {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            if (eth_blockNumberMap != null) {
                for (Long key : eth_blockNumberMap.keySet()) {
                    if (System.currentTimeMillis() - key > 60 * 1000) {
                        callback(JsonRpc.success(jsonRpc.getId(), eth_blockNumberMap.get(key)));
                        return;
                    }
                }
            }
            eth_blockNumberMap = null;
            Map<String, Object> map = new HashMap<>();
            map.put("chain", UserDao.INSTANCE.getDefaultChain());
            map.put("id", jsonRpc.getId());
            map.put("method", "eth_blockNumber");
            map.put("args", jsonRpc.getParams());
            RetrofitClient.getInstance().invokePostBody(context, Api.ETH_CALL, map).subscribe(new JsonRpcObserver<EthCallResponse<String>>(context, false, false) {
                @Override
                public void onSuccess(EthCallResponse<String> data) {
                    if (data.getResult() != null) {
                        Log.i("jsBridge", "**************success");
                        if (eth_blockNumberMap == null) {
                            eth_blockNumberMap = new HashMap<>();
                        }
                        eth_blockNumberMap.put(System.currentTimeMillis(), data.getResult());
                        callback(JsonRpc.success(jsonRpc.getId(), data.getResult()));
                    }
                }

                @Override
                public void onFail(@NotNull String msg, int code) {
                    Log.i("jsBridge", "**************error********" + msg);
                    //透传失败应该返回  约定好的错误码和错误信息
//               callback(JsonRpc.fail(jsonRpc.getId(), new JsonRpc.JsonRpcError(0,msg)));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "eth_blockNumber error : ", e.fillInStackTrace());
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage())));
        }

    }


    /**
     * @param arg
     */
    @JavascriptInterface
    public void eth_sendTransaction(String arg) {
        Log.i(TAG, "call eth_sendTransaction " + arg);


        try {
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            TransferReq req = jsonRpc.getParams().getJSONObject(0).toJavaObject(TransferReq.class);
            //根据当前网络调用对应的交易tools 广播交易
            ExtKt.showPasswordPop(context, password -> {
                WalletInfo walletInfo = WalletInfoDaoHelper.loadDefaultWallet();
                if (walletInfo.validatePassword(password)) {
                    //私钥
                    try {
                        String priKey = walletInfo.decrypt(password);
                        MetaMaskWalletApi walletApi = getWalletApi();
                        BigInteger nonce = hexToBigInteger(req.getNonce());
                        BigInteger gas = hexToBigInteger(req.getGas());
                        BigInteger gasPrice = hexToBigInteger(req.getGasPrice());
                        BigInteger value = hexToBigInteger(req.getValue());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message msg = new Message();
                                msg.what = CALLBACK;
                                try {
                                    EthSendTransactionPo po = walletApi.sendRawTransaction(priKey, nonce, gasPrice, gas, req.getTo(), value, req.getData());
                                    msg.obj = JsonRpc.success(jsonRpc.getId(), po.getTxHash());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(TAG, e.getMessage());
                                    msg.obj = JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage()));
                                }
                                handler.sendMessage(msg);
                            }
                        }).start();

                    } catch (JsonRpcException e) {
                        callback(JsonRpc.fail(jsonRpc.getId(), e.error));
                    } catch (NulsException e) {
                        callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError()));
                    }
                } else {
                    callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._4100.getError()));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "eth_sendTransaction error : ", e.fillInStackTrace());
            JsonRpc jsonRpc = JSON.parseObject(arg, JsonRpc.class);
            callback(JsonRpc.fail(jsonRpc.getId(), JsonRpcErrorEnum._32603.getError(e.getMessage())));
        }

    }


    public JsBridge(Context context, FragmentManager fragmentManager, WebView webView, String selectAddress) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.webView = webView;
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowContentAccess(true);
        webSetting.setAppCacheEnabled(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setBlockNetworkImage(false);
        webSetting.setDefaultTextEncodingName("utf-8");
        webSetting.setSupportZoom(true);
//        // 设置出现缩放工具
//        webSetting.setBuiltInZoomControls(true);
        //扩大比例的缩放
        webSetting.setUseWideViewPort(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);

                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        boolean isConnected = true; //这里的网络状态在点击Dapp时候已经进行了判断 这里必然为true
                        //能够进入这里说明用户已经过授权并且拿到了当前钱包在当前链下的的address
//                        String selectAddress = "0x1C7b75db2F8983C3B318D957D3A789f89003e0C0"; //TODO 根据当前打开的URL，查询是否已经授权账户，如果授权应通过此地址返回给前端, 是否授权的判断条件应该是 URL+网路+地址
                        String Day = "yyyyMMdd";
                        String minute = "yyyyMMddHHmmss";
                        String cache = DateUtils.convertDate(new Date(), minute); //js缓存级别
                        StringBuilder script = new StringBuilder();
                        script.append("javascript:;(function() {")
                                //导入js
                                .append("var adapter = document.createElement('script');")
                                .append("adapter.type = 'text/javascript';")
                                .append("adapter.src = \"" + JS_URL + "nabox-android-adapter.js?" + cache + "\";")
                                .append("document.body.appendChild(adapter);")
                                //导入js
                                .append("var adapter = document.createElement('script');")
                                .append("adapter.type = 'text/javascript';")
                                .append("adapter.src = \"https://cdn.bootcdn.net/ajax/libs/vConsole/3.4.0/vconsole.min.js\";")
                                .append("document.body.appendChild(adapter);")
                                //导入js
                                .append("var metamask = document.createElement('script');")
                                .append("metamask.type = 'text/javascript';")
                                .append("metamask.src = \"" + JS_URL + "nabox-metamask-core.js?" + cache + "\";")
                                .append("metamask.onload = function() { window.ethereum.connected = " + isConnected + "; window.ethereum.selectAddress='" + selectAddress + "' };")
                                .append("document.body.appendChild(metamask);")
                                .append("}());");
                        webView.loadUrl(script.toString());

                        Log.i("JSBridge", "load script ");
                    }
                });

            }


            /**
             * android 6.0以上 监听http 请求错误码
             * @param view
             * @param request
             * @param errorResponse
             */
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                int statusCode = errorResponse.getStatusCode();
                System.out.println("onReceivedHttpError code = " + statusCode);
                if (200 != statusCode) {
//                    view.loadUrl("about:blank");// 避免出现默认的错误界面
//                    view.loadUrl(errorUrl_404);
//                    emptyLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showLoading();
            }
        });
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
//            }
//
//        });
        webView.addJavascriptInterface(this, "naboxApp");
    }


    private void callback(JsonRpc res) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "callback :" + JSON.toJSONString(res));
                webView.evaluateJavascript("javascript:" + "naboxJs.callback('" + JSON.toJSONString(res) + "');", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i(TAG, "callback return : " + value);
                    }
                });
            }
        });
    }

    private <T> T getWalletApi() throws JsonRpcException {
        String chain = UserDao.INSTANCE.getDefaultChain();
        if (CoinTypeEnum.NULS.code().equals(chain) || CoinTypeEnum.NERVE.equals(chain)) {
            throw new JsonRpcException(JsonRpcErrorEnum._4200);
        }
        if (CoinTypeEnum.BSC.code().equals(chain)) {
            return (T) HtgWalletApi.getInstance(BnbContext.symbol, chain, BnbContext.rpcAddress);
        } else if (CoinTypeEnum.ETH.code().equals(chain)) {
            return (T) HtgWalletApi.getInstance(EthContext.symbol, chain, EthContext.rpcAddress);
        } else {
            return (T) HtgWalletApi.getInstance(HtContext.symbol, chain, HtContext.rpcAddress);
        }
    }

    private BigInteger hexToBigInteger(String hex) {
        if (StringUtils.isBlank(hex)) {
            return null;
        }
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        return new BigInteger(hex, 16);
    }


}
