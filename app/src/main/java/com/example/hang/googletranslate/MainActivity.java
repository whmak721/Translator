package com.example.hang.googletranslate;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private Locale currentTranslateLang = Locale.getDefault();
    private Locale currentSpokenLang = Locale.getDefault();

    private Locale locSpanish = new Locale("es", "");
    private Locale locRussian = new Locale("ru", "");
    private Locale locPortuguese = new Locale("pt", "");
    private Locale locDutch = new Locale("nl", "");
    private Locale locHK = new Locale("yue", "");
    private Locale locChinese = new Locale("zh", "TW");

    private Locale[] languages = {locDutch, Locale.FRENCH, Locale.GERMAN, Locale.ITALIAN, locPortuguese, locRussian, locSpanish, locHK, locChinese, Locale.ENGLISH, Locale.JAPANESE};
    private int spinnerTransIndex = 0;
    private int spinnerSpeakIndex = 0;
    private String[] arrayOfTranslations;
    private int countDown = 10;

    RequestQueue rq;
    String key = "AIzaSyAy52ilPeKqEhJ_zjwfBLbnvnykB1FDTPw";
    TextToSpeech toSpeech;
    int result;
    String urlLang = "https://translation.googleapis.com/language/translate/v2/languages?key=" + key;
    Spinner spinnerTranslate;
    Spinner spinnerSpeak;
    private boolean autoMode = false;
    private boolean Lang1Clicked = false;
    private boolean Lang2Clicked = false;
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;
    RecognitionProgressView recognitionProgressView;
    ImageButton Lang1;
    ImageButton Lang2;
    int mute = R.drawable.speak_0;
    int[] imageArray = {R.drawable.speak_1, R.drawable.speak_2, R.drawable.speak_3};
    int rmute = R.drawable.rspeak_0;
    int[] rimageArray = {R.drawable.rspeak_1, R.drawable.rspeak_2, R.drawable.rspeak_3};
    Button save;
    TextView txtRecord;

    ProgressBar progressBar;

    private ListView messagesContainer;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    TextView noMessage;

    Boolean timeOut = false;
    Boolean onResult = false;

    Context context = this;
    Gson gson = new Gson();

    TextView count1;
    TextView count2;

    Boolean waiting = false;


    @Override
    public void onStop() {
        super.onStop();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        mEditor.remove("current");
        mEditor.apply();
        //Do whatever you want to do when the application stops.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_list:
                Intent intent = new Intent(MainActivity.this, ConversationList.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle("Translator");
        setContentView(R.layout.activity_main);

        checkPermission();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        spinnerSpeak = (Spinner) findViewById(R.id.spinner_speak);
        rq = Volley.newRequestQueue(this);
        Lang1 = (ImageButton) findViewById(R.id.Lang1);
        final Button Auto = (Button) findViewById(R.id.Auto);
        Lang2 = (ImageButton) findViewById(R.id.Lang2);
        ImageView reset = (ImageView) findViewById(R.id.ivreset);
        noMessage = (TextView) findViewById(R.id.noConversation);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        save = (Button) findViewById(R.id.save);
        count1 = (TextView) findViewById(R.id.count);
        count2 = (TextView) findViewById(R.id.count2);
        txtRecord = (TextView) findViewById(R.id.txtRecord);

        //select language item for Language 2
        spinnerTranslate = (Spinner) findViewById(R.id.spinner_trans);
        spinnerTranslate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                currentTranslateLang = languages[index];
                spinnerTransIndex = index;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                currentTranslateLang = languages[0];
                spinnerTransIndex = 0;
            }
        });

        //select language item for Language 1
        spinnerSpeak.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                currentSpokenLang = languages[index];
                spinnerSpeakIndex = index;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                currentSpokenLang = languages[0];
                spinnerSpeakIndex = 0;
            }
        });

        initControls();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditor = mPreferences.edit();

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String json = gson.toJson(adapter.getChatMessages());
                                        ArrayList<ChatMessage> temp = gson.fromJson(json, new TypeToken<ArrayList<ChatMessage>>() {
                                        }.getType());
                                        mEditor.putString(String.valueOf(userInput.getText()), json);
                                        mEditor.commit();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        //reset button
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtRecord.setText("");
                if (!adapter.getChatMessages().isEmpty()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Clear conversation?");
                    alertDialog.setMessage("Current conversation will be erased!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    chatHistory.clear();
                                    adapter = new ChatAdapter(MainActivity.this, new ArrayList<ChatMessage>());
                                    messagesContainer.setAdapter(adapter);
                                    noMessage.setVisibility(View.VISIBLE);
                                    mEditor = mPreferences.edit();
                                    mEditor.remove("current");
                                    mEditor.apply();
                                }
                            });
                    alertDialog.show();

                }
            }
        });


        //auto switch language mode
        Auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoMode) {
                    autoMode = false;
                    mSpeechRecognizer.destroy();
                    Lang1Clicked = false;
                    Lang2Clicked = false;
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    autoMode = true;
                    progressBar.setVisibility(View.VISIBLE);
                }
                Log.e("autoMode", String.valueOf(autoMode));
            }
        });

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        //select color for RecognitionProgressView
        int[] colors = {
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color4),
                ContextCompat.getColor(this, R.color.color5)
        };

        //select height for RecognitionProgressView
        int[] heights = {30, 34, 28, 33, 26};

        //set attributes of RecognitionProgressView
        recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);
        recognitionProgressView.setSpeechRecognizer(mSpeechRecognizer);
        recognitionProgressView.setColors(colors);
        recognitionProgressView.setBarMaxHeightsInDp(heights);
        recognitionProgressView.setCircleRadiusInDp(4);
        recognitionProgressView.setSpacingInDp(4);
        recognitionProgressView.setIdleStateAmplitudeInDp(2);
        recognitionProgressView.setRotationRadiusInDp(15);
        //start animation
        recognitionProgressView.play();

        //Language 1 action
        Lang1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", String.valueOf(mSpeechRecognizer));
                if (Lang1Clicked) {
                    Lang1Clicked = false;
                    mSpeechRecognizer.cancel();
                    recognitionProgressView.stop();
                    recognitionProgressView.play();
                }
                //Lang1 being clicked again
                else {
                    Lang1Clicked = true;
                    Lang2Clicked = false;
                    speakeImageChange("Lang1");
                    Log.e("Lang1Clicked", String.valueOf(Lang1Clicked));
                    initSpeechRecognizer(currentSpokenLang);
                    Log.e("currentSpokenLang", String.valueOf(currentSpokenLang));
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    Log.e("Lang1", "StartListening");
                }
            }
        });

        //Language 2 action
        Lang2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Lang2Clicked) {
                    Lang2Clicked = false;
                    mSpeechRecognizer.cancel();
                    recognitionProgressView.stop();
                    recognitionProgressView.play();
                }
                //Lang2 being clicked again
                else {
                    Lang2Clicked = true;
                    Lang1Clicked = false;
                    speakeImageChange("Lang2");
                    Log.e("Lang2Clicked", String.valueOf(Lang2Clicked));
                    initSpeechRecognizer(currentTranslateLang);
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    Log.e("Lang2", "StartListening");
                }
            }
        });

        //initialize TexttoSpeech
        toSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                Log.e("TAG", "TextToSpeech.SUCCESS");
                toSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.e("TAG", "onStart");
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        //reset RecognitionProgressView animation
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchLang();
                                recognitionProgressView.stop();
                                recognitionProgressView.play();
                                Log.e("TAG", "onDone");
                            }
                        });
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.e("TAG", "onError");
                    }
                });
            }
        });
    }

    //display speech bubbles
    public void displayMessage(ChatMessage message) {
        Log.e("TAG", "new message");
        if (noMessage.getVisibility() == View.VISIBLE)
            noMessage.setVisibility(View.INVISIBLE);
        adapter.add(message);
        chatHistory.add(message);
        adapter.notifyDataSetChanged();
        String json = gson.toJson(adapter.getChatMessages());
        ArrayList<ChatMessage> temp = gson.fromJson(json, new TypeToken<ArrayList<ChatMessage>>() {
        }.getType());
        mEditor = mPreferences.edit();
        mEditor.putString("current", json);
        mEditor.commit();
        scroll();
    }

    //display latest speech bubble
    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    //initialize speech containers and speech array
    private void initControls() {
        Intent intent = getIntent();
        ArrayList<ChatMessage> chat = new ArrayList<>();
        chatHistory = new ArrayList<>();
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        //Display selected conversation history
        if (intent.getStringArrayExtra("item") != null) {
            Log.e("item", intent.getStringArrayExtra("item")[1]);
            String[] splitor = intent.getStringArrayExtra("item")[1].split("<=>");
            ArrayAdapter arrayAdapter = (ArrayAdapter) spinnerTranslate.getAdapter();
            spinnerSpeak.setSelection(arrayAdapter.getPosition(splitor[0]));
            arrayAdapter = (ArrayAdapter) spinnerSpeak.getAdapter();
            spinnerTranslate.setSelection(arrayAdapter.getPosition(splitor[1]));
            String message = mPreferences.getString(intent.getStringArrayExtra("item")[0], "");
            ArrayList<ChatMessage> chatMessages = gson.fromJson(message, new TypeToken<ArrayList<ChatMessage>>() {
            }.getType());
            chat = chatMessages;
            noMessage.setVisibility(View.INVISIBLE);
            txtRecord.setText("Record: " + intent.getStringArrayExtra("item")[0]);
        } else if (mPreferences.contains("current")) {
            ArrayList<ChatMessage> temp = gson.fromJson(mPreferences.getString("current", ""), new TypeToken<ArrayList<ChatMessage>>() {
            }.getType());
            String[] splitor = temp.get(0).getTranslate().split("<=>");
            ArrayAdapter arrayAdapter = (ArrayAdapter) spinnerTranslate.getAdapter();
            spinnerSpeak.setSelection(arrayAdapter.getPosition(splitor[0]));
            arrayAdapter = (ArrayAdapter) spinnerSpeak.getAdapter();
            spinnerTranslate.setSelection(arrayAdapter.getPosition(splitor[1]));
            chat = temp;
            noMessage.setVisibility(View.INVISIBLE);
        }
        adapter = new ChatAdapter(MainActivity.this, chat);
        messagesContainer.setAdapter(adapter);
    }

    //changing image while speaking
    public void speakeImageChange(String Lang) {
        final Handler handler = new Handler();
        if (Lang.equals("Lang1")) {
            Runnable runnable = new Runnable() {
                int i = 0;

                public void run() {
                    Lang1.setImageResource(imageArray[i]);
                    i++;
                    if (i > imageArray.length - 1) {
                        i = 0;
                    }
                    if (Lang1Clicked) {
                        handler.postDelayed(this, 500);
                    } else
                        Lang1.setImageResource(mute);
                }
            };
            handler.postDelayed(runnable, 0);
        } else {

            Runnable runnable = new Runnable() {
                int i = 0;

                public void run() {
                    Lang2.setImageResource(rimageArray[i]);
                    i++;
                    if (i > rimageArray.length - 1) {
                        i = 0;
                    }
                    if (Lang2Clicked) {
                        handler.postDelayed(this, 500);
                    } else
                        Lang2.setImageResource(rmute);
                }
            };
            handler.postDelayed(runnable, 0);
        }
    }

    //action taken after text to speech
    public void switchLang() {
        if (autoMode) {
            Log.e("AUTO", String.valueOf(autoMode));
            if (Lang1Clicked) {
                Log.e("Lang1Clicked", String.valueOf(Lang1Clicked));
                Lang1Clicked = false;
                Log.e("Lang1 End", "");
                Lang2.performClick();
            } else if (Lang2Clicked) {
                Log.e("Lang2Clicked", String.valueOf(Lang2Clicked));
                Lang2Clicked = false;
                Log.e("Lang2 End", "");
                Lang1.performClick();
            }
        } else {
            Lang1Clicked = false;
            Lang2Clicked = false;
        }
        recognitionProgressView.stop();
        recognitionProgressView.play();
    }

    //Translate text
    public void translateText(ArrayList<String> text) {
        String targetLang = "";
        if (Lang1Clicked) {
            targetLang = currentTranslateLang.toString();
        } else if (Lang2Clicked) {
            targetLang = currentSpokenLang.toString();
        }
        if (targetLang.equals(locHK.toString())) {
            targetLang = locChinese.toString();
        }
        String urlString = "https://translation.googleapis.com/language/translate/v2?key=" + key + "&q=" + text.get(0) + "&target=" + targetLang;
        Log.e("url: ", urlString);
        sendJSONRequest(text, urlString, targetLang);
    }

    //Start speaking text
    public void speakText(String text) {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        amanager.setStreamMute(AudioManager.STREAM_RING, false);
        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
        } else {
            if (Lang1Clicked)
                toSpeech.setLanguage(currentTranslateLang);
            else
                toSpeech.setLanguage(currentSpokenLang);
            Log.e("TAG", "speak");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.e("SDK", "Hello");
                String myUtteranceID = "myUtteranceID";
                toSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, myUtteranceID);
            } else {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "myUtteranceID");
                toSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, hashMap);
            }
        }
        mSpeechRecognizer.destroy();
    }

    //Initialize SpeechRecognizer ###lang.toString###
    public void initSpeechRecognizer(Locale lang) {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        amanager.setStreamMute(AudioManager.STREAM_RING, true);
        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        onResult = false;
        Log.e("TAG", "initSpeechRecognizer");
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognitionProgressView.setSpeechRecognizer(mSpeechRecognizer);
        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                //super.onReadyForSpeech(params);
            }

            @Override
            public void onBeginningOfSpeech() {
                if (!waiting) {
                    waitForResponse();
                }
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                super.onRmsChanged(rmsdB);
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                super.onBufferReceived(buffer);
            }

            @Override
            public void onEndOfSpeech() {
                super.onEndOfSpeech();
            }

            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "Client side error";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "Network error";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No match";
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RecognitionService busy";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "error from server";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No speech input";
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        break;
                    default:
                        message = "Didn't understand, please try again.";
                        break;
                }
            }

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                translateText(matches);
                // matches are the return values of speech recognition engine
                // Use these values for whatever you wish to do
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (!onResult && !matches.get(0).isEmpty())
                    onResult = true;
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                super.onEvent(eventType, params);
            }
        });

        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang.toString());
    }

    //Check permission for recording audio
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    public void waitForResponse() {
        waiting = true;
        timeOut = false;
        final Handler handler = new Handler();
        final int[] count = {countDown};
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (count[0] > 0) {
                    count[0]--;
                    //results come while counting
                    if (onResult) {
                        handler.removeCallbacksAndMessages(null);
                        count1.setText(String.valueOf(countDown));
                        count2.setText(String.valueOf(countDown));
                        waiting = false;
                    }
                    else {
                        //Lang1 clicked again while counting
                        if (Lang1Clicked) {
                            count1.setText("0" + String.valueOf(count[0]));
                            handler.postDelayed(this, 1000);
                        }
                        //Lang2 clicked again while counting
                        else if (Lang2Clicked) {
                            count2.setText("0" + String.valueOf(count[0]));
                            handler.postDelayed(this, 1000);
                        }
                        //counting ends
                        else {
                            handler.removeCallbacksAndMessages(null);
                            count1.setText(String.valueOf(countDown));
                            count2.setText(String.valueOf(countDown));
                            waiting = false;
                        }
                    }
                } else {
                    //No results
                    if (!onResult) {
                        count1.setText(String.valueOf(countDown));
                        count2.setText(String.valueOf(countDown));
                        timeOut = true;
                        Toast.makeText(MainActivity.this, "No voice input", Toast.LENGTH_LONG).show();
                        mSpeechRecognizer.destroy();
                        waiting = false;
                        switchLang();
                    }
                }
            }

        }, 0);
    }

    //Translate by requesting URL
    public void sendJSONRequest(final ArrayList<String> oriTxt, String urlString, final String targetLang) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray info;
                    info = response.getJSONObject("data").getJSONArray("translations");
                    JSONObject jsonObject = info.getJSONObject(0);
                    String data = jsonObject.getString("translatedText");
                    if (data.contains("&#39;")) {
                        data = data.replaceAll("&#39;", "'");
                    }
                    ChatMessage chatMessage = new ChatMessage();
                    if (!Lang1Clicked) {
                        chatMessage.setMe(false);
                    } else {
                        chatMessage.setMe(true);
                    }
                    chatMessage.setMessage(data);
                    chatMessage.setChoice(0);
                    chatMessage.setTargetLang(targetLang);
                    chatMessage.setMatches(oriTxt);
                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setTranslate(spinnerSpeak.getSelectedItem().toString() + "<=>" + spinnerTranslate.getSelectedItem().toString());
                    displayMessage(chatMessage);
                    speakText(data);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rq.add(jsonObjectRequest);
    }

    //Image onClick event
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_DOWN:
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                break;

        }
        return true;
    }
}


