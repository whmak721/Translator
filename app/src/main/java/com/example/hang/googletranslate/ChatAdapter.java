package com.example.hang.googletranslate;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import static com.example.hang.googletranslate.Translator.*;

/**
 * Created by HanG on 22/3/2018.
 */

public class ChatAdapter extends BaseAdapter {

    private final List<ChatMessage> chatMessages;
    private Activity context;
 //   private String targetLang;
    final HashMap<String, String> hashMap = new HashMap<>();
    final HashMap<String, String> hashMap2 = new HashMap<>();

 //   private int rowSelected = -1;

    public ChatAdapter(Activity context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    public List<ChatMessage> getChatMessages(){
        return chatMessages;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final boolean[] check1 = {true};
        final boolean[] check2 = {true};
        final ViewHolder holder;
        final ChatMessage chatMessage = getItem(position);

            LayoutInflater vi = context.getLayoutInflater();
            convertView = vi.inflate(R.layout.messageview, null);
            holder = createViewHolder(convertView);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(convertView.getContext(), android.R.layout.simple_spinner_item, chatMessages.get(position).getMatches());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            holder.matches.setAdapter(adapter);
            holder.matches2.setAdapter(adapter);
            holder.matches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!check1[0]){
                        final int getPosition = (Integer) adapterView.getTag();
                        Log.e("getPosition", String.valueOf(getPosition));
                        chatMessages.get(getPosition).setChoice(i);
                        Translator translator = new Translator(context);
                        translator.translate(holder.matches.getSelectedItem().toString(), chatMessage.getTargetLang(), new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            chatMessages.get(getPosition).setMessage(result);
                            holder.txtMessage.setText(chatMessages.get(position).getMessage());
                        }
                    });
                }
                check1[0] = false;
            }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            holder.matches2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!check2[0]) {
                        final int getPosition = (Integer) adapterView.getTag();
                        Log.e("getPosition", String.valueOf(getPosition));
                        chatMessages.get(getPosition).setChoice(i);
                        Translator translator = new Translator(context);
                        translator.translate(holder.matches2.getSelectedItem().toString(), chatMessage.getTargetLang(), new VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                chatMessages.get(getPosition).setMessage(result);
                                holder.txtMessage.setText(chatMessages.get(position).getMessage());
                            }
                        });
                    }
                    check2[0] =false;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            convertView.setTag(holder);
            convertView.setTag(R.id.txtMessage, holder.txtMessage);
            convertView.setTag(R.id.txtInfo, holder.txtInfo);
            convertView.setTag(R.id.matches, holder.matches);
            convertView.setTag(R.id.matches2, holder.matches2);

        Log.e("position", String.valueOf(position));
        holder.matches.setTag(position);
        holder.matches2.setTag(position);
        holder.txtInfo.setText(chatMessages.get(position).getDate());
        holder.txtMessage.setText(chatMessages.get(position).getMessage());
        holder.matches.setSelection(chatMessages.get(position).getChoice());
        holder.matches2.setSelection(chatMessages.get(position).getChoice());

        boolean myMsg = chatMessage.getIsme() ;//Just a dummy check
        //to simulate whether it me or other sender
        setAlignment(holder, myMsg);
        return convertView;
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe) {
        if (!isMe) {
            holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.matches.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.matches.setLayoutParams(layoutParams);
            holder.matches.setVisibility(View.VISIBLE);
            holder.matches2.setVisibility(View.GONE);
        } else {
            holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.matches2.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.matches2.setLayoutParams(layoutParams);
            holder.matches2.setVisibility(View.VISIBLE);
            holder.matches.setVisibility(View.GONE);
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        holder.matches = (Spinner) v.findViewById(R.id.matches);
        holder.matches2 = (Spinner) v.findViewById(R.id.matches2);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        public Spinner matches;
        public Spinner matches2;
    }

}