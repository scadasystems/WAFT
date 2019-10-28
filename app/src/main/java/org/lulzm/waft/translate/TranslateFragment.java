package org.lulzm.waft.translate;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;
import org.lulzm.waft.R;

import java.util.Objects;

import xyz.hasnat.sweettoast.SweetToast;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranslateFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSIONS = 100;

    final String nmtClientId = "9ktevnXZUrLj_OKQ0U47";
    final String nmtClientSecret = "zP81ESfodD";
    final String cssClientId = "sjz5j7vhcc";
    final String cssClientSecret = "S7TORTD1cFcVgdSXW9tmAy5ZmjZ0LmGbByIYyQAv";

    // 권한 체크
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                SweetToast.error(getActivity(), getString(R.string.need_permission));
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(() -> ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1), 2000);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);

        ImageView translate_arrow_right = view.findViewById(R.id.translate_arrow_right);
        ImageButton btn_copy = view.findViewById(R.id.btn_copy);
        ImageButton btn_speak = view.findViewById(R.id.btn_speak);

        // 다크모드 가져오기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("change_theme", getActivity().MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            Glide.with(this).load(R.drawable.ic_arrow_forward_white_24dp).into(translate_arrow_right);
            Glide.with(this).load(R.drawable.ic_content_copy_white_24dp).into(btn_copy);
            Glide.with(this).load(R.drawable.ic_volume_up_white_24dp).into(btn_speak);
        } else {
            Glide.with(this).load(R.drawable.ic_arrow_forward_black_24dp).into(translate_arrow_right);
            Glide.with(this).load(R.drawable.ic_content_copy_black_24dp).into(btn_copy);
            Glide.with(this).load(R.drawable.ic_volume_up_black_24dp).into(btn_speak);
        }

        Button btn_translate = view.findViewById(R.id.btn_translate);
        btn_translate.setOnClickListener(v -> {
            TextInputEditText nmtSourceText = view.findViewById(R.id.edt_translate_before);
            String text = Objects.requireNonNull(nmtSourceText.getText()).toString();

            Spinner csrSourceSpinner = view.findViewById(R.id.csr_lang_source_spinner);
            String selSourceItem = csrSourceSpinner.getSelectedItem().toString();

            Spinner csrTragetSpinner = view.findViewById(R.id.csr_lang_target_spinner);
            String selTargetItem = csrTragetSpinner.getSelectedItem().toString();

            PapagoNmtTask papagoNmtTask = new PapagoNmtTask();
            papagoNmtTask.execute(text, convertLangItem(selSourceItem), convertLangItem(selTargetItem), nmtClientId, nmtClientSecret);
        });

        // 번역 전
        Spinner s = view.findViewById(R.id.csr_lang_source_spinner);
        String[] str = getActivity().getResources().getStringArray(R.array.csr_lang_source_spinner);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, str);
        stringArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_items);
        s.setAdapter(stringArrayAdapter);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] items;

                if (parent.getItemAtPosition(position).equals("영어")) {
                    items = new String[]{"한국어", "중국어(간체)", "중국어(번체)", "프랑스어"};
                } else if (parent.getItemAtPosition(position).equals("한국어")) {
                    items = new String[]{"영어", "중국어(간체)", "중국어(번체)", "일본어", "프랑스어", "스페인어", "베트남어", "태국어"};
                } else {
                    items = new String[]{"한국어"};
                }

                Spinner spinner = getActivity().findViewById(R.id.csr_lang_target_spinner);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, items);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_items);
                spinner.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinner_translated = view.findViewById(R.id.csr_lang_target_spinner);
        spinner_translated.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String[] speak_items;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("영어")) {
                    speak_items = new String[]{"clara(영어)", "matt(영어)"};
                } else if (parent.getItemAtPosition(position).equals("일본어")) {
                    speak_items = new String[]{"yuri(일본어)", "shinji(일본어)"};
                } else if (parent.getItemAtPosition(position).equals("중국어(간체)") || parent.getItemAtPosition(position).equals("중국어(번체)")) {
                    speak_items = new String[]{"meimei(중국어)", "liangliang(중국어)"};
                } else if (parent.getItemAtPosition(position).equals("스페인어")) {
                    speak_items = new String[]{"carmen(스페인어)", "jose(스페인어)"};
                } else {
                    speak_items = new String[]{""};
                }

                // 스피커
                Spinner speak_spinner = getActivity().findViewById(R.id.spinner_css_lang);
                ArrayAdapter<String> speakArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, speak_items);
                speakArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_items);
                speak_spinner.setAdapter(speakArrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private class PapagoNmtTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            return NmtProc.main(strings[0], strings[1], strings[2], strings[3], strings[4]);
        }

        @Override
        protected void onPostExecute(String s) {
            ReturnThreadResult(s);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void ReturnThreadResult(String result) {
        String rlt = result;

        try {
            JSONObject jsonObject = new JSONObject(rlt);
            String text = jsonObject.getString("message");

            jsonObject = new JSONObject(text);
            jsonObject = new JSONObject(jsonObject.getString("result"));
            text = jsonObject.getString("translatedText");

            AppCompatTextView txtResult = getActivity().findViewById(R.id.tv_translate_after);
            txtResult.setMovementMethod(new ScrollingMovementMethod());
            ImageButton btn_copy = getActivity().findViewById(R.id.btn_copy);

            String TranslatedText = text;

            txtResult.setText(String.valueOf(TranslatedText));
            btn_copy.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Context context = getActivity();
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("TranslatedText", TranslatedText);
                    if (!TranslatedText.isEmpty()) {
                        clipboardManager.setPrimaryClip(clipData);
                        SweetToast.success(getActivity(), getString(R.string.success_trans_clip));
                    }
                }
                return false;
            });

            ImageButton btn_speak = getActivity().findViewById(R.id.btn_speak);
            String SpeakText = text;
            btn_speak.setOnClickListener(v -> {
                Spinner spinner = getActivity().findViewById(R.id.spinner_css_lang);
                String selItem = spinner.getSelectedItem().toString();

                String[] splits = selItem.split("\\(");

                String speaker = splits[0];

                if (speaker.isEmpty() || speaker.equals("")) {
                    speaker = "";
                    SweetToast.warning(getActivity(), getString(R.string.speaker_not));
                }

                NaverTTSTask tts = new NaverTTSTask();
                tts.execute(SpeakText, speaker, cssClientId, cssClientSecret);
            });

            Spinner spinner_css = Objects.requireNonNull(getActivity()).findViewById(R.id.spinner_css_lang);

            spinner_css.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


        } catch (Exception e) {
        }
    }

    private String convertLangItem(String strItem) {
        if (strItem.equals("한국어"))
            return "ko";
        else if (strItem.equals("중국어(간체)"))
            return "zh-CN";
        else if (strItem.equals("중국어(번체)"))
            return "zh-TW";
        else if (strItem.equals("영어"))
            return "en";
        else if (strItem.equals("일본어"))
            return "ja";
        else if (strItem.equals("스페인어"))
            return "es";
        else if (strItem.equals("프랑스어"))
            return "fr";
        else if (strItem.equals("베트남어"))
            return "vi";
        else if (strItem.equals("태국어"))
            return "th";
        else if (strItem.equals("인도네시아어"))
            return "id";
        else
            return "";
    }

    private class NaverTTSTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            CssProc.main(strings[0], strings[1], strings[2], strings[3]);
            return null;
        }
    }

    // 권한 체크
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                // 위치 권한 허용X
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
                }
        }
    }


}
