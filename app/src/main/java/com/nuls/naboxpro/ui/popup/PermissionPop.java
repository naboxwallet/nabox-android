package com.nuls.naboxpro.ui.popup;


import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lxj.xpopup.core.CenterPopupView;
import com.nuls.naboxpro.R;
import com.nuls.naboxpro.entity.PermissionInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yange on 2020/4/7 0007
 * 描述：
 */
public class PermissionPop extends CenterPopupView {

    Context context;
    CloseCallback closeCallback;
    public PermissionPop(@NonNull Context context, CloseCallback closeCallback) {
        super(context);
        this.context = context;
        this.closeCallback = closeCallback;
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_request_permissions;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        popupInfo.hasShadowBg =false;
        RecyclerView recyclerView = findViewById(R.id.recycler_permission);
        Button btn = findViewById(R.id.tv_open_permissions);
        ImageView close = findViewById(R.id.iv_permission_close);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        BaseQuickAdapter adapter = new BaseQuickAdapter<PermissionInfo, BaseViewHolder>(R.layout.item_permission) {
            @Override
            protected void convert(BaseViewHolder helper, PermissionInfo item) {
                helper.setText(R.id.tv_permission_des, item.getPerStr());
                helper.setText(R.id.tv_content, item.getContentStr());
                helper.setImageResource(R.id.iv_permission_icon,item.getIcon());
            }
        };
        recyclerView.setAdapter(adapter);
        List<PermissionInfo> list = new ArrayList<PermissionInfo>();
        list.add(new PermissionInfo(R.mipmap.img_permission_2, context.getString(R.string.write_external_storage),context.getString(R.string.used_1)));
        list.add(new PermissionInfo(R.mipmap.img_permission_3, context.getString(R.string.read_phone_state),context.getString(R.string.used_2)));
        list.add(new PermissionInfo(R.mipmap.img_permission_5, context.getString(R.string.camera),context.getString(R.string.used_3)));
        adapter.setNewInstance(list);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCallback.dismiss(true);
                dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeCallback.dismiss(false);
                dismiss();
            }
        });
    }




    public interface  CloseCallback{

        void dismiss(boolean state);

    }

}
