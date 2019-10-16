package br.com.bossini.usjt_ccp3bn_mcb_ws_livros_rest_json;

import android.content.Context;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView livrosRecyclerView;
    private List <Livro> livros;
    private LivroAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        livrosRecyclerView =
                findViewById(R.id.livrosRecyclerView);
        livros = new ArrayList<>();
        adapter = new LivroAdapter(this, livros);
        LinearLayoutManager llm =
                new LinearLayoutManager(this);
        livrosRecyclerView.setAdapter(adapter);
        livrosRecyclerView.setLayoutManager(llm);
        swipeRefreshLayout =
                findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                obtemLivros();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
    }

    private String montaURL (String...partesDaURL){
        StringBuilder sb = new StringBuilder("");
        for (String parteDaURL : partesDaURL){
            sb.append(parteDaURL);
        }
        return sb.toString();
    }

    private void obtemLivros (){
        String url =
                montaURL(
                    getString(R.string.host_address),
                    getString(R.string.host_port),
                    getString(R.string.endpoint_base),
                    getString(R.string.endpoint_listar)
        );
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++){
                            try {
                                JSONObject iesimo = response.getJSONObject(i);
                                String titulo = iesimo.getString("titulo");
                                String autor = iesimo.getString("autor");
                                String edicao = iesimo.getString ("edicao");
                                Livro l = new Livro (titulo, autor, edicao);
                                livros.add(l);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        requestQueue.add(req);
    }
}

class LivroViewHolder extends RecyclerView.ViewHolder{
    public TextView tituloTextView;
    public TextView autorTextView;
    public TextView edicaoTextView;

    public LivroViewHolder (View raiz){
        super (raiz);
        tituloTextView =
                raiz.findViewById(R.id.tituloTextView);
        autorTextView =
                raiz.findViewById(R.id.autorTextView);
        edicaoTextView =
                raiz.findViewById(R.id.edicaoTextView);
    }
}

class LivroAdapter extends
        RecyclerView.Adapter <LivroViewHolder>{

    private Context context;
    private List<Livro> livros;

    public LivroAdapter(Context context, List<Livro> livros) {
        this.context = context;
        this.livros = livros;
    }

    @NonNull
    @Override
    public LivroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View raiz = inflater.inflate(
                R.layout.list_item,
                parent,
                false
        );
        return new LivroViewHolder(raiz);
    }

    @Override
    public void onBindViewHolder(
            @NonNull LivroViewHolder holder,
                                 int position) {
        Livro l = livros.get(position);
        holder.tituloTextView.setText(
                l.getTitulo()
        );
        holder.edicaoTextView.setText(
                l.getEdicao()
        );
        holder.autorTextView.setText(
                l.getAutor()
        );
    }
    @Override
    public int getItemCount() {
        return livros.size();
    }
}