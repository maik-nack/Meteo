package com.maiknack.meteo;

import android.util.Log;

import com.google.common.io.LittleEndianDataInputStream;

import java.net.InetAddress;
import java.net.Socket;

import static com.maiknack.meteo.Utils.closeStream;

class TcpClient {

    private Short mTemperature = null, mPress = null;
    private TCPListener mMessageListener = null;
    private boolean mRun = false;
    private LittleEndianDataInputStream mBufferIn;
    private String mIpAddress = "127.0.0.1";
    private int mPort = 10000;

    TcpClient() {}

    void stopClient() {
        mRun = false;
        if (mBufferIn != null) closeStream(mBufferIn);
        mBufferIn = null;
        mMessageListener = null;
        mTemperature = null;
        mPress = null;
    }

    boolean run() {

        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(mIpAddress);

            Log.d("TCP Client", "C: Connecting...");

            Socket socket = new Socket(serverAddr, mPort);

            try {
                mMessageListener.connectionSuccessful();

                mBufferIn = new LittleEndianDataInputStream(socket.getInputStream());

                while (mRun) {

                    mTemperature = mBufferIn.readShort();
                    mPress = mBufferIn.readShort();

                    if (mTemperature != null && mPress != null && mMessageListener != null) {
                        mMessageListener.messageReceived(mTemperature, mPress);
                    }

                }

                return true;

            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                socket.close();
            }
            mMessageListener.connectionFailed();
            return false;

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
            mMessageListener.connectionFailed();
            return false;
        }
    }

    void setServerSettings(String ip, int port) {
        mIpAddress = ip;
        mPort = port;
    }

    void setListener(TCPListener listener) {
        if (!mRun) mMessageListener = listener;
    }

    public interface TCPListener {
        void messageReceived(Short... message);
        void connectionFailed();
        void connectionSuccessful();
    }

}
