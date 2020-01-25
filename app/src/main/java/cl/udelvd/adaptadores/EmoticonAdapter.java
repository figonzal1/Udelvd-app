package cl.udelvd.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import cl.udelvd.R;
import cl.udelvd.modelo.Emoticon;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class EmoticonAdapter extends ArrayAdapter<Emoticon> {

    private List<Emoticon> emoticonList;
    private Context context;

    public EmoticonAdapter(@NonNull Context context, List<Emoticon> emoticons) {
        super(context, R.layout.spinner_emoticones, emoticons);
        this.context = context;
        this.emoticonList = emoticons;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Emoticon emoticon = emoticonList.get(position);

        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert mInflater != null;
            convertView = mInflater.inflate(R.layout.spinner_emoticones, parent, false);

            holder.iv_emoticon = convertView.findViewById(R.id.id_emoticon);
            holder.tv_descripcion_emoticon = convertView.findViewById(R.id.tv_descripcion_emoticon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Cargar imagen en spinner de emoticon
        Glide.with(context)
                .load(emoticon.getUrl())
                .apply(new RequestOptions()
                        .error(R.drawable.not_found))
                .transition(withCrossFade())
                .into(holder.iv_emoticon);

        holder.tv_descripcion_emoticon.setText(emoticon.getDescripcion());

        return convertView;
    }

    @Override
    public int getCount() {
        return emoticonList.size();
    }

    private static class ViewHolder {
        ImageView iv_emoticon;
        TextView tv_descripcion_emoticon;
    }
}
