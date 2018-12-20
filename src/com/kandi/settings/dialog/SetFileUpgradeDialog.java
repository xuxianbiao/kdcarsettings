package com.kandi.settings.dialog;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.kandi.settings.Configs;
import com.kandi.settings.R;
import com.kandi.settings.adapter.SetFileListAdapter;
import com.kandi.settings.utils.SharedPreferencesUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class SetFileUpgradeDialog extends Dialog {

	public SetFileUpgradeDialog(Context context,DialogDismissListener dialogDismissListener) {
		super(context,R.style.my_dialog);
		this.dialogDismissListener = dialogDismissListener;
	}

	DialogDismissListener dialogDismissListener;
	
	private List<String> items = null;	
	private List<String> paths = null;
	private String rootPath;
	private String curPath;
	//private TextView mPath;	//路径显示view控件

	private Button buttonConfirm;
	private final static String TAG = "SetFileUpgrade";
	ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_file_choose);
		rootPath = this.getContext().getResources().getString(R.string.Root_Path);
		listview = (ListView) findViewById(android.R.id.list);
		
		buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
		buttonConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(dialogDismissListener != null){
					dialogDismissListener.onDismiss(curPath, 0, null);
				}
				dismiss();
			}
		});
		//获取当前目录
		getFileDir(curPath);
	}

	/**
	 * @param dir
	 */
	public void setFileDir(String dir){
		this.curPath = dir;
		File f = new File(curPath);
		if(!f.exists()){
			f.mkdirs();
		}
	}
	
	private void getFileDir(String filePath) {
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(filePath);
		File[] files = f.listFiles(new HexOrS19File());
		if (!filePath.equals(rootPath)) {
			items.add("a_root");
			paths.add(rootPath);
			items.add("b_previous");
			paths.add(f.getParent());
		}
		
		if(files != null){
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				items.add(file.getName());
				paths.add(file.getPath());
			}			
		}
		Collections.sort(paths,String.CASE_INSENSITIVE_ORDER);//路径按照字符顺序排列
		if(listview != null){
			listview.setAdapter(new SetFileListAdapter(getContext(), items, paths));
			listview.setOnItemClickListener(itemClickListener);
		}
	}
	/**
	 * @bref	文件过滤显示，默认只能显示hex和S19文件。
	 * @author hujj_kd
	 *
	 */
	class HexOrS19File implements FilenameFilter{

		@Override
		public boolean accept(File arg0, String arg1) {
			// TODO Auto-generated method stub
			File f = new File(arg0.getPath()+"/"+arg1);
			if(f.isDirectory()){
				return true;
			}
			return IsHexOrS19File(arg1);
		}
		
		private boolean IsHexOrS19File(String name){
			if(name.toLowerCase().endsWith(".hex") || name.toLowerCase().endsWith(".s19") || name.toLowerCase().endsWith(".bin")){
				return true;
			}else{
				return false;
			}
		}
		
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			File file = new File(paths.get(position));
			if (file.isDirectory()) {
				curPath = paths.get(position);
				getFileDir(paths.get(position));
			} else {
				//获取当前的文件地址和名称
				curPath = file.getAbsolutePath().toString();
				if(dialogDismissListener != null){
					dialogDismissListener.onDismiss(curPath, 0, null);
				}
				dismiss();
				//finish();
				//openFile(file);
			}
		}
	};
	
	public void onAttachedToWindow() {
		getFileDir(curPath);
	};
}
