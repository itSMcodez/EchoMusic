package com.itsmcodez.echomusic;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.itsmcodez.echomusic.databinding.ActivityCrashHandlerBinding;
import java.io.InputStream;

public class CrashHandlerActivity extends AppCompatActivity {
    private ActivityCrashHandlerBinding binding;
    private String[] exceptionType = {
			"StringIndexOutOfBoundsException",
			"IndexOutOfBoundsException",
			"ArithmeticException",
			"NumberFormatException",
			"ActivityNotFoundException"
	};
	private String[] errMessage= {
			"Invalid string operation\n",
			"Invalid list operation\n",
			"Invalid arithmetical operation\n",
			"Invalid toNumber block operation\n",
			"Invalid intent operation"
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind to views
        binding = ActivityCrashHandlerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
		Intent intent = getIntent();
		String errMsg = "";
		String madeErrMsg = "";
		if(intent != null){
			errMsg = intent.getStringExtra("error");
			String[] spilt = errMsg.split("\n");
			//errMsg = spilt[0];
			try {
				for (int j = 0; j < exceptionType.length; j++) {
					if (spilt[0].contains(exceptionType[j])) {
						madeErrMsg = errMessage[j];
						int addIndex = spilt[0].indexOf(exceptionType[j]) + exceptionType[j].length();
						madeErrMsg += spilt[0].substring(addIndex, spilt[0].length());
						break;
					}
				}
				if(madeErrMsg.isEmpty()) madeErrMsg = errMsg;
			}catch(Exception e){}
		}
        
        binding.crashMsg.setText(madeErrMsg);
        
        binding.close.setOnClickListener(view -> {
                finish();
        });
        
        final String crashLog = madeErrMsg;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Toast.makeText(getApplicationContext(), "Crash Log copied!", Toast.LENGTH_LONG).show();
            }
        });
        binding.report.setOnClickListener(view -> {
                ClipData clip = ClipData.newPlainText("CRASH_LOG", crashLog);
                clipboard.setPrimaryClip(clip);
                try {
                	
                    Intent launchGithub = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com:itSMcodez/EchoMusic.git"));
                    launchGithub.addCategory(Intent.CATEGORY_BROWSABLE);
                    launchGithub.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launchGithub);
                    
                } catch(ActivityNotFoundException err) {
                	
                }
        });
        
        binding.copy.setOnClickListener(view -> {
                ClipData clip = ClipData.newPlainText("CRASH_LOG", crashLog);
                clipboard.setPrimaryClip(clip);
        });
    }
}
