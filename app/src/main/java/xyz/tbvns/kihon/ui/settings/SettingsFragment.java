package xyz.tbvns.kihon.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.core.os.LocaleListCompat;
import xyz.tbvns.EZConfig;
import xyz.tbvns.kihon.Constants;
import xyz.tbvns.kihon.Settings;
import xyz.tbvns.kihon.R;
import xyz.tbvns.kihon.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;

    private final ActivityResultLauncher<Uri> folderPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), uri -> {
                if (uri != null) {
                    requireContext().getContentResolver().takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Settings.mihonPath = uri.toString();
                    try {
                        EZConfig.save();
                        DocumentFile pickedDir = DocumentFile.fromTreeUri(requireContext(), uri);
                        boolean createExtract = true;
                        for (DocumentFile file : pickedDir.listFiles()) {
                            if ("extracted".equals(file.getName())) {
                                Constants.ExtractedFile = file;
                                createExtract = false;
                            }
                        }
                        if (createExtract) Constants.ExtractedFile = pickedDir.createDirectory("extracted");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(requireContext(), getString(R.string.select_mihon_folder) + ": " + uri, Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "Folder selection cancelled");
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        Button selectFolderButton = binding.getRoot().findViewById(R.id.button);
        selectFolderButton.setOnClickListener(v -> folderPickerLauncher.launch(null));

        Spinner languageSpinner = binding.getRoot().findViewById(R.id.languageSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setSelection(getCurrentLanguageIndex());
        languageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1 -> AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"));
                    case 2 -> AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("zh-CN"));
                    default -> AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList());
                }
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        return binding.getRoot();
    }

    private int getCurrentLanguageIndex() {
        String lang = AppCompatDelegate.getApplicationLocales().toLanguageTags();
        if (lang.startsWith("zh")) return 2;
        if (lang.startsWith("en")) return 1;
        return 0;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
