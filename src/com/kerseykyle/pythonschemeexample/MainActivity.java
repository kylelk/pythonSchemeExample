package com.kerseykyle.pythonschemeexample;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Get the intent that started this activity
		Intent intent = getIntent();
		// check if this intent is started via custom scheme link
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
		  Uri uri = intent.getData();
		  // may be some test here with your custom uri
		  try {
				String encodedPythonCode=uri.toString().substring(9);
				String decodedPythonCode;
				decodedPythonCode = URLDecoder.decode(encodedPythonCode, "UTF-8");
				EditText input = (EditText) findViewById(R.id.input);
				input.setText(decodedPythonCode);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  	Button mRunButton = (Button) findViewById(R.id.run_button);
		  	mRunButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub 
					EditText input = (EditText) findViewById(R.id.input);
					String codeToRun = input.getText().toString();
					onQPyExec(codeToRun);
				}
			});
		}

	    }
	
	private final int SCRIPT_EXEC_PY = 40001;
	private final String extPlgPlusName = "com.hipipal.qpyplus";
	public static boolean checkAppInstalledByName(Context context, String packageName) {
	    if (packageName == null || "".equals(packageName))  
	        return false;  
	    try {  
	        ApplicationInfo info = context.getPackageManager().getApplicationInfo(  
	                packageName, PackageManager.GET_UNINSTALLED_PACKAGES);  
	        
	        Log.d("QPYMAIN",  "checkAppInstalledByName:"+packageName+" found");
	        return true;  
	    } catch (NameNotFoundException e) {  
	        Log.d("QPYMAIN",  "checkAppInstalledByName:"+packageName+" not found");

	        return false;  
	    }  
	}
	
	public void onQPyExec(String source) {
		
		if (checkAppInstalledByName(getApplicationContext(), extPlgPlusName)) {

	        Intent intent = new Intent();
	        intent.setClassName(extPlgPlusName, extPlgPlusName+".MPyApi");
	        intent.setAction(extPlgPlusName+".action.MPyApi");

	        Bundle mBundle = new Bundle(); 
	        mBundle.putString("app", "myappid");
	        mBundle.putString("act", "onPyApi");
	        mBundle.putString("flag", "onQPyExec");            // any String flag you may use in your context
	        mBundle.putString("param", "");          // param String param you may use in your context
	        
	        /*
	         * The String Python code, you can put your py file in res or raw or intenet, so that you can get it the same way, which can make it scalable
	         */ 
	        mBundle.putString("pycode", source);        

	        intent.putExtras(mBundle);

	        startActivityForResult(intent, SCRIPT_EXEC_PY);
	    } else {
	        Toast.makeText(getApplicationContext(), "Please install QPython first", Toast.LENGTH_LONG).show();

	    	try {
		        Uri uLink = Uri.parse("market://details?id=com.hipipal.qpyplus");
		        Intent intent = new Intent( Intent.ACTION_VIEW, uLink );
		        startActivity(intent);
	    	} catch (Exception e) {
		        Uri uLink = Uri.parse("http://qpython.com");
		        Intent intent = new Intent( Intent.ACTION_VIEW, uLink );
		        startActivity(intent);
	    	}
	    	
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	    if (requestCode == SCRIPT_EXEC_PY) {
	    	if (data!=null) {
		        Bundle bundle = data.getExtras();
		        String flag = bundle.getString("flag"); // flag you set
		        String param = bundle.getString("param"); // param you set 
		        String result = bundle.getString("result"); // Result your Pycode generate
		        //Toast.makeText(this, "onQPyExec: return ("+result+")", Toast.LENGTH_SHORT).show();
		        TextView outputArea = (TextView) findViewById(R.id.output);
		        outputArea.setText(result);
		        outputArea.setTextIsSelectable(true);
	    	} else {
		        Toast.makeText(this, "onQPyExec: data is null", Toast.LENGTH_SHORT).show();

	    	}
	    }
	}
	
}