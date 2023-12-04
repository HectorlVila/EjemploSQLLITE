package hector.lagunas.ejemplosqllite;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.j256.ormlite.dao.Dao;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hector.lagunas.ejemplosqllite.adapters.ProductoAdapter;
import hector.lagunas.ejemplosqllite.configuraciones.Configuracion;
import hector.lagunas.ejemplosqllite.databinding.ActivityMainBinding;
import hector.lagunas.ejemplosqllite.helpers.ProductosHelper;
import hector.lagunas.ejemplosqllite.modelos.Producto;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ArrayList<Producto> listaProducto;
    private ProductoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProductosHelper helper;
    private Dao<Producto, Integer> daoProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listaProducto = new ArrayList<>();

        adapter = new ProductoAdapter(MainActivity.this, listaProducto, R.layout.product_view_holder);
        layoutManager = new LinearLayoutManager(this);

        binding.contentMain.contenedor.setAdapter(adapter);
        binding.contentMain.contenedor.setLayoutManager(layoutManager);

        helper = new ProductosHelper(this, Configuracion.BD_NAME, null, Configuracion.BD_VERSION);

        if (helper !=null){
            try {
                daoProductos = helper.getDaoProductos();
                listaProducto.addAll(daoProductos.queryForAll());
                adapter.notifyItemRangeInserted(0, listaProducto.size());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearProducto().show();
            }
        });
    }

    private AlertDialog crearProducto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("CREAR PRODUCTO");
        builder.setCancelable(false);

        View productView = LayoutInflater.from(this).inflate(R.layout.product_view_model, null);
        EditText txtNombre = productView.findViewById(R.id.txtNombreProductViewModel);
        EditText txtCantidad= productView.findViewById(R.id.txtCantidadProductViewModel);
        EditText txtPrecio = productView.findViewById(R.id.txtPrecioProductViewModel);
        builder.setView(productView);

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (txtNombre.getText().toString().isEmpty() || txtCantidad.getText().toString().isEmpty() || txtPrecio.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "FALTAN DATOS :(", Toast.LENGTH_SHORT).show();
                }else {
                    Producto producto = new Producto(txtNombre.getText().toString(), Integer.parseInt(txtCantidad.getText().toString()), Float.parseFloat(txtPrecio.getText().toString()));
                    listaProducto.add(producto);
                    adapter.notifyItemInserted(listaProducto.size() -1);
                    try {
                        daoProductos.create(producto);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        return builder.create();
    }

}