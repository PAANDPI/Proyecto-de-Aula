package com.example.loginfirebasemail77.modelos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.loginfirebasemail77.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class adaptadorReconocimiento extends RecyclerView.Adapter<adaptadorReconocimiento.ViewHolder>  implements  View.OnClickListener  {

    private List<reconocimientoFire> Data;
    private LayoutInflater myinflater;
    private Context context;
    private View.OnClickListener listener;

    public adaptadorReconocimiento(List<reconocimientoFire> itemList, Context context)
    {
        this.myinflater=LayoutInflater.from(context);
        this.context=context;
        this.Data=itemList;
    }

    @Override
    public  int getItemCount()
    { return Data.size();
    }

    @Override
    public adaptadorReconocimiento.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view=myinflater.inflate(R.layout.elementoscaecus, null);
        view.setOnClickListener(this);

        return new adaptadorReconocimiento.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final adaptadorReconocimiento.ViewHolder holder, final int position)
    {
        holder.bindData(Data.get(position));

    }

    public  void  setItems(List<reconocimientoFire> items)
    {Data=items;}

    public  void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }
    @Override
    public void onClick(View v) {
        if(listener!=null)
        {
            listener.onClick(v);
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView reconocimiento,confienza;
        ImageView imgReconocimiento;
        String url;
        ViewHolder(View itemView)
        {
            super(itemView);
            imgReconocimiento=itemView.findViewById(R.id.imgFotoReconocimientoD);
            reconocimiento=itemView.findViewById(R.id.txtNombreReconocimientoD);
            confienza=itemView.findViewById(R.id.TxtConfianzaD);

        }
        void bindData(final  reconocimientoFire item)
        {
            url=item.getUrl();
            Picasso.get().load(url).resize(100,100).centerCrop().into(imgReconocimiento);
            reconocimiento.setText(item.getReconocimiento().replace(";","\n"));
            confienza.setText(item.getConfianza().replace(";","\n"));

        }
    }
}
