package org.ebookdroid.pdfdroid.dialog;

import org.ebookdroid.core.BaseViewerActivity.DialogDismissListener;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kandi.settings.R;


public class MenuDialog extends Dialog{

    private DialogDismissListener onelistener;
    private DialogDismissListener threelistener;
    
    private TextView menu_goto_page;
    private TextView menu_close;
    
    public MenuDialog(Context context,DialogDismissListener onelistener,DialogDismissListener threelistener) {
        super(context,R.style.my_dialog);
        this.onelistener = onelistener;
        this.threelistener = threelistener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menudialog);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        getWindow().setGravity(Gravity.RIGHT | Gravity.TOP);
        lp.x = 10;
        lp.y = 150;
        getWindow().setAttributes(lp);
        menu_goto_page = (TextView) findViewById(R.id.menu_goto_page);
        menu_goto_page.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                if(onelistener != null){
                    onelistener.onDismiss();
                }
                dismiss();
            }
        });
        menu_close = (TextView) findViewById(R.id.menu_close);
        menu_close.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                if(threelistener != null){
                    threelistener.onDismiss();
                }
                dismiss();
            }
        });
    }

}
