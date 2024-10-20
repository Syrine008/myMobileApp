package com.example.myapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.material.navigation.NavigationView;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TicketElectrique extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ImageView menuIcon;
    private NavigationView navigationView;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btnGetTicket;
    private static int i = 0, iA = 0, iB = 0, iC = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ticket_electrique);

        drawerLayout = findViewById(R.id.drawer_layout_ticket);
        menuIcon = findViewById(R.id.menu_ticket);
        navigationView = findViewById(R.id.navigation_view_ticket);

        radioGroup = findViewById(R.id.rgTicket);
        btnGetTicket = findViewById(R.id.btnGetTicket);

        navigationDrawer();

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.ticket) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(TicketElectrique.this, ProfilActivity.class));
            }else if (item.getItemId() == R.id.home) {
                startActivity(new Intent(TicketElectrique.this, HomeActivity.class));
            }
            return true;
        });

        btnGetTicket.setOnClickListener(v -> {
            int radioId = radioGroup.getCheckedRadioButtonId();
            radioButton = findViewById(radioId);


            try {
                createPdf(radioButton.getText().toString());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createPdf(String ticketName) throws FileNotFoundException {

        i++;
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "Ticket Electrique " + i + ".pdf");

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A6);
        document.setMargins(5, 5, 5, 5);

        Drawable d = getDrawable(R.drawable.test_logo);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapData = stream.toByteArray();
        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER).setHeight(200).setWidth(200);

        Paragraph title = new Paragraph("Ticket Electrique").setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER);
        Paragraph welcome = new Paragraph("welcome").setBold().setFontSize(13).setTextAlignment(TextAlignment.CENTER);

        Paragraph numTicket = null, nameTicket = null;

        if (ticketName.equals("Choix A")) {
            iA++;
            nameTicket = new Paragraph("choix A").setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);
            numTicket = new Paragraph("A0" + iA).setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);
        } else if (ticketName.equals("Choix B")) {
            iB++;
            nameTicket = new Paragraph("choix B").setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);
            numTicket = new Paragraph("B0" + iB).setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);

        } else if (ticketName.equals("Choix C")) {
            iC++;
            nameTicket = new Paragraph("choix C").setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);
            numTicket = new Paragraph("C0" + iC).setBold().setFontSize(15).setTextAlignment(TextAlignment.CENTER);

        }

        float[] width = {100f, 100f};
        Table table = new Table(width);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        table.addCell(new Cell().add(new Paragraph("Date")));
        table.addCell(new Cell().add(new Paragraph(LocalDate.now().format(dateTimeFormatter))));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        table.addCell(new Cell().add(new Paragraph("Time")));
        table.addCell(new Cell().add(new Paragraph(LocalTime.now().format(timeFormatter))));

        document.add(image);
        document.add(title);
        document.add(welcome);
        document.add(nameTicket);
        document.add(numTicket);
        document.add(table);

        document.close();
        Toast.makeText(this, "Pdf generated!", Toast.LENGTH_SHORT).show();
    }

    private void navigationDrawer() {

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.ticket);
        navigationView.bringToFront();

        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        drawerLayout.setScrimColor(getResources().getColor(R.color.colorApp));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }
}