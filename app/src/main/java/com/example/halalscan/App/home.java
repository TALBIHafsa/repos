package com.example.halalscan.App;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.halalscan.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

public class home extends AppCompatActivity {
    Button btn_scan;
    DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btn_scan = findViewById(R.id.button4);
        database = FirebaseDatabase.getInstance().getReference("products");

        btn_scan.setOnClickListener(v -> scanCode());
    }
    private void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String scannedCode = result.getContents();
            database.orderByChild("id").equalTo(scannedCode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            product product = dataSnapshot.getValue(product.class);
                            Intent intent = new Intent(home.this, products.class);
                            intent.putExtra("productId", product.getId());
                            intent.putExtra("productName", product.getName());
                            intent.putExtra("productImage", product.getImage());
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(home.this, "Product not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(home.this, "Failed to read from database", Toast.LENGTH_SHORT).show();
                }
            });
        }
    });

    public void goToFavourits(View v){
        Intent i = new Intent(this, favorits.class);
        startActivity(i);
    }
    public void goToNotifications(View v){
        Intent i = new Intent(this, notification.class);
        startActivity(i);
    }
    public void goToProduct(View v){
        Intent i = new Intent(this, products.class);
        startActivity(i);
    }
    public void goToSearch(View v){
        Intent i = new Intent(this, productList.class);
        startActivity(i);
    }

}