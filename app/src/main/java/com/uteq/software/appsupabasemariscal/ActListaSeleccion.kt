package com.uteq.software.appsupabasemariscal

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.uteq.software.appsupabasemariscal.data.SupabaseManager
import com.uteq.software.appsupabasemariscal.models.Materia
import com.uteq.software.appsupabasemariscal.utils.SupabaseErrorHandler
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

private infix fun Unit.catch(e: Any) {}

class ActListaSeleccion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_lista_seleccion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val actNiveles = findViewById<AutoCompleteTextView>(R.id.listaNiveles)
        actNiveles.setOnItemClickListener { parent, view, position, id ->
            val nivelSeleccionado = parent.getItemAtPosition(position).toString()
            Toast.makeText( this,
                "Nivel seleccionado: $nivelSeleccionado",
                Toast.LENGTH_LONG).show()

            llenarMaterias(position+1)
        }



    }
    fun llenarMaterias(nivel: Int) {
        val actMaterias = findViewById<AutoCompleteTextView>(R.id.listaMaterias)
        val lstMaterias = ArrayList<String>()

        lifecycleScope.launch {
            try {
                val listaMaterias = ArrayList(
                    SupabaseManager.client
                        .from("materias")
                        .select {
                            filter {
                                eq("nivel", nivel)
                            }
                            order("nombre", Order.ASCENDING)
                        }
                        .decodeList<Materia>()
                )
                for (materia in listaMaterias) {
                    lstMaterias.add(materia.nombre ?: "")
                }
            } catch (e: RestException) {
                SupabaseErrorHandler.show(this@ActListaSeleccion, e)
                lstMaterias.clear()
            } finally {
                val adapter = ArrayAdapter(
                    this@ActListaSeleccion,
                    android.R.layout.simple_spinner_dropdown_item,
                    lstMaterias
                )
                actMaterias.setAdapter(adapter)
            }
        }
    }

}