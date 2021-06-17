package com.nuls.naboxpro.ui.popup

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.widget.EditText
import com.lxj.xpopup.core.CenterPopupView
import com.nuls.naboxpro.R
import com.nuls.naboxpro.utils.showToast
import kotlinx.android.synthetic.main.pop_edit_wallet_name_layout.view.*
import java.util.regex.Pattern

class EditWalletNamePop(context: Context):CenterPopupView(context){

    override fun getImplLayoutId() = R.layout.pop_edit_wallet_name_layout

    var listener:EditSuccessListener?=null
    var oldName:String?=null

    constructor(context: Context,oldName:String,listener:EditSuccessListener):this(context){
        this.listener = listener
        this.oldName = oldName
    }

    override fun onCreate() {
        super.onCreate()
        popupInfo.isMoveUpToKeyboard = false
        tv_old_name.text = oldName
        setEditTextInhibitInputSpaChat(edit_new_name)
        btn_cancel.setOnClickListener {
            dismiss()
        }
        btn_sure.setOnClickListener {
            if(TextUtils.isEmpty(edit_new_name.text)){
                showToast(context.getString(R.string.input_wallet_name))
            }else{
                listener?.editSuccess(edit_new_name.text.toString().trim())
                dismiss()
            }
        }


    }


    companion object{

       public interface EditSuccessListener{

            fun  editSuccess(name:String)
        }

    }


    fun setEditTextInhibitInputSpaChat(editText: EditText) {
        val filter_space = InputFilter { source, start, end, dest, dstart, dend ->
            if (source == " ")
                ""
            else
                null
        }
        val filter_speChat = InputFilter { charSequence, i, i1, spanned, i2, i3 ->
            val speChat = "[`~!@#_$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）— +|{}【】‘；：”“’。，、？]"
            val pattern = Pattern.compile(speChat)
            val matcher = pattern.matcher(charSequence.toString())
            if (matcher.find())
                ""
            else
                null
        }
        editText.filters = arrayOf(filter_space, filter_speChat, InputFilter.LengthFilter(12))
    }

}