package com.neosantara.prospek.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.neosantara.prospek.model.Prospect;
import com.neosantara.prospek.model.ProspectPayload;
import com.neosantara.prospek.model.ProspectUpdatePayload;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ProspectViewModel viewModel;
    private LinearLayout loginPanel;
    private LinearLayout prospectsPanel;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText nameInput;
    private EditText prospectEmailInput;
    private EditText phoneInput;
    private ArrayAdapter<Prospect> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProspectViewModel.class);
        setContentView(createContentView());
        observeViewModel();
    }

    private View createContentView() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), dp(12));

        TextView title = new TextView(this);
        title.setText("Prospek Pelanggan");
        title.setTextSize(24);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        createLoginPanel(root);
        createProspectsPanel(root);

        return root;
    }

    private void createLoginPanel(LinearLayout root) {
        loginPanel = new LinearLayout(this);
        loginPanel.setOrientation(LinearLayout.VERTICAL);

        emailInput = createInput("Email akun ERP", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        passwordInput = createInput("Password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        Button loginButton = new Button(this);
        loginButton.setText("Login");
        loginButton.setOnClickListener(view -> viewModel.login(
            emailInput.getText().toString().trim(),
            passwordInput.getText().toString()
        ));

        loginPanel.addView(emailInput);
        loginPanel.addView(passwordInput);
        loginPanel.addView(loginButton);
        root.addView(loginPanel, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
    }

    private void createProspectsPanel(LinearLayout root) {
        prospectsPanel = new LinearLayout(this);
        prospectsPanel.setOrientation(LinearLayout.VERTICAL);
        prospectsPanel.setVisibility(View.GONE);

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        TextView heading = new TextView(this);
        heading.setText("Daftar prospek");
        heading.setTextSize(18);
        heading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        toolbar.addView(heading, new LinearLayout.LayoutParams(0, dp(48), 1));

        Button logoutButton = new Button(this);
        logoutButton.setText("Logout");
        logoutButton.setOnClickListener(view -> viewModel.logout());
        toolbar.addView(logoutButton);
        prospectsPanel.addView(toolbar);

        nameInput = createInput("Nama kontak", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        prospectEmailInput = createInput("Email (opsional jika ada telepon)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        phoneInput = createInput("Nomor telepon (opsional jika ada email)", InputType.TYPE_CLASS_PHONE);
        prospectsPanel.addView(nameInput);
        prospectsPanel.addView(prospectEmailInput);
        prospectsPanel.addView(phoneInput);

        Button createButton = new Button(this);
        createButton.setText("Tambah prospek");
        createButton.setOnClickListener(view -> submitProspect());
        prospectsPanel.addView(createButton);

        ListView listView = new ListView(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_2, android.R.id.text1, new ArrayList<>()) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                Prospect prospect = getItem(position);
                TextView primary = row.findViewById(android.R.id.text1);
                TextView secondary = row.findViewById(android.R.id.text2);

                if (prospect != null) {
                    primary.setText(prospect.name);
                    secondary.setText(formatProspectDetails(prospect));
                }

                return row;
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Prospect prospect = adapter.getItem(position);

            if (prospect != null) {
                viewModel.loadProspect(prospect.id);
            }
        });
        prospectsPanel.addView(listView, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1
        ));

        root.addView(prospectsPanel, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1
        ));
    }

    private void observeViewModel() {
        viewModel.getAuthenticated().observe(this, isAuthenticated -> {
            boolean authenticated = Boolean.TRUE.equals(isAuthenticated);
            loginPanel.setVisibility(authenticated ? View.GONE : View.VISIBLE);
            prospectsPanel.setVisibility(authenticated ? View.VISIBLE : View.GONE);
        });

        viewModel.getProspects().observe(this, prospects -> {
            adapter.clear();

            if (prospects != null) {
                adapter.addAll(prospects);
            }

            adapter.notifyDataSetChanged();
        });

        viewModel.getSelectedProspect().observe(this, prospect -> {
            if (prospect != null) {
                showProspectDetails(prospect);
                viewModel.clearSelectedProspect();
            }
        });

        viewModel.getMessage().observe(this, message -> {
            if (!TextUtils.isEmpty(message)) {
                if ("Prospek berhasil ditambahkan.".equals(message)) {
                    nameInput.setText("");
                    prospectEmailInput.setText("");
                    phoneInput.setText("");
                }

                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submitProspect() {
        String name = nameInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Nama kontak wajib diisi.");
            return;
        }

        String email = prospectEmailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        if (!validateContact(email, phone, prospectEmailInput, phoneInput)) {
            return;
        }

        viewModel.createProspect(new ProspectPayload(
            name,
            email.isEmpty() ? null : email,
            phone.isEmpty() ? null : phone
        ));
    }

    private boolean validateContact(String email, String phone, EditText emailField, EditText phoneField) {
        if (email.isEmpty() && phone.isEmpty()) {
            emailField.setError("Isi email atau nomor telepon.");
            phoneField.setError("Isi email atau nomor telepon.");
            return false;
        }

        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Format email tidak valid.");
            emailField.requestFocus();
            return false;
        }

        return true;
    }

    private void showProspectDetails(Prospect prospect) {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(24), dp(8), dp(24), dp(8));
        scrollView.addView(details);

        addDetailRow(details, "ID", String.valueOf(prospect.id));
        addDetailRow(details, "Nama Kontak", prospect.name);
        addDetailRow(details, "Nama Perusahaan", prospect.company_name);
        addDetailRow(details, "Jabatan", prospect.job_title);
        addDetailRow(details, "Email", prospect.email);
        addDetailRow(details, "Nomor Telepon", prospect.phone);
        addDetailRow(details, "Kota", prospect.city);
        addDetailRow(details, "Sumber Prospek", prospect.source);
        addDetailRow(details, "Status", prospect.status);
        addDetailRow(details, "Catatan", prospect.notes);
        addDetailRow(details, "Jadwal Tindak Lanjut", prospect.next_follow_up_at);
        addDetailRow(details, "ID Penanggung Jawab", prospect.assigned_user_id == null
            ? null
            : String.valueOf(prospect.assigned_user_id));
        addDetailRow(details, "Dibuat", prospect.created_at);
        addDetailRow(details, "Diperbarui", prospect.updated_at);

        new AlertDialog.Builder(this)
            .setTitle("Detail Prospek")
            .setView(scrollView)
            .setNegativeButton("Hapus", (dialog, selectedAction) -> confirmDeleteProspect(prospect))
            .setNeutralButton("Tutup", null)
            .setPositiveButton("Ubah", (dialog, selectedAction) -> showEditProspectDialog(prospect))
            .show();
    }

    private void addDetailRow(LinearLayout container, String label, String value) {
        TextView row = new TextView(this);
        row.setText(label + ": " + (TextUtils.isEmpty(value) ? "—" : value));
        row.setTextSize(16);
        row.setPadding(0, dp(8), 0, dp(8));
        container.addView(row);
    }

    private void showEditProspectDialog(Prospect prospect) {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(24), dp(8), dp(24), 0);

        EditText editedName = createInput("Nama kontak", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText editedEmail = createInput("Email (opsional jika ada telepon)", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText editedPhone = createInput("Nomor telepon (opsional jika ada email)", InputType.TYPE_CLASS_PHONE);
        editedName.setText(prospect.name);
        editedEmail.setText(prospect.email);
        editedPhone.setText(prospect.phone);
        form.addView(editedName);
        form.addView(editedEmail);
        form.addView(editedPhone);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Ubah prospek")
            .setView(form)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan", null)
            .create();

        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String name = editedName.getText().toString().trim();
            String email = editedEmail.getText().toString().trim();
            String phone = editedPhone.getText().toString().trim();

            if (name.isEmpty()) {
                editedName.setError("Nama kontak wajib diisi.");
                editedName.requestFocus();
                return;
            }

            if (!validateContact(email, phone, editedEmail, editedPhone)) {
                return;
            }

            viewModel.updateProspect(prospect.id, new ProspectUpdatePayload(
                name,
                email.isEmpty() ? null : email,
                phone.isEmpty() ? null : phone
            ));
            dialog.dismiss();
        }));
        dialog.show();
    }

    private void confirmDeleteProspect(Prospect prospect) {
        new AlertDialog.Builder(this)
            .setTitle("Hapus prospek")
            .setMessage("Hapus prospek " + prospect.name + "?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus", (dialog, selectedAction) -> viewModel.deleteProspect(prospect.id))
            .show();
    }

    private EditText createInput(String hint, int inputType) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(true);
        input.setInputType(inputType);
        return input;
    }

    private String formatProspectDetails(Prospect prospect) {
        StringBuilder details = new StringBuilder();

        if (!TextUtils.isEmpty(prospect.company_name)) {
            details.append(prospect.company_name).append(" · ");
        }

        if (!TextUtils.isEmpty(prospect.email)) {
            details.append(prospect.email);
        } else if (!TextUtils.isEmpty(prospect.phone)) {
            details.append(prospect.phone);
        }

        if (!TextUtils.isEmpty(prospect.status)) {
            if (details.length() > 0) {
                details.append(" · ");
            }

            details.append(prospect.status);
        }

        return details.toString();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
