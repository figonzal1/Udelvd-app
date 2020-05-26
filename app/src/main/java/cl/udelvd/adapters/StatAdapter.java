package cl.udelvd.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import cl.udelvd.R;
import cl.udelvd.models.Stat;
import cl.udelvd.views.activities.WebViewActivity;

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.StatViewHolder> {

    private List<Stat> statList;
    private Context context;


    public StatAdapter(List<Stat> statList, Context context) {
        this.statList = statList;
        this.context = context;
    }

    @NonNull
    @Override
    public StatAdapter.StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_stat, parent, false);
        return new StatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StatViewHolder holder, int position) {

        final Stat stat = statList.get(position);

        holder.tvName.setText(stat.getName());

        holder.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(context.getString(R.string.INTENT_LINK_GRAFICO), stat.getUrl());
                context.startActivity(intent);
            }
        });

        //CONFIG BLUE LINK
        int index = stat.getUrl().indexOf("//");
        String subtring = stat.getUrl().substring(index + 2);
        holder.tvLink.setText(String.format(context.getString(R.string.FORMATO_LINK_DIRECTO), subtring));

        holder.tvLink.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable spans = (Spannable) holder.tvLink.getText();
        ClickableSpan clickSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {

                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(context.getString(R.string.INTENT_LINK_GRAFICO), stat.getUrl());

                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, R.string.COPIADO_PORTAPAPELES, Toast.LENGTH_LONG).show();
                }

            }
        };
        spans.setSpan(clickSpan, holder.tvLink.getText().toString().indexOf("rebrand.ly"), spans.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return statList.size();
    }

    public void updateList(List<Stat> statList) {
        this.statList = statList;
        notifyDataSetChanged();
    }

    static class StatViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvLink;
        private MaterialButton btnGo;

        StatViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_stat_name);
            tvLink = itemView.findViewById(R.id.tv_short_link);
            btnGo = itemView.findViewById(R.id.btn_go);
        }
    }
}
