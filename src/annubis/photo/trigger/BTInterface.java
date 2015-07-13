package annubis.photo.trigger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BTInterface {
	private BluetoothDevice device = null;// le périphérique (le module bluetooth)
    private BluetoothSocket socket = null;
    private InputStream receiveStream = null;// Canal de réception
    private OutputStream sendStream = null;// Canal d'émission
	public String Message;
	
	public BTInterface(String BTName) {
	     
	    // On récupère la liste des périphériques associés
	    Set<BluetoothDevice> setpairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
	    BluetoothDevice[] pairedDevices = (BluetoothDevice[]) setpairedDevices.toArray(new BluetoothDevice[setpairedDevices.size()]);
	     
	    // On parcours la liste pour trouver notre module bluetooth
	    for(int i=0;i<pairedDevices.length;i++)
	    {
	        // On teste si ce périphérique contient le nom du module bluetooth connecté au microcontrôleur
	        if(pairedDevices[i].getName().contains(BTName)) {
	         
	            device = pairedDevices[i];
	            try {
	                // On récupère le socket de notre périphérique
	            	Method m;
	            	m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
	            	socket = (BluetoothSocket)m.invoke(device, Integer.valueOf(1)); 
	                //socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
	                 
	                receiveStream = socket.getInputStream();// Canal de réception (valide uniquement après la connexion)
	                sendStream = socket.getOutputStream();// Canal d'émission (valide uniquement après la connexion)
	                 
	            } catch (Exception e) {
	            	Message = e.getMessage();
	            }
	            break;
	        }
	    }
	}
	
	public void connect() {
	    new Thread() {
	        @Override public void run() {
	            try {
	                socket.connect();// Tentative de connexion
	                Message = "Connexion réussie!";
	            } catch (Exception e) {
	                // Echec de la connexion
	                Message = e.getMessage();
	            }
	        }
	    }.start();
	}
	
	public void sendData(String data) {
	    try {
	        // On écrit les données dans le buffer d'envoi
	        sendStream.write(data.getBytes());
	         
	        // On s'assure qu'elles soient bien envoyés
	        sendStream.flush();
	         
	    } catch (IOException e) {
	    	Message = e.getMessage();
	    }
	}
	
	
	public void close() {
	    try {
	        socket.close();
	    } catch (IOException e) {
	    	Message = e.getMessage();
	    }
	}
}
