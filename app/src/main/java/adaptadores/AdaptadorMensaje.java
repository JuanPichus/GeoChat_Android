package adaptadores;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a22100178_geochat.R;

import org.w3c.dom.Text;

public class AdaptadorMensaje extends RecyclerView.Adapter<AdaptadorMensaje.MyActivity> {
    public Context context;

    @NonNull
    @Override
    public AdaptadorMensaje.MyActivity onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = View.inflate(context, R.layout.message_holder, null);
        MyActivity obj = new MyActivity(v);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorMensaje.MyActivity holder, int position) {
        final int pos = position;
        holder.mensaje.setText(""); //aqui poner el mensaje
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyActivity extends RecyclerView.ViewHolder {
        public TextView mensaje;

        public MyActivity(@NonNull View itemView) {
            super(itemView);
            mensaje = itemView.findViewById(R.id.message);
        }
    }
}
