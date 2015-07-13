package annubis.photo.trigger;

import java.util.Set;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

public class PhotoTriggerActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 0;
	private static final int RESULT_ENABLE_BT = 10;
	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
	private BTInterface _BTInterface;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //enable BT
        if (_bluetooth == null) {
            //Device does not support Bluetooth
        	Alert("Erreur","Bluetooth non supporté");
        }else
        {
        	//_bluetooth.enable(); 
        	if (!_bluetooth.isEnabled()) {

        	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        	    onActivityResult(REQUEST_ENABLE_BT, RESULT_ENABLE_BT, enableBtIntent);
        	}
        } 
        
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar1);
        seekBar.setProgress(0);
        seekBar.incrementProgressBy(1);
        seekBar.setMax(1);
        seekBar.setProgress(0);
        final Drawable d = getResources().getDrawable(R.drawable.thumb_big64);
    	d.setBounds(new Rect(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight()));
    	seekBar.setThumb(d);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            	if(progress == 1)
            	{	
            		Send("S");
            		AddTextConsole("Photo OK!");
            	}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            	final Drawable d = getResources().getDrawable(R.drawable.thumb_bigred64);
            	d.setBounds(new Rect(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight()));
            	seekBar.setThumb(d);
            	Send("F");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            	final Drawable d = getResources().getDrawable(R.drawable.thumb_big64);
            	d.setBounds(new Rect(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight()));
            	seekBar.setThumb(d);
            	try{
            	Thread.sleep(200);
            	}catch( Exception e)
            	{
            	}
            	Send("f");
            	Send("s");
            	seekBar.setProgress(0);
            }
        });
        
    }
    
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
      if ((requestCode == REQUEST_ENABLE_BT) && resultCode==-1)
      {
    	  AddTextConsole("BT Started.");
      }
    }
    
    public void Refresh(View view) {
    	BTScan();
    }
    
    private void BTScan()
    {
    	 if (_bluetooth.isEnabled()) {
	        	Set<BluetoothDevice> pairedDevices = _bluetooth.getBondedDevices();
	        	// If there are paired devices
	        	if (pairedDevices.size() > 0) {
	        	    // Loop through paired devices
	        	    for (BluetoothDevice device : pairedDevices) {
	        	        // Add the name and address to an array adapter to show in a ListView
	        	    	TextView view2 = (TextView) findViewById(R.id.Console);
	        	    	String txt = view2.getText().toString();
	        	        //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	        	    	txt+= "\n"+ device.getName() + "\n" + device.getAddress();
	        	    	view2.setText(txt);
	        	    }
	        	}
    	 }
    }
    
    
    public void Connect(View view)
    {
    	Connect();
    }
    
    public void Connect()
    {
    	try
    	{
	    	 if (_bluetooth.isEnabled()) 
	    	 {
	    		 if(_BTInterface != null)
	    		 {
	    			 _BTInterface.close();
	    			 _BTInterface = null;
	    		 }
	    		 else
	    		 {
		    		 _BTInterface = new BTInterface(((TextView) findViewById(R.id.btdevice)).getText().toString());
		    		 TestFoError();
		    		 _BTInterface.connect();
		    		 TestFoError();
	    		 }
	    		 if(_BTInterface != null)
	    		 {
	    			 AddTextConsole("Ready !");
	    		 }

	    	 }
	    	 
    	}catch (Exception e) {}finally{};
    }
 
    
    public void Send(View view) 
    {
    		CheckBox ReFocus = (CheckBox) findViewById(R.id.refocus);
	    	TextView Tempo = (TextView) findViewById(R.id.editText1);
	    	if(ReFocus.isChecked())
	    		Send("A" + Tempo.getText().toString()); //A = avec Focus a chaque fois
	    	else
	    		Send("P" + Tempo.getText().toString());  //Pas de focus a chaque fois
    } 
    
    public void Send(String Txt) {
    	if(_BTInterface!=null)
    	{
	    	_BTInterface.sendData(Txt);
	    	TestFoError();
	    	AddTextConsole("Send :" + Txt);
    	}else
    	{
    		AddTextConsole("_BTInterface = null");
    	}
    } 
    
    public void TestFoError() {
    	if(_BTInterface!=null)
    	{
	    	if(_BTInterface.Message != "" && _BTInterface.Message != null)
	    	{
	    		AddTextConsole("BTInterface: "+_BTInterface.Message);
	    		_BTInterface.Message="";
	    	}
    	}else
    	{
    		AddTextConsole("_BTInterface = null");
    	}
    } 
    
    public void Reset(View view) {
    	Send("R");
    	((CheckBox) findViewById(R.id.checkBox2)).setChecked(false);
    	((CheckBox) findViewById(R.id.Shutter)).setChecked(false);
    }
    
    public void Photo(View view) {
    	Send("P");
    }
    
    public void Focus(View view) {
    	if(((CheckBox)view).isChecked())
			Send("F");
		else
			Send("f");
    }
    
    public void Shutter(View view) {
    	if(view instanceof CheckBox)
    	{
    		if(((CheckBox)view).isChecked())
    			Send("S");
    		else
    			Send("s");
    	}
    }
    
    public void Clear(View view) {
    	TextView view2 = (TextView) findViewById(R.id.Console);
    	view2.setText("");
    }
    
    public void Alert(String titre, String message)
    {
    	AlertDialog.Builder box = new AlertDialog.Builder(this);
    	box.setTitle(titre)
        .setMessage(message)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
            	// do nothing
            }
         })
         .show();
    }
    
    private void AddTextConsole(String Txt)
    {
    	TextView view2 = (TextView) findViewById(R.id.Console);
    	view2.setText(view2.getText().toString()+"\n"+Txt);
    }
    
    private void SetTextConsole(String Txt)
    {
    	TextView view2 = (TextView) findViewById(R.id.Console);
    	view2.setText(Txt);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if(_BTInterface !=null)
    		_BTInterface.close();
    	
    }
    
    public void onBackPressed()
    {
    		if(_BTInterface !=null)
        		_BTInterface.close();
        	
            this.finish();
     }

}